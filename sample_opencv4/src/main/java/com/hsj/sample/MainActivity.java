package com.hsj.sample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.core.Mat;

import java.io.File;

/**
 * @Author:hsj
 * @Date:2020-07-21
 * @Class:MainActivity
 * @Desc:
 */
public class MainActivity extends AppCompatActivity {

    private ImageView iv;
    private TextView tv_search;
    private Button btn_convert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.iv);
        tv_search = findViewById(R.id.tv_search);
        btn_convert = findViewById(R.id.btn_convert);
        tv_search.setOnClickListener(v -> selectYUV());
        btn_convert.setOnClickListener(v -> convertYUV());
    }

    private void selectYUV() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        File dir = Environment.getExternalStorageDirectory();
        Uri uri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(this, getPackageName() + ".FileProvider", dir);
        } else {
            uri = Uri.fromFile(dir);
        }
        intent.setDataAndType(uri, "file/*");
        startActivityForResult(intent, 0);
    }

    private void convertYUV() {
        String path = tv_search.getText().toString().trim();
        if (TextUtils.isEmpty(path)) return;
        File file = new File(path);
        if (!file.exists() && file.isDirectory()) {
            Toast.makeText(this, "Please check file", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO set yuv file param
        Mat mat = new Mat();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            String path = getPathFromURI(data.getData());
            if (TextUtils.isEmpty(path)) {
                tv_search.setText("");
                btn_convert.setEnabled(false);
            } else {
                tv_search.setText(path);
                btn_convert.setEnabled(true);
            }
        }
    }

    private String getPathFromURI(Uri uri) {
        if (uri == null) return null;
        final String scheme = uri.getScheme();
        if (scheme == null) return uri.getPath();
        if (ContentResolver.SCHEME_FILE.equals(scheme)) return uri.getPath();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA},
                    null, null, null);
            if (cursor == null) return null;
            String path = null;
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                if (index > -1) {
                    path = cursor.getString(index);
                }
            }
            cursor.close();
            return path;
        }
        return null;
    }

}
