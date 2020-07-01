package com.cb.softwares.doctorapp.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.adapter.UserAdapter;
import com.cb.softwares.doctorapp.database.MyDatabase;
import com.cb.softwares.doctorapp.model.User;
import com.cb.softwares.doctorapp.util.dividerLineForRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UserFragment extends Fragment {


    private UserAdapter adapter;

    private List<User> list = new ArrayList<>();

    private String TAG = "User";


    public UserFragment() {
        // Required empty public constructor
    }


    @BindView(R.id.userRecyclerview)
    RecyclerView recyclerView;

    MyDatabase db;


    double userCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, v);

        setLayoutManager();
        setAdapter();


        db = MyDatabase.getDatabase(getActivity());

        //new getCount().execute();
        // new getAllUsers().execute();

        getUsers();

        return v;
    }


    private class getAllUsers extends AsyncTask<Void, Void, List<User>> {
        @Override
        protected List<User> doInBackground(Void... voids) {

            return db.doctorDao().getAllUsers();
        }

        @Override
        protected void onPostExecute(List<User> users) {
            super.onPostExecute(users);
            list.addAll(users);

            adapter.notifyDataSetChanged();
            getUsers();
            Log.w(TAG, "count of users " + list.size());
        }
    }


    private class getCount extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {


//            list.addAll(db.doctorDao().getAllUsers());

            Log.w(TAG, "users count " + list.size());

            userCount = db.doctorDao().getUserCount();

            Log.w(TAG, "local user count " + userCount);
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.w(TAG, "On post called");

        }
    }


    private class InsertUsers extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... users) {
            try {
                db.doctorDao().insertUsers(users[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.w(TAG, "insert called");
            return null;
        }
    }

    private void getUsers() {


        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = reference.limitToLast(100);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.w(TAG, "count " + dataSnapshot.exists() + dataSnapshot.getChildrenCount());

                list.clear();
                boolean isDataAvailable = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    User user = snapshot.getValue(User.class);

                    if (user.getType().equalsIgnoreCase("u")) {

                        if (!user.getId().equalsIgnoreCase(firebaseUser.getUid())) {
                            list.add(user);
                            isDataAvailable = true;
                            // new InsertUsers().execute(user);
                        }
                    }
                }

                if (isDataAvailable) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void setLayoutManager() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
    }


    private void setAdapter() {
        adapter = new UserAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new dividerLineForRecyclerView(getActivity()));
    }


}
