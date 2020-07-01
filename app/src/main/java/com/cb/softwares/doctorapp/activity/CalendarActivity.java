package com.cb.softwares.doctorapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.cb.softwares.doctorapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

public class CalendarActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnResetPassword;
    private Button btnBack;
    private FirebaseAuth mAuth;

    private CalendarView mCalendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mCalendar = (CalendarView) findViewById(R.id.calendarView) ;
        btnBack = (Button) findViewById(R.id.btn_back);




        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
              //  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
              //  String selectedDate = sdf.format(new Date(mCalendar.getDate()));

                resultIntent.putExtra("date", new Date(mCalendar.getDate()).getTime());
                setResult(CalendarActivity.RESULT_OK, resultIntent);

                finish();
            }
        });
    }

}