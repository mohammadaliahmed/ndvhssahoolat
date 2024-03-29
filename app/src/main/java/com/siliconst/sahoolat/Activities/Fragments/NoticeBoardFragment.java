package com.siliconst.sahoolat.Activities.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.siliconst.sahoolat.Adapters.NoticesAdapter;
import com.siliconst.sahoolat.Models.Notice;
import com.siliconst.sahoolat.NetworkResponses.ApiResponse;
import com.siliconst.sahoolat.R;
import com.siliconst.sahoolat.Utils.AppConfig;
import com.siliconst.sahoolat.Utils.CommonUtils;
import com.siliconst.sahoolat.Utils.UserClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeBoardFragment extends Fragment {
    private View rootView;

    RecyclerView recycler;
    NoticesAdapter adapter;
    private List<Notice> itemList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.noticeboard_fragment, container, false);
        recycler = rootView.findViewById(R.id.recycler);

        adapter = new NoticesAdapter(getContext(), itemList);
        recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recycler.setAdapter(adapter);


        getDataFromServer();
        return rootView;

    }

    private void getDataFromServer() {
        UserClient getResponse = AppConfig.getRetrofit().create(UserClient.class);

        JsonObject map = new JsonObject();
        map.addProperty("api_username", AppConfig.API_USERNAME);
        map.addProperty("api_password", AppConfig.API_PASSOWRD);


        Call<ApiResponse> call = getResponse.notices(map);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getNotices() != null) {
                        adapter.setItemList(response.body().getNotices());
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
