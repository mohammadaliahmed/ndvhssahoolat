package com.siliconst.ndvhssahoolat.Activities.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.siliconst.ndvhssahoolat.Adapters.FaqsAdapter;
import com.siliconst.ndvhssahoolat.Adapters.NoticesAdapter;
import com.siliconst.ndvhssahoolat.Models.FaqsModel;
import com.siliconst.ndvhssahoolat.Models.Notice;
import com.siliconst.ndvhssahoolat.NetworkResponses.ApiResponse;
import com.siliconst.ndvhssahoolat.R;
import com.siliconst.ndvhssahoolat.Utils.AppConfig;
import com.siliconst.ndvhssahoolat.Utils.CommonUtils;
import com.siliconst.ndvhssahoolat.Utils.UserClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaqDataFragment extends Fragment {
    private View rootView;

    RecyclerView recycler;
    private List<FaqsModel> itemList = new ArrayList<>();
    private FaqsAdapter adapter;
    Integer departmentId;

    public FaqDataFragment(Integer departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.faq_data_fragment, container, false);
        recycler = rootView.findViewById(R.id.recycler);
        adapter = new FaqsAdapter(getContext(), FAQsFragment.faqMap.get(departmentId));
        recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recycler.setAdapter(adapter);


        return rootView;

    }


}
