package com.cb.softwares.doctorapp.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.adapter.ChatListAdapter;
import com.cb.softwares.doctorapp.database.MyDatabase;
import com.cb.softwares.doctorapp.model.UserForChatDetails;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatFragment extends Fragment {

    public ChatFragment() {
        // Required empty public constructor
    }


    @BindView(R.id.chatRecyclerView)
    RecyclerView recyclerView;


    private ChatListAdapter adapter;
    private FirebaseUser firebaseUser;
    DatabaseReference reference;

    private ArrayList<String> usersList = new ArrayList<>();
    private List<UserForChatDetails> list = new ArrayList<>();

    MyDatabase db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, v);
        setLayoutManager();
        setAdapter();
        db = MyDatabase.getDatabase(getActivity());
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        new getAllChats().execute();

    }

    private class getAllChats extends AsyncTask<Void, Void, List<UserForChatDetails>> {
        @Override
        protected List<UserForChatDetails> doInBackground(Void... voids) {

            return db.doctorDao().getAllChats();
        }

        @Override
        protected void onPostExecute(List<UserForChatDetails> userForChatDetails) {
            super.onPostExecute(userForChatDetails);


            list.clear();
            list.addAll(userForChatDetails);

            if (list.size() > 0) {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                } else {
                    setAdapter();
                }
            }
        }
    }


    private void setLayoutManager() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
    }


    private void setAdapter() {
        adapter = new ChatListAdapter(getActivity(), list, ChatFragment.this);
        recyclerView.setAdapter(adapter);

    }


}
