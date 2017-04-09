package com.divya.www.gcloud;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.net.URL;
import java.net.URLConnection;

public class Login extends AppCompatActivity {
    Button btLogin;
    EditText etemail, etpassword;
    DataInputStream dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btLogin = (Button) findViewById(R.id.btlogin);
        etemail = (EditText) findViewById(R.id.etEmailLogin);
        etpassword = (EditText) findViewById(R.id.etPasswordLogin);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = etemail.getText().toString();
                final String password = etpassword.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://192.168.43.192:8084/GCloud/user_login");
                            URLConnection conn = url.openConnection();
                            conn.setRequestProperty("email", email);
                            conn.setRequestProperty("password", password);

                            conn.connect();

                            dis = new DataInputStream(conn.getInputStream());
                            String str = dis.readLine();

                            if (str.equals("user_found")) {
                                SharedPreferences sp = getSharedPreferences("mydata", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("email", email);
                                editor.commit();
                                startActivity(new Intent(getBaseContext(), userhome.class));

                            } else if (str.equals("password_incorrect")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getBaseContext(), "Wrong password!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getBaseContext(), Login.class));
                                    }
                                });
                            } else if (str.equals("signup_first")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getBaseContext(), "SignUp First !", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getBaseContext(), SignUp.class));
                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            }
        });
    }
}
