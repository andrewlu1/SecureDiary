package cn.andrewlu.app.securediary;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.amap.api.location.AMapLocalWeatherLive;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtilsEx;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.andrewlu.app.securediary.utils.LocationUtil;
import cn.andrewlu.app.securediary.utils.PictureHelper;

/**
 * Created by andrewlu on 2015/12/2.
 */
public class NewDiaryActivity extends PictureSelectActivity {
    private final static DateFormat df = new SimpleDateFormat("yyyy年M月d日    ");

    @ViewInject(R.id.diaryInput)
    private RichEditText richEditText;

    @ViewInject(R.id.fab)
    private FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_diary);
        LocationUtil.getInstance().setOnWeatherUpdateListener(
                new LocationUtil.OnWeatherUpdateListener() {
                    @Override
                    public void onWeatherUpdated(AMapLocalWeatherLive weatherLive) {
                        if (weatherLive != null) {
                            String subTitle = df.format(new Date()) + weatherLive.getWeather();
                            mToolbar.setSubtitle(subTitle);
                        }
                    }
                });
        //插入图片.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectDialog();
            }
        });
        setOnSelectPicListener(new PictureHelper.OnSelectPicListener() {
            @Override
            public void onSelectPicture(String picUri, boolean finished) {
                if (finished) {
                    richEditText.appendImage(picUri);
                }
            }
        });
    }

    @OnClick({R.id.saveBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveBtn: {
                String text = richEditText.getText();
                Diary diary = new Diary();
                diary.setCreateAt(new Date());
                AMapLocalWeatherLive weatherLive = LocationUtil.getInstance().getWeatherLive();
                if (weatherLive != null) {
                    diary.setWeather(weatherLive.getWeather());
                }
                diary.setTexts(text);
                diary.setClearText(richEditText.getClearText());
                DbUtilsEx dbUtilsEx = DbUtilsEx.create(this);
                dbUtilsEx.save(diary);
                finish();
                break;
            }
        }
    }

    @Override
    public int getToolbarId() {
        return R.id.toolbar;
    }
}
