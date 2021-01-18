package com.siliconst.ndvhssahoolat.Activities.StaffManagement;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.siliconst.ndvhssahoolat.Adapters.StaffTicketsAdapter;
import com.siliconst.ndvhssahoolat.Models.Ticket;
import com.siliconst.ndvhssahoolat.NetworkResponses.ApiResponse;
import com.siliconst.ndvhssahoolat.R;
import com.siliconst.ndvhssahoolat.Utils.AppConfig;
import com.siliconst.ndvhssahoolat.Utils.CommonUtils;
import com.siliconst.ndvhssahoolat.Utils.SharedPrefs;
import com.siliconst.ndvhssahoolat.Utils.UserClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffDashboard extends AppCompatActivity {

    TextView name, email, phone, designation;
    CircleImageView image;
    RecyclerView recycler;
    StaffTicketsAdapter adapter;
    private List<Ticket> ticketList = new ArrayList<>();
    Spinner statusSpinner;
    private String statusChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        statusSpinner = findViewById(R.id.statusSpinner);
        image = findViewById(R.id.image);
        recycler = findViewById(R.id.recycler);
        name = findViewById(R.id.name);
        designation = findViewById(R.id.designation);

        name.setText("WELCOME BACK, " + SharedPrefs.getUser().getName());
        phone.setText(SharedPrefs.getUser().getPhone());
        email.setText(SharedPrefs.getUser().getEmail());
        designation.setText(SharedPrefs.getUser().getDesignation());
        Glide.with(this).load(AppConfig.BASE_URL_Image + SharedPrefs.getUser().getAvatar()).placeholder(R.drawable.ic_profile).into(image);

        recycler = findViewById(R.id.recycler);
        adapter = new StaffTicketsAdapter(this, ticketList, new StaffTicketsAdapter.StaffTicketsAdapterCallbacks() {
            @Override
            public void onViewTask(Ticket ticket) {
                showDialogDetails(ticket);
            }
        });
        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recycler.setAdapter(adapter);

        getdataFromServer();


    }

    private void showDialogDetails(Ticket ticket) {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.alert_dialog_ticket_details, null);
        dialog.setContentView(layout);
        TextView houseNumber = layout.findViewById(R.id.houseNumber);
        TextView title = layout.findViewById(R.id.title);
        TextView description = layout.findViewById(R.id.description);
        TextView block = layout.findViewById(R.id.block);
        TextView phone = layout.findViewById(R.id.phone);
        TextView status = layout.findViewById(R.id.status);
        Button close = layout.findViewById(R.id.close);
        Button call = layout.findViewById(R.id.call);

        houseNumber.setText(ticket.getUser().getHousenumber());
        block.setText(ticket.getUser().getBlock());
        phone.setText(ticket.getUser().getPhone());
        status.setText("Ticket: " + ticket.getStatus());

        if (ticket.getStatus().equalsIgnoreCase("closed")) {
            status.setBackgroundColor(getResources().getColor(R.color.colorGreen));
        } else if (ticket.getStatus().equalsIgnoreCase("pending")) {
            status.setBackgroundColor(getResources().getColor(R.color.colorOriginalBlue));
        } else if (ticket.getStatus().equalsIgnoreCase("processing")) {
            status.setBackgroundColor(getResources().getColor(R.color.colorPurple));
        } else {
            status.setBackgroundColor(getResources().getColor(R.color.colorRed));
        }


        description.setText(ticket.getDescription());
        title.setText(ticket.getSubject());
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + ticket.getUser().getPhone()));
                startActivity(i);

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    private void setupPrioritySpinner() {
        List<String> list = new ArrayList<>();
        list.add("All");
        list.add("Open");
        list.add("Pending");
        list.add("Processing");
        list.add("Closed");
        list.add("Resolved");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(dataAdapter);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statusChosen = list.get(position);
                if (statusChosen.equalsIgnoreCase("all")) {
                    adapter.filter("");
                } else {
                    adapter.filter(statusChosen);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void getdataFromServer() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);
        map.addProperty("id", SharedPrefs.getUser().getId());


        Call<ApiResponse> call = getResponse.assignedTickets(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getTickets() != null) {
                        ticketList = response.body().getTickets();
                        adapter.updateList(response.body().getTickets());
                        setupPrioritySpinner();
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
}
