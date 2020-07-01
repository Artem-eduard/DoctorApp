package com.cb.softwares.doctorapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.fragment.DatePickerFragment;
import com.cb.softwares.doctorapp.fragment.TimePickerFragment;
import com.cb.softwares.doctorapp.sharedpreference.SharedPreferenceManager;
import com.cb.softwares.doctorapp.util.UtilActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ShowAppointmentActivity extends UtilActivity {


    @BindView(R.id.txtName)
    TextView txtName;
    @BindView(R.id.txtAge)
    TextView txtAge;
    @BindView(R.id.txtDate)
    TextView txtDate;
    @BindView(R.id.txtTime)
    TextView txtTime;
    @BindView(R.id.txtMobile)
    TextView txtMobile;
    @BindView(R.id.profileImage)
    CircleImageView profileImage;


    @BindView(R.id.txtUpdate)
    TextView txtUpdate;
    @BindView(R.id.updateProgressBar)
    ProgressBar progressBar;

    String id, date, time;


    SharedPreferenceManager sharedPreferenceManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_appointment);
        ButterKnife.bind(this);


        sharedPreferenceManager = new SharedPreferenceManager(this);


        if (getIntent().getExtras() != null) {
            Bundle args = getIntent().getExtras();
            String name = args.getString("name");
            String mobile = args.getString("mobile");
            String age = args.getString("age") + " yrs";
            date = args.getString("date");
            time = args.getString("time");
            String gender = args.getString("gender");
            id = args.getString("id");

            txtName.setText(name);
            txtMobile.setText(mobile);
            txtAge.setText(age);
            txtDate.setText(date);
            txtTime.setText(time);

            if (gender.equalsIgnoreCase("Male")) {
                profileImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.man));
            } else {
                profileImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bigender));
            }
        }


        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sharedPreferenceManager.getUserType().equalsIgnoreCase("A")) {
                    TimePickerFragment fragment = new TimePickerFragment();
                    fragment.show(getSupportFragmentManager(), "Time");

                    showView(txtUpdate);
                }
            }
        });

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferenceManager.getUserType().equalsIgnoreCase("A")) {
                    DatePickerFragment fragment = new DatePickerFragment();
                    fragment.showNow(getSupportFragmentManager(), "Date");
                    showView(txtUpdate);
                }
            }
        });
    }

    @OnClick(R.id.txtUpdate)
    public void update() {

        boolean isNeedToUpdate = false;
        if (!date.equalsIgnoreCase(txtDate.getText().toString().trim())) {

            isNeedToUpdate = true;
        }

        if (!time.equalsIgnoreCase(txtTime.getText().toString().trim())) {
            isNeedToUpdate = true;
        }

        if (isNeedToUpdate) {


            showView(progressBar);
            goneView(txtUpdate);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Appointments").child(id);
            HashMap<String, Object> map = new HashMap<>();
            map.put("date", txtDate.getText().toString().trim());
            map.put("time", txtTime.getText().toString().trim());
            reference.updateChildren(map);


            goneView(progressBar);

            Toastmsg(ShowAppointmentActivity.this,"Successfully updated");

        } else {
            return;
        }


    }


    @OnClick(R.id.imgBackArrow)
    public void back() {

        finish();
    }
}
