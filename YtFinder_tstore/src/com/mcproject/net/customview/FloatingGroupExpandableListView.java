package com.mcproject.net.customview;

import com.mcproject.ytfavorite_t.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public class FloatingGroupExpandableListView extends ExpandableListView {

	private static final int[] EMPTY_STATE_SET = {};

	// State indicating the group is expanded
	private static final int[] GROUP_EXPANDED_STATE_SET = {android.R.attr.state_expanded};

	// State indicating the group is empty (has no children)
	private static final int[] GROUP_EMPTY_STATE_SET = {android.R.attr.state_empty};

	// State indicating the group is expanded and empty (has no children) 
	private static final int[] GROUP_EXPANDED_EMPTY_STATE_SET = {android.R.attr.state_expanded, android.R.attr.state_empty};

	// States for the group where the 0th bit is expanded and 1st bit is empty.
	private static final int[][] GROUP_STATE_SETS = {
		EMPTY_STATE_SET, // 00
		GROUP_EXPANDED_STATE_SET, // 01
		GROUP_EMPTY_STATE_SET, // 10
		GROUP_EXPANDED_EMPTY_STATE_SET // 11
	};

	private WrapperExpandableListAdapter mAdapter;
	private OnScrollListener mOnScrollListener;

	// By default, the floating group is enabled
	private boolean mFloatingGroupEnabled = true;
	private View mFloatingGroupView;
	private int mFloatingGroupPackedPosition;
	private int mFloatingGroupScrollY;
	private OnScrollFloatingGroupListener mOnScrollFloatingGroupListener;

	// An AttachInfo instance is added to the FloatingGroupView in order to have proper touch event handling
	private Object mViewAttachInfo;
	private boolean mHandledByOnInterceptTouchEvent;
	private boolean mHandledByOnTouchEvent;
	private Runnable mOnClickAction;

	private boolean mSelectorEnabled;
	private boolean mShouldPositionSelector;
	private boolean mDrawSelectorOnTop;
	private Drawable mSelector;
	private int mSelectorPosition;
	private final Rect mSelectorRect = new Rect();
	private Runnable mPositionSelectorOnTapAction;

	private final Rect mIndicatorRect = new Rect();

	public FloatingGroupExpandableListView(Context context) {
		super(context);
		init();
	}

	public FloatingGroupExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FloatingGroupExpandableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		super.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(mOnScrollListener != null) {
					mOnScrollListener.onScrollStateChanged(view, scrollState);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if(mOnScrollListener != null) {
					mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				}

				if(mFloatingGroupEnabled && mAdapter != null && mAdapter.getGroupCount() > 0 && visibleItemCount > 0) {
					createFloatingGroupView(firstVisibleItem);
				}
			}
		});

		mOnClickAction = new Runnable() {

			@Override
			public void run() {
				if(mAdapter.isGroupExpanded(mFloatingGroupPackedPosition)) {
					collapseGroup(mFloatingGroupPackedPosition);
				} else {
					expandGroup(mFloatingGroupPackedPosition);
				}
				setSelectedGroup(mFloatingGroupPackedPosition);
			}

		};

		mPositionSelectorOnTapAction = new Runnable() {

			@Override
			public void run() {
				positionSelectorOnFloatingGroup();
				setPressed(true);
				if (mFloatingGroupView != null) {
					mFloatingGroupView.setPressed(true);
				}
			}
		};
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		// Reflection is used here to obtain info about the selector
		mSelectorPosition = (Integer) ReflectionUtils.getFieldValue(AbsListView.class, "mSelectorPosition", FloatingGroupExpandableListView.this);
		mSelectorRect.set((Rect) ReflectionUtils.getFieldValue(AbsListView.class, "mSelectorRect", FloatingGroupExpandableListView.this));

		if(!mDrawSelectorOnTop) {
			drawDefaultSelector(canvas);
		}

		super.dispatchDraw(canvas);

		if(mFloatingGroupEnabled && mFloatingGroupView != null && mFloatingGroupView.getVisibility() == View.VISIBLE) {
			if(!mDrawSelectorOnTop) {
				drawFloatingGroupSelector(canvas);
			}

			canvas.save();
			canvas.clipRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
			drawChild(canvas, mFloatingGroupView, getDrawingTime());
			drawFloatingGroupIndicator(canvas);
			canvas.restore();
		}

		if(mDrawSelectorOnTop) {
			drawDefaultSelector(canvas);
			drawFloatingGroupSelector(canvas);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		final int action = ev.getAction() & MotionEvent.ACTION_MASK;

		if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_CANCEL) {
			mHandledByOnInterceptTouchEvent = false;
			mHandledByOnTouchEvent = false;
			mShouldPositionSelector = false;
		}

		// If touch events are being handled by onInterceptTouchEvent() or onTouchEvent() we shouldn't dispatch them to the floating group
		if(!mHandledByOnInterceptTouchEvent && !mHandledByOnTouchEvent && mFloatingGroupView != null) {
			final int screenCoords[] = new int[2];
			getLocationInWindow(screenCoords);
			final RectF floatingGroupRect = new RectF(screenCoords[0] + mFloatingGroupView.getLeft(), screenCoords[1] + mFloatingGroupView.getTop(), screenCoords[0] + mFloatingGroupView.getRight(), screenCoords[1] + mFloatingGroupView.getBottom());

			if(floatingGroupRect.contains(ev.getRawX(), ev.getRawY())) {
				if(mSelectorEnabled) {
					switch(action) {
					case MotionEvent.ACTION_DOWN:
						mShouldPositionSelector = true;
						removeCallbacks(mPositionSelectorOnTapAction);
						postDelayed(mPositionSelectorOnTapAction, ViewConfiguration.getTapTimeout());
						break;
					case MotionEvent.ACTION_UP:
						positionSelectorOnFloatingGroup();
						setPressed(true);
						if (mFloatingGroupView != null) {
							mFloatingGroupView.setPressed(true);
						}
						break;
					}
				}

				if(mFloatingGroupView.dispatchTouchEvent(ev)) {
					onInterceptTouchEvent(ev);
					return true;
				}
			}
		}

		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		mHandledByOnInterceptTouchEvent = super.onInterceptTouchEvent(ev);
		return mHandledByOnInterceptTouchEvent;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		mHandledByOnTouchEvent = super.onTouchEvent(ev);
		return mHandledByOnTouchEvent;
	}

	@Override
	public void setSelector(Drawable sel) {
		super.setSelector(new ColorDrawable(Color.TRANSPARENT));
		if (mSelector != null) {
			mSelector.setCallback(null);
			unscheduleDrawable(mSelector);
		}
		mSelector = sel;
		mSelector.setCallback(this);
	}

	@Override
	public void setDrawSelectorOnTop(boolean onTop) {
		super.setDrawSelectorOnTop(onTop);
		mDrawSelectorOnTop = onTop;
	}

	@Override
	public void setAdapter(ExpandableListAdapter adapter) {
		if(!(adapter instanceof WrapperExpandableListAdapter)) {
			throw new IllegalArgumentException("The adapter must be an instance of WrapperExpandableListAdapter");
		}
		setAdapter((WrapperExpandableListAdapter) adapter);
	}

	public void setAdapter(WrapperExpandableListAdapter adapter) {
		super.setAdapter(adapter);
		mAdapter = adapter;
	}

	@Override
	public void setOnScrollListener(OnScrollListener listener) {
		mOnScrollListener = listener;
	}

	public void setFloatingGroupEnabled(boolean floatingGroupEnabled) {
		mFloatingGroupEnabled = floatingGroupEnabled;
	}

	public void setOnScrollFloatingGroupListener(OnScrollFloatingGroupListener listener) {
		mOnScrollFloatingGroupListener = listener;
	}

	private void createFloatingGroupView(int position) {
		mFloatingGroupView = null;
		mFloatingGroupPackedPosition = getPackedPositionGroup(getExpandableListPosition(position));

		for(int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			final Object tag = child.getTag(R.id.fgelv_tag_changed_visibility);
			if(tag instanceof Boolean) {
				final boolean changedVisibility = (Boolean) tag;
				if(changedVisibility) {
					child.setVisibility(View.VISIBLE);
					child.setTag(R.id.fgelv_tag_changed_visibility, null);
				}
			}
		}
		
		if(!mFloatingGroupEnabled) {
			return;
		}

		final int floatingGroupFlatPosition = getFlatListPosition(getPackedPositionForGroup(mFloatingGroupPackedPosition));
		final int floatingGroupListPosition = floatingGroupFlatPosition - position;

		if(floatingGroupListPosition >= 0 && floatingGroupListPosition < getChildCount()) {
			final View currentGroupView = getChildAt(floatingGroupListPosition);

			if(currentGroupView.getTop() > getPaddingTop()) {
				return;
			} else if(currentGroupView.getTop() <= getPaddingTop() && currentGroupView.getVisibility() == View.VISIBLE) {
				currentGroupView.setVisibility(View.INVISIBLE);
				currentGroupView.setTag(R.id.fgelv_tag_changed_visibility, true);
			}
		}

		if(mFloatingGroupPackedPosition >= 0) {
			mFloatingGroupView = mAdapter.getGroupView(mFloatingGroupPackedPosition, mAdapter.isGroupExpanded(mFloatingGroupPackedPosition), mFloatingGroupView, this);

			if(!mFloatingGroupView.isClickable()) {
				mSelectorEnabled = true;
				mFloatingGroupView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						postDelayed(mOnClickAction, ViewConfiguration.getPressedStateDuration());
					}
				});
			} else {
				mSelectorEnabled = false;
			}

			loadAttachInfo();
			setAttachInfo(mFloatingGroupView);
		}

		if(mFloatingGroupView == null || mFloatingGroupView.getVisibility() != View.VISIBLE) {
			return;
		}

		final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(getWidth() - getPaddingLeft() - getPaddingRight(),  MeasureSpec.EXACTLY);
		final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(getHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.AT_MOST);
		
		if(mFloatingGroupView.getLayoutParams() == null) {
			mFloatingGroupView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		}
		
		mFloatingGroupView.measure(widthMeasureSpec, heightMeasureSpec);

		int floatingGroupScrollY = 0;
		
		final int nextGroupFlatPosition = getFlatListPosition(getPackedPositionForGroup(mFloatingGroupPackedPosition + 1));
		final int nextGroupListPosition = nextGroupFlatPosition - position;

		if(nextGroupListPosition >= 0 && nextGroupListPosition < getChildCount()) {
			final View nextGroupView = getChildAt(nextGroupListPosition);
			
			if(nextGroupView != null && nextGroupView.getTop() < getPaddingTop() + mFloatingGroupView.getMeasuredHeight() + getDividerHeight()) {
				floatingGroupScrollY = nextGroupView.getTop() - (getPaddingTop() + mFloatingGroupView.getMeasuredHeight() + getDividerHeight());
			}
		}

		final int left = getPaddingLeft();
		final int top = getPaddingTop() + floatingGroupScrollY;
		final int right = left + mFloatingGroupView.getMeasuredWidth();
		final int bottom = top + mFloatingGroupView.getMeasuredHeight();
		mFloatingGroupView.layout(left, top, right, bottom);

		mFloatingGroupScrollY = floatingGroupScrollY;
		if(mOnScrollFloatingGroupListener != null) {
			mOnScrollFloatingGroupListener.onScrollFloatingGroupListener(mFloatingGroupView, mFloatingGroupScrollY);
		}
	}

	private void loadAttachInfo() {
		if(mViewAttachInfo == null) {
			mViewAttachInfo = ReflectionUtils.getFieldValue(View.class, "mAttachInfo", FloatingGroupExpandableListView.this);
		}
	}

	private void setAttachInfo(View v) {
		if(v == null) {
			return;
		}
		if(mViewAttachInfo != null) {
			ReflectionUtils.setFieldValue(View.class, "mAttachInfo", v, mViewAttachInfo);
		}
		if(v instanceof ViewGroup) {
			final ViewGroup viewGroup = (ViewGroup) v;
			for(int i = 0; i < viewGroup.getChildCount(); i++) {
				setAttachInfo(viewGroup.getChildAt(i));
			}
		}
	}

	private void positionSelectorOnFloatingGroup() {
		if(mShouldPositionSelector && mFloatingGroupView != null) {
			final int floatingGroupFlatPosition = getFlatListPosition(getPackedPositionForGroup(mFloatingGroupPackedPosition));
			ReflectionUtils.invokeMethod(AbsListView.class, "positionSelector", new Class<?>[]{int.class, View.class}, FloatingGroupExpandableListView.this, floatingGroupFlatPosition, mFloatingGroupView);
			invalidate();
		}
		mShouldPositionSelector = false;
		removeCallbacks(mPositionSelectorOnTapAction);
	}

	private void drawDefaultSelector(Canvas canvas) {
		final int floatingGroupFlatPosition = getFlatListPosition(getPackedPositionForGroup(mFloatingGroupPackedPosition));
		final int selectorListPosition = mSelectorPosition - getFirstVisiblePosition();

		if(selectorListPosition >= 0 && selectorListPosition < getChildCount() && mSelectorRect != null && !mSelectorRect.isEmpty()) {
			if(mSelectorPosition != floatingGroupFlatPosition || mSelectorPosition == floatingGroupFlatPosition && mFloatingGroupView == null) {
				drawSelector(canvas);
			}
		}
	}

	private void drawFloatingGroupSelector(Canvas canvas) {
		final int floatingGroupFlatPosition = getFlatListPosition(getPackedPositionForGroup(mFloatingGroupPackedPosition));

		if(mFloatingGroupEnabled && mFloatingGroupView != null && mFloatingGroupView.getVisibility() == View.VISIBLE && mSelectorPosition == floatingGroupFlatPosition && mSelectorRect != null && !mSelectorRect.isEmpty()) {
			mSelectorRect.set(mFloatingGroupView.getLeft(), mFloatingGroupView.getTop(), mFloatingGroupView.getRight(), mFloatingGroupView.getBottom());
			drawSelector(canvas);
		}
	}

	private void drawSelector(Canvas canvas) {
		canvas.save();
		canvas.clipRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
		if(isPressed()) {
			mSelector.setState(getDrawableState());
		} else {
			mSelector.setState(EMPTY_STATE_SET);
		}
		mSelector.setBounds(mSelectorRect);
		mSelector.draw(canvas);
		canvas.restore();
	}

	private void drawFloatingGroupIndicator(Canvas canvas) {
		final Drawable groupIndicator = (Drawable) ReflectionUtils.getFieldValue(ExpandableListView.class, "mGroupIndicator", FloatingGroupExpandableListView.this);
		if(groupIndicator != null) {
			final int stateSetIndex =
					(mAdapter.isGroupExpanded(mFloatingGroupPackedPosition) ? 1 : 0) | // Expanded?
					(mAdapter.getChildrenCount(mFloatingGroupPackedPosition) > 0 ? 2 : 0); // Empty?
			groupIndicator.setState(GROUP_STATE_SETS[stateSetIndex]);

			final int indicatorLeft = (Integer) ReflectionUtils.getFieldValue(ExpandableListView.class, "mIndicatorLeft", FloatingGroupExpandableListView.this);
			final int indicatorRight = (Integer) ReflectionUtils.getFieldValue(ExpandableListView.class, "mIndicatorRight", FloatingGroupExpandableListView.this);
			mIndicatorRect.set(indicatorLeft + getPaddingLeft(), mFloatingGroupView.getTop(), indicatorRight + getPaddingLeft(), mFloatingGroupView.getBottom());

			groupIndicator.setBounds(mIndicatorRect);
			groupIndicator.draw(canvas);
		}
	}

	public interface OnScrollFloatingGroupListener {
		public void onScrollFloatingGroupListener(View floatingGroupView, int scrollY);
	}
}
