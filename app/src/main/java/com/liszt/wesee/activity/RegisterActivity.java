package com.liszt.wesee.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.liszt.wesee.R;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import Cookies.PersistentCookieStore;
import datahelper.AESencrypt;
import watchers.accountWatcher;
import watchers.confirmPasswordWatcher;
import watchers.emptyWatcher;

import watchers.mobileWatcher;
import watchers.passwordWatcher;
import watchers.registerWatcher;

public class RegisterActivity extends AppCompatActivity {
    final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000, 1000);
    EditText mobile;
    EditText account;
    TextView wrongaccount;
    EditText password;
    TextView wrongpassword;
    EditText confirmPassword;
    TextView notsame;
    EditText vcode;
    CheckBox recommend;
    Button register;
    Button sendVcode;
    String encoderules;
    String recommendAccpet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mobile = (EditText) findViewById(R.id.mobile);
        account = (EditText) findViewById(R.id.account);
        wrongaccount = (TextView) findViewById(R.id.txt_wrongusername);
        password = (EditText) findViewById(R.id.password);
        wrongpassword = (TextView) findViewById(R.id.txt_wrongpassword);
        confirmPassword = (EditText) findViewById(R.id.conformPassword);
        notsame = (TextView) findViewById(R.id.txt_notsame);
        vcode = (EditText) findViewById(R.id.vcode);
        sendVcode = (Button) findViewById(R.id.sendVcode);
        recommend = (CheckBox) findViewById(R.id.recommended_accept);
        register = (Button) findViewById(R.id.register);
        EditText editTextList[] = {account,password,confirmPassword,mobile,vcode};
        account.addTextChangedListener(new accountWatcher(account,wrongaccount));
        mobile.addTextChangedListener(new mobileWatcher(mobile, sendVcode));
        password.addTextChangedListener(new registerWatcher(editTextList,register));
        confirmPassword.addTextChangedListener(new registerWatcher(editTextList,register));
        mobile.addTextChangedListener(new registerWatcher(editTextList,register));
        vcode.addTextChangedListener(new registerWatcher(editTextList,register));
        password.addTextChangedListener(new passwordWatcher(password,wrongpassword));
        confirmPassword.addTextChangedListener(new confirmPasswordWatcher(confirmPassword, password, notsame));
        sendVcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new RegisterActivity.MyThread2(mobile.getText().toString()).start();
            }
        });

        encoderules = getResources().getString(R.string.encoderules);
        recommendAccpet = "1";
        recommend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    recommendAccpet = "1";
                }
                else {
                    recommendAccpet = "0";
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new RegisterActivity.MyThread(account.getText().toString(), AESencrypt.AESEncode(password.getText().toString(), encoderules), AESencrypt.AESEncode(confirmPassword.getText().toString(), encoderules), vcode.getText().toString(),recommendAccpet).start();
//                String str = AESencrypt.AESEncode(password.getText().toString(),encoderules);
//                String str2 = AESencrypt.AESDncode(str,encoderules);
//                Log.v("jiami:",str);
//                Log.v("jiemi:",str2);

            }
        });
    }

        class MyThread extends Thread {
            private String username;
            private String password;
            private String confirmPassword;
            private String vcode;
            private String recommendedAccept;

            public MyThread(String username, String passsword, String confirmPassword, String vcode, String recommendedAccept) {

                this.username = username;
                this.password = passsword;
                this.confirmPassword = confirmPassword;
                this.vcode = vcode;
                this.recommendedAccept = recommendedAccept;
            }

            @Override
            public void run() {
                EasyHttp.get("user/register").params("username", username).params("password", password).params("confirmPassword", confirmPassword).params("vcode", vcode).params("recommendedAccept",recommendedAccept).execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        Toast.makeText(RegisterActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            int code = obj.optInt("code");
                            if (code == -1) {
                                Toast.makeText(RegisterActivity.this, "未发送验证码", Toast.LENGTH_LONG).show();
                            } else if (code == 0) {

                                Toast.makeText(RegisterActivity.this, "错误的短信验证码", Toast.LENGTH_LONG).show();
                            } else if (code == 1) {
                                Toast.makeText(RegisterActivity.this, "两次密码不一样", Toast.LENGTH_LONG).show();
                            } else if (code == 2) {
                                Toast.makeText(RegisterActivity.this, "该用户名已存在", Toast.LENGTH_LONG).show();
                            } else if (code == 3) {
                                Toast.makeText(RegisterActivity.this, "无法注册", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                                PersistentCookieStore cookieStore = new PersistentCookieStore(getApplicationContext());
                                cookieStore.removeAll();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });
            }
        }
    class MyThread2 extends Thread {
        private String mobile;


        public MyThread2(String mobile) {

            this.mobile = mobile;

        }

        @Override
        public void run() {
            EasyHttp.get("sms/sendVcode_register").params("mobile", mobile).execute(new SimpleCallBack<String>() {
                @Override
                public void onError(ApiException e) {
                    Toast.makeText(RegisterActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        int code = obj.optInt("code");
                        if (code == 1) {
                           myCountDownTimer.start();
                        } else if (code == 2) {
                            Toast.makeText(RegisterActivity.this, "该手机号已注册", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "错误的手机号", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }



    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            sendVcode.setClickable(false);
            sendVcode.setText(l / 1000 + "秒");

        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            sendVcode.setText("重新获取");
            //设置可点击
            sendVcode.setClickable(true);
        }
    }

    }



