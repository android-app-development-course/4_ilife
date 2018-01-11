package com.example.administrator.ilife_manager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;


public class infomation extends AppCompatActivity {

    private static final int REQUEST_CHOOSE_IMAGE = 0x01;
    private static final int REQUEST_WRITE_EXTERNAL_PERMISSION_GRANT = 0xff;
    private Button btn_info_save;
    private ImageView photo;
    private EditText et_info_nickname;
    private EditText et_info_email;
    private EditText et_info_phone;
    Bitmap bm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infomation);
        photo=(ImageView)findViewById(R.id.iv_info_photo);
        findViewById(R.id.iv_info_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareToOpenAlbum();
            }
        });
        btn_info_save=(Button)findViewById(R.id.btn_info_save);
        et_info_nickname=(EditText)findViewById(R.id.et_info_nickname);
        et_info_email=(EditText)findViewById(R.id.et_info_email);
        et_info_phone=(EditText)findViewById(R.id.et_info_phone);
        inniting();

        btn_info_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                String nickname=et_info_nickname.getText().toString();
                String email=et_info_email.getText().toString();
                String phone=et_info_phone.getText().toString();
                if (nickname.equals("")||email.equals("")||phone.equals(""))
                {
                    Toast.makeText(view.getContext(),"请输入完整信息",Toast.LENGTH_SHORT).show();
                }
                else {
                    FilesaveInfo.saveUserInfo(view.getContext(), nickname, email, phone);
                    setResult(22, intent);
                    finish();
                }
            }
        });
    }

    private void inniting()
    {
        Map<String,String> userInfo=FilesaveInfo.getUserInfo(this);
        if(userInfo!=null)
        {
            et_info_nickname.setText(userInfo.get("nickname"));
            et_info_email.setText(userInfo.get("email"));
            et_info_phone.setText(userInfo.get("phone"));
        }
        String path= "/sdcard/my_info_photo.png";
        File file = new File(path);
        if(file.exists())
        {
            Bitmap tempbitmap = BitmapFactory.decodeFile(path);
            tempbitmap=dealBitmap(tempbitmap);
            photo.setImageBitmap(tempbitmap);
        }

    }

    private Bitmap dealBitmap(Bitmap bitMap)
    {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        // 设置想要的大小
        int newWidth = 100;
        int newHeight = 100;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        bitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix,
                true);
        return bitMap;
    }

    private void prepareToOpenAlbum() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_PERMISSION_GRANT);
        } else {
            openAlbum();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_EXTERNAL_PERMISSION_GRANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum();
            } else {
                Toast.makeText(infomation.this, "You denied the write_external_storage permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            Uri uri =  data.getData();
            Log.d("Tianma", "Uri = " + uri);
            String path = ImageUtils.getRealPathFromUri(this, uri);
            Log.d("Tianma", "realPath = " + path);
            int requiredHeight = photo.getHeight();
            int requiredWidth = photo.getWidth();
            bm = ImageUtils.decodeSampledBitmapFromDisk(path, requiredWidth, requiredHeight);
            photo.setImageBitmap(bm);
            saveMyBitmap("my_info_photo",bm);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void saveMyBitmap(String bitName,Bitmap mBitmap){
        File f = new File("/sdcard/" + bitName + ".png");
        try {
            f.createNewFile();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
