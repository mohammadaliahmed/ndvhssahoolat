package com.siliconst.ndvassistant.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.siliconst.ndvassistant.Activities.UserManagement.LoginActivity;
import com.siliconst.ndvassistant.Adapters.RepliesAdapter;
import com.siliconst.ndvassistant.Models.Reply;
import com.siliconst.ndvassistant.Models.Ticket;
import com.siliconst.ndvassistant.NetworkResponses.ApiResponse;
import com.siliconst.ndvassistant.R;
import com.siliconst.ndvassistant.Utils.AppConfig;
import com.siliconst.ndvassistant.Utils.CommonUtils;
import com.siliconst.ndvassistant.Utils.SharedPrefs;
import com.siliconst.ndvassistant.Utils.UserClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListOfRepliesToTicket extends AppCompatActivity {

    RecyclerView recycler;
    private List<Reply> repliesList = new ArrayList<>();
    private int ticketId;
    private Ticket ticketModel;
    TextView description, title, tokenNumber;
    RepliesAdapter adapter;
    ImageView sendMessage;
    EditText message;
    private String messageToSend;
    ImageView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_replies);


        ticketId = getIntent().getIntExtra("ticketId", 0);
        tokenNumber = findViewById(R.id.tokenNumber);
        sendMessage = findViewById(R.id.sendMessage);
        message = findViewById(R.id.message);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        close = findViewById(R.id.close);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new RepliesAdapter(this, repliesList);
        recycler.setAdapter(adapter);
        getRepliesFromServer();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getText().length() == 0) {
                    message.setError("Cant send empty message");
                } else {
                    sendMessageNow();
                }
            }
        });


    }

    private void sendMessageNow() {
        messageToSend = message.getText().toString();
        message.setText("");

        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("ticketId", ticketId);
        map.addProperty("reply", messageToSend);
        map.addProperty("userId", SharedPrefs.getUser().getId());


        Call<ApiResponse> call = getResponse.sendReply(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getReplies() != null) {
                        repliesList = response.body().getReplies();
                        adapter.setItemList(repliesList);
                        recycler.scrollToPosition(repliesList.size() - 1);

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

    private void getRepliesFromServer() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("ticketId", ticketId);


        Call<ApiResponse> call = getResponse.getReplies(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getReplies() != null) {
                        repliesList = response.body().getReplies();
                        adapter.setItemList(repliesList);
                        ticketModel = response.body().getTicket();
                        recycler.scrollToPosition(repliesList.size() - 1);
                        if (ticketModel != null) {
                            setupTicketUi();
                        }
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

    private void setupTicketUi() {
        tokenNumber.setText("Ticket #:   " + ticketModel.getTokenNo() + " | Date: " + ticketModel.getCreatedAt());
        title.setText(ticketModel.getSubject());
        description.setText(ticketModel.getDescription());
    }

}
