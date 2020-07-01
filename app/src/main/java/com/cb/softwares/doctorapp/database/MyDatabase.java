package com.cb.softwares.doctorapp.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.cb.softwares.doctorapp.database.dao.DoctorDao;
import com.cb.softwares.doctorapp.model.ChatDetails;
import com.cb.softwares.doctorapp.model.User;
import com.cb.softwares.doctorapp.model.UserForChatDetails;
import com.cb.softwares.doctorapp.util.TimestampConverter;

@Database(entities = {User.class, ChatDetails.class, UserForChatDetails.class}, version = 1)
@TypeConverters({TimestampConverter.class})
public abstract class MyDatabase extends RoomDatabase {


    public abstract DoctorDao doctorDao();

    private static MyDatabase INSTANCE;

    public static MyDatabase getDatabase(final Context context) {

        if (INSTANCE == null) {
            synchronized (MyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class, "doctor_db").build();
                }

            }
        }

        return INSTANCE;
    }

}
