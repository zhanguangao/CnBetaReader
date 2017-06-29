/**
 * @file XListView.java
 * @package me.maxwin.view
 * @create Mar 18, 2012 6:28:41 PM
 * @author Maxwin
 * @description An ListView support (a) Pull down to refresh, (b) Pull up to load more.
 * 		Implement IXListViewListener, and see stopRefresh() / stopLoadMore().
 */
package org.zreo.cnbetareader.Views;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import org.zreo.cnbetareader.R;

public class XListView extends ListView implements OnScrollListener {
	private final String TAG = getClass().getName();
	private float mLastY = -1; // save event y
	private Scroller mScroller; // used for scroll back
	private OnScrollListener mScrollListener; // user's scroll listener

	// the interface to trigger refresh and load more.
	private IXListViewListener mListViewListener;

	// -- header view
	private XListViewHeader mHeaderView;
	// header view content, use it to calculate the Header's height. And hide it
	// when disable pull refresh.
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private int mHeaderViewHeight; // header view's height
	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false; // is refreashing.

	// -- footer view
	private XListViewFooter mFooterView;
	private boolean mEnablePullLoad;
	private boolean mPullLoading;
	private boolean mIsFooterReady = false;

	// total list items, used to detect is at the bottom of listview.
	private int mTotalItemCount;

	// for mScroller, scroll back from header or footer.
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400; // scroll back duration
	private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
														// at bottom, trigger
														// load more.
	private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
													// feature.
	public View quickReturnView;
	private int mCachedVerticalScrollRange;
	private int mQuickReturnHeight;
	private TranslateAnimation anim;
	private int firstVisibleItem;
	private int mLastOffset;
	private int mScrollY;
	private boolean isAnimation;
	private boolean isOnScreen = true;

	/**
	 * @param context
	 */
	public XListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public XListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	/**
	 * 下拉快速显示item功能
	 */
	 public void setQuickReturnView(final View quickReturnView) {
	 this.quickReturnView = quickReturnView;
	 // this.quickReturnIntercept = (QuickReturnInterceptor) quickReturnView;
	 // 添加视图树变动的监听器来监听视图的变化
	 // 如果Layout重新布局的话，该监听器的方法将会被调用
	 getViewTreeObserver().addOnGlobalLayoutListener(
	 new OnGlobalLayoutListener() {
	 @Override
	 public void onGlobalLayout() {
	 mCachedVerticalScrollRange = -1;
	
	 // 得到QuickReturnView的高度
	 mQuickReturnHeight = quickReturnView.getHeight();
	 mCachedVerticalScrollRange = computeVerticalScrollRange();
	 Log.i(TAG, " [h = " + mQuickReturnHeight
	 + "] AND [r = " + mCachedVerticalScrollRange
	 + "]");
	 }
	 });
	 }

	private void initWithContext(Context context) {
		mScroller = new Scroller(context, new DecelerateInterpolator());
		// XListView need the scroll event, and it will dispatch the event to
		// user's listener (as a proxy).
		super.setOnScrollListener(this);

		// init header view
		mHeaderView = new XListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView
				.findViewById(R.id.xlistview_header_content);
		mHeaderTimeView = (TextView) mHeaderView
				.findViewById(R.id.xlistview_header_time);
		addHeaderView(mHeaderView);

		// init footer view
		mFooterView = new XListViewFooter(context);

		// init header height
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mHeaderViewHeight = mHeaderViewContent.getHeight();
						getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// make sure XListViewFooter is the last footer view, and only add once.
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
		}
		super.setAdapter(adapter);
	}

	/**
	 * enable or disable pull down refresh feature. 是否允许下拉
	 * 
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) { // disable, hide the content
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * enable or disable pull up load more feature. 是否允许上拉
	 * 
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			mFooterView.hide();
			mFooterView.setOnClickListener(null);
			// make sure "pull up" don't show a line in bottom when listview
			// with one page
			setFooterDividersEnabled(false);
		} else {
			mPullLoading = false;
			mFooterView.show();
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
			// make sure "pull up" don't show a line in bottom when listview
			// with one page
			setFooterDividersEnabled(true);
			// both "pull up" and "click" will invoke load more.
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	/**
	 * stop refresh, reset header view.
	 */
	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
		}
	}

	/**
	 * stop load more, reset footer view.
	 */
	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
		}
	}

	/**
	 * set last refresh time
	 * 
	 * @param time
	 */
	public void setRefreshTime(String time) {
		mHeaderTimeView.setText(time);
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	private void updateHeaderHeight(float delta) {
		mHeaderView.setVisiableHeight((int) delta
				+ mHeaderView.getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
				mHeaderView.setState(XListViewHeader.STATE_READY);
			} else {
				mHeaderView.setState(XListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0); // scroll to top each time
	}

	/**
	 * reset header view's height.
	 */
	private void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();
		if (height == 0) // not visible.
			return;
		// refreshing and header isn't shown fully. do nothing.
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0; // default: scroll back to dismiss header.
		// is refreshing, just scroll back to show all the header.
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);
		// trigger computeScroll
		invalidate();
	}

	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
													// more.
				mFooterView.setState(XListViewFooter.STATE_READY);
			} else {
				mFooterView.setState(XListViewFooter.STATE_NORMAL);
			}
		}
		mFooterView.setBottomMargin(height);

		// setSelection(mTotalItemCount - 1); // scroll to bottom
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}
	}

	private void startLoadMore() {
		mPullLoading = true;
		mFooterView.setState(XListViewFooter.STATE_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
//			Log.w(getClass().getName(), " MotionEvent.ACTION_DOWN");
			break;
		case MotionEvent.ACTION_MOVE:
//			Log.w(getClass().getName(), " MotionEvent.ACTION_MOVE");
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0
					&& (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
				// the first item is showing, header has shown or pull down.
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				// last item, already pulled up or want to pull up.
				updateFooterHeight(-deltaY / OFFSET_RADIO);
			}
			break;
		default:
//			Log.w(getClass().getName(), " MotionEvent.default");
			mLastY = -1; // reset
			if (getFirstVisiblePosition() == 0) {
				// invoke refresh
				if (mEnablePullRefresh
						&& mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
					mPullRefreshing = true;
					mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
					if (mListViewListener != null) {
						mListViewListener.onRefresh();
					}
				}
				resetHeaderHeight();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
				// invoke load more.
				if (mEnablePullLoad
						&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA
						&& !mPullLoading) {
					startLoadMore();
				}
				resetFooterHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	public int getComputedScrollY() {
		return computeVerticalScrollOffset() - mLastOffset;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		 // send to user's listener
		 mTotalItemCount = totalItemCount;
		 if (mScrollListener != null) {
		 mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
		 totalItemCount);
		 }
		 /**
		  * 下拉快速显示item功能
		  */
		 this.firstVisibleItem = firstVisibleItem;
		 // isScrollUp用于标识用户是否在向上移动：
		 // null 表示当前没有移动
		 // true 表示向上移动
		 // false 表示向下移动
		 Boolean isScrollUp = null;
		 if (mCachedVerticalScrollRange != -1) {
		 // mScrllY保存的是上一次的位置
		 // computedScrollY保存的是当前位置
		 int computedScrollY = getComputedScrollY();
		 Log.i(TAG, "y = " + mScrollY + " , my = " + computedScrollY);
		 // 如果mScrollY == computedScrollY，表示没有移动，
		 // 上次的mScrollY位置大于本次computedScrollY,表示向上移动
		 // 否则表示向下移动
		 isScrollUp = mScrollY == computedScrollY ? null
		 : mScrollY > computedScrollY;
		 // 保存本次移动到的位置，以便下次使用
		 mScrollY = computedScrollY;
		 }
		
		 if (isScrollUp == null) {
		 return;
		 }
		 // 用户在向上移动 && QuickReturnView不在屏幕上 && QuickReturnView没有在执行动画
		 // 那么执行动画让QuickReturnView可见
		 if (isScrollUp && !isOnScreen && !isAnimation) {
		 isAnimation = true;
		 anim = new TranslateAnimation(0, 0, mQuickReturnHeight, 0);
		 anim.setFillAfter(true);
		 anim.setDuration(200);
		 anim.setAnimationListener(new Animation.AnimationListener() {
		 @Override
		 public void onAnimationStart(Animation animation) {
		 }
		
		 @Override
		 public void onAnimationEnd(Animation animation) {
		 isOnScreen = true;
		 isAnimation = false;
		 // quickReturnIntercept.setInterceptTouchEvent(false);
		 }
		
		 @Override
		 public void onAnimationRepeat(Animation animation) {
		 }
		 });
		 quickReturnView.startAnimation(anim);
		 }
		 // 用户在向下移动 && QuickReturnView在屏幕上 && QuickReturnView没有在执行动画
		 // 那么执行动画让QuickReturnView不可见
		 else if (!isScrollUp && isOnScreen && !isAnimation) {
		 isAnimation = true;
		 anim = new TranslateAnimation(0, 0, 0, mQuickReturnHeight);
		 anim.setFillAfter(true);
		 anim.setDuration(200);
		 anim.setAnimationListener(new Animation.AnimationListener() {
		 @Override
		 public void onAnimationStart(Animation animation) {
		 }
		
		 @Override
		 public void onAnimationEnd(Animation animation) {
		 isOnScreen = false;
		 isAnimation = false;
		 // quickReturnIntercept.setInterceptTouchEvent(true);
		 }
		
		 @Override
		 public void onAnimationRepeat(Animation animation) {
		 }
		 });
		 // 执行动画
		 quickReturnView.startAnimation(anim);
		 }
	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	/**
	 * you can listen ListView.OnScrollListener or this one. it will invoke
	 * onXScrolling when header/footer scroll back.
	 */
	public interface OnXScrollListener extends OnScrollListener {
		void onXScrolling(View view);
	}

	/**
	 * implements this interface to get refresh/load more event.
	 */
	public interface IXListViewListener {
		void onRefresh();

		void onLoadMore();
	}
}
