package com.free.tools.androidr.storage;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by Zh.m on 2021/6/17
 * Describe:
 */
class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 查找文件文件路径
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void findFile() {

//        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        Uri extnerl = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Downloads.DISPLAY_NAME + "=?";
        String[] args = new String[]{"first_bug.text"};
        String[] projections = new String[]{MediaStore.Downloads._ID};

        Cursor cursor = getContentResolver().query(extnerl, projections, selection, args, null);

        if (cursor.moveToFirst()) {
            Uri queryUir = ContentUris.withAppendedId(extnerl, cursor.getLong(0));
            Toast.makeText(MainActivity.this, "查询success" + queryUir, Toast.LENGTH_LONG).show();
            cursor.close();

            ContentResolver contentResolver = getContentResolver();

            InputStream inputStream = null;
            try {
                inputStream = contentResolver.openInputStream(queryUir);

                byte[] buffer = new byte[1024];
                int len = 0;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((len = inputStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                bos.close();

                String str = new String(bos.toByteArray());
                //tv_find_file.setText(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void insertOwnerFile() {
        //在自身目录下创建apk文件夹
        File apkFile = getExternalFilesDir("apk");

        String apkFilePath = getExternalFilesDir("apk").getAbsolutePath();
        File newFile = new File(apkFilePath + File.separator + "temp.text");
        OutputStream os = null;
        try {
            os = new FileOutputStream(newFile);
            if (os != null) {
                os.write("我是插入的数据".getBytes(StandardCharsets.UTF_8));
                os.flush();
            }
            Toast.makeText(MainActivity.this, "創建陳工" + newFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e1) {

            }
        }


    }

    private void searchImage() {
        Uri extnerl = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Images.Media.DISPLAY_NAME + "=?";
        String[] args = new String[]{"5555555555555.jpg"};
//        String[] projections = new String[]{MediaStore.Images.Media._ID};
        String[] projections = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
        };



        Cursor cursor = getContentResolver().query(extnerl, projections, selection, args, null);
        if (cursor.moveToFirst()) {
            Uri queryUir = ContentUris.withAppendedId(extnerl, cursor.getLong(0));
            Toast.makeText(MainActivity.this, "查询success" + queryUir, Toast.LENGTH_LONG).show();
            cursor.close();
            // 给图片设置bitmap
            try {
                ParcelFileDescriptor fd = getContentResolver().openFileDescriptor(queryUir, "r");
                if (fd != null) {
                    Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());
                    fd.close();
                    //iv_pic.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void insertFile() {
        ContentResolver contentResolver = getContentResolver();
//      Uri uri = MediaStore.Files.getContentUri("external");
        Uri uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        ContentValues values = new ContentValues();
        String path = Environment.DIRECTORY_DOWNLOADS + "/bug";
        values.put(MediaStore.Downloads.RELATIVE_PATH, path);
        values.put(MediaStore.Downloads.DISPLAY_NAME, "first_bug.text");
        values.put(MediaStore.Downloads.TITLE, path);
        Uri resultUri = contentResolver.insert(uri, values);

        if (resultUri != null) {
            Toast.makeText(MainActivity.this, "创建成功", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "创建失败", Toast.LENGTH_LONG).show();
        }
        try {
            OutputStream outputStream = contentResolver.openOutputStream(resultUri);
            //将字符串转成字节
            byte[] contentInBytes = "我是插入文件的内容".toString().getBytes();
            outputStream.write(contentInBytes);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(MainActivity.this, "插入成功", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        File file = new File(path);
//        if (file.exists()) {
//            if (file.isDirectory()) {
//                Toast.makeText(MainActivity.this, "写入陈宫", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(MainActivity.this, "写入陈宫", Toast.LENGTH_LONG).show();
//            }
//        } else {
//            Toast.makeText(MainActivity.this, "写入成功", Toast.LENGTH_LONG).show();
//        }
    }

    private void insertImage() {
        String disPlayName = "5555555555555.jpg";
        ContentResolver contentResolver1 = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, disPlayName);
        contentValues.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.ImageColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/wxq/");
        Uri insert = contentResolver1.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (insert != null) {
            Bitmap bitmap = getBitmap(this, R.mipmap.ic_launcher);
            try {
                OutputStream outputStream = contentResolver1.openOutputStream(insert);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
                Toast.makeText(MainActivity.this, "插入图片成功", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static Bitmap getBitmap(Context context, int vectorDrawableId) {
        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
        }
        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission(MainActivity mainActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainActivity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        File file = new File(getFilesDir().getPath() + File.separator, "AAAAAAAAAAA.txt");
        File file2 = new File(Environment.getExternalStorageState() + File.separator, "ccccccc.txt");
        try {
            file.createNewFile();
//            file2.createNewFile();
            Toast.makeText(this, "创建成功", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "创建失败", Toast.LENGTH_LONG).show();
        }
    }


    private static void saveFile(Context context, Uri insertUri) {
        if (insertUri == null) {
            return;
        }
        String mFilePath = insertUri.toString();
        InputStream is = null;
        OutputStream os = null;
        try {
            os = context.getContentResolver().openOutputStream(insertUri);
            if (os == null) {
                return;
            }
            int read;
            File sourceFile = new File("");
            if (sourceFile.exists()) { // 文件存在时
                is = new FileInputStream(sourceFile); // 读入原文件
                byte[] buffer = new byte[1024];
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // 下载图片到picture 目录


    public void downLoadImage(final String fileUrl, final String fileName) {

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void run() {
                // TODO Auto-generated method stub
                URL url = null;
                try {
                    url = new URL(fileUrl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                    final Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                    if (uri != null) {
                        OutputStream outputStream = getContentResolver().openOutputStream(uri);
                        if (outputStream != null) {
                            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                            if (copyFileWithStream(bos, inputStream)) { //成功下载图片
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            ParcelFileDescriptor fd = getContentResolver().openFileDescriptor(uri, "r");
                                            if (fd != null) {
                                                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());
                                                fd.close();
                                                //iv_pic.setImageBitmap(bitmap);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    }
                    bis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static boolean copyFileWithStream(OutputStream os, InputStream is) {
        if (os == null || is == null) {
            return false;
        }
        int read = 0;
        while (true) {
            try {
                byte[] buffer = new byte[1444];
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                    os.flush();
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    os.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
