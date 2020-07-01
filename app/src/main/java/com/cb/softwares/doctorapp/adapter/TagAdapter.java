package com.cb.softwares.doctorapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.databinding.AdapterTagBinding;
import com.cb.softwares.doctorapp.interfaces.ModifyTagInterface;
import com.cb.softwares.doctorapp.model.TagCreation;

import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> implements ModifyTagInterface {

    private Context context;
    private ArrayList<TagCreation> list;
    private ModifyTagInterface modifyTagInterface;

    public TagAdapter(Context context, ArrayList<TagCreation> list) {
        this.context = context;
        this.list = list;
        this.modifyTagInterface = (ModifyTagInterface) context;
    }


    @Override
    public void addDates(TagCreation creation) {
        modifyTagInterface.addDates(creation);
    }

    @NonNull
    @Override
    public TagAdapter.TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        AdapterTagBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_tag, parent, false);
        binding.setModifyTag(this);
        binding.txtDates.setSelected(true);
        return new TagViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TagAdapter.TagViewHolder holder, int position) {
        TagCreation creation = list.get(position);
        holder.binding.setTags(creation);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void modifyToCustomDate(TagCreation creation) {
        modifyTagInterface.modifyToCustomDate(creation);
    }

    @Override
    public void modifyToToday(TagCreation creation) {

        modifyTagInterface.modifyToToday(creation);
    }

    @Override
    public void modifyToTomorrow(TagCreation creation) {
        modifyTagInterface.modifyToTomorrow(creation);
    }

    @Override
    public void modifyDates(TagCreation creation) {
        modifyTagInterface.modifyDates(creation);
    }

    public class TagViewHolder extends RecyclerView.ViewHolder {
        AdapterTagBinding binding;

        public TagViewHolder(@NonNull AdapterTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
