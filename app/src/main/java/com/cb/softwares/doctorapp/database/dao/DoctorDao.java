package com.cb.softwares.doctorapp.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.cb.softwares.doctorapp.model.ChatDetails;
import com.cb.softwares.doctorapp.model.User;
import com.cb.softwares.doctorapp.model.UserForChatDetails;

import java.util.Date;
import java.util.List;

@Dao
public interface DoctorDao {

    @Insert
    public void insertUsers(User user);

    @Insert
    public void insertAllUsers(User... user);

    @Query("select count(*) from User")
    public double getUserCount();


    @Query("select * from User")
    public List<User> getAllUsers();

    @Query("select * from ChatDetails order by time")
    public List<ChatDetails> getChatMessages();

    @Insert
    public void insertChatMessage(ChatDetails... details);

    @Query("update ChatDetails set isseen = :val where sender = :receiver ")
    public void updateSeenMessage(String receiver, boolean val);

    @Insert
    public void insertChatDetails(UserForChatDetails... details);

    @Query("select count(*) from UserForChatDetails where id= :id")
    public int getCount(String id);

    @Query("select * from UserForChatDetails")
    public List<UserForChatDetails> getAllChats();


    @Query("update UserForChatDetails set lastMsg = :msg, time = :time where id = :receiver")
    public void changeLastMsg(String msg, Date time,String receiver);


    @Query("select autoId from ChatDetails order by autoId desc limit 1")
    public long getAutoId();

}
