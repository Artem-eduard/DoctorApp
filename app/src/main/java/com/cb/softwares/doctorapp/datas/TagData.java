package com.cb.softwares.doctorapp.datas;

import com.cb.softwares.doctorapp.dao.CreateTagDao;

public class TagData {


   public CreateTagDao tagDao;

    volatile
    static TagData instance;

    public static TagData getInstance() {
        if (instance == null) {
            synchronized (TagData.class) {
                instance = new TagData();
            }
        }
        return instance;
    }


    private TagData() {
        tagDao = new CreateTagDao();
    }
}
