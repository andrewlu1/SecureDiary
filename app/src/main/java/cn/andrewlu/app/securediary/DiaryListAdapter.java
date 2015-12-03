package cn.andrewlu.app.securediary;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by andrewlu on 2015/12/2.
 */
public class DiaryListAdapter extends RecyclerViewAdapter {
    private List<Diary> mData;
    private final static DateFormat df = new SimpleDateFormat("yyyy年M月d日");
    private final static int colors[] = {0xffB03060, 0xffFE9A76,
            0xffFF1493,0xffA0A0A0,0xffB413EC,
            0xff008080, 0xff0E6EB8, 0xffEE82EE};
    private final Random mRandom = new Random();

    public DiaryListAdapter(List<Diary> diaryList) {
        mData = diaryList;
        if (mData == null) {
            mData = new ArrayList<Diary>(0);
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Diary diary = (Diary) getItem(position);
        holder.setText(R.id.date, df.format(diary.getCreateAt()));
        holder.setText(R.id.weather, diary.getWeather());
        holder.setText(R.id.diary, diary.getClearText());
        holder.setClickable(R.id.rootItem, true);//设置某一子控件可被点击.
        CardView cardView = (CardView) holder.itemView;
        cardView.setCardBackgroundColor(colors[mRandom.nextInt(colors.length)]);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public View getItemView(LayoutInflater inflater, ViewGroup root, int viewType) {
        return inflater.inflate(R.layout.item_diary_list, root, false);
    }
}
