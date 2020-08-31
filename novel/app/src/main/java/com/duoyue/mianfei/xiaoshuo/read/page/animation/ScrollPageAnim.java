package com.duoyue.mianfei.xiaoshuo.read.page.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.view.*;
import android.widget.FrameLayout;
import com.duoyue.lib.base.log.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * 原理:仿照ListView源码实现的上下滑动效果
 * <p>
 */
public class ScrollPageAnim extends PageSwitcher {
    private static final String TAG = "ScrollAnimation";
    // 滑动追踪的时间
    private static final int VELOCITY_DURATION = 1000;
    private VelocityTracker mVelocity;

    protected Layer mCurLayer;
    protected Layer mNextLayer;
    protected Layer mTopLayer;

    // 被废弃的图片列表
    private ArrayDeque<BitmapView> mScrapViews;
    // 正在被利用的图片列表
    private ArrayList<BitmapView> mActiveViews = new ArrayList<>(2);

    // 是否处于刷新阶段
    private boolean isRefresh = true;
    private boolean isMove = false;
    private boolean canStart = false;
    private BitmapView tmpView;

    // 底部填充
    private Iterator<BitmapView> downIt;


    public ScrollPageAnim(int w, int h, int marginWidth, int marginHeight, ViewGroup view, PageSwitcher.OnPageChangeListener listener) {
        super(w, h, marginWidth, marginHeight, view, listener);
        // 创建两个BitmapView
        initWidget(view);
    }

    private void initWidget(ViewGroup rootView) {
        mCurLayer = new Layer();
        mNextLayer = new Layer();
        FrameLayout layout = (FrameLayout) rootView.getChildAt(0);
        mCurLayer.rootLayoutForExtra = layout;
        mNextLayer.rootLayoutForExtra = layout;
//        mBgBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.RGB_565);
        mCurLayer.bitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.RGB_565);
        mTopLayer = mCurLayer;
//        mNextLayer.bitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.RGB_565);


        mScrapViews = new ArrayDeque<>(2);
        for (int i = 0; i < 2; ++i) {
            BitmapView view = new BitmapView();
            view.bitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.RGB_565);
            view.srcRect = new Rect(0, 0, mViewWidth, mViewHeight);
            view.destRect = new Rect(0, 0, mViewWidth, mViewHeight);
            view.top = 0;
            view.bottom = view.bitmap.getHeight();

            mScrapViews.push(view);
        }
        onLayout();
        isRefresh = false;
    }

    // 修改布局,填充内容
    private void onLayout() {
        // 如果还没有开始加载，则从上到下进行绘制
        if (mActiveViews.size() == 0) {
            fillDown(0, 0);
            mDirection = PageSwitcher.Direction.NONE;
        } else {
            int offset = (int) (mTouchY - mLastY);
//            Logger.e(TAG, "移动的偏移量是offset = " + offset + ", mTouchY = " + mTouchY + ", mLastY = " + mLastY);
            // 判断是下滑还是上拉 (下滑)
            if (offset > 0) {
                int topEdge = mActiveViews.get(0).top;
                fillUp(topEdge, offset);
//                Logger.e(TAG, "看上一页");
            }
            // 上拉
            else {
                // 底部的距离 = 当前底部的距离 + 滑动的距离 (因为上滑，得到的值肯定是负的)
                int bottomEdge = mActiveViews.get(mActiveViews.size() - 1).bottom;
                fillDown(bottomEdge, offset);
//                Logger.e(TAG, "看下一页");
            }
        }
    }

    /**
     * 创建View填充底部空白部分
     *
     * @param bottomEdge :当前最后一个View的底部，在整个屏幕上的位置,即相对于屏幕顶部的距离
     * @param offset     :滑动的偏移量
     */
    private void fillDown(int bottomEdge, int offset) {

        downIt = mActiveViews.iterator();
        BitmapView view;
        // 进行删除
        while (downIt.hasNext()) {
            view = downIt.next();
            view.top = view.top + offset;
            view.bottom = view.bottom + offset;
            // 设置允许显示的范围
            view.destRect.top = view.top;
            view.destRect.bottom = view.bottom;

            // 判断是否越界了
            if (view.bottom <= 0) {
                // 添加到废弃的View中
                mScrapViews.add(view);
                // 从Active中移除
                downIt.remove();
                // 如果原先是从上加载，现在变成从下加载，则表示取消
                if (mDirection == PageSwitcher.Direction.UP) {
                    mListener.pageCancel();
                    mDirection = PageSwitcher.Direction.NONE;
                    Logger.e(TAG, "上滑 .越界了，取消滑动" );
                }
                if (mDirection == Direction.DOWN) {
                    Logger.e(TAG + "#point", "上滑, 广告位上-->下切换： mNextLayer.isExtraAfterChapter = " + mNextLayer.isExtraAfterChapter
                        + ", mCurLayer.isExtraAfterChapter = " + mCurLayer.isExtraAfterChapter
                            /*+ ", mCurLayer.isExtraChapterEnd = " + mCurLayer.isExtraChapterEnd
                            + ", mNextLayer.isExtraChapterEnd = " + mNextLayer.isExtraChapterEnd*/);
                    if (mNextLayer.isExtraAfterChapter) {
                        Logger.e(TAG, "上滑,切换广告显示位置" );
                        mCurLayer.isExtraAfterChapter = true;
                        mNextLayer.isExtraAfterChapter = false;
                    } else {
                        mCurLayer.isExtraAfterChapter = false;
                        mNextLayer.isExtraAfterChapter = false;
                    }
                    // 处理章节末的激励视频入口显示
                    if (mNextLayer.isExtraChapterEnd) {
                        Logger.e(TAG + "#point", "上滑,切换章节末视频入口显示位置" );
                        mCurLayer.isExtraChapterEnd = true;
                        mNextLayer.isExtraChapterEnd = false;
                    } else {
                        mCurLayer.isExtraChapterEnd = false;
                        mNextLayer.isExtraChapterEnd = false;
                    }
                }
                Logger.e(TAG, "上滑0.越界了 view = " + view.destRect + ", mViewHeight = " + mViewHeight);
            }
//            Logger.e(TAG, "上滑1. view = " + view.destRect + ", mViewHeight = " + mViewHeight);
        }

        // 滑动之后的最后一个 View 的距离屏幕顶部上的实际位置
        int realEdge = bottomEdge + offset;

        // 进行填充
        while (realEdge < mViewHeight && mActiveViews.size() < 2) {
            // 从废弃的Views中获取一个
            view = mScrapViews.getFirst();
/*          //擦除其Bitmap(重新创建会不会更好一点)
            eraseBitmap(view.bitmap,view.bitmap.getWidth(),view.bitmap.getHeight(),0,0);*/
            if (view == null) {
                Logger.e(TAG, "上滑2. view = null");
                return;
            }

            Bitmap cancelBitmap = mNextLayer.bitmap;
            mNextLayer.bitmap = view.bitmap;

            if (!isRefresh) {
//                Logger.e(TAG, "上滑3. 不需要更新");
                boolean hasNext = mListener.hasNext(); //如果不成功则无法滑动

                // 如果不存在next,则进行还原
                if (!hasNext) {
                    canStart = false;
//                    Logger.e(TAG, "上滑4. 没有下一个界面了");
                    mNextLayer.bitmap = cancelBitmap;
                    for (BitmapView activeView : mActiveViews) {
                        activeView.top = 0;
                        activeView.bottom = mViewHeight;
                        // 设置允许显示的范围
                        activeView.destRect.top = activeView.top;
                        activeView.destRect.bottom = activeView.bottom;
                    }
                    abortAnim();
                    return;
                }
            }

            // 如果加载成功，那么就将View从ScrapViews中移除
            mScrapViews.removeFirst();
            // 添加到存活的Bitmap中
            mActiveViews.add(view);
            mDirection = PageSwitcher.Direction.DOWN;

            // 设置Bitmap的范围
            view.top = realEdge;
            view.bottom = realEdge + view.bitmap.getHeight();
            // 设置允许显示的范围
            view.destRect.top = view.top;
            view.destRect.bottom = view.bottom;

            realEdge += view.bitmap.getHeight();
        }
    }

    private Iterator<BitmapView> upIt;

    /**
     * 创建View填充顶部空白部分
     *
     * @param topEdge : 当前第一个View的顶部，到屏幕顶部的距离
     * @param offset  : 滑动的偏移量
     */
    private void fillUp(int topEdge, int offset) {
        // 首先进行布局的调整
        upIt = mActiveViews.iterator();
        BitmapView view;
        while (upIt.hasNext()) {
            view = upIt.next();
            view.top = view.top + offset;
            view.bottom = view.bottom + offset;
            //设置允许显示的范围
            view.destRect.top = view.top;
            view.destRect.bottom = view.bottom;

            // 判断是否越界了
            if (view.top >= mViewHeight) {
                // 添加到废弃的View中
                mScrapViews.add(view);
                // 从Active中移除
                upIt.remove();
                // 如果原先是下，现在变成从上加载了，则表示取消加载

                if (mDirection == PageSwitcher.Direction.DOWN) {
                    mListener.pageCancel();
                    mDirection = PageSwitcher.Direction.NONE;
                }
//                Logger.e(TAG, "下滑0.越界了 view = " + view.destRect + ", mViewHeight = " + mViewHeight);
                if (mDirection == Direction.UP) {
                    Logger.e(TAG + "#point", "下滑, 广告位上-->下切换： mNextLayer.isExtraAfterChapter = " + mNextLayer.isExtraAfterChapter
                            + ", mCurLayer.isExtraAfterChapter = " + mCurLayer.isExtraAfterChapter);
                    if (mNextLayer.isExtraAfterChapter) {
                        mCurLayer.isExtraAfterChapter = true;
                        mNextLayer.isExtraAfterChapter = false;
                    } else {
                        mCurLayer.isExtraAfterChapter = false;
                        mNextLayer.isExtraAfterChapter = false;
                    }

                    // 处理章节末的激励视频入口显示
                    if (mNextLayer.isExtraChapterEnd) {
//                        Logger.e(TAG + "#point", "下滑,切换章节末视频入口显示位置" );
                        mCurLayer.isExtraChapterEnd = true;
                        mNextLayer.isExtraChapterEnd = false;
                    } else {
                        mCurLayer.isExtraChapterEnd = false;
                        mNextLayer.isExtraChapterEnd = false;
                    }
                }
            }
//            Logger.e(TAG, "下滑1. 移动后的view = " + view.destRect);
        }

        // 滑动之后，第一个 View 的顶部距离屏幕顶部的实际位置。
        int realEdge = topEdge + offset;

        // 对布局进行View填充
        while (realEdge > 0 && mActiveViews.size() < 2) {
            // 从废弃的Views中获取一个
            view = mScrapViews.getFirst();
            if (view == null) {
                return;
            }

            // 判断是否存在上一章节
            Bitmap cancelBitmap = mNextLayer.bitmap;
            mNextLayer.bitmap = view.bitmap;
            if (!isRefresh) {
//                Logger.e(TAG, "下滑3. 不刷新");
                boolean hasPrev = mListener.hasPrev(); // 如果不成功则无法滑动
                // 如果不存在next,则进行还原
                if (!hasPrev) {
//                    Logger.e(TAG, "下滑 不存在下页");
                    mNextLayer.bitmap = cancelBitmap;
                    for (BitmapView activeView : mActiveViews) {
                        activeView.top = 0;
                        activeView.bottom = mViewHeight;
                        // 设置允许显示的范围
                        activeView.destRect.top = activeView.top;
                        activeView.destRect.bottom = activeView.bottom;
                    }
                    abortAnim();
                    return;
                }
            }
            // 如果加载成功，那么就将View从ScrapViews中移除
            mScrapViews.removeFirst();
            // 加入到存活的对象中
            mActiveViews.add(0, view);
            mDirection = PageSwitcher.Direction.UP;
            // 设置Bitmap的范围
            view.top = realEdge - view.bitmap.getHeight();
            view.bottom = realEdge;

            // 设置允许显示的范围
            view.destRect.top = view.top;
            view.destRect.bottom = view.bottom;
            realEdge -= view.bitmap.getHeight();
        }
    }

    /**
     * 对Bitmap进行擦除
     *
     * @param b
     * @param width
     * @param height
     * @param paddingLeft
     * @param paddingTop
     */
    private void eraseBitmap(Bitmap b, int width, int height,
                             int paddingLeft, int paddingTop) {
     /*   if (mInitBitmapPix == null) return;
        b.setPixels(mInitBitmapPix, 0, width, paddingLeft, paddingTop, width, height);*/
    }

    /**
     * 重置位移
     */
    public void resetBitmap() {
        isRefresh = true;
        // 将所有的Active加入到Scrap中
        for (BitmapView view : mActiveViews) {
            mScrapViews.add(view);
        }
        // 清除所有的Active
        mActiveViews.clear();
        // 重新进行布局
        onLayout();
        isRefresh = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        // 初始化速度追踪器
        if (mVelocity == null) {
            mVelocity = VelocityTracker.obtain();
        }

        mVelocity.addMovement(event);
        // 设置触碰点
        setTouchPoint(x, y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMove = false;
                isRunning = false;
                // 设置起始点
                setStartPoint(x, y);
                // 停止动画
                abortAnim();
                break;
            case MotionEvent.ACTION_MOVE:
                final int slop = ViewConfiguration.get(mView.getContext()).getScaledTouchSlop();
                if (!isMove) {
                    isMove = Math.abs(mStartY - y) > slop;
                }
                if (isMove) {
                    mListener.onMoveing();

                    mVelocity.computeCurrentVelocity(VELOCITY_DURATION);
                    isRunning = true;

                    // 进行刷新
                    mView.postInvalidate();
                    canStart = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                mListener.actionUp();
                isMove = false;
                isRunning = false;
                // 开启动画
                startAnim();
                // 删除检测器
                mVelocity.recycle();
                mVelocity = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                try {
                    mVelocity.recycle(); // if velocityTracker won't be used should be recycled
                    mVelocity = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isMove = false;
                break;
        }
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        //进行布局
        onLayout();

        //绘制背景
        canvas.drawBitmap(mCurLayer.bitmap, 0, 0, null);
        //绘制内容
        canvas.save();
        //移动位置
        canvas.translate(0, mMarginHeight);
        //裁剪显示区域
        canvas.clipRect(0, 0, mViewWidth, mViewHeight);
        /*//设置背景透明
        canvas.drawColor(0x40);*/
        //绘制Bitmap
        if (mActiveViews.size() > 0) {
            tmpView = mActiveViews.get(0);
            canvas.drawBitmap(tmpView.bitmap, tmpView.srcRect, tmpView.destRect, null);
        }
        if (mActiveViews.size() > 1) {
            tmpView = mActiveViews.get(1);
            canvas.drawBitmap(tmpView.bitmap, tmpView.srcRect, tmpView.destRect, null);
        }
        if (mCurLayer.isExtraAfterChapter || mCurLayer.isExtraAfterBook) {
            mCurLayer.rootLayoutForExtra.setTranslationX(0);
            if (mDirection == Direction.DOWN) {
                mCurLayer.rootLayoutForExtra.setTranslationY(mActiveViews.get(0).destRect.top - mMarginHeight);
            } else {
                if (mActiveViews.size() > 1) {
                    mCurLayer.rootLayoutForExtra.setTranslationY(mActiveViews.get(1).destRect.top - mMarginHeight);
                } else {
                    mCurLayer.rootLayoutForExtra.setTranslationY(mActiveViews.get(0).destRect.top - mMarginHeight);
                }
            }
            mListener.drawExtra(canvas);
            Logger.e(TAG, "当前页是广告");
        } else if (mNextLayer.isExtraAfterChapter || mNextLayer.isExtraAfterBook) {
            Logger.e(TAG, "下一页是广告，广告位置");
            mCurLayer.rootLayoutForExtra.setTranslationX(0);
            if (mActiveViews.size() > 1) {
                if (getDirection() == PageSwitcher.Direction.DOWN) {
                    mNextLayer.rootLayoutForExtra.setTranslationY(mActiveViews.get(1).destRect.top - mMarginHeight);
                } else {
                    mNextLayer.rootLayoutForExtra.setTranslationY(mActiveViews.get(0).destRect.top - mMarginHeight);
                }
            } else {
                mNextLayer.rootLayoutForExtra.setTranslationY(mActiveViews.get(0).destRect.top - mMarginHeight);
            }
            mListener.drawExtra(canvas);
        }
        Logger.e(TAG + "#point", " mNextLayer.isExtraAfterChapter = " + mNextLayer.isExtraAfterChapter
                + ", mCurLayer.isExtraAfterChapter = " + mCurLayer.isExtraAfterChapter
                + ", mCurLayer.isExtraChapterEnd = " + mCurLayer.isExtraChapterEnd
                + ", mNextLayer.isExtraChapterEnd = " + mNextLayer.isExtraChapterEnd);
        canvas.restore();
    }

    @Override
    public synchronized void startAnim() {
        isRunning = true;
        isMove = true;
        mScroller.fling(0, (int) mTouchY, 0, (int) mVelocity.getYVelocity() / 2
                , 0, 0, Integer.MAX_VALUE * -1, Integer.MAX_VALUE);
    }

    @Override
    public void scrollAnim() {
        if (mScroller.computeScrollOffset()) {
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            setTouchPoint(x, y);
            if (mScroller.getFinalX() == x && mScroller.getFinalY() == y) {
                isRunning = false;
                isMove = false;
                mTouchY = mLastY;

                mScroller.abortAnimation();
                return;
            }
            mView.postInvalidate();
        }
    }

    @Override
    public void abortAnim() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
            isRunning = false;
            isMove = false;
        }
    }

    @Override
    public Layer getBgBitmap() {
        return mCurLayer;
    }

    @Override
    public Layer getNextBitmap() {
        return mNextLayer;
    }

    @Override
    public Layer getTopBitmap() {
        return mTopLayer;
    }

    public int getMoveOffset() {
        if (mActiveViews.size() == 0) {
            return 0;
        }
        BitmapView tmpView = mActiveViews.get(0);
        Logger.e("#point", "第一个view : " + tmpView.top);
        if (mActiveViews.size() > 1) {
            tmpView = mActiveViews.get(1);
            Logger.e("#point", "第二个view : " + tmpView.top);
        }
        if (mCurLayer.isExtraChapterEnd) {
            if (mDirection == Direction.DOWN) {
                return mActiveViews.get(0).destRect.top;
            } else {
                if (mActiveViews.size() > 1) {
                    return mActiveViews.get(1).destRect.top;
                } else {
                    return mActiveViews.get(0).destRect.top;
                }
            }
        } else if (mNextLayer.isExtraChapterEnd) {
            if (mDirection == Direction.DOWN) {
                if (mActiveViews.size() > 1) {
                    return mActiveViews.get(1).destRect.top;
                } else {
                    return mActiveViews.get(0).destRect.top;
                }
            } else {
                return mActiveViews.get(0).destRect.top;
            }
        } else {
            return mActiveViews.get(0).destRect.top;
        }
    }

    /**
     * 判断是否使用当前页的页码来显示
     * @return
     */
    public boolean isCurPosition() {
        if (mActiveViews == null || mActiveViews.isEmpty()) {
            return true;
        }
        if (mDirection == Direction.DOWN) {
            if (mActiveViews.size() > 1) {
                BitmapView firstView = mActiveViews.get(0);
                BitmapView secondView = mActiveViews.get(1);
                Logger.e("#point", "firstView.top = " + (firstView.top / 2) + ", secondView.top = " + (secondView.top / 2)+ ", mViewHeight / 2 = " + mViewHeight / 2);
                if (firstView.top <= mViewHeight / 2 && secondView.top <= mViewHeight / 2) {
                    return true;
                } else {
                    return false;
                }
            } else {
                BitmapView firstView = mActiveViews.get(0);
                Logger.e("#point", "firstView.top = " + (firstView.top / 2) + ", mViewHeight / 2 = " + mViewHeight / 2);
                if (firstView.top <= mViewHeight / 2) {
                    return false;
                } else {
                    return false;
                }
            }
        } else if (mDirection == Direction.UP) {
            if (mActiveViews.size() > 1) {
                BitmapView firstView = mActiveViews.get(0);
                BitmapView secondView = mActiveViews.get(1);
                Logger.e("#point", "firstView.top = " + (firstView.top / 2) + ", secondView.top = " + (secondView.top  / 2) + ", mViewHeight / 2 = " + mViewHeight / 2);
                if (firstView.bottom >= mViewHeight / 2 && secondView.bottom >= mViewHeight / 2) {
                    return true;
                } else {
                    return false;
                }
            } else {
                BitmapView firstView = mActiveViews.get(0);
                Logger.e("#point", "firstView.top = " + (firstView.top / 2) + ", mViewHeight / 2 = " + mViewHeight / 2);
                if (firstView.bottom >= mViewHeight / 2) {
                    return false;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    public boolean isCanStart() {
        return canStart;
    }

    private static class BitmapView {
        Bitmap bitmap;
        Rect srcRect;
        Rect destRect;
        int top;
        int bottom;
    }
}
