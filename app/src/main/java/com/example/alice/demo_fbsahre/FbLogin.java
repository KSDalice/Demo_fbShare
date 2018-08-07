package com.example.alice.demo_fbsahre;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class FbLogin extends AppCompatActivity {

    Button custom_button ;
    LoginButton fb_button;
    ImageView imgAvatar;
    CallbackManager callbackManager;
    ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fb_login);
        printHashKey();
        //init FB
        callbackManager = CallbackManager.Factory.create();
        fb_button = (LoginButton) findViewById(R.id.fb_button);
        custom_button = (Button)findViewById(R.id.custom_button);
        imgAvatar = (ImageView) findViewById(R.id.avatar);

        fb_button.setReadPermissions(Arrays.asList("public_profile", "email"));
        fb_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mDialog = new ProgressDialog(FbLogin.this);
                mDialog.setMessage("Retrieving data ...");
                mDialog.show();

                AccessToken accesstoken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(accesstoken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        mDialog.dismiss();
                        Log.d("object", object.toString());
                        Log.d("response", response.toString());
                        getData(object);
                    }
                });
                //包入你想要得到的資料 送出request

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);

                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d("FB", "CANCEL");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FB", error.toString());
            }
        });

        // 自訂登入FB按鈕
        custom_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If already login
                if (AccessToken.getCurrentAccessToken() != null) {
                    //Just set User Id
                    AccessToken acctoken = AccessToken.getCurrentAccessToken();
                    getProfile(acctoken);
                } else {
                    registerFB();
                }
            }
        });

    }

    private void getData(JSONObject object) {
        try {
            URL profile_picture = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?width=250&height=250");
            Picasso.with(this).load(profile_picture.toString()).into(imgAvatar);
//            LoginManager.getInstance().logOut();
            Log.v("fb", object.toString());
            Log.v("fb", "Email:" + object.getString("email"));
            Log.v("fb", "Birthday:" + object.getString("birthday"));
            Log.v("fb", "Friends: " + object.getJSONObject("friends").getJSONObject("summary").getString("total_count"));

        } catch (MalformedURLException e) {
            Log.e("MalformedURLException",e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e("JSONException",e.getMessage());
        }
    }

    // 取得使用者資料
    private void getProfile(AccessToken accesstoken) {

        GraphRequest graphRequest = GraphRequest.newMeRequest(accesstoken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                getData(object);
                Log.e("成功回傳東西1", response.toString());
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, link, email, gender, picture.type(large), location");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();

        // 登出
        //LoginManager.getInstance().logOut();
    }

    // 登入FB
    private void registerFB() {
        // 向fb請求讀取權限
        LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList("public_profile", "email"));

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getProfile(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e("使用者按離開", "別做夢了，他不要你了");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("ＦＢ錯了", "ＦＢ錯了，請告它!!");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
