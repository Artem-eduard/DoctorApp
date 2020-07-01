package com.cb.softwares.doctorapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.databinding.FragmentTagCreationBinding;
import com.cb.softwares.doctorapp.interfaces.CreateTag;
import com.cb.softwares.doctorapp.model.TagCreation;
import com.cb.softwares.doctorapp.ui.Activity_TAG;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class TagCreationFragment extends Fragment implements CreateTag {


    private String TAG = "TagCreation";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentTagCreationBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tag_creation, container, false);
        binding.setCreate(this);


        TagCreation creation = new TagCreation("", "", "", "", "", "");
        binding.setTags(creation);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void createTag(TagCreation creation) {
        Log.w(TAG, "creation val  " + creation.getHospitalName());

        if (!(creation.getHospitalName().trim().isEmpty() || creation.getHospitalAddress().trim().isEmpty() || creation.getStartTime().trim().isEmpty() || creation.getEndTime().trim().isEmpty())) {
            creation.setId(get_current_date() + "_" + get_current_time() + "_" + generate_random_number());
            ((Activity_TAG) getActivity()).viewModel.addTags(creation);
            ((Activity_TAG) getActivity()).navController.navigateUp();


        } else {
            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
        }

    }

    public String get_current_date() {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        StringBuilder string = new StringBuilder()
                // Month is 0 based so add 1
                .append(year).append("-")
                .append(month + 1).append("-")
                .append(day);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        return string.toString();

    }


    public String get_current_time() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
        return sdf.format(d);
    }


    public int generate_random_number() {
        Random rand = new Random();
        return rand.nextInt(1000);
    }


}
