package com.cb.softwares.doctorapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.activity.FontManager;
import com.cb.softwares.doctorapp.activity.MainActivity;
import com.cb.softwares.doctorapp.activity.ShowAppointmentActivity;
import com.cb.softwares.doctorapp.model.AppointmentDetails;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {


    private Context context;
    private ArrayList<AppointmentDetails> list;
    private MainActivity activity;
    private String userType;
    private Typeface iconFont;

    public AppointmentAdapter(Context context, ArrayList<AppointmentDetails> list, MainActivity activity, String userType) {
        this.context = context;
        this.list = list;
        this.activity = activity;
        this.userType = userType;
        iconFont = FontManager.getTypeface(this.context, FontManager.FONTAWESOME);

    }

    @NonNull
    @Override
    public AppointmentAdapter.AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_appointment, viewGroup, false);
        FontManager.markAsIconContainer(v.findViewById(R.id.txtCall), iconFont);
        return new AppointmentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.AppointmentViewHolder holder, int i) {

        final int pos = i;

        final AppointmentDetails details = list.get(i);

        //holder.txtName.setText(details.getName());
        holder.txtName.setText("Raghu: 32");
        String gender = "Gender : " + details.getGender() + " / " + details.getAge() + " yrs";
        //holder.txtGender.setText(gender);
        holder.txtGender.setText("Excel Care : 10.24");

        holder.txtHospitalName.setText(details.getHospitalName());

        String dateAndTime = details.getDate() + " : " + details.getTime();

        holder.txtDateTime.setText(dateAndTime);

        if (details.getGender().equalsIgnoreCase("Male")) {
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.man));
        } else {
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bigender));
        }

        if (userType.equalsIgnoreCase("A")) {
            holder.imgConfirm.setVisibility(View.VISIBLE);

            if (details.getisConfirmedByDoctor()) {

              //  holder.cardView.setBackground(ContextCompat.getDrawable(context, R.drawable.green_border));
                holder.imgConfirm.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.survey_done));
                holder.txtConfirmation.setText("Confirmed");
            } else {
              //  holder.txtConfirmation.setText("Not yet confirmed");
                holder.cardView.setBackground(ContextCompat.getDrawable(context, R.drawable.red_border));
                holder.imgConfirm.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_box));
            }

        } else {
            holder.imgConfirm.setVisibility(View.VISIBLE);
            if (details.getisConfirmedByDoctor()) {
                holder.txtConfirmation.setText("Confirmed");
              //  holder.cardView.setBackground(ContextCompat.getDrawable(context, R.drawable.green_border));

                holder.imgConfirm.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.survey_done));
            } else {
                holder.txtConfirmation.setText("Not yet confirmed");
                holder.txtConfirmation.setTextColor(0xFFFFFFFF);
                holder.cardView.setCardBackgroundColor(0xFF0000F0);
             //   holder.cardView.setBackground(ContextCompat.getDrawable(context, R.drawable.red_border));
                holder.imgConfirm.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_box));
            }
        }

        holder.imgConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d("userType", userType);

                if (userType.equalsIgnoreCase("A")) {
                    if (details.getisConfirmedByDoctor()) {
                        activity.updateStatus(false, details.getId(), pos);
                    } else {

                        activity.updateStatus(true, details.getId(), pos);
                    }
                }
            }
        });


        holder.txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.alertForCancelAppointment(details , pos);
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ShowAppointmentActivity.class).putExtra("name", details.getName())
                        .putExtra("age", details.getAge()).putExtra("gender", details.getGender()).putExtra("time", details.getTime())
                        .putExtra("date", details.getDate()).putExtra("mobile", details.getMobile())
                        .putExtra("id", details.getId()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AppointmentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.txtGender)
        TextView txtGender;
        @BindView(R.id.image)
        ImageView imageView;
        @BindView(R.id.txtCall)
        TextView txtCall;
        @BindView(R.id.txtCancel)
        TextView txtCancel;

        @BindView(R.id.cardview)
        CardView cardView;

        @BindView(R.id.imgConfirm)
        ImageView imgConfirm;

        @BindView(R.id.txtConfirmation)
        TextView txtConfirmation;


        @BindView(R.id.txtHospitalName)
        TextView txtHospitalName;


        @BindView(R.id.txtDateTime)
        TextView txtDateTime;


        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
