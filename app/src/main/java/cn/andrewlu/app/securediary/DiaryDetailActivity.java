package cn.andrewlu.app.securediary;

import android.os.Bundle;

import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by andrewlu on 2015/12/2.
 */
public class DiaryDetailActivity extends BaseActivity {
    private final static DateFormat df = new SimpleDateFormat("yyyy年M月d日    ");

    @ViewInject(R.id.diaryInput)
    private RichEditText richEditText;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_diary_detail);
        richEditText.setEditable(false);
        Diary extral = (Diary) getIntent().getSerializableExtra("diary");
        if (extral != null) {
            mToolbar.setSubtitle(df.format(extral.getCreateAt()) + extral.getWeather());
            richEditText.setText(extral.getTexts());
        }
    }

    public int getToolbarId() {
        return R.id.toolbar;
    }
}
