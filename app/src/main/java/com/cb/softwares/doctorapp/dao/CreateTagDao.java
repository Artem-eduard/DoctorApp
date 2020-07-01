package com.cb.softwares.doctorapp.dao;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.cb.softwares.doctorapp.model.TagCreation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateTagDao {


    private String TAG = "CreateDao";

    private ArrayList<TagCreation> list = new ArrayList<>();

    private MutableLiveData<List<TagCreation>> tagsDataList = new MutableLiveData<>();


    public MutableLiveData<List<TagCreation>> getQuotes() {

        Query query = FirebaseDatabase.getInstance().getReference("Tags");
        list.clear();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        list.add(snapshot.getValue(TagCreation.class));
                    }
                    tagsDataList.setValue(list);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return tagsDataList;
    }


    public void addTags(TagCreation creation) {
        Log.w(TAG, "creation val " + creation.getId());
        list.add(creation);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tags");
        HashMap<String, Object> map = new HashMap<>();
        map.put("hospitalName", creation.getHospitalName());
        map.put("hospitalAddress", creation.getHospitalAddress());
        map.put("dates", creation.getDates());
        map.put("startTime", creation.getStartTime());
        map.put("endTime", creation.getEndTime());
        map.put("id", creation.getId());
        reference.child(creation.getId()).setValue(map);
        tagsDataList.setValue(list);
    }


    public void modifyTag(TagCreation creation) {
        Log.w(TAG, "pos " + creation.getFormatDate() + " : " + creation.getDates());
        int pos = -1;
        for (int i = 0; i <= list.size() - 1; i++) {
            if (creation.getId().equals(list.get(i).getId())) {
                pos = i;
                break;
            }
        }

        if (pos != -1) {
            list.remove(pos);
            list.add(pos, creation);
            tagsDataList.setValue(list);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tags").child(creation.getId());
            HashMap<String, Object> map = new HashMap<>();
            map.put("dates", creation.getDates());
            reference.updateChildren(map);

        }
    }


    public void removeTag(TagCreation creation) { //Added by Artem 04.24
        Log.w(TAG, "pos " + creation.getFormatDate() + " : " + creation.getDates());
        int pos = -1;
        for (int i = 0; i <= list.size() - 1; i++) {
            if (creation.getId().equals(list.get(i).getId())) {
                pos = i;
                break;
            }
        }

        if (pos != -1) {
            list.remove(pos);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tags").child(creation.getId());
            reference.removeValue();

            tagsDataList.setValue(list);


        }
    }


}
