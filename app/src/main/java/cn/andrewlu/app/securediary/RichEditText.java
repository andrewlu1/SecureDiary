package cn.andrewlu.app.securediary;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.lidroid.xutils.BitmapUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by andrewlu on 2015/11/26.
 * 可以插入图片的输入框组件。
 * 图片需要在正确的位置，同时可以保存网络。
 */

public class RichEditText extends LinearLayout {
    private List<EditText> editTexts = new LinkedList<EditText>();
    private static BitmapUtils bitmapUtils = null;
    private final ColorStateList mColorStateList = new
            ColorStateList(
            new int[][]{
                    new int[]{android.R.attr.state_enabled},
                    new int[]{}},
            new int[]{0xff222222, 0xff333355});
    private Typeface mTypeFace = null;

    public RichEditText(Context context) {
        super(context);
        init();
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (getChildCount() > 0) {
            throw new RuntimeException("RichEditText cannot have any child view.");
        }
        if (bitmapUtils == null) {
            bitmapUtils = new BitmapUtils(getContext());
        }
        try {
            AssetManager manager = getContext().getAssets();
            mTypeFace = Typeface.createFromAsset(manager, "font_rui.ttf");
        } catch (Exception e) {
        }
        appendEditText(null);
        setOrientation(VERTICAL);
    }

    //设置字体大小。
    public void setTextSize(float size) {
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v instanceof EditText) {
                ((EditText) v).setTextSize(size);
            }
        }
    }

    //在屁股末尾插入文本。
    private void appendEditText(CharSequence text) {
        //如果屁股上已有EditText,就直接向里边插入文本。
        if (getChildCount() > 0) {
            View v = getChildAt(getChildCount() - 1);
            if (v instanceof EditText) {
                ((EditText) v).setText(new SpannableString(text));
                if (mEditable) {
                    v.setBackgroundColor(Color.argb(20, 0xa0, 0xa0, 0xa0));
                } else {
                    v.setBackgroundColor(Color.TRANSPARENT);
                }
                return;
            }
        }
        EditText t = new EditText(getContext());
        //t.setBackgroundColor(Color.argb(20, 0xa0, 0xa0, 0xa0));
        t.setTextSize(16);
        LayoutParams p = new LayoutParams(-1, -2);
        p.leftMargin = p.topMargin = p.rightMargin = 10;
        p.gravity = Gravity.LEFT | Gravity.TOP;
        addView(t, p);
        if (text != null) {
            t.setText(new SpannableString(text));
        }
        t.setTextColor(mColorStateList);
        if (mTypeFace != null) {
            t.setTypeface(mTypeFace);
        }
        if (mEditable) {
            t.requestFocus();
            t.setMovementMethod(LinkMovementMethod.getInstance());
            t.setBackgroundColor(Color.argb(20, 0xa0, 0xa0, 0xa0));
        } else {
            t.setBackgroundColor(Color.TRANSPARENT);
        }
        t.setEnabled(mEditable);
    }

    //在适当的位置插入一张图片。图片用的是本地路径或网络路径。
    public void appendImage(String imgFilePath) {
        View view = getFocusedChild();
        CharSequence subStr = "";
        int childIndex = -1;
        //如果当前焦点在中间某一个EditText上.就需要拆分开来.
        if (view != null && view instanceof EditText) {
            EditText text = (EditText) view;
            int index = text.getSelectionStart();
            if (index > 0) {
                Editable str = text.getText();
                if (str.length() > 0) {
                    subStr = str.subSequence(index, str.length());
                    text.setText(str.subSequence(0, index));
                }
            }
            childIndex = getChildIndexAt(view);
        }

        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        LayoutParams p = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400);
        p.leftMargin = p.topMargin = p.rightMargin = 10;
        bitmapUtils.display(imageView, imgFilePath);
        if (childIndex > 0) {
            addView(imageView, childIndex + 1, p);
        } else {
            addView(imageView, p);
        }
        imageView.setTag(imgFilePath);
        //查找后边是否有EditText.并把文字补到下边去.
        EditText nextEditText = findNextEditText(childIndex + 1);
        if (nextEditText == null) {
            appendEditText(subStr);
        } else {
            if (subStr != null) {
                Editable e = nextEditText.getText();
                nextEditText.setText(e.insert(0, subStr));
                nextEditText.requestFocus();
            }
        }
    }

    private boolean mEditable = true;

    public boolean getEditable() {
        return mEditable;
    }

    public void setEditable(boolean editable) {
        this.mEditable = editable;
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v instanceof EditText) {
                v.setEnabled(editable);
            }
        }
    }

    public void appendText(String text) {
        appendEditText(text);
    }

    //得到输入的所有内容。
    public String getText() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[");
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View c = getChildAt(i);
            buffer.append("{");
            if (c instanceof EditText) {
                String t = ((EditText) c).getText().toString();
                buffer.append("t:0,c=\"").append(t).append("\"}");
            } else if (c instanceof ImageView) {
                String tag = (String) c.getTag();
                if (tag != null) {
                    buffer.append("t:1,c=\"").append(tag).append("\"}");
                }
            }
            if (i < count - 1) {
                buffer.append(",");
            }
        }
        buffer.append("]");
        return buffer.toString();
    }

    public String getClearText() {
        StringBuilder buffer = new StringBuilder();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View c = getChildAt(i);
            if (c instanceof EditText) {
                String t = ((EditText) c).getText().toString();
                buffer.append(t);
            }
        }
        return buffer.toString();
    }

    //设置格式化的文本内容。
    public void setText(String text) {
        try {
            JSONArray array = new JSONArray(text);
            if (array == null) return;
            int size = array.length();
            for (int i = 0; i < size; i++) {
                JSONObject object = array.getJSONObject(i);
                int type = object.optInt("t");
                String content = object.optString("c", "");
                if (type == 0) {
                    if (content != null && content.length() > 0) {
                        appendEditText(content);
                    }
                } else if (type == 1) {
                    appendImage(content);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            clearText();
            appendEditText(text);
        }
    }

    private void clearText() {
        removeAllViews();
        appendEditText(null);
    }

    private int getChildIndexAt(View child) {
        if (child == null) return -1;
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) == child) return i;
        }
        return -1;
    }

    private EditText findNextEditText(int start) {
        if (start < 0 || start >= getChildCount()) return null;
        //查找下一个是EditText的控件.
        for (int i = start; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof EditText) {
                return (EditText) getChildAt(i);
            }
        }
        return null;
    }
}
