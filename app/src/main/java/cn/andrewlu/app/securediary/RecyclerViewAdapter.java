package cn.andrewlu.app.securediary;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

/**
 * Created by andrewlu on 2015/12/2.
 */
public abstract class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements View.OnClickListener {
    private LayoutInflater mInflater = null;

    @Override
    public final RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        View v = getItemView(mInflater, parent, viewType);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    //重写父类此函数,注入onBindData();
    public final void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        holder.onBindData(getItem(position));
        onBindViewHolder(holder, position);
    }

    //获取pos位置的数据对象.
    public abstract Object getItem(int position);

    //获取待显示的ViewItem的布局
    public abstract View getItemView(LayoutInflater inflater, ViewGroup root, int viewType);

    @Override
    public final void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClicked(v, v.getTag());
        }
    }

    //ViewHolder.
    public final class ViewHolder extends RecyclerView.ViewHolder {
        private Object data;

        public ViewHolder(View itemView) {
            super(itemView);
            //itemView.setOnClickListener(RecyclerViewAdapter.this);
        }

        //在数据绑定的部分,
        private final void onBindData(Object o) {
            itemView.setTag(o);
            data = o;
        }

        public View findViewById(int rId) {
            return itemView.findViewById(rId);
        }

        //设置文本框内容.
        public void setText(int rId, String text) {
            TextView t = (TextView) itemView.findViewById(rId);
            if (t != null) {
                t.setText(text);
            }
        }

        //用户调用 setImage来设置图片控件.
        // 设置图片的过程交给用户来处理.这里不作处理.因为加载图片可能是URL.也可能是本地文件.交给用户更加灵活.
        public void setImage(int rId, ImageCallback callback) {
            if (callback != null) {
                callback.onSetImage((ImageView) itemView.findViewById(rId));
            }
        }

        //设置View的状态.
        public void setEnable(int rId, boolean enabled) {
            View view = itemView.findViewById(rId);
            if (view != null) {
                view.setEnabled(enabled);
            }
        }

        public void setChecked(int rId, boolean checked) {
            CompoundButton button = (CompoundButton) itemView.findViewById(rId);
            if (button != null) {
                button.setChecked(checked);
            }
        }

        public void setClickable(int rId, boolean clickable) {
            View view = (rId == itemView.getId() ? itemView : itemView.findViewById(rId));
            if (view != null) {
                if (clickable) {
                    view.setTag(data);
                    view.setOnClickListener(RecyclerViewAdapter.this);
                } else {
                    view.setTag(null);
                    view.setOnClickListener(null);
                }
            }
        }
    }

    private OnItemClickListener onItemClickListener;

    public final void setOnItemClickListener(OnItemClickListener l) {
        this.onItemClickListener = l;
    }

    public interface OnItemClickListener {
        //这个v可能是整个子view.也可能是view中的某个控件.如按钮.
        void onItemClicked(View v, Object data);
    }

    //用于设置图片控件时的回调接口.
    public interface ImageCallback {
        void onSetImage(ImageView view);
    }

}
