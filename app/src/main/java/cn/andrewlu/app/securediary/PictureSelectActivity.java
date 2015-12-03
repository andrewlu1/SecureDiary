package cn.andrewlu.app.securediary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import cn.andrewlu.app.securediary.utils.PictureHelper;

/**
 * Created by andrewlu on 2015/11/18.
 */
public class PictureSelectActivity extends BaseActivity {
    private PictureHelper mPictureHelper;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mPictureHelper = new PictureHelper(this);
    }

    public final PictureHelper getPictureHelper() {
        return mPictureHelper;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPictureHelper.onActivityResult(requestCode, resultCode, data);
    }

    public final void setOnSelectPicListener(PictureHelper.OnSelectPicListener l) {
        mPictureHelper.setOnSelectPicListener(l);
    }

    public void openSelectDialog() {
        //弹出选项.是调用相机还是相册.
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("选择图片来源")
                .setSingleChoiceItems(new String[]{"拍照", "从相册选择"}, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0: {
                                mPictureHelper.selectFrom(PictureHelper.FROM_CAMERA);
                                break;
                            }
                            case 1: {
                                mPictureHelper.selectFrom(PictureHelper.FROM_FILE);
                                break;
                            }
                        }
                        dialogInterface.dismiss();
                    }
                }).create();
        dialog.show();
    }
}
