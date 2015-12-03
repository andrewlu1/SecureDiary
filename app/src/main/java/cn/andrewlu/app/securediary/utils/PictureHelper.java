package cn.andrewlu.app.securediary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by andrewlu on 2015/11/11.
 * 可以从相机/相册中选取图片.通过回调函数获取到所选择的图片.
 */
public class PictureHelper {
    private Activity context;
    public static final int FROM_CAMERA = 0x1000;
    public static final int FROM_FILE = 0x1001;
    public static final int FROM_VIDEO = 0x1002;
    private File cameraTargetFile;

    public PictureHelper(Activity context) {
        this.context = context;
        if (context == null) {
            throw new RuntimeException("PictureHelper context must not be null!");
        }
    }

    public void selectFrom(int from) {
        switch (from) {
            case FROM_CAMERA: {
                cameraTargetFile = createImageFile();
                //跳转到拍照界面.
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraTargetFile));
                context.startActivityForResult(intent, FROM_CAMERA);
                break;
            }
            case FROM_FILE: {
                //跳转到相册界面.
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                context.startActivityForResult(intent, FROM_FILE);
                break;
            }
        }
    }

    //必须在activity中调用此方法.否则功能不正常.
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (l == null) return;
        if (resultCode == Activity.RESULT_CANCELED) {
            l.onSelectPicture(null, false);
        } else if (resultCode == Activity.RESULT_OK) {
            String filePath = null;
            switch (requestCode) {
                case FROM_CAMERA: {
                    filePath = cameraTargetFile.getAbsolutePath();
                    break;
                }
                case FROM_FILE: {
                    Uri selectedImage = data.getData();
                    String[] filePathColumns = {MediaStore.Images.Media.DATA};
                    Cursor c = context.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePathColumns[0]);
                    filePath = c.getString(columnIndex);
                    c.close();
                    break;
                }
            }

            l.onSelectPicture(filePath, true);
        }
    }

    private final File createImageFile() {
        //File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File target = new File(dir, Math.random() + ".jpg");
        return target;
    }

    private OnSelectPicListener l = null;

    public void setOnSelectPicListener(OnSelectPicListener l) {
        this.l = l;
    }

    //当选择了图片后执行此功能.
    public interface OnSelectPicListener {
        void onSelectPicture(String picUri, boolean finished);
    }

    public static Bitmap getThumbnail(String filePath, int width, int height) {

        width = width <= 0 ? 200 : width;
        height = height <= 0 ? 200 : height;

        Bitmap bmp = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        if (options.outHeight == 0 || options.outWidth == 0) {
            return null;
        }
        options.inJustDecodeBounds = false;

        //当图片尺寸大于所需要尺寸时才缩小.
        if (options.outWidth > width && options.outHeight > height) {
            if (width < height) {
                options.inSampleSize = options.outWidth / width;
                options.outHeight = options.outHeight / options.inSampleSize;
                options.outWidth = width;
            } else {
                options.inSampleSize = options.outHeight / height;
                options.outWidth = options.outWidth / options.inSampleSize;
                options.outHeight = height;
            }
        }
        bmp = BitmapFactory.decodeFile(filePath, options);
        return bmp;
    }

}
