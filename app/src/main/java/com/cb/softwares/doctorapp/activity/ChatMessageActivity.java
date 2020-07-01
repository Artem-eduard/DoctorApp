package com.cb.softwares.doctorapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.adapter.MessageAdapter;
import com.cb.softwares.doctorapp.database.MyDatabase;
import com.cb.softwares.doctorapp.interfaces.DeleteMessageClick;
import com.cb.softwares.doctorapp.model.Chat;
import com.cb.softwares.doctorapp.model.ChatDetails;
import com.cb.softwares.doctorapp.model.User;
import com.cb.softwares.doctorapp.model.UserForChatDetails;
import com.cb.softwares.doctorapp.notification.APIService;
import com.cb.softwares.doctorapp.notification.Client;
import com.cb.softwares.doctorapp.notification.Data;
import com.cb.softwares.doctorapp.notification.MyResponse;
import com.cb.softwares.doctorapp.notification.Sender;
import com.cb.softwares.doctorapp.sharedpreference.SharedPreferenceManager;
import com.cb.softwares.doctorapp.util.FileUtils;
import com.cb.softwares.doctorapp.util.RealPathUtil;
import com.cb.softwares.doctorapp.util.UtilActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.necistudio.libarary.FilePickerActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatMessageActivity extends UtilActivity implements TextWatcher, DeleteMessageClick {


    @BindView(R.id.edtMessage)
    EditText edtMessage;
    @BindView(R.id.btnSend)
    ImageView btnSend;

    @BindView(R.id.txtName)
    TextView txtName;

    @BindView(R.id.txtStatus)
    TextView txtStatus;

    @BindString(R.string.online)
    String online;
    @BindString(R.string.offline)
    String offline;

    @BindView(R.id.messageRecyclerView)
    RecyclerView recyclerView;


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private String TAG = "ChatActivity";

    String username = "Chat";
    String receiver = "";
    String token = "empty";

    FirebaseAuth auth;
    FirebaseUser user;


    DatabaseReference reference;

    MessageAdapter adapter;
    ArrayList<Chat> list = new ArrayList<>();


    ValueEventListener seenListener;

    APIService apiService;
    boolean notify = false;


    long maxId;

    boolean isExistInChatList = false;

    MyDatabase db;

    private long lastAutoID = 0;
    private int limit = 100;

    String key;


    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.btnAttachment)
    ImageView btnAttachment;

    @BindView(R.id.btnImage)
    ImageView btnImage;

    Uri sharedDataUri;
    boolean isUriAvailable;

    SharedPreferenceManager sharedPreferenceManager;

    private static int firstVisibleInListview;


    private static int WRITE_PERMISSION = 123;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {


            isUriAvailable = getIntent().getExtras().getBoolean("isUriAvailable");

            if (isUriAvailable) {

                sharedDataUri = Uri.parse(getIntent().getExtras().getString("uri"));


                Toastmsg(ChatMessageActivity.this, "Please wait sending your file");

            }

            username = getIntent().getExtras().getString("name");
            receiver = getIntent().getExtras().getString("id");
            token = getIntent().getExtras().getString("token");
        }


        db = MyDatabase.getDatabase(this);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference();

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        sharedPreferenceManager = new SharedPreferenceManager(this);

        setup_toolbar_with_back(toolbar);

        txtName.setText(username);


        setLayoutManager();
        setAdapter();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        edtMessage.addTextChangedListener(this);

        getMessageCount();

        if (sharedPreferenceManager.getUserType().equalsIgnoreCase("A")) {

            key = user.getUid() + receiver;
            getStatus();


        } else {
            seenMessage(receiver);
            key = sharedPreferenceManager.getDoctorId() + user.getUid();
        }


        showView(progressBar);
        readMessage(user.getUid(), receiver, limit);
        // new getData().execute();


    }


    private void getMessageCount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");


        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.w(TAG, "message count " + dataSnapshot.getChildrenCount());


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    maxId = chat.getId();
                }

              /*  if (dataSnapshot.exists()) {
                    maxId = dataSnapshot.getChildrenCount();

                }*/

                sendSharedData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.btnAttachment)
    public void addAttachment() {
        performFileSearch();
    }


    private static final int REQUEST_FOR_IMAGE = 12;

    @OnClick(R.id.btnImage)
    public void pickImage() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            askPermission();
            // Permission is not granted
            // Should we show an explanation?
           /* if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION
                );

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }*/
        } else {
            // Permission has already been granted
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_FOR_IMAGE);
        }


    }


    private static final int READ_REQUEST_CODE = 42;


    public void performFileSearch() {


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            askPermission();
            // Permission is not granted
            // Should we show an explanation?
           /* if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION
                );

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }*/
        } else {
            // Permission has already been granted
            Intent intent = new Intent(getApplicationContext(), FilePickerActivity.class);
            startActivityForResult(intent, READ_REQUEST_CODE);
        }







        /* Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, READ_REQUEST_CODE);*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().

            String path = data.getStringExtra("path");
            if (path != null) {

                String fileName = receiver + new Date().toString() + path.substring(path.lastIndexOf("."), path.length());


                notify = true;
                sendAttachment(user.getUid(), receiver, fileName, path, "doc");


            } else {
                Toastmsg(ChatMessageActivity.this, "Can't select file try again later");
            }


            Log.w(TAG, "path " + path);
           /* Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();

                String path1 = resultData.getData().getPath();

                if (path1 != null) {
                    uploadFileToServer(path1);
                } else {
                    uploadFileToServer(uri.getPath());
                }

                Log.w(TAG, "path 1 " + path1);

               *//* Log.w(TAG,"path 1 "+ path1);
                try {
                    String path = FileUtils.getPath(this, uri);
                    Log.w(TAG, "path  " + path);
                    if (path != null) {
                        uploadFileToServer(path);
                    } else {
                        uploadFileToServer(uri.getPath());
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }*//*
                Log.w(TAG, "Uri: " + uri.getPath());
                // showImage(uri);
            }*/
        } else if (requestCode == REQUEST_FOR_IMAGE && resultCode == Activity.RESULT_OK) {

            Uri uri = null;
            if (data != null) {
                uri = data.getData();

                //  String path1 = data.getData().getPath();
                try {
                    String path = FileUtils.getPath(this, uri);

                    Log.w(TAG, "path  " + path);

                    if (path != null) {

                        String fileName = receiver + new Date().toString() + path.substring(path.lastIndexOf("."), path.length());


                        notify = true;
                        sendAttachment(user.getUid(), receiver, fileName, path, "image");


                    } else {
                        Toastmsg(ChatMessageActivity.this, "Can't select file try again later");
                    }

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }


                // showImage(uri);
            }

        }
    }


    private void uploadFileToServer(String filePath, final String fileName, final long id, String type) {


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference mountainsRef = storageRef.child(fileName);

        StorageReference mountainImagesRef = storageRef.child("images/" + fileName);

        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());

        if (type.equalsIgnoreCase("image")) {

            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Log.w(TAG, "on upload success ");
                    try {
                        Log.w(TAG, " success id " + id);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(String.valueOf(id));
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("isProgressing", "false");
                        reference.updateChildren(map);

                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                }
            });


        } else {

            try {
                InputStream stream = new FileInputStream(new File(filePath));

                UploadTask uploadTask = mountainsRef.putStream(stream);
                uploadTask.addOnFailureListener(new OnFailureListener() {


                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.w(TAG, "on upload failure ");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Log.w(TAG, "on upload success ");

                        try {
                            Log.w(TAG, " success id " + id);

                            String itemId = id + "";
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(itemId);

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("isProgressing", "false");
                            reference.updateChildren(map);


                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.w(TAG, "on progress working " + taskSnapshot.getBytesTransferred());
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        /*imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();*/
       /* Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });*/


    }


    private static final int STORAGE_PERMISSION = 12;


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toastmsg(ChatMessageActivity.this, "Please enable this permission");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    private void askPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION
        );

    }

    public void downloadFile(final int pos) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            askPermission();
            // Permission is not granted
            // Should we show an explanation?
           /* if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }*/
        } else {


            final Chat chat = list.get(pos);

          /*  chat.setIsDownloading("true");
            adapter.notifyDataSetChanged();*/
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            final StorageReference ref = storageRef.child(chat.getMessage());

            File mkDir = new File(Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name));

            if (!mkDir.exists()) {
                mkDir.mkdir();
            }
            final File file = new File(mkDir, chat.getMessage());

            ref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    chat.setIsDownloaded("true");
                    chat.setIsProgressing("false");
                    chat.setReceiverLocalPath(file.getAbsolutePath());

                    adapter.notifyItemChanged(pos);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(String.valueOf(chat.getId()));
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("isDownloaded", "true");
                    map.put("receiverLocalPath", file.getAbsolutePath());
                    reference.updateChildren(map);
                    Log.w(TAG, "on successfully download " + file.getAbsolutePath());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "on failure download");
                }
            });
        }


    }

    public void openFile(int pos) {


        Chat chat = list.get(pos);

        boolean isSender = false;
        File file = null;
        if (chat.getSender().equalsIgnoreCase(user.getUid())) {
            file = new File(chat.getSenderLocalPath());
            isSender = true;
        } else {
            isSender = false;
            file = new File(chat.getReceiverLocalPath());
        }
        // Get URI and MIME type of file
        if (file.exists()) {
            Uri uri = FileProvider.getUriForFile(this, "com.cb.softwares.doctorapp.fileprovider", file);
            String mime = getContentResolver().getType(uri);

            // Open file with user selected app
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mime);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {
            Toastmsg(ChatMessageActivity.this, "File nt found");
            if (!isSender) {
                showAlertDialog(pos);
            }
        }
    }


    private void showAlertDialog(final int pos) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AlertTheme);

        // Setting Dialog Title
        alertDialog.setTitle("File Not Found...");

        // Setting Dialog Message
        alertDialog.setMessage("Do you want to download this file again?");

        // Setting Icon to Dialog

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // Write your code here to invoke YES event
                downloadFile(pos);
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event

                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }


    private void setLayoutManager() {
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int currentFirstVisible = manager.findFirstVisibleItemPosition();

                if (currentFirstVisible > firstVisibleInListview)
                    Log.w("RecyclerView scrolled: ", "scroll up!");
                else
                    Log.w("RecyclerView scrolled: ", "scroll down!");

                firstVisibleInListview = currentFirstVisible;


                Log.w(TAG, "visible pos " + firstVisibleInListview);


            }


            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

       /* if (s.length() > 0) {

            updateStatus("Typing.....");
        } */
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.w(TAG, "on after changed working " + s);
        // updateStatus("Online");

        if (s.length() > 0) {

            if (!sharedPreferenceManager.getUserType().equalsIgnoreCase("A")) {
                updateStatus("Typing.....");
            }

            showView(btnSend);
            goneView(btnAttachment);
            goneView(btnImage);
        } else {
            if (!sharedPreferenceManager.getUserType().equalsIgnoreCase("A")) {
                updateStatus("Online");
            }
            showView(btnAttachment);
            showView(btnImage);
            goneView(btnSend);
        }


    }

    @Override
    public void select(int pos) {
        Chat chat = list.get(pos);
        if (!chat.isSelected()) {
            chat.setSelected(true);
            deleteList.add(chat.getId());
            adapter.notifyItemChanged(pos);
        }
    }

    @Override
    public void unSelect(int pos) {
        Chat chat = list.get(pos);
        if (chat.isSelected()) {
            deleteList.remove(chat.getId());
            chat.setSelected(false);
            adapter.notifyItemChanged(pos);

            if (deleteList.size() == 0) {
                clearMenu();
            }
        }
    }


    @Override
    public void onSelectEnable(int pos) {
        Chat chat = list.get(pos);
        if (!chat.isSelected()) {
            isInActionMode = true;
            chat.setSelected(true);
            adapter.notifyItemChanged(pos);

            deleteList.add(chat.getId());


            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.delete_msg_menu);
        }
    }

    private void clearMenu() {
        toolbar.getMenu().clear();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete:
                for (int i = 0; i <= deleteList.size() - 1; i++) {
                    reference.child(deleteList.get(i) + "").removeValue();
                }


                deleteList.clear();
                isInActionMode = false;
                clearMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }


    ArrayList<Long> deleteList = new ArrayList<>();

    public boolean isInActionMode = false;

    private class getData extends AsyncTask<Void, Void, List<ChatDetails>> {
        @Override
        protected List<ChatDetails> doInBackground(Void... voids) {
            db.doctorDao().updateSeenMessage(receiver, true);

            lastAutoID = db.doctorDao().getAutoId();

            Log.w(TAG, "last auto id " + lastAutoID);


            return db.doctorDao().getChatMessages();
        }

        @Override
        protected void onPostExecute(List<ChatDetails> chatDetails) {
            super.onPostExecute(chatDetails);
            for (ChatDetails details : chatDetails) {

                //  Chat chat = new Chat(details.getSender(), details.getReceiver(), details.getMessage(), details.isIsseen(), details.getTime().toString(), details.getId());

                //  list.add(chat);
            }

            if (list.size() > 0) {
                adapter.notifyDataSetChanged();
            }

            Log.w(TAG, "get local mesages count " + chatDetails.size());


        }


    }


    private void seenMessage(final String id) {

        Log.w(TAG, "seen messsage called");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");


        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.w(TAG, "seen count " + dataSnapshot.getChildrenCount());

                //   maxId = dataSnapshot.getChildrenCount();

                sendSharedData();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    Log.w(TAG, "receiver  " + chat.getReceiver() + " sender " + id);
                    if (chat.getReceiver() != null) {
                        if (chat.getReceiver().equalsIgnoreCase(user.getUid())) {

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("isseen", true);
                            snapshot.getRef().updateChildren(map);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void getStatus() {

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("id").equalTo(receiver);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                Log.w(TAG, "user count " + dataSnapshot.getChildrenCount());


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    User user = snapshot.getValue(User.class);

                    if (user != null) {

                        token = user.getToken();

                        if (user.getStatus().equalsIgnoreCase("Online")) {


                            txtStatus.setText(online);
                        } else {
                            txtStatus.setText(user.getStatus());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAdapter() {

        adapter = new MessageAdapter(this, list, user.getUid(), ChatMessageActivity.this);
        recyclerView.setAdapter(adapter);
    }


    @OnClick(R.id.btnSend)
    public void send() {
        if (!edtMessage.getText().toString().trim().isEmpty()) {

            notify = true;
            sendMessage(user.getUid(), receiver, edtMessage.getText().toString().trim());
        }
    }


    ArrayList<DatabaseReference> referenceList = new ArrayList<>();

    private void sendAttachment(final String sender, final String receiver, String message, String localPath, String type) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        HashMap<String, Object> map = new HashMap<>();
        map.put("sender", sender);
        map.put("receiver", receiver);
        map.put("senderLocalPath", localPath);
        map.put("receiverLocalPath", "empty");
        map.put("type", "A");
        map.put("isProgressing", "true");
        map.put("isDownloaded", "false");
        map.put("message", message);
        map.put("isseen", false);
        map.put("key", key);
        map.put("isDownloading", "false");

        Date date = new Date();

        map.put("time", date.toString());

        maxId = maxId + 1;
        long id = maxId;


        Log.w(TAG, "max id " + maxId);

        map.put("id", id);
        reference.child(String.valueOf(maxId)).setValue(map);


        final String msg = message;


        uploadFileToServer(localPath, message, id, type);
        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotification(receiver, user.getName(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(String sender, final String receiver, String message) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        HashMap<String, Object> map = new HashMap<>();
        map.put("sender", sender);
        map.put("receiver", receiver);
        map.put("type", "T");
        map.put("message", message);
        map.put("isseen", false);
        map.put("key", key);

        Date date = new Date();

        map.put("time", date.toString());
        maxId = maxId + 1;
        long id = maxId;
        Log.w(TAG, "max Id send message  " + maxId);


        map.put("id", id);
        reference.child(String.valueOf(id)).setValue(map);
        edtMessage.getText().clear();

        final String msg = message;


        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotification(receiver, user.getName(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        hideKeyboard(ChatMessageActivity.this);

    }


    private class InsertChatMessage extends AsyncTask<UserForChatDetails, Void, Void> {
        @Override
        protected Void doInBackground(UserForChatDetails... details) {


            UserForChatDetails chatDetails = details[0];

            int count = db.doctorDao().getCount(receiver);
            if (count == 1) {
                db.doctorDao().changeLastMsg(chatDetails.getLastMsg(), chatDetails.getTime(), receiver);
            } else {
                UserForChatDetails userForChatDetails = new UserForChatDetails(receiver, txtName.getText().toString().trim(), token, chatDetails.getLastMsg(), 0, chatDetails.getTime());
                db.doctorDao().insertChatDetails(userForChatDetails);
                isExistInChatList = true;
            }
            // db.doctorDao().insertChatMessage(chatDetails);
            return null;
        }
    }


    private void sendNotification(final String receiver, final String name, final String msg) {

        Data data = new Data(user.getUid(), R.mipmap.ic_launcher, name + " : " + msg, "New Message", receiver, 1);

        Sender sender = new Sender(data, token);


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


    SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

    private void readMessage(final String myUid, final String userId, final int limit) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = reference.orderByChild("key").equalTo(key);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.w(TAG, "server child count " + dataSnapshot.getChildrenCount());

                goneView(progressBar);
                list.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    list.add(chat);
                }

                if (list.size() > 0) {
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(list.size() - 1);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                goneView(progressBar);
            }
        });
    }

    private void sendSharedData() {
        if (isUriAvailable) {

            if (sharedDataUri != null) {
                String path = RealPathUtil.getPath(ChatMessageActivity.this, sharedDataUri);
                Log.w(TAG, "uri file path " + path);

                if (path != null) {

                    ContentResolver cR = getContentResolver();
                    MimeTypeMap mime = MimeTypeMap.getSingleton();

                    String type = mime.getExtensionFromMimeType(cR.getType(sharedDataUri));
                    String fileName = receiver + new Date().toString() + path.substring(path.lastIndexOf("."), path.length());
                    notify = true;
                    try {
                        switch (type) {
                            case "jpg":
                            case "jpeg":
                            case "png":
                                sendAttachment(user.getUid(), receiver, fileName, path, "image");
                                break;
                            default:
                                sendAttachment(user.getUid(), receiver, fileName, path, "doc");

                        }

                        sharedDataUri = null;
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toastmsg(ChatMessageActivity.this, "Can't select file try again later");
                }


            }

            isUriAvailable = false;
        }

    }


    @Override
    protected void onPause() {
        super.onPause();

        Log.w(TAG, "on pause working");

        if (sharedPreferenceManager.getUserType().equalsIgnoreCase("A")) {
            if (list.size() > 0) {
                Chat chat = list.get(list.size() - 1);
                try {
                    UserForChatDetails details;

                    if (chat.getType().equalsIgnoreCase("T")) {
                        details = new UserForChatDetails(receiver, txtName.getText().toString().trim(), token, chat.getMessage(), 0, format.parse(chat.getTime()));
                    } else {
                        details = new UserForChatDetails(receiver, txtName.getText().toString().trim(), token, "Attachment", 0, format.parse(chat.getTime()));
                    }

                    new InsertChatMessage().execute(details);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if (seenListener != null) {
            reference.removeEventListener(seenListener);
        }
    }
}
