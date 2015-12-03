package cn.andrewlu.app.securediary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andrewlu on 2015/10/31.
 *
 * @description 一个图案解锁控件.通过手指在控件上划动生成一个图案密码.
 */
public class PatternLockView extends SurfaceView implements SurfaceHolder.Callback {
    WorkThread workThread;
    //由于最多只有9个点,因此只需要声明9个空间的列表即可.
    private final List<Integer> mPoints = new ArrayList<Integer>(9);

    public PatternLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PatternLockView(Context context) {
        super(context);
        init();
    }

    public PatternLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //自定义初始化函数.
    private void init() {
        //规定控件的最小尺寸为(100,100),否则以wrap_content显示在界面上时,最小尺寸为0.
        setMinimumHeight(100);
        setMinimumWidth(100);

        //设置背景透明要增加额外设置.
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        //通过设置这个监听器,可以监听到控件初始化完成或销毁的事件.进而控制绘图周期.
        getHolder().addCallback(this);//有了这一句,下面的几个重写函数才回被调用.
    }


    private PointF mCurrentFingerPoint = null;
    boolean isCorrect = true;

    //监听手指触摸事件.
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            //手指按下位置.
            case MotionEvent.ACTION_DOWN: {
                mPoints.clear();//抬起手指时清除点的坐标.或者按下时清除也可以.
                //记录按下位置.
                mCurrentFingerPoint = new PointF(e.getX(), e.getY());

                //按下时,清除上一次对错结果.
                isCorrect = true;

                break;
            }
            //手指在屏幕上移动.
            case MotionEvent.ACTION_MOVE: {
                //记录移动位置.
                mCurrentFingerPoint.set(e.getX(), e.getY());
                break;
            }
            //手指在屏幕上抬起.表示此次解锁操作结束.
            case MotionEvent.ACTION_UP: {
                //抬起手指时,清除当前位置.
                mCurrentFingerPoint = null;
                //手指抬起,传递相应的数据出去,适合使用监听器实现.
                if (mUnlockListener != null) {
                    //密码该是什么呢?
                    String password = mPoints.toString();//直接将数组的值转换为字符串传递出去.
                    //接收外界传过来的对错结果.
                    isCorrect = mUnlockListener.onUnlock(password);
                    //当密码正确时,立即清除mPoints,不再让界面画线了.否则就计时2s后再清除.
                    if(isCorrect){
                        mPoints.clear();
                    }
                }

                break;
            }
        }
        return true;
    }

    //手指抬起的监听器.
    public interface OnUnlockListener {
        //监听器传入手指划动后形成的图形密码值.监听器处理后告诉控件,密码是否是确.由此来显示红色图案.
        public boolean onUnlock(String password);
    }

    private OnUnlockListener mUnlockListener = null;

    public void setOnUnlockListener(OnUnlockListener l) {
        mUnlockListener = l;
    }

    //SurfaceView的绘图界面是要经过一定时间的准备的,因此是异步初始化完成的.要开始绘图必须等控件初始化完成.
    //此函数标识控件可以开始绘图了.
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //在里边可以绘图了.一般是创建一个单独的绘图线程,在线程里绘图.
        workThread = new WorkThread();
        workThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //控件尺寸变化时会被调用.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //控件被回收时会调用.一般是销毁绘图的工作线程.
        if (workThread != null) {
            workThread.setStop();//想要停止线程,不能再使用stop函数了.应该自己实现销毁过程.
            workThread = null;
        }

    }


    private class WorkThread extends Thread {
        private boolean runFlag = true;

        //由于每次绘制都要用到paint,因此做为属性更合适,减少重复创建paint的内存开销.
        //参数:Paint.ANIT_ALIAS_FLAG 用于绘制更精细的图形,减少毛边感觉.
        private Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //用来画连线的笔.
        private Paint mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //用map来记录下9个点的固定坐标归一值.
        private final Map<Point, Integer> mCirclePointMap = new HashMap<Point, Integer>(9);

        //用来反向存储9个圆心的坐标值.以便需要时能够直接查找到某点实际坐标.
        private final Map<Integer, PointF> mCirclePointMap2 = new HashMap<Integer, PointF>(9);
        private final int COLOR_NORMAL = Color.argb(100, 0, 0, 200);
        private final int COLOR_ERROR = Color.argb(100, 200, 0, 0);

        public WorkThread() {
            //设置画笔的绘制效果为描边的空心图形.还有其他几种.FILL:填充. FILL_AND_STROKE:填充并描边.
            mCirclePaint.setStyle(Paint.Style.STROKE);
            mCirclePaint.setStrokeWidth(5);//设置描边的线宽.
            mCirclePaint.setColor(COLOR_NORMAL);//设置图形的颜色为半透明的蓝色调.黑色太丑了.
            //边缘还不够圆滑.设置一个细小的阴影效果.阴影带有渐变效果,会模糊掉尖锐的边缘.看起来就更加圆滑.
            mCirclePaint.setShadowLayer(1, 0, 0, COLOR_NORMAL);

            mFillPaint.setStyle(Paint.Style.FILL);
            mFillPaint.setStrokeWidth(5);//当设置样式为填充时,此值表示线条的宽度.
            mFillPaint.setColor(COLOR_NORMAL);
        }

        public void setStop() {
            runFlag = false;//下一个循环会自动结束线程.进而销毁线程.
        }

        public void run() {
            Canvas canvas = null;
            //得到的是整个View界面的尺寸及位置.经过刚才的测试,这个也是不准确的..留待解决.
            Rect viewBound = getHolder().getSurfaceFrame();
            //根据最小边长,计算出一个正方形.
            int width = viewBound.width();
            if (viewBound.height() < width) {//如果高度小于width,则以高度做为正方形的边长.
                width = viewBound.height();
            }
            int width_2 = width / 2;
            int width_7 = width / 7;
            float width_14 = width / 14.0f;
            //由最小边长即可获得一个正方形区域.
            Rect realBound = new Rect(viewBound.centerX() - width_2, viewBound.centerY() - width_2,
                    viewBound.centerX() + width_2, viewBound.centerY() + width_2);

            //初始化mCirclePointMap.k值表示是第几个圆.
            for (int i = 1, k = 0; i < 6; i += 2) {
                for (int j = 1; j < 6; j += 2) {
                    //思考为什么是p(j,i)而不是(i,j);
                    mCirclePointMap.put(new Point(j, i), k++);
                }
            }
            //初始化mCirclePointMap2.k值表示是第几个圆.
            for (int i = 3, k = 0; i < 14; i += 4) {
                //每列.画9个圆时用到了这个循环.
                for (int j = 3; j < 14; j += 4) {
                    //画圆时用到了这9个坐标值..
                    mCirclePointMap2.put(k++, new PointF(j * width_14 + realBound.left, i * width_14 + realBound.top));
                }
            }

            Point fingerPoint = new Point(0, 0);

            //用path圈出来一个水平方向的三角形路径.坐标点分别为:(0,10),(0,-10),(10,0)
            //思考:如果想要个正三角形,值该如何修改.
            Path trianglePath = new Path();
            trianglePath.moveTo(width_7, 10);//让三角形起始位置偏移一个距离,不再紧靠圆心位置.
            trianglePath.lineTo(width_7, -10);
            trianglePath.lineTo(width_7 + 17.32f, 0);//修改10=>1.732*10 = 17.32.可以得到一个正三角.
            trianglePath.close();

            long lastTimeMills = 0, timeNeedSleep = 0;
            int correctTimer = 0;
            //绘图过程是无限循环,不停重复的.因此要while(true).并不是绘制一次就不再画了.
            while (runFlag) {
                lastTimeMills = System.currentTimeMillis();

                try {
                    //通过在线程中lockCanvas可以获取一个用于绘制界面的画布.
                    canvas = getHolder().lockCanvas();

                    //绘画前先清除上一帧的内容.
                    // 通过在界面上画透明颜色,并设置交插模式为CLEAR.可使界面已有内容消失.类似于橡皮擦.
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                    //绘制过程.
                    {
                        //画个圆圈试试.
                        //canvas.drawCircle(100, 100, 50, mCirclePaint);
                        //画出9个圆所在的实际区域.
                        //canvas.drawRect(realBound, mCirclePaint);
                        //画圆之前判断对错.然后修改画笔颜色.
                        if (isCorrect) {
                            mCirclePaint.setColor(COLOR_NORMAL);
                            mFillPaint.setColor(COLOR_NORMAL);
                            correctTimer =0;
                        } else {
                            mCirclePaint.setColor(COLOR_ERROR);
                            mFillPaint.setColor(COLOR_ERROR);
                            correctTimer++;//当错误时,timer负责计时.
                            if(correctTimer>=30){
                                //计时过了60帧了.即2秒钟了.
                                isCorrect = true;
                                correctTimer = 0;
                                mPoints.clear();//清除连线的坐标记录.
                            }
                        }
                        //用循环的方式画9个圆.
                        //每行.
                        for (int i = 3; i < 14; i += 4) {
                            //每列.
                            for (int j = 3; j < 14; j += 4) {
                                //实际起始位置应该是实际区域realBound的起始位置算起.所以x,y应该分别加上left,top.
                                canvas.drawCircle(j * width_14 + realBound.left, i * width_14 + realBound.top, width_14, mCirclePaint);
                            }
                        }
                        //画出手指当前位置.并用文字将位置坐标显示在左上角.
                        //不需要再显示手指位置的圆了.
//                    if (mCurrentFingerPoint != null) {
//                        canvas.drawCircle(mCurrentFingerPoint.x, mCurrentFingerPoint.y, 5, mCirclePaint);
//                        //由于mCirclePaint是一个空心的画笔,因此不适合写文字.需要再创建一个画笔.
//                        //canvas.drawText(mCurrentFingerPoint.toString(), 10, 20, mFillPaint);
//                    }

                        //判断手指是否在圆内.
                        if (mCurrentFingerPoint != null) {
                            fingerPoint.x = (int) (mCurrentFingerPoint.x - realBound.left) / width_7;
                            fingerPoint.y = (int) (mCurrentFingerPoint.y - realBound.top) / width_7;
                            //在界面上显示x,y.
                            //canvas.drawText(fingerPoint.toString(), 10, 40, mFillPaint);

                            //如果x,y表示的点在map中,则number不为空.可以知道手指在某一个圆中.
                            //每次判断都要声明一个point对象,浪费内存.放入while外边.
                            Integer number = mCirclePointMap.get(fingerPoint);
                            if (number != null) {
                                //说明经过了某个点.需要把这个点记录下来.因此需要声明一个List来记录这些点值.
                                if (mPoints.contains(number)) {
                                    //如果已经包含了此点,说明手指已经经过了这个点,不需要再放进来了.
                                } else {
                                    mPoints.add(number);
                                }
                            }
                        }


                        //把这一过程拿到mCurrentFinger!=null外边.
                        //在所有经过的圆圈的圆心上画一个实心的小圆,表示经过了这个圆.
                        if (mPoints.isEmpty()) {//如何控制连线自动消失.
                        } else {
                            PointF prevPoint = null, currPoint = null;
                            //需要得到每个圆圈的圆心坐标.如何得到.mPoints中存储的是某个点的归一值.
                            // 需要通过建立映射关系来查找实际的圆心坐标.使用Map来存储.
                            for (Integer p : mPoints) {
                                //通过归一值来查找点的实际坐标对象.
                                currPoint = mCirclePointMap2.get(p);
                                canvas.drawCircle(currPoint.x, currPoint.y, 8, mFillPaint);

                                //将每两个点进行连线.需要知道上一个点和当前点.
                                if (prevPoint != null) {//如果上一个点坐标是空的,说明是第一个点.
                                    canvas.drawLine(prevPoint.x, prevPoint.y, currPoint.x, currPoint.y, mFillPaint);

                                    //在画线的位置,相应的画三角形.思考如何使三角形正确的朝向.
                                    //drawTriangle();
                                    {
                                        canvas.save();
                                        //通过旋转画布,使画出的三角形与连线垂直.
                                        //计算需要旋转的角度. atan2 得到的是弧度值.还需要转换为角度值.
                                        canvas.save();
                                        double angle = Math.atan2(currPoint.y - prevPoint.y, currPoint.x - prevPoint.x);
                                        canvas.rotate((float) Math.toDegrees(angle), prevPoint.x, prevPoint.y);
                                        canvas.translate(prevPoint.x, prevPoint.y);
                                        canvas.drawPath(trianglePath, mFillPaint);
                                        canvas.restore();
                                    }
                                }
                                //画完了,当前点就变成上一个点了.
                                prevPoint = currPoint;

                            }

                            //应该在for循环外边画这最后一条线.
                            //绘制最后一个经过的圆心到手指间的连线.
                            if(mCurrentFingerPoint!= null) {
                                canvas.drawLine(currPoint.x, currPoint.y, mCurrentFingerPoint.x, mCurrentFingerPoint.y, mFillPaint);
                            }
                        }
                    }

                    //计算需要休眠多久.
                    timeNeedSleep = 33 - (System.currentTimeMillis() - lastTimeMills);
                    Thread.sleep(timeNeedSleep);

                } catch (Exception e) {
                } finally {//把最后这几句放到finally中去,防止因程序隐藏的bug异常导致canvas没有解锁.而造成绘图崩溃.
                    //绘制完成,释放canvas.
                    if (canvas != null) {
                        getHolder().unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}
