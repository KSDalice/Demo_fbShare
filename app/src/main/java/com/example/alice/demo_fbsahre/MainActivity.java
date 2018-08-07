package com.example.alice.demo_fbsahre;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;

//import java.lang.annotation.Target;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_VIDEO_CODE = 1000;
    Button btnShareLink, btnSharePhoto, btnShareVideo, btnLogin;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    com.squareup.picasso.Target target = new com.squareup.picasso.Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if (ShareDialog.canShow(SharePhotoContent.class)) {
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        printHashKey();
        //init view
        btnShareLink = (Button) findViewById(R.id.btn_share_link);
        btnShareVideo = (Button) findViewById(R.id.btn_share_Vedio);
        btnSharePhoto = (Button) findViewById(R.id.btn_share_Photo);
        btnLogin = (Button) findViewById(R.id.btn_login) ;

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);


        btnShareLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create callback
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(MainActivity.this, "ShareSuccessful", Toast.LENGTH_SHORT).show();
                        Log.v("log", "成功");
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, "ShareCancel", Toast.LENGTH_SHORT).show();
                        Log.v("log", "取消");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.v("log", "失敗" + error.getMessage());
                    }
                });

                ShareLinkContent linkcontent = new ShareLinkContent.Builder()
                        .setQuote("this is useful content")
                        .setContentUrl(Uri.parse("https://www.youtube.com/"))
                        .build();
                if (shareDialog.canShow(ShareLinkContent.class)) {
                    shareDialog.show(linkcontent);
                }
            }
        });

        btnSharePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create callback
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(MainActivity.this, "ShareSuccessful", Toast.LENGTH_SHORT).show();
                        Log.v("log", "成功");
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, "ShareCancel", Toast.LENGTH_SHORT).show();
                        Log.v("log", "取消");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.v("log", "失敗" + error.getMessage());
                    }
                });
                Picasso.with(getBaseContext())
                        .load("http://www.hotpets.com.tw/wp-content/uploads/2016/10/%E6%94%B9%E5%96%84%E4%B8%8D%E5%8F%97%E6%8E%A7%E5%88%B6%E4%B9%8B%E6%B3%95%E9%AC%A5%E5%B0%8F%E6%95%99%E5%AE%A4-01-675x372.jpg")
                        .into(target);
            }
        });

        btnShareVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Choose Video dialog
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select video"), REQUEST_VIDEO_CODE);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FbLogin.class);
                startActivity(intent);
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_VIDEO_CODE) {
                Uri selectedVideo = data.getData();
                ShareVideo video = new ShareVideo.Builder().setLocalUrl(selectedVideo).build();
                ShareVideoContent videoContent = new ShareVideoContent.Builder()
                        .setContentTitle("This is useful video")
                        .setContentDescription("Funny video from Youtube")
                        .setVideo(video)
                        .build();
                if (shareDialog.canShow(ShareVideoContent.class)) {
                    shareDialog.show(videoContent);
                }
            }
        }
    }

    public boolean isAppInstalled(String packageName) {
        try {
            getApplicationContext().getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }



    // print 電腦的 HashKey
    public void printHashKey() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.alice.demo_fbsahre",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("Key Hash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Key Hash", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("Key Hash", e.toString());
        }
    }
}
