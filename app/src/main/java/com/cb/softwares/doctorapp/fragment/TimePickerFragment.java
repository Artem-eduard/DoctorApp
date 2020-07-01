package com.cb.softwares.doctorapp.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.activity.BookAppointmentActivity;
import com.cb.softwares.doctorapp.sharedpreference.SharedPreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


    Calendar c = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the current time as the default values for the time picker
        c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(), this, hour, minute, false);
    }

    //onTimeSet() callback method
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //Do something with the user chosen time
        //Get reference of host activity (XML Layout File) TextView widget


        Log.w("Fragment", c.get(Calendar.YEAR) + "" + c.get(Calendar.MONTH) + "" + c.get(Calendar.DATE));
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), hourOfDay, minute);


        Calendar c1 = Calendar.getInstance();
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getActivity());

        String startTime = sharedPreferenceManager.getSTART_TIME();
        int startHour = Integer.parseInt(startTime.substring(0, startTime.indexOf(":")));
        int startMin = Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1, startTime.lastIndexOf(" ")));

        Log.w("Fragment", "start hour" + startHour + ":" + startMin);


        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), hourOfDay, minute, 0);
        Date date = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        c1.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), startHour, startMin, 0);
        Date startTimeDate = c1.getTime();


        //end time
        Calendar c2 = Calendar.getInstance();
        String endTime = sharedPreferenceManager.getEND_TIME();
        int endHour = Integer.parseInt(endTime.substring(0, endTime.indexOf(":")));
        int endMin = Integer.parseInt(endTime.substring(endTime.indexOf(":") + 1, endTime.lastIndexOf(" ")));
        c2.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), endHour, endMin, 0);
        Date endTimeDate = c2.getTime();

        Log.w("Fragment", "time " + date.toString() + " " + startTimeDate.toString() + " time check " + date.after(startTimeDate) + " equal " + date.equals(startTimeDate));




        if ((sdf.format(date).equalsIgnoreCase(startTime)) || (date.after(startTimeDate) && (date.before(endTimeDate)))) {
            if (getActivity() instanceof BookAppointmentActivity) {
                EditText tv = (EditText) getActivity().findViewById(R.id.edtTime);
                tv.setText(sdf.format(date));
            } else {

                TextView txt = (TextView) getActivity().findViewById(R.id.txtTime);
                txt.setText(sdf.format(date));
            }
        }else{
            Log.w("Fragment","else part woking");
            String msg = "Please choose time between "+ sharedPreferenceManager.getSTART_TIME()+" to "+  sdf.format(endTimeDate);
            Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
        }


        //Display the user changed time on TextView

    }
}
