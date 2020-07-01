package com.cb.softwares.doctorapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.adapter.TagAdapter;
import com.cb.softwares.doctorapp.databinding.FragmentTagsBinding;
import com.cb.softwares.doctorapp.interfaces.TagsInterface;
import com.cb.softwares.doctorapp.model.TagCreation;
import com.cb.softwares.doctorapp.ui.Activity_TAG;

import java.util.ArrayList;

public class TagsFragment extends Fragment implements TagsInterface {

    private String TAG = "TagsFragment";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setAdapter();
        Log.w(TAG, "on view created calling");

    }

    BroadcastReceiver myBroadcastReceiver;

    private void initialize() {

        myBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                adapter.notifyDataSetChanged();
            }
        };

        getActivity().registerReceiver(myBroadcastReceiver, new IntentFilter("Update"));

    }

    FragmentTagsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tags, container, false);
        binding.setTags(this);
        Log.w(TAG, "on create view calling");
        return binding.getRoot();
    }


    @Override
    public void createTag() {
        ((Activity_TAG) getActivity()).navController.navigate(R.id.tagsToTagsCreation);
    }


    TagAdapter adapter;
    ArrayList<TagCreation> list = new ArrayList<>();

    void setAdapter() {
        adapter = new TagAdapter(getActivity(), ((Activity_TAG) getActivity()).list);
        binding.setAdapter(adapter);

    }


    @Override
    public void onPause() {
        super.onPause();
        if (myBroadcastReceiver != null) {
            getActivity().unregisterReceiver(myBroadcastReceiver);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
        initialize();
    }


}
