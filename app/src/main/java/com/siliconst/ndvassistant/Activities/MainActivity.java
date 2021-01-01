package com.siliconst.ndvassistant.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.siliconst.ndvassistant.Adapters.HomeTicketsAdapter;
import com.siliconst.ndvassistant.Models.Ticket;
import com.siliconst.ndvassistant.Models.User;
import com.siliconst.ndvassistant.NetworkResponses.ApiResponse;
import com.siliconst.ndvassistant.R;
import com.siliconst.ndvassistant.Utils.AppConfig;
import com.siliconst.ndvassistant.Utils.CommonUtils;
import com.siliconst.ndvassistant.Utils.SharedPrefs;

import com.siliconst.ndvassistant.Utils.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView seeAll;
    RecyclerView recycler;
    HomeTicketsAdapter adapter;
    private List<Ticket> itemList = new ArrayList<>();
    ImageView createTicket;
    TextView name, email, phone, address;

    TextView noticeBoard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = findViewById(R.id.name);
        noticeBoard = findViewById(R.id.noticeBoard);
        seeAll = findViewById(R.id.seeAll);
        createTicket = findViewById(R.id.createTicket);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        recycler = findViewById(R.id.recycler);
        seeAll.setPaintFlags(seeAll.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        noticeBoard.setPaintFlags(seeAll.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        name.setText(SharedPrefs.getUser().getName());
        phone.setText(SharedPrefs.getUser().getPhone());
        email.setText(SharedPrefs.getUser().getEmail());
        address.setText("House # " + SharedPrefs.getUser().getHousenumber() + ", Block " + SharedPrefs.getUser().getBlock());
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SeeAllTickets.class));
            }
        });


        noticeBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,NoticeBoards.class));
            }
        });
        adapter = new HomeTicketsAdapter(this, itemList);
        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recycler.setAdapter(adapter);
//        swipeController = new MessageSwipeController(this, new SwipeControllerActions() {
//            @Override
//            public void showReplyUI(int position) {
////                showReplyLayout(position);
//            }
//        });

//        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
//        itemTouchhelper.attachToRecyclerView(recycler);

        createTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateTicket.class));
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isComplete()) {
                    String token = task.getResult().getToken();
                    updateFcmKey(token);

                }
            }
        });


    }

    private void getMyTicketsFromServer() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUser().getId());


        Call<ApiResponse> call = getResponse.homeTickets(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getTickets() != null) {
                        adapter.updateList(response.body().getTickets());
                    }
                } else {
                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyTicketsFromServer();
    }

    private void updateFcmKey(String token) {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUser().getId());
        map.addProperty("fcmKey", token);

        Call<ApiResponse> call = getResponse.updateFcmKey(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    ApiResponse object = response.body();

                    if (object != null && object.getUser() != null) {
                        User user = object.getUser();
                        SharedPrefs.setUser(user);
                    }

                } else {
//                    CommonUtils.showToast(response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }


}