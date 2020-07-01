package com.cb.softwares.doctorapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.util.UtilActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends UtilActivity {


    private String TAG = "Register";
    @BindView(R.id.edtName)
    EditText edtName;

    @BindView(R.id.edtUsername)
    EditText edtUsername;

    @BindView(R.id.edtMobile)
    EditText edtMobileNumber;

    @BindView(R.id.edtPassword)
    EditText edtPassword;

    @BindView(R.id.btnRegister)
    Button btnRegister;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference reference;


    public long maxID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        ButterKnife.bind(this);

        getBundleArgs();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    private void getBundleArgs() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String name = bundle.getString("googleSignInAccountName");
            String emailAddress = bundle.getString("googleSignInAccountEmailAddress");
            edtUsername.setEnabled(false);
            edtUsername.setClickable(false);

            edtName.setText(name);
            edtUsername.setText(emailAddress);
        }

    }


    private void getId() {
        reference = FirebaseDatabase.getInstance().getReference("Users");
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

    }


    @OnClick(R.id.btnRegister)
    public void register() {
        String name = edtName.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String mobileNumber = edtMobileNumber.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();


        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(mobileNumber) || TextUtils.isEmpty(password)) {

            Toastmsg(RegisterActivity.this, "All Fields are required");
        } else {

            goneView(btnRegister);
            showView(progressBar);
            register(name, username, mobileNumber, password);
        }
    }


    private void register(final String name, final String username, final String mobileNumber, String password) {
        mFirebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    assert user != null;
                    String userid = user.getUid();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", userid);
                    map.put("username", username);
                    map.put("mobileNumber", mobileNumber);
                    map.put("imageURL", "Default");
                    map.put("name", name);
                    map.put("type", "u");
                    map.put("status", "online");
                    long id = maxID + 1;
                    map.put("userid", id);
                    map.put("last_time", new Date().toString());


                    reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                RegisterActivity.this.finish();
                            }
                        }
                    });
                } else {

                    showView(btnRegister);
                    goneView(progressBar);
                    Log.w(TAG, "task" + task.getException());
                    Toastmsg(RegisterActivity.this, "You cant register with this email and password");
                }

            }
        });
    }
}
