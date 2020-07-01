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
import com.cb.softwares.doctorapp.model.User;
import com.cb.softwares.doctorapp.sharedpreference.SharedPreferenceManager;
import com.cb.softwares.doctorapp.util.UtilActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

public class LoginActivity extends UtilActivity {

    private String TAG = "Login";

    @BindView(R.id.btnLogin)
    Button btnLogin;

    @BindView(R.id.loginProgressBar)
    ProgressBar loginProgressBar;

    @BindView(R.id.btnRegister)
    Button btnRegister;

    @BindView(R.id.btnForgotPassword)
    Button btnForgotPassword;




    @BindView(R.id.edtUsername)
    EditText edtUserName;

    @BindView(R.id.edtpassword)
    EditText edtPassword;


    SharedPreferenceManager sharedPreferenceManager;
    FirebaseAuth auth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        sharedPreferenceManager = new SharedPreferenceManager(this);


        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            gotoMainActivity();
        }
    }

    private void gotoMainActivity() {

        startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));

        LoginActivity.this.finish();
    }


    @OnClick(R.id.btnRegister)
    public void register() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }


    @OnClick(R.id.btnForgotPassword)
    public void forgotPassword() {
        String username = edtUserName.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
/*
        if (TextUtils.isEmpty(username)) {
            Toastmsg(LoginActivity.this, "Email Fields are required");

        } else {
            FirebaseAuth.getInstance().sendPasswordResetEmail(username)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toastmsg(LoginActivity.this, "Email Sent!");
                            }
                        }
                    });
        }*/
        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
    }


    @OnClick(R.id.sign_in_button)
    public void googleSignIn() {
        showView(loginProgressBar);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @OnClick(R.id.btnLogin)
    public void login() {

        String username = edtUserName.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toastmsg(LoginActivity.this, "All Fields are required");

        } else {
            showView(loginProgressBar);

            auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        if (auth.getUid() != null) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(auth.getUid());

                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);

                                    if (user != null) {

                                            Log.w(TAG, "Login call userType" + user.getType());
                                            sharedPreferenceManager.setUserType(user.getType());
                                            gotoMainActivity();

                                    }

                                    goneView(loginProgressBar);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.w(TAG, "initialize call on cancel working");
                                    goneView(loginProgressBar);
                                }
                            });
                        }
                        goneView(loginProgressBar);
                        gotoMainActivity();

                    } else {
                        goneView(loginProgressBar);
                        Log.w(TAG, "Task not successful " + task.getException());

                        if (Objects.requireNonNull(task.getException()).getLocalizedMessage() != null) {
                            Toastmsg(LoginActivity.this, task.getException().getLocalizedMessage());
                        } else {
                            Toastmsg(LoginActivity.this, "Error try again");
                        }
                    }

                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                goneView(loginProgressBar);
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential:success");

                            if (auth.getUid() != null) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(auth.getUid());

                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);

                                        if (user != null) {
                                            if (user.getUsername().equalsIgnoreCase(acct.getEmail())) {
                                                Log.w(TAG, "Login call userType" + user.getType());
                                                sharedPreferenceManager.setUserType(user.getType());
                                                gotoMainActivity();
                                            } else {
                                                gotoRegisterActivity(acct.getDisplayName(), acct.getEmail());
                                            }
                                        } else {
                                            gotoRegisterActivity(acct.getDisplayName(), acct.getEmail());
                                        }

                                        goneView(loginProgressBar);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.w(TAG, "initialize call on cancel working");
                                        goneView(loginProgressBar);
                                    }
                                });
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            goneView(loginProgressBar);
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void gotoRegisterActivity(String name, String emailAddress) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class)
                .putExtra("googleSignInAccountName", name)
                .putExtra("googleSignInAccountEmailAddress", emailAddress));
    }
}
