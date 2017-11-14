package com.pjtech.android.ridesocial.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.pjtech.android.ridesocial.GlobalData;
import com.pjtech.android.ridesocial.R;
import com.pjtech.android.ridesocial.model.PhotoUploadResponse;
import com.pjtech.android.ridesocial.model.RequestType;
import com.pjtech.android.ridesocial.model.UserSignupResponse;
import com.pjtech.android.ridesocial.rest.ApiClient;
import com.pjtech.android.ridesocial.rest.ApiInterface;
import com.pjtech.android.ridesocial.utils.PreferenceUtils;
import com.pjtech.android.ridesocial.utils.Tool;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class ProfileManagerActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener {

    Context mContext    = null;
    CircleImageView mProfileImage     = null;
    Button mNext    = null;
    TextView mSkip  = null;

    Bitmap photoBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_manager);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mContext = this;

        mProfileImage   = (CircleImageView)this.findViewById(R.id.profileImage);
        mNext   = (Button)this.findViewById(R.id.btn_next);
        mSkip   = (TextView)this.findViewById(R.id.btn_skip);

        if (GlobalData.userInfo.photoUrl != null && !GlobalData.userInfo.photoUrl.equals(""))
        {
            Picasso.with(this).load(ApiClient.BASE_URL + GlobalData.userInfo.photoUrl)
                    .placeholder(R.drawable.profile).fit().centerCrop().into(mProfileImage);
        }

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (photoBitmap != null)
                {
                    dlg_progress.show();
                    String filename = mContext.getFilesDir().getAbsolutePath() + "/profile.png";
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(filename);
                        photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                        // PNG is a lossless format, the compression factor (100) is ignored
                    } catch (Exception e) {
                        e.printStackTrace();
                        dlg_progress.dismiss();
                    } finally {
                        try {
                            if (out != null) {
                                out.close();
                                uploadProfileImage(filename);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            dlg_progress.dismiss();
                        }

                    }
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setClass(ProfileManagerActivity.this, PaymentSetupActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        mSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ProfileManagerActivity.this, PaymentSetupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mProfileImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, v);
                popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(ProfileManagerActivity.this);
                popup.show();
            }
        });

    }

    private void uploadProfileImage(String filename) {
        String username = PreferenceUtils.getStringValue(mContext, PreferenceUtils.KEY_USERNAME, "");
        String cellNum = PreferenceUtils.getStringValue(mContext, PreferenceUtils.KEY_CALLNUM, "");

        File file = new File(filename);

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/png"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("photo", file.getName(), reqFile);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<PhotoUploadResponse> call = apiService.uploadPhoto(username, cellNum, body);

        call.enqueue(new Callback<PhotoUploadResponse>() {
            @Override
            public void onResponse(Call<PhotoUploadResponse> call, Response<PhotoUploadResponse> response) {
                dlg_progress.dismiss();
                if (!response.isSuccessful()) return;

                if (response.body().status.equals("0")) {
                    Intent intent = new Intent();
                    intent.setClass(ProfileManagerActivity.this, PaymentSetupActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if (response.body().status.equals("1")) {
                    Toast.makeText(mContext, mContext.getText(R.string.invalid_user), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PhotoUploadResponse> call, Throwable t) {
                // Log error here since request failed
                dlg_progress.dismiss();
                Toast.makeText(mContext, mContext.getText(R.string.connect_err), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isCameraPermissionGranted(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, RequestType.MY_PERMISSIONS_REQUEST_CAMERA);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.pick_image) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, RequestType.PICK_IMAGE_REQUEST);
        }
        else if (item.getItemId() == R.id.pick_camera) {
            if (isCameraPermissionGranted()){
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, RequestType.TAKE_PHOTO_REQUEST);
                }
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestType.MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, RequestType.TAKE_PHOTO_REQUEST);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try{
                if (requestCode == RequestType.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                    /*** load image from gallery ***/
                    Uri uri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        // Log.d(TAG, String.valueOf(bitmap));

                        updatePhoto(bitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if (requestCode == RequestType.TAKE_PHOTO_REQUEST) {
                    /*** take picture by using device camera ***/
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    updatePhoto(bitmap);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void updatePhoto(Bitmap bitmap){
        photoBitmap = Tool.scaleBitmap(bitmap);
        mProfileImage.setImageBitmap(bitmap);
    }
}
