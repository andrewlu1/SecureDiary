package cn.andrewlu.app.securediary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;


import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by andrewlu on 2015/11/10.
 */
public class BaseActivity extends AppCompatActivity {
    protected Toolbar mToolbar = null;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    //setContentView 重定义.
    public void setContentView(int resID) {
        super.setContentView(resID);
        onViewCreated();
    }

    public void setContentView(View view) {
        super.setContentView(view);
        onViewCreated();
    }

    public void setContentView(View view, ViewGroup.LayoutParams p) {
        super.setContentView(view, p);
        onViewCreated();
    }

    private void onViewCreated() {
        if (getToolbarId() > 0) {
            mToolbar = (Toolbar) findViewById(getToolbarId());
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
                mToolbar.setTitleTextColor(Color.WHITE);
                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onNavigationClicked();
                    }
                });
            }
        }
        ViewUtils.inject(this);
    }

    //需要重写.如果没有toolbar.就不需要重写.
    public int getToolbarId() {
        //return -1;
        return R.id.toolbar;
    }

    //返回键被按下的处理.
    public void onNavigationClicked() {
        finish();
    }


    public boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    //可以在非UI线程中使用.
    public void showToast(final String msg) {
        if (isMainThread()) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void showToast(final int stringRes) {
        if (isMainThread()) {
            Toast.makeText(this, stringRes, Toast.LENGTH_SHORT).show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BaseActivity.this, stringRes, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //获取跳转用的Intent.使用此intent将清空前面的activity.
    public Intent newIntentWithClearTop(Class<? extends Activity> target) {
        Intent i = new Intent(this, target);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }

    public Intent newIntent(Class<? extends Activity> target) {
        return new Intent(this, target);
    }

    public void startActivity(Class<? extends Activity> target) {
        Intent i = newIntent(target);
        startActivity(i);
    }

    public void startActivityClearTop(Class<? extends Activity> target) {
        Intent i = newIntentWithClearTop(target);
        startActivity(i);
    }


    public void onBackPressed() {
        finish();
    }

    private long lastBackPressedTime = 0;

    //需要连续两次调用此函数.一般在返回键事件中调用.
    public void stopApp() {
        if (System.currentTimeMillis() - lastBackPressedTime < 1000) {
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            lastBackPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
        }
    }
}
