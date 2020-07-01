package com.cb.softwares.doctorapp.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.fragment.TimePickerFragment;
import com.cb.softwares.doctorapp.model.AppointmentDetails;
import com.cb.softwares.doctorapp.model.TagCreation;
import com.cb.softwares.doctorapp.notification.APIService;
import com.cb.softwares.doctorapp.notification.Client;
import com.cb.softwares.doctorapp.notification.Data;
import com.cb.softwares.doctorapp.notification.MyResponse;
import com.cb.softwares.doctorapp.notification.Notification;
import com.cb.softwares.doctorapp.notification.Sender;
import com.cb.softwares.doctorapp.sharedpreference.SharedPreferenceManager;
import com.cb.softwares.doctorapp.util.UtilActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.cb.softwares.doctorapp.volley.Constants.BOOKING_REQUEST;
import static com.cb.softwares.doctorapp.volley.Constants.getServerURL;

public class BookAppointmentActivity extends UtilActivity {


    @BindView(R.id.edtName)
    EditText edtName;

    @BindView(R.id.edtAge)
    EditText edtAge;
    @BindView(R.id.edtDate)
    EditText edtDate;
    @BindView(R.id.edtTime)
    EditText edtTime;
    @BindView(R.id.edtMobile)
    EditText edtMobile;

    @BindView(R.id.spinnerGender)
    Spinner spinnerGender;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    @BindArray(R.array.genderArray)
    String[] genderArray;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnBookAppointment)
    Button btnBookAppointmwent;


    @BindView(R.id.formLayout)
    LinearLayout formLayout;

    @BindView(R.id.hospitalSpinner)
    Spinner hospitalSpinner;

    @BindView(R.id.dateSpinner)
    Spinner dateSpinner;


    String date, gender = "";

    FirebaseAuth auth;
    FirebaseUser user;

    private String TAG = "BookAppointment";

    DatabaseReference reference;
    SharedPreferenceManager sharedPreferenceManager;


    private long maxID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        ButterKnife.bind(this);

        setAdapterForSpinner();

        setup_toolbar_with_back(toolbar, "Book Appointment");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        progressBar.setVisibility(View.VISIBLE);
        formLayout.setVisibility(View.GONE);


        reference = FirebaseDatabase.getInstance().getReference("Appointments");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    maxID = (dataSnapshot.getChildrenCount());

                Log.w(TAG, "max id" + maxID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (getIntent().getExtras() != null) {


            date = getIntent().getExtras().getString("date");

            edtDate.setText(date);

        }

        setClickListenerForTime();

        getTags();


        sharedPreferenceManager = new SharedPreferenceManager(this);
        Log.w(TAG, " key " + sharedPreferenceManager.getDoctorToken());
    }


    ArrayList<TagCreation> list = new ArrayList<>();


    private void getTags() {
        DatabaseReference query = FirebaseDatabase.getInstance().getReference("Tags");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        list.add(snapshot.getValue(TagCreation.class));
                    }


                    if (list.size() > 0) {
                        spinnerSelectedListener();
                        loadData();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private ArrayList<String> hospitalNameList = new ArrayList<>();

    private void loadData() {

        for (TagCreation creation : list) {

            hospitalNameList.add(creation.getHospitalName());
        }

        if (hospitalNameList.size() > 0) {
            hospitalNameList.add(0, "Select Hospital");
            hospitalSpinner.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            setAdapterForSpinner(hospitalSpinner, hospitalNameList);

        }
    }


    String hospitalName = "";
    String timings = "";

    private void spinnerSelectedListener() {
        hospitalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {

                    formLayout.setVisibility(View.VISIBLE);
                    hospitalName = hospitalSpinner.getSelectedItem().toString();

                    TagCreation creation = list.get(position - 1);

                    timings = creation.getStartTime() + " - " + creation.getEndTime();

                    edtTime.setText(timings);

                    ArrayList<String> dateList = new ArrayList<>();
                    try {

                        JSONArray array = new JSONArray(creation.getDates());
                        JsonParser parser;
                        for (int i = 0; i <= array.length() - 1; i++) {
                           String strDate =  array.getJSONObject(i).getString("dates");
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                            Date date = null;
                            try {
                                date = sdf.parse(strDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date.after(new Date())) {
                                dateList.add(array.getJSONObject(i).getString("dates"));
                            }
                        }

                        if (dateList.size() > 0) {
                            dateList.add(0, "Select Available Date");
                            dateSpinner.setVisibility(View.VISIBLE);
                            setAdapterForSpinner(dateSpinner, dateList);
                            dateList.add("Choose Your Date");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {

                    Spinner spinner = (Spinner) findViewById(R.id.dateSpinner);
                    if (position == spinner.getAdapter().getCount()-1)
                    {

                        BookAppointmentActivity.this.startActivityForResult(new Intent(BookAppointmentActivity.this, CalendarActivity.class), 0);
                        return;
                    }

                    date = dateSpinner.getSelectedItem().toString();
                    edtDate.setText(date);
                } else {
                    edtDate.setText(date);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (0) : {
                if (resultCode == CalendarActivity.RESULT_OK) {
                    Date date = new Date(data.getExtras().getLong("date", -1));

                    edtDate.setText(date.toString());
                    // TODO Update your TextView.
                }
                break;
            }
        }
    }
    private void setAdapterForSpinner(Spinner spinner, ArrayList<String> list) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);


    }


    private void setClickListenerForTime() {
        edtTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    TimePickerFragment fragment = new TimePickerFragment();
                    fragment.show(getSupportFragmentManager(), "Time");
                    return true;
                } else {

                    return false;
                }
            }
        });
    }

    private void setAdapterForSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_text, R.id.txtSpinnerText, genderArray);
        spinnerGender.setAdapter(adapter);

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    gender = spinnerGender.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @OnClick(R.id.btnBookAppointment)
    public void bookAppointment() {

        String name = edtName.getText().toString().trim();
        String age = edtAge.getText().toString().trim();
        String time = edtTime.getText().toString().trim();
        String mobile = edtMobile.getText().toString().trim();
        if (!(TextUtils.isEmpty(name) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(time) || TextUtils.isEmpty(date) || TextUtils.isEmpty(gender))) {
            goneView(btnBookAppointmwent);
            showView(progressBar);


            //sendNotification(hospitalName, name, sharedPreferenceManager.getDoctorToken());


            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Appointments");

            Query query = reference.orderByChild("date").equalTo(date);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    int i=0;
                    for (DataSnapshot snapShot : dataSnapshot.getChildren()) {

                        AppointmentDetails details = snapShot.getValue(AppointmentDetails.class);

                        if (details.getName().toUpperCase().equals(edtName.getText().toString().trim().toUpperCase()) && details.getHospitalName().equals(hospitalName))
                        {
                            i++;  //Same name same hospital same date

                        }
                        if (i==2) //this is real duplicated. if it is 1, it means real insert.
                        {
                            Toastmsg(BookAppointmentActivity.this, "Duplicated Request for Booking!");
                            snapShot.getRef().removeValue();
                            goneView(progressBar);
                            showView(btnBookAppointmwent);
                            return;
                        }

                        //User user = snapShot.getValue(.class);
                      //  sharedPreferenceManager.setDoctorName(user.getName());
                     //   sharedPreferenceManager.setDoctorID(user.getId());
                      //  sharedPreferenceManager.setDoctorUserID(user.getUserid());
                      //  sharedPreferenceManager.setDoctorToken(user.getToken());
                      //  Log.w(TAG, "doctor data " + user.getToken() + user.getId() + "" + user.getUserid());


                    }
                    if (i==2) {

                        sendNotification(hospitalName, edtName.getText().toString().trim(), sharedPreferenceManager.getDoctorToken());
                        sendServerRequest(getServerURL(edtMobile.getText().toString().trim(), BOOKING_REQUEST, date + " " + edtTime.getText().toString().trim()));

                        Toastmsg(BookAppointmentActivity.this, "Successfully sent your request");

                        goneView(progressBar);
                           finish();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            HashMap<String, Object> map = new HashMap<>();

            map.put("id", String.valueOf(maxID + 1));
            map.put("name", name);
            map.put("date", date);
            map.put("time", timings);
            map.put("age", age);
            map.put("gender", gender);
            map.put("userid", user.getUid());
            map.put("mobile", mobile);
            map.put("isConfirmedByDoctor", false);
            map.put("hospitalName", hospitalName);

            String s = reference.child(String.valueOf(maxID)).getDatabase().toString();

            reference.child(String.valueOf(maxID + 1)).setValue(map);


          //  finish();


        } else {
            Toastmsg(BookAppointmentActivity.this, "All fields are required");
        }
    }


    private void sendNotification(String hospitalName, String name, String token) {

        APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        Data data = new Data(user.getUid(), R.mipmap.ic_launcher, name + " booked a new appointment", hospitalName + " - New Appointment", token, 2);


        Notification data1 = new Notification(hospitalName + " - New Appointment", name + " booked a new appointment", name + " booked a new appointment");
        Sender sender = new Sender(data, token, data1);
        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                Log.w(TAG, "response " + response.body());
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        // Toastmsg(ChatMessageActivity.this, "Failed");
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

                Log.w(TAG, "error " + t.getStackTrace());

            }
        });
    }


}
