package cn.andrewlu.app.securediary;

import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * Created by andrewlu on 2015/12/3.
 */
public class SettingActivity extends BaseActivity {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_setting);
    }

    @OnClick({R.id.pswdSetting, R.id.syncNetwork})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pswdSetting: {
                startActivity(LockActivity.class);
                break;
            }
            case R.id.syncNetwork: {
                //checkUpdate();
                break;
            }
        }
    }
}
