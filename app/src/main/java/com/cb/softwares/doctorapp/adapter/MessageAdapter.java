package com.cb.softwares.doctorapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.activity.ChatMessageActivity;
import com.cb.softwares.doctorapp.interfaces.DeleteMessageClick;
import com.cb.softwares.doctorapp.model.Chat;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private static final int MSG_TYPE_RIGHT_MAX = 2;
    private static final int MSG_TYPE_LEFT_MAX = 3;
    private static final int MSG_TYPE_LEFT_ATTACHMENT = 4;
    private static final int MSG_TYPE_RIGHT_ATTACHMENT = 5;


    private Context context;
    private ArrayList<Chat> list;
    private String uid;
    private ChatMessageActivity activity;

    private DeleteMessageClick click;

    private SimpleDateFormat format, format1;
    private RecyclerView.ViewHolder holder;
    private int i;

    public MessageAdapter(Context context, ArrayList<Chat> list, String uid, ChatMessageActivity activity) {
        this.context = context;
        this.list = list;
        this.uid = uid;
        format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        format1 = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        this.activity = activity;

        click = (DeleteMessageClick) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = null;
        if (i == MSG_TYPE_LEFT) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_chat_message_right, viewGroup, false);

            return new MessageViewHolder(v);
        } else if (i == MSG_TYPE_RIGHT) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_chat_message_left, viewGroup, false);
            return new MessageViewHolder(v);
        } else if (i == MSG_TYPE_RIGHT_MAX) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_chat_message_max_right, viewGroup, false);
            return new MessageViewHolder(v);
        } else if (i == MSG_TYPE_LEFT_MAX) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_chat_message_max_left, viewGroup, false);
            return new MessageViewHolder(v);
        } else if (i == MSG_TYPE_LEFT_ATTACHMENT) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_chat_attachment_left, viewGroup, false);
            return new AttachmentViewHolder(v);
        } else {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_chat_attachment_right, viewGroup, false);
            return new AttachmentViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {


        final Chat details = list.get(i);


        Log.w("Adapter ", "details " + details.isSelected());

        final int pos = i;


        if (details.isSelected()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
        } else {
            holder.itemView.setBackgroundColor(0x00000000);
        }


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (!details.isSelected()) {

                    click.onSelectEnable(pos);
                }
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (activity.isInActionMode) {
                    if (!details.isSelected()) {

                        click.select(pos);
                    } else {
                        click.unSelect(pos);
                    }
                }
            }
        });


        if (details.getType().equalsIgnoreCase("T")) {
            ((MessageViewHolder) holder).txtMessage.setText(details.getMessage());
            try {
                String dateTime = details.getTime();
                Date formatDate = format.parse(dateTime);

                ((MessageViewHolder) holder).txtTime.setText(format1.format(formatDate));
            } catch (Exception e) {

                e.printStackTrace();
            }


            if (details.getSender().equalsIgnoreCase(uid)) {
                if (details.isIsseen()) {
                    ((MessageViewHolder) holder).imgTick.setVisibility(View.VISIBLE);
                } else {
                    ((MessageViewHolder) holder).imgTick.setVisibility(View.GONE);
                }
            } else {
                ((MessageViewHolder) holder).imgTick.setVisibility(View.GONE);
            }


        } else {


            String type = getType(details.getMessage());


            ((AttachmentViewHolder) holder).txtMessage.setText(details.getMessage());

            if (!details.getSender().equalsIgnoreCase(uid)) {

                if (details.getIsDownloaded().equalsIgnoreCase("true")) {

                    ((AttachmentViewHolder) holder).imgDownload.setVisibility(View.VISIBLE);

                    switch (type) {
                        case "pdf":
                            ((AttachmentViewHolder) holder).imgLocalImage.setVisibility(View.GONE);
                            ((AttachmentViewHolder) holder).imgDownload.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pdf));
                            break;
                        case "txt":
                        case "doc":
                        case "docx":
                            ((AttachmentViewHolder) holder).imgLocalImage.setVisibility(View.GONE);
                            ((AttachmentViewHolder) holder).imgDownload.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_word));
                            break;

                        case "jpg":
                        case "jpeg":
                        case "png":
                            ((AttachmentViewHolder) holder).imgLocalImage.setVisibility(View.VISIBLE);
                            ((AttachmentViewHolder) holder).imgDownload.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_image));

                            break;

                        default:
                            ((AttachmentViewHolder) holder).imgLocalImage.setVisibility(View.GONE);
                            ((AttachmentViewHolder) holder).imgDownload.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_attachment));


                    }

                    Log.w("Adapter", "visible working");


                    Uri uri = Uri.fromFile(new File(details.getReceiverLocalPath()));
                    Picasso.get().load(uri).resize(300, 300).centerCrop().error(R.drawable.ic_error_image).into(((AttachmentViewHolder) holder).imgLocalImage);

                } else {

                    if (details.getIsDownloading().equalsIgnoreCase("true")) {
                        ((AttachmentViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                    } else {
                        ((AttachmentViewHolder) holder).imgDownload.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_download));

                        ((AttachmentViewHolder) holder).progressBar.setVisibility(View.GONE);
                        ((AttachmentViewHolder) holder).imgDownload.setVisibility(View.VISIBLE);
                    }

                    Log.w("Adapter", "not visible working");
                    ((AttachmentViewHolder) holder).imgLocalImage.setVisibility(View.GONE);

                }


                ((AttachmentViewHolder) holder).imgDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        activity.downloadFile(pos);
                    }
                });


            } else {

                if (details.getIsProgressing().equalsIgnoreCase("true")) {
                    ((AttachmentViewHolder) holder).progressBar.setVisibility(View.VISIBLE);

                    ((AttachmentViewHolder) holder).imgDownload.setVisibility(View.GONE);

                } else {
                    ((AttachmentViewHolder) holder).progressBar.setVisibility(View.GONE);
                    ((AttachmentViewHolder) holder).imgDownload.setVisibility(View.VISIBLE);

                    switch (type) {
                        case "pdf":
                            ((AttachmentViewHolder) holder).imgLocalImage.setVisibility(View.GONE);
                            ((AttachmentViewHolder) holder).imgDownload.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pdf));
                            break;

                        case "txt":
                        case "doc":
                        case "docx":
                            ((AttachmentViewHolder) holder).imgLocalImage.setVisibility(View.GONE);
                            ((AttachmentViewHolder) holder).imgDownload.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_word));
                            break;

                        case "jpg":
                        case "jpeg":
                        case "png":
                            Uri uri = Uri.fromFile(new File(details.getSenderLocalPath()));
                            Picasso.get().load(uri).resize(300, 300).centerCrop().error(R.drawable.ic_error_image).into(((AttachmentViewHolder) holder).imgLocalImage);

                            ((AttachmentViewHolder) holder).imgLocalImage.setVisibility(View.VISIBLE);
                            ((AttachmentViewHolder) holder).imgDownload.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_image));

                            break;

                        default:
                            ((AttachmentViewHolder) holder).imgLocalImage.setVisibility(View.GONE);
                            ((AttachmentViewHolder) holder).imgDownload.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_attachment));

                    }

                }


            }

            ((AttachmentViewHolder) holder).imgLocalImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.openFile(pos);
                }
            });


            ((AttachmentViewHolder) holder).txtMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    activity.openFile(pos);
                }
            });

            try {
                String dateTime = details.getTime();
                Date formatDate = format.parse(dateTime);

                ((AttachmentViewHolder) holder).txtTime.setText(format1.format(formatDate));
            } catch (Exception e) {

                e.printStackTrace();
            }


            if (details.getSender().equalsIgnoreCase(uid)) {
                if (details.isIsseen()) {
                    ((AttachmentViewHolder) holder).imgTick.setVisibility(View.VISIBLE);
                } else {
                    ((AttachmentViewHolder) holder).imgTick.setVisibility(View.GONE);
                }
            } else {
                ((AttachmentViewHolder) holder).imgTick.setVisibility(View.GONE);
            }


        }


        Log.w("Adapter ", "is seen " + details.isIsseen());


    }

    @Override
    public int getItemViewType(int position) {
        Chat details = list.get(position);


        if (details.getType().equalsIgnoreCase("T")) {
            if (details.getSender().equalsIgnoreCase(uid)) {


                if (details.getMessage().length() >= 25) {
                    return MSG_TYPE_RIGHT_MAX;
                } else {
                    return MSG_TYPE_LEFT;
                }
            } else {
                if (details.getMessage().length() >= 25) {
                    return MSG_TYPE_LEFT_MAX;
                } else {
                    return MSG_TYPE_RIGHT;
                }
            }
        } else {

            if (details.getSender().equalsIgnoreCase(uid)) {

                return MSG_TYPE_RIGHT_ATTACHMENT;
            } else {
                return MSG_TYPE_LEFT_ATTACHMENT;
            }
        }


    }


    private String getType(String name) {
        String val = name.substring(name.lastIndexOf(".") + 1, name.length());
        return val;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.txtMessage)
        TextView txtMessage;
        @BindView(R.id.imgTick)
        ImageView imgTick;
        @BindView(R.id.txtTime)
        TextView txtTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class AttachmentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtMessage)
        TextView txtMessage;
        @BindView(R.id.imgTick)
        ImageView imgTick;
        @BindView(R.id.txtTime)
        TextView txtTime;

        @BindView(R.id.imgLocalImage)
        ImageView imgLocalImage;

        @BindView(R.id.progressBar)
        ProgressBar progressBar;
        @BindView(R.id.imgDownload)
        ImageView imgDownload;


        public AttachmentViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
