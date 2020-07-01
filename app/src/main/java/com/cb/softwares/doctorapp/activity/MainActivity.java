package com.cb.softwares.doctorapp.activity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.adapter.AppointmentAdapter;
import com.cb.softwares.doctorapp.model.AppointmentDetails;
import com.cb.softwares.doctorapp.model.Chat;
import com.cb.softwares.doctorapp.model.User;
import com.cb.softwares.doctorapp.sharedpreference.SharedPreferenceManager;
import com.cb.softwares.doctorapp.ui.Activity_TAG;
import com.cb.softwares.doctorapp.util.RealPathUtil;
import com.cb.softwares.doctorapp.util.UtilActivity;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cb.softwares.doctorapp.volley.Constants.BOOKING_CANCELLED;
import static com.cb.softwares.doctorapp.volley.Constants.BOOKING_CONFIRMED;
import static com.cb.softwares.doctorapp.volley.Constants.getServerURL;

public class MainActivity extends UtilActivity {


    @BindView(R.id.compactcalendar_view)
    CompactCalendarView calendarView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    SimpleDateFormat format;

    @BindView(R.id.txtTitle)
    TextView txtTitle;

    @BindView(R.id.imgDownArrow)
    ImageView imgArrow;

    @BindView(R.id.txtNoAppointment)
    TextView txtNoAppointment;


    @BindView(R.id.appointmentRecyclerView)
    RecyclerView recyclerView;

    String currentDay;


    private String TAG = "MainActivity";

    AppointmentAdapter adapter;
    ArrayList<AppointmentDetails> list = new ArrayList<>();
    Calendar cal;


    FirebaseUser user;
    DatabaseReference reference;
    FirebaseAuth auth;
    Date currentDate;

    SharedPreferenceManager sharedPreferenceManager;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    Uri sharedDataUri;

    FirebaseRemoteConfig firebaseRemoteConfig;

    private void gotoTagActivity() {
        startActivity(new Intent(MainActivity.this, Activity_TAG.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sharedPreferenceManager = new SharedPreferenceManager(MainActivity.this);


        calendarView.addEvent(new Event(ContextCompat.getColor(this, R.color.primary), 23321323));


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        if (user != null) {
            getSharedFiles(getIntent());
            setup_toolbar(toolbar);


            calendarView.shouldSelectFirstDayOfMonthOnScroll(false);

            format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());


            currentDate = new Date();
            currentDay = (String) DateFormat.format("dd", currentDate);
            changeTitleDate(currentDate);
            clickListenerForCalendarview();
            set_Linearlayout_manager(recyclerView, this);
            setAdapter();

            initialize();

            initializeRemoteConfig();


            Log.w(TAG, "my key " + sharedPreferenceManager.getFCMKey());

            // registerWithMobileNumber();


            if (sharedPreferenceManager.isCompletelySetuped()) {
                updateFCMToken();
            }

            Log.w(TAG, "current date " + format.format(currentDate));

            // getAfterValue();

//            if(sharedPreferenceManager.getCurrentDate().equalsIgnoreCase("")){
//
//            }

            if (sharedPreferenceManager.getUserType().equalsIgnoreCase("u")) {
                getDoctorDetails(false);
            }

        } else {

            startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            MainActivity.this.finish();
        }


        // new sendEmail().execute();

        //   sendMessage();
    }


    private void getSharedFiles(Intent intent) {

        Log.w(TAG, "get files called");

        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendImage(intent);
                //   handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            } else if (type.equals("application/pdf")) {
                handleSendImage(intent);

                Log.w(TAG, "application pdf working");
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
           /* if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }*/
        } else {


        }
    }


    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.w(TAG, "on new intent working");
        getSharedFiles(intent);
    }


    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared


            sharedDataUri = imageUri;
            ContentResolver cR = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();

            String type = mime.getExtensionFromMimeType(cR.getType(imageUri));
            Log.w(TAG, "uri path " + "type  " + type);
            try {
                String path = RealPathUtil.getPath(this, imageUri);
                Log.w(TAG, "real path " + path);


                gotoChat(true);

            } catch (Exception e) {
                Log.w(TAG, "Exception worked");

                Toastmsg(MainActivity.this, "Sorry couldn't get the file");
                e.printStackTrace();
            }


            Log.w(TAG, "shared file path " + imageUri.getAuthority());
        } else {
            Log.w(TAG, "image uri null");
        }
    }

    private void gotoChat(boolean isUriAvailable) {
        if (sharedPreferenceManager.getUserType().equalsIgnoreCase("A")) {
            startActivity(new Intent(MainActivity.this, ChatMainActivity.class).putExtra("isUriAvailable", isUriAvailable).putExtra("uri", sharedDataUri == null ? "empty" : sharedDataUri.toString()));
        } else {
            if (sharedPreferenceManager.getDoctorId().length() > 0) {
                startActivity(new Intent(this, ChatMessageActivity.class).putExtra("isUriAvailable", isUriAvailable).putExtra("uri", sharedDataUri == null ? "empty" : sharedDataUri.toString()).putExtra("name", sharedPreferenceManager.getDoctorName()).putExtra("token", sharedPreferenceManager.getDoctorToken()).putExtra("id", sharedPreferenceManager.getDoctorId()));
            } else {
                Toastmsg(MainActivity.this, "Please wait getting doctor details");
                getDoctorDetails(isUriAvailable);
            }
        }
    }




   /* private void registerWithMobileNumber() {
        String phoneNumber = "+918675505758";
        String smsCode = "123456";

        Log.w(TAG, "regirter with mobile number called");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuthSettings firebaseAuthSettings = firebaseAuth.getFirebaseAuthSettings();

// Configure faking the auto-retrieval with the whitelisted numbers.
        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber, smsCode);

        PhoneAuthProvider phoneAuthProvider = PhoneAuthProvider.getInstance();
        phoneAuthProvider.verifyPhoneNumber(
                phoneNumber,
                60L,
                TimeUnit.SECONDS,
                this, *//* activity *//*
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {


                        Log.w(TAG, "on verification completed");
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                    }


                });


    }*/


    public void getDoctorDetails(final boolean isUriAvailable) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = reference.orderByChild("type").equalTo("A");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapShot : dataSnapshot.getChildren()) {

                    User user = snapShot.getValue(User.class);
                    sharedPreferenceManager.setDoctorName(user.getName());
                    sharedPreferenceManager.setDoctorID(user.getId());
                    sharedPreferenceManager.setDoctorUserID(user.getUserid());
                    sharedPreferenceManager.setDoctorToken(user.getToken());
                    Log.w(TAG, "doctor data " + user.getToken() + user.getId() + "" + user.getUserid());
                    break;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        deleteMesages();
        super.onBackPressed();
    }


    private void deleteMesages() {

        Log.w(TAG, "delete called");


        /*DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        Query applesQuery = ref.orderByChild("sender").equalTo(user.getUid());

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });*/
    }

    public void getAfterValue() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        Query query = reference.orderByChild("id").startAt(1);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.w(TAG, " get after count " + dataSnapshot.getChildrenCount());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat chat = snapshot.getValue(Chat.class);

                    Log.w(TAG, "id " + chat.getId());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void updateFCMToken() {

        if (!sharedPreferenceManager.isFCMKeyUpdated()) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                HashMap<String, Object> map = new HashMap<>();
                map.put("token", sharedPreferenceManager.getFCMKey());
                reference.updateChildren(map);

                sharedPreferenceManager.setISFCMKEYUPDATED(true);
            }

        } else {
            Log.w(TAG, "else part working");
        }
    }


    private void initializeRemoteConfig() {


        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings.Builder().build();
        firebaseRemoteConfig.setConfigSettings(settings);
        firebaseRemoteConfig.fetch(0).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Log.w(TAG, "task success");
                    firebaseRemoteConfig.activateFetched();
                } else {
                    Log.w(TAG, "task failure");
                }


                getValuesFromRemoteConfig();

            }
        });


    }

    private void getValuesFromRemoteConfig() {


        String startTime = firebaseRemoteConfig.getString("start_time");
        String endTime = firebaseRemoteConfig.getString("end_time");
        String breakTime = firebaseRemoteConfig.getString("break");


        Log.w(TAG, "start time " + startTime + " end time " + endTime);

        sharedPreferenceManager.setStartTime(startTime);
        sharedPreferenceManager.setEndTime(endTime);
        sharedPreferenceManager.setBreakTime(breakTime);
    }


    private void initialize() {

        Log.w(TAG, "initialize call");


        if (sharedPreferenceManager.getUserType().equalsIgnoreCase("empty")) {
            Log.w(TAG, "initialize call inside if");
            reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

            getAppointmentsDatesForCalendar();

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    Log.w(TAG, "initialize call usersType()" + user.getType());
                    sharedPreferenceManager.setUserType(user.getType());
                    supportInvalidateOptionsMenu();

                    calLApi();

                    sharedPreferenceManager.setISCOMPLETELY_SETUPED(true);
                    updateFCMToken();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "initialize call on cancel working");
                }
            });

        } else {

            if (sharedPreferenceManager.getUserType().equalsIgnoreCase("A")) {
                getAppointmentsDatesForCalendar();
            }

            calLApi();
        }


    }

    private void calLApi() {

        Log.w(TAG, "user type " + sharedPreferenceManager.getUserType());

        showView(progressBar);
        if (sharedPreferenceManager.getUserType().equalsIgnoreCase("U")) {

            getAppointments("userid", user.getUid());
        } else if (sharedPreferenceManager.getUserType().equalsIgnoreCase("A")) {

            getAppointments("date", format.format(currentDate));
        }
    }


    private long getMilliseconds(String myDate) {


        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date date = sdf.parse(myDate);
            return date.getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    private void getAppointments(String key, String value) {
        Log.w(TAG, "call get appointments " + key + " value " + value);
        Query query = FirebaseDatabase.getInstance().getReference("Appointments").orderByChild(key).equalTo(value);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();
                Log.w(TAG, "count " + dataSnapshot.getChildrenCount());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AppointmentDetails details = snapshot.getValue(AppointmentDetails.class);
                    Log.w(TAG, "date " + details.getDate());
                    //  calendarView.addEvent(new Event(R.color.icons, getMilliseconds(details.getDate())));
                    list.add(details);
                }

                if (list.size() > 0) {
                    goneView(txtNoAppointment);
                    if (adapter == null) {
                        setAdapter();
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    showView(txtNoAppointment);
                }

                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });


    }


    private void getAppointmentsDatesForCalendar() {

        DatabaseReference query = FirebaseDatabase.getInstance().getReference("Appointments");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.w(TAG, "appointments count " + dataSnapshot.getChildrenCount());

                calendarView.removeAllEvents();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AppointmentDetails details = snapshot.getValue(AppointmentDetails.class);

                    Log.w(TAG, "date of apointment " + details.getDate());

                    calendarView.addEvent(new Event(Color.parseColor("#F44336"), getMilliseconds(details.getDate())));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void alertForCancelAppointment(final AppointmentDetails appointmentDetails, final int pos) {
        new AlertDialog.Builder(this, R.style.AlertTheme)
                .setTitle("Cancel Appointment")
                .setMessage("Are you sure you want to cancel this appointment?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        cancelAppointment(appointmentDetails, pos);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private void cancelAppointment(AppointmentDetails appointmentDetails, int pos) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Appointments").child(appointmentDetails.getId());

        //removing artist
        dR.removeValue();

        sendServerRequest(getServerURL(appointmentDetails.getMobile(), BOOKING_CANCELLED, appointmentDetails.getDate() + " " + appointmentDetails.getTime()));

        try {

            list.remove(pos);
            adapter.notifyDataSetChanged();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        Toastmsg(MainActivity.this, "Your appointment has been cancelled");
    }


    public void updateStatus(boolean val, String id, int pos) {

        Log.w(TAG, "id " + id + " val " + val);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Appointments").child(id);
        HashMap<String, Object> map = new HashMap<>();
        map.put("isConfirmedByDoctor", val);

        reference.updateChildren(map);



        sendServerRequest(getServerURL((String)map.get("mobile"), BOOKING_CONFIRMED, map.get("date") + " " + map.get("time")));

        Toastmsg(MainActivity.this, "Successfully confirmed request");
    }

    private void setAdapter() {
        adapter = new AppointmentAdapter(this, list, MainActivity.this, sharedPreferenceManager.getUserType());
        recyclerView.setAdapter(adapter);
    }


    private void clickListenerForCalendarview() {
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Log.w(TAG, "date clicked" + format.format(dateClicked));
                Date selectedDate = null;

                selectedDate = dateClicked;
                Log.w(TAG, "selectedDate" + selectedDate);
                String selectedAsString = format.format(dateClicked);
                String todayAsString = format.format(currentDate);

                calendarView.setSelected(false);


                if (selectedDate.before(currentDate)) {
                    if (selectedAsString.equalsIgnoreCase(todayAsString)) {
                        Log.w(TAG, "getUserType" + sharedPreferenceManager.getUserType());
                        if (sharedPreferenceManager.getUserType().equalsIgnoreCase("U")) {

                            startActivity(new Intent(MainActivity.this, BookAppointmentActivity.class).putExtra("date", format.format(dateClicked)));
                        } else if (sharedPreferenceManager.getUserType().equalsIgnoreCase("A")) {
                            showView(progressBar);
                            getAppointments("date", format.format(dateClicked));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Sorry can't book the appointment,please select valid date", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    System.out.println("" + selectedDate + " is OK; going to next activity");
                    System.out.println("" + sharedPreferenceManager.getUserType() + " getUserType");
                    Log.w(TAG, "getUserType" + sharedPreferenceManager.getUserType());
                    if (sharedPreferenceManager.getUserType().equalsIgnoreCase("U")) {

                        startActivity(new Intent(MainActivity.this, BookAppointmentActivity.class).putExtra("date", format.format(dateClicked)));
                    } else if (sharedPreferenceManager.getUserType().equalsIgnoreCase("A")) {
                        showView(progressBar);
                        getAppointments("date", format.format(dateClicked));
                    }

                }
            }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {


                Log.w(TAG, "month scroll working" + format.format(firstDayOfNewMonth));
                changeTitleDate(firstDayOfNewMonth);
            }
        });
    }


    @OnClick(R.id.txtTitle)
    public void showCalendar() {

        showHideCalendar();
    }

    @OnClick(R.id.imgDownArrow)
    public void clickListenerImage() {

        showHideCalendar();
    }

    MenuItem calendarDate;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        calendarDate = menu.findItem(R.id.calendarDate);
        View actionView = (View) calendarDate.getActionView();
        TextView txtDate = (TextView) actionView.findViewById(R.id.cart_badge);
        txtDate.setText(currentDay);


        MenuItem tag = menu.findItem(R.id.tags);
        Log.w(TAG, "options get users " + sharedPreferenceManager.getUserType());

        if (sharedPreferenceManager.getUserType().equalsIgnoreCase("A")) {
            tag.setVisible(true);
       }


        Log.w(TAG, "curent day " + currentDay);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.logout) {
            user = auth.getCurrentUser();

            if (user != null) {
                auth.signOut();
                googleClientSignOut();

                // user.delete();
                sharedPreferenceManager.setISFCMKEYUPDATED(false);
                Log.w(TAG, "signout call set userType empty" + sharedPreferenceManager.getUserType());
                sharedPreferenceManager.setUserType("empty");
                sharedPreferenceManager.setISCOMPLETELY_SETUPED(false);
                startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                MainActivity.this.finish();
            }
            return true;
        } else if (item.getItemId() == R.id.chat) {
           /* if (sharedPreferenceManager.getUserType().equalsIgnoreCase("A")) {
                startActivity(new Intent(MainActivity.this, ChatMainActivity.class));
            } else {
                if (sharedPreferenceManager.getDoctorId().length() > 0) {
                    startActivity(new Intent(this, ChatMessageActivity.class).putExtra("name", sharedPreferenceManager.getDoctorName()).putExtra("token", sharedPreferenceManager.getDoctorToken()).putExtra("id", sharedPreferenceManager.getDoctorId()));
                } else {
                    Toastmsg(MainActivity.this, "Please wait getting doctor details");
                    getDoctorDetails();
                }
            }*/

            gotoChat(false);

        } else if (item.getItemId() == R.id.tags) {
            gotoTagActivity();
        }
        return super.onOptionsItemSelected(item);

    }

    private void showHideCalendar() {


        if (calendarView.isShown()) {
            goneView(calendarView);
            imgArrow.setImageResource(R.drawable.ic_down_arrow);
        } else {
            showView(calendarView);
            imgArrow.setImageResource(R.drawable.ic_up_arrow);
        }
    }


    private void changeTitleDate(Date currentDate) {
        String monthString = (String) DateFormat.format("MMM", currentDate);
        String year = (String) DateFormat.format("yyyy", currentDate);


        String date = monthString + " - " + year;
        txtTitle.setText(date);

    }


}
