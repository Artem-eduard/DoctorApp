package com.cb.softwares.doctorapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.activity.ChatMainActivity;
import com.cb.softwares.doctorapp.activity.ChatMessageActivity;
import com.cb.softwares.doctorapp.model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {


    private Context context;
    List<User> list;

    public UserAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_chat_users, viewGroup, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int i) {

        final User details = list.get(i);
        holder.txtName.setText(details.getName());
        holder.txtEmail.setText(details.getUsername());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //    ((ChatMainActivity)context).sharedDataUri
                // .putExtra("isUriAvailable", isUriAvailable).putExtra("uri", sharedDataUri == null ? "empty" : sharedDataUri.toString())

                context.startActivity(new Intent(context, ChatMessageActivity.class)
                        .putExtra("isUriAvailable", ((ChatMainActivity) context).isUriAvailable).putExtra("uri", ((ChatMainActivity) context).sharedDataUri == null ? "empty" : ((ChatMainActivity) context).sharedDataUri.toString())
                        .putExtra("name", details.getName()).putExtra("token", details.getToken()).putExtra("id", details.getId()));
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.txtEmail)
        TextView txtEmail;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
