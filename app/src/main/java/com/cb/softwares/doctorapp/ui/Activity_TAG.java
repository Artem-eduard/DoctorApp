package com.cb.softwares.doctorapp.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.interfaces.ModifyTagInterface;
import com.cb.softwares.doctorapp.model.TagCreation;
import com.cb.softwares.doctorapp.util.TagUtil;
import com.cb.softwares.doctorapp.util.UtilActivity;
import com.cb.softwares.doctorapp.viewmodel.TagViewModel;
import com.cb.softwares.doctorapp.viewmodel.TagViewModelFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Activity_TAG extends UtilActivity implements ModifyTagInterface {


    private String TAG = "activitytag";
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_host);
        ButterKnife.bind(this);
        setup_toolbar(toolbar, "Tags");
        navController = Navigation.findNavController(this, R.id.my_nav_host_fragment);
        initialize();
    }


    public NavController navController;


    public TagViewModel viewModel;

    public ArrayList<TagCreation> list = new ArrayList<>();

    public void initialize() {

        TagViewModelFactory factory = new TagUtil().provideTagViewModelFactory();

        viewModel = ViewModelProviders.of(Activity_TAG.this, factory).get(TagViewModel.class);
        viewModel.getTags().observe(this, new Observer<List<TagCreation>>() {
            @Override
            public void onChanged(List<TagCreation> tagCreations) {

                Log.w(TAG, "current time " + get_current_time());
                list.clear();
                if (tagCreations.size() > 0) {
                    list.addAll(tagCreations);
                    Intent i = new Intent("Update");
                    sendBroadcast(i);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeObservers();
        Log.w(TAG, "on destroy working");
    }


    public void removeObservers() {
        if (viewModel != null && viewModel.getTags().hasObservers()) {
            viewModel.getTags().removeObservers(Activity_TAG.this);

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeObservers();

    }

    @Override
    public void modifyToCustomDate(TagCreation creation) {
        Log.w(TAG, "modify custom date working " + creation.getHospitalName());
        customPicker(creation);

    }

    @Override
    public void modifyToToday(TagCreation creation) {

        Log.w(TAG, "modify today working " + creation.getHospitalName());
        addDates(creation, get_current_date());
    }

    @Override
    public void modifyToTomorrow(TagCreation creation) {
        Log.w(TAG, "modify tomorrow working " + creation.getHospitalName());
        addDates(creation, getTomorrow());
    }

    @Override
    public void modifyDates(TagCreation creation) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("data", creation);
        bundle.putString("type","modifyDate");
        navController.navigate(R.id.tagsModification, bundle);

    }

    @Override
    public void addDates(TagCreation creation) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", creation);
        bundle.putString("type","addDates");
        navController.navigate(R.id.tagsModification, bundle);

    }

    private void addDates(TagCreation creation, String date) {

        try {

            JSONArray array = new JSONArray(creation.getDates());
            JSONObject object = new JSONObject();
            object.put("dates", date);
            array.put(object);
            creation.setDates(array.toString());
            creation.setFormatDate(creation.getAllDates());
            viewModel.modifyTags(creation);
            Log.w(TAG, "dates1 " + array.toString());

        } catch (JSONException e) {
            try {
                JSONArray array = new JSONArray();
                JSONObject object = new JSONObject();
                object.put("dates", date);
                array.put(object);
                creation.setDates(array.toString());
                creation.setFormatDate(creation.getAllDates());
                Log.w(TAG, "dates2 " + creation.getFormatDate());
                viewModel.modifyTags(creation);

            } catch (JSONException e1) {

            }

        }
    }

    DatePickerDialog datePicker;


    private void customPicker(final TagCreation creation) {

        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        datePicker = new DatePickerDialog(Activity_TAG.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                try {

                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                    Date date = format.parse(dayOfMonth + "-" + (month + 1) + "-" + year);
                    addDates(creation, format.format(date));
                } catch (ParseException e) {

                }
            }
        }, year, month, day);

        datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        datePicker.getDatePicker().setMaxDate(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7));
        datePicker.show();


    }
}
