package com.cb.softwares.doctorapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.activity.ChatMessageActivity;
import com.cb.softwares.doctorapp.fragment.ChatFragment;
import com.cb.softwares.doctorapp.model.UserForChatDetails;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    private Context context;
    private List<UserForChatDetails> list;
    private ChatFragment fragment;
    private SimpleDateFormat format, format1;


    public ChatListAdapter(Context context, List<UserForChatDetails> list, ChatFragment fragment) {
        this.context = context;
        this.list = list;
        this.fragment = fragment;
        format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        format1 = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
    }

    @NonNull
    @Override
    public ChatListAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_chat_list, viewGroup, false);
        return new ChatListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ChatListViewHolder holder, int i) {

       final UserForChatDetails details = list.get(i);

        holder.txtName.setText(details.getName());
        holder.txtLastMessage.setText(details.getLastMsg());

        try {

            Log.w("Adapter","time "+ details.getTime().toString());
            String dateTime = format.format(details.getTime());
            Date formatDate = format.parse(dateTime);
            holder.txtTime.setText(format1.format(formatDate));
        } catch (Exception e) {

            e.printStackTrace();
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatMessageActivity.class).putExtra("name",details.getName()).putExtra("token",details.getToken()).putExtra("id",details.getId()));
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtName)
        TextView txtName;

        @BindView(R.id.txtLastMessage)
        TextView txtLastMessage;

        @BindView(R.id.txtTime)
        TextView txtTime;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
