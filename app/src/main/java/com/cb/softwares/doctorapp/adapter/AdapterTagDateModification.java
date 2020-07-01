package com.cb.softwares.doctorapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.fragment.TagsModificationFragment;
import com.cb.softwares.doctorapp.interfaces.DateModification;
import com.cb.softwares.doctorapp.model.CalendarModel;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterTagDateModification extends RecyclerView.Adapter<AdapterTagDateModification.DateViewHolder> {


    private ArrayList<CalendarModel> list;
    private DateModification modification;
    private Context context;
    private TagsModificationFragment modificationFragment;
    private HashMap<String, String> map;
    private boolean isDateClicks;


    public AdapterTagDateModification(ArrayList<CalendarModel> list, Context context, TagsModificationFragment modificationFragment, HashMap<String, String> map, boolean isDateClicks) {
        this.list = list;
        modification = (DateModification) modificationFragment;
        this.context = context;
        this.modificationFragment = modificationFragment;
        this.map = map;
        this.isDateClicks = isDateClicks;
    }

    @NonNull
    @Override
    public AdapterTagDateModification.DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_date_modify, parent, false);
        return new DateViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTagDateModification.DateViewHolder holder, int position) {
        final int pos = position;
        final CalendarModel model = list.get(pos);
        holder.txtDate.setText(model.getDate());


        if (model.isSelected()) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary));
        } else if (map.containsKey(model.getFullDate())) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary));
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.icons));
        }

      /*  holder.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modification.modifyDate(pos);
            }
        });*/

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

              /*  if (!isDateClicks) {
                    if (pos > 6) {
                        if (!model.isSelected()) {
                            modification.enableSelection(pos);
                        }
                    }
                }
                return true;*/
              return true;
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isDateClicks) {
                    if (pos > 6) {
                        if (!model.isSelected()) {
                            modification.enableSelection(pos);
                            modification.select(pos);
                        }
                        else
                        {
                            modification.unSelect(pos);
                        }
                    }
                }else
                {
                    modification.modifyTime(pos);
                }
                return;
            }

            /*
                if (!isDateClicks) {

                    if (modificationFragment.isInActionMode || model.isSelected()) {



                        if (pos > 6) {

                            if (model.isSelected()) {
                                modification.unSelect(pos);
                            } else {
                                modification.select(pos);
                            }
                        }
                    }
                }else{

                    modification.modifyTime(pos);
                }
            }*/
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DateViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtDate)
        TextView txtDate;

        @BindView(R.id.cardView)
        CardView cardView;


        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
