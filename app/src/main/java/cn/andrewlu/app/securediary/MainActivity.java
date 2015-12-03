package cn.andrewlu.app.securediary;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.lidroid.xutils.DbUtilsEx;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity {

    @ViewInject(R.id.diaryListView)
    private RecyclerView diaryListView;

    @ViewInject(R.id.refreshLayout)
    private SwipeRefreshLayout refreshLayout;

    @ViewInject(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(NewDiaryActivity.class);
                //startActivity(LockActivity.class);
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initList();
                refreshLayout.setRefreshing(false);
            }
        });
        initList();
    }

    @Override
    public int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(SettingActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        stopApp();
    }

    private void initList() {
        DbUtilsEx dbUtilsEx = DbUtilsEx.create(this);
        List<Diary> diaries = dbUtilsEx.findAll(Diary.class);

        DiaryListAdapter adapter = new DiaryListAdapter(diaries);
        diaryListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        diaryListView.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View v, Object data) {
                Diary d = (Diary) data;
                //showToast(d.getWeather());
                Intent i = newIntent(DiaryDetailActivity.class);
                i.putExtra("diary", d);
                startActivity(i);
            }
        });
    }
}
