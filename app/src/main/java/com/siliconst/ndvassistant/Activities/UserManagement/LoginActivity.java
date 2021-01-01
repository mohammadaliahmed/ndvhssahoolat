package com.siliconst.ndvassistant.Activities.UserManagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.siliconst.ndvassistant.Activities.MainActivity;
import com.siliconst.ndvassistant.NetworkResponses.ApiResponse;
import com.siliconst.ndvassistant.R;
import com.siliconst.ndvassistant.Utils.AppConfig;
import com.siliconst.ndvassistant.Utils.CommonUtils;
import com.siliconst.ndvassistant.Utils.SharedPrefs;
import com.siliconst.ndvassistant.Utils.UserClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextView register;
    Button login;
    EditText phone, password;

    TextView characters;
    RelativeLayout wholeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        register = findViewById(R.id.register);

        characters = findViewById(R.id.characters);
        wholeLayout = findViewById(R.id.wholeLayout);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.getText().length() < 11) {
                    phone.setError("Enter correct phone number");
                } else if (password.getText().length() < 6) {
                    password.setError("Enter 6 or more characters");
                } else {
                    loginNow();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                characters.setText(phone.getText().length() + "/11");
            }
        });


    }

    private void loginNow() {
        wholeLayout.setVisibility(View.VISIBLE);
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);
        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("password", password.getText().toString());
        map.addProperty("phone", phone.getText().toString());

        Call<ApiResponse> call = getResponse.login(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                wholeLayout.setVisibility(View.VISIBLE);
                if (response.code() == 200) {
                    if (response.body().getCode() == 200) {
                        CommonUtils.showToast("Successfully Logged in");
                        SharedPrefs.setUser(response.body().getUser());
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    } else {

                        CommonUtils.showToast(response.body().getMessage());
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                wholeLayout.setVisibility(View.VISIBLE);
            }
        });

    }
}
