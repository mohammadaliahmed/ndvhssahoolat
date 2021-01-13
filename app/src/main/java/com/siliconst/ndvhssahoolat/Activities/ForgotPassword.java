package com.siliconst.ndvhssahoolat.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.siliconst.ndvhssahoolat.NetworkResponses.ApiResponse;
import com.siliconst.ndvhssahoolat.R;
import com.siliconst.ndvhssahoolat.Utils.AppConfig;
import com.siliconst.ndvhssahoolat.Utils.UserClient;

import retrofit2.Call;

public class ForgotPassword extends AppCompatActivity {

    RelativeLayout wholeLayout;
    EditText phone;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forogt_password);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("Forgot password");

        phone = findViewById(R.id.phone);
        send = findViewById(R.id.send);
        wholeLayout = findViewById(R.id.wholeLayout);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.getText().length() < 5) {
                    phone.setError("Enter phone");
                } else {
                    resetPasswordAPI();
                }
            }
        });
    }

    private void resetPasswordAPI() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("phone", phone.getText().toString());

        Call<ApiResponse> call = getResponse.resetpassword(map);
//        call.enqueue(new Callback<ApiResponse>() {
//            @Override
//            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
//                if (response.code() == 200) {
//                    ApiResponse object = response.body();
//
//
//                } else {
//                    CommonUtils.showToast(response.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse> call, Throwable t) {
//
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}