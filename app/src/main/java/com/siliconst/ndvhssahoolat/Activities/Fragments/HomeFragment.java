package com.siliconst.ndvhssahoolat.Activities.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;
import com.siliconst.ndvhssahoolat.Activities.CreateTicket;
import com.siliconst.ndvhssahoolat.Activities.EditProfile;
import com.siliconst.ndvhssahoolat.Activities.MainActivity;
import com.siliconst.ndvhssahoolat.Activities.NoticeBoards;
import com.siliconst.ndvhssahoolat.Activities.SeeAllTickets;
import com.siliconst.ndvhssahoolat.Adapters.HomeTicketsAdapter;
import com.siliconst.ndvhssahoolat.Models.Ticket;
import com.siliconst.ndvhssahoolat.NetworkResponses.ApiResponse;
import com.siliconst.ndvhssahoolat.R;
import com.siliconst.ndvhssahoolat.Utils.AppConfig;
import com.siliconst.ndvhssahoolat.Utils.CommonUtils;
import com.siliconst.ndvhssahoolat.Utils.SharedPrefs;
import com.siliconst.ndvhssahoolat.Utils.UserClient;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    TextView seeAll;
    RecyclerView recycler;
    HomeTicketsAdapter adapter;
    private List<Ticket> itemList = new ArrayList<>();
    ImageView createTicket;
    TextView name, email, phone, address;

    CardView profile;
    CircleImageView image;
    private View rootView;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home_fragment, container, false);
        name = rootView.findViewById(R.id.name);
        image = rootView.findViewById(R.id.image);
        profile = rootView.findViewById(R.id.profile);
        seeAll = rootView.findViewById(R.id.seeAll);
        createTicket = rootView.findViewById(R.id.createTicket);
        email = rootView.findViewById(R.id.email);
        phone = rootView.findViewById(R.id.phone);
        address = rootView.findViewById(R.id.address);
        recycler = rootView.findViewById(R.id.recycler);
        seeAll.setPaintFlags(seeAll.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        name.setText(SharedPrefs.getUser().getName());
        phone.setText(SharedPrefs.getUser().getPhone());
        email.setText(SharedPrefs.getUser().getEmail());
        address.setText("House # " + SharedPrefs.getUser().getHousenumber() + "," + SharedPrefs.getUser().getBlock());
        Glide.with(this).load(AppConfig.BASE_URL_Image + SharedPrefs.getUser().getAvatar()).placeholder(R.drawable.ic_profile).into(image);
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SeeAllTickets.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, EditProfile.class));
            }
        });


        adapter = new HomeTicketsAdapter(context, itemList);
        recycler.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        recycler.setAdapter(adapter);
//        swipeController = new MessageSwipeController(this, new SwipeControllerActions() {
//            @Override
//            public void showReplyUI(int position) {
////                showReplyLayout(position);
//            }
//        });

//        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
//        itemTouchhelper.attachToRecyclerView(recycler);






        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        getMyTicketsFromServer();
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

}
