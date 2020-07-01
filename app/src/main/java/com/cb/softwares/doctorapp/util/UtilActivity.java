package com.cb.softwares.doctorapp.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.sharedpreference.SharedPreferenceManager;
import com.cb.softwares.doctorapp.volley.VolleyController;
import com.cb.softwares.doctorapp.volley.VolleyStringResponseListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;


/**
 * Created by DELL on 10-01-2018.
 */

public class UtilActivity extends AppCompatActivity {

    private String TAG = UtilActivity.class.getSimpleName();

    ProgressDialog pDialog;
    public SpannableString s;

    int year, month, day;
    private BlockingDeque queue = new LinkedBlockingDeque();


    Date startedTime, endedTime;


    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    private String location_string;
    private static LocationManager locationManager;

    boolean isGPSEnabled = false;
    boolean canGetLocation = false;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 0 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 3000;

    public GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initGoogleSignInOptions();
    }

    public void setup_toolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.foot);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // toolbar_text_style();
        // toolbar.setTitle(s);
    }

    public void initGoogleSignInOptions() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void googleClientSignOut() {
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }


    public void setup_toolbar(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(title);
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.foot);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // toolbar_text_style();
        // toolbar.setTitle(s);
    }

    @SuppressLint("DefaultLocale")
    public String generateRandomNumber() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    public void showView(View view) {
        if (!view.isShown()) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public void goneView(View view) {
        if (view.isShown()) {
            view.setVisibility(View.GONE);
        }
    }

    public void invisibleView(View view) {
        if (!view.isShown()) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public void setup_toolbar_with_back(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.foot);
        //  getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.img_back));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // toolbar_text_style();
        // toolbar.setTitle(s);

        getSupportActionBar().setTitle(title);
    }

    public void setup_toolbar_with_back(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.foot);
        //  getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.img_back));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // toolbar_text_style();
        // toolbar.setTitle(s);

    }


    //make dir
    public void makeDir() {
        File newDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/compressimage");
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
    }

    public void createTextFile(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/compressimage/");
            //  File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();

            //  Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reWriteTextFile(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/compressimage/");
            //  File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            StringBuilder text = new StringBuilder();

            if (gpxfile.exists()) {

                BufferedReader br = new BufferedReader(new FileReader(gpxfile));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();


                sBody = text + "\n" + sBody;
                write(gpxfile, sBody);

            } else {
                write(gpxfile, sBody);
            }
            //  Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void write(File file, String sBody) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




  /*  public void askPermission(String[] permission) {
        askCompactPermissions(permission, new PermissionResult() {
            @Override
            public void permissionGranted() {

            }

            @Override
            public void permissionDenied() {

            }

            @Override
            public void permissionForeverDenied() {

            }
        });
    }

    public boolean checkPermissionGranted(String permission, Context context) {
        return isPermissionGranted(context, permission);
    }*/

    public String getExtensionType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

   /* //check permissions
    public void askAllPermission() {
        askCompactPermissions(new String[]{PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION, PermissionUtils.Manifest_READ_PHONE_STATE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_SEND_SMS, PermissionUtils.Manifest_READ_CONTACTS, PermissionUtils.Manifest_RECORD_AUDIO}, new PermissionResult() {
            @Override
            public void permissionGranted() {
                //permission granted
                //replace with your action
            }

            @Override
            public void permissionDenied() {
                //permission denied
                //replace with your action
            }

            @Override
            public void permissionForeverDenied() {
                // user has check 'never ask again'
                // you need to open setting manually
                //  Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                //  Uri uri = Uri.fromParts("package", getPackageName(), null);
                //   intent.setData(uri);
                //  startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
            }
        });
    }*/


 /*   GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    LocationSettingsRequest.Builder locationSettingsRequest;

    public void mEnableGps() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(UtilActivity.this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        mLocationSetting();
    }

    public void mLocationSetting() {

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);

        locationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        mResult();

    }

    PendingResult<LocationSettingsResult> pendingResult;

    public static final int REQUEST_LOCATION = 001;

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private String TAG = "Util";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_LOCATION) {
            try {
                final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
            } catch (Exception e) {

            }
            switch (requestCode) {
                case REQUEST_LOCATION:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            // All required changes were successfully made
                            Log.w(TAG, "Gps enabled");
                            break;
                        case Activity.RESULT_CANCELED:
                            // The user was asked to change settings, but chose not to
                            Log.w(TAG, "Gps not enabled");
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
    }


    public void mResult() {
        pendingResult = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, locationSettingsRequest.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();


                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {

                            status.startResolutionForResult(UtilActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.


                        break;
                }
            }

        });
    }*/

    //check external storage permission
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean check_external_storage_permission(Context context) {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //File write logic here
            return true;
        } else {
            return false;
        }
    }

    public void set_Linearlayout_manager(RecyclerView recyclerView, Context context) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    //set horizontal layout manager
    public void set_horizontal_layout_manager(RecyclerView recyclerView, Context context) {

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    }

    public void set_grid_layout_manager(RecyclerView recyclerView, Context context, int count) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, count));
    }

    //get file type
    public String getFileType(String path) {
        return path.substring(path.lastIndexOf(".") + 1, path.length());
    }

    //get mime type
    public String getMimeType(Uri uri) {
        String type;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getApplicationContext().getContentResolver();
            type = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        type = type.substring(type.indexOf("/") + 1, type.length());
        return type;
    }


    public int quality(String path) {
        File file = new File(path);
        int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
        //Log.w(TAG, "file size : " + file_size);
        if (file_size < 2048) {
            return 20;
        } else {
            return 10;
        }

    }

    //Convert bitmap into string
    public String getStringImage(Bitmap bmp, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }


/*
    public void alert_for_no_internet() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.noInternet))
                .setMessage(getResources().getString(R.string.makeSureInternet))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                // .setIcon(R.mipmap.ic_person_add_black_24dp)
                .show();
    }
*/

   /* //set toolbar text style
    public void toolbar_text_style() {
        s = new SpannableString(getResources().getString(R.string.gallery_activity));
        Typeface type = Typeface.createFromAsset(getAssets(), "GeosansLight.ttf");
        s.setSpan(new CustomTypefaceSpan(type), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }*/

    public void Toastmsg(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void Snackbar_msg(View v, String msg) {
        Snackbar.make(v, msg, Snackbar.LENGTH_LONG).show();
    }


    /*public void show_dialog(Context context) {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(getResources().getString(R.string.pdialogmsg));
        pDialog.setCancelable(false);
        pDialog.show();
    }*/

    public String get_current_date() {

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        StringBuilder string = new StringBuilder()
                // Month is 0 based so add 1
                .append(day).append("-")
                .append(month + 1).append("-")
                .append(year);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        return string.toString();

    }


    public String get_current_time() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
        return sdf.format(d);
    }


    public String getTomorrow() {
        String oldDate = get_current_date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(oldDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Incrementing the date by 1 day
        c.add(Calendar.DAY_OF_MONTH, 1);
        return sdf.format(c.getTime());

    }

    public void dismiss_dialog() {
        pDialog.dismiss();
    }

    public int generate_random_number() {
        Random rand = new Random();
        return rand.nextInt(20);
    }

    /**
     * Function to show settings alert dialog
     */
/*
    public void showSettingsAlert(final Context mContext) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle(getResources().getString(R.string.gpsSettings));

        // Setting Dialog Message
        alertDialog.setMessage(getResources().getString(R.string.gpsmsg));

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton(getResources().getString(R.string.action_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
*/
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public boolean isValidMobile(String phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }

    /*check gps enabled or not*/
    public boolean is_enabled_gps(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        // getting GPS status
        assert locationManager != null;
        return locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    public Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        return Bitmap.createBitmap(image, 0, 0, width, height, matrix, false);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

/*
    public void getLocation() {
        try {

            if (locationManager == null) {
                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            }
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                Log.w(TAG, "GPS Enabled");
                canGetLocation = true;
                isGPSEnabled = true;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        //if (latitude == 0L && longitude != 0L) {


                        Log.w(TAG, "Get location working");
                        //  publishMessage(latitude + "," + longitude);

                      */
/*  LatLng myLocation = new LatLng(latitude, longitude);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                                myLocation).zoom(18).build();
                        map.clear();

                        map.addMarker(new MarkerOptions().position(myLocation)
                                .title("My Location"));
                        //   map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
                        //   map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*//*


                        Log.w(TAG, " Latitude " + latitude + " Longitude " + longitude);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/


    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }


    public void updateStatus(String status) {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (sharedPreferenceManager.isCompletelySetuped()) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                HashMap<String, Object> map = new HashMap<>();
                map.put("status", status);
                reference.updateChildren(map);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateStatus("Offline");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public void sendServerRequest(String url) {
        try {
            VolleyController.sendServerRequest(this, url, new VolleyStringResponseListener() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Response : " + response);
                }

                @Override
                public void onError(VolleyError volleyError) {
                    Log.d(TAG, "VolleyError : " + volleyError.networkResponse);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
