package com.cb.softwares.doctorapp.repository;

import androidx.lifecycle.MutableLiveData;

import com.cb.softwares.doctorapp.dao.CreateTagDao;
import com.cb.softwares.doctorapp.model.TagCreation;

import java.util.List;

public class TagRepository {

    private CreateTagDao tagDao;


    TagRepository(CreateTagDao tagDao) {
        this.tagDao = tagDao;
    }


    public void addTag(TagCreation tagCreation) {
        tagDao.addTags(tagCreation);
    }


    public MutableLiveData<List<TagCreation>> getTags() {
        return tagDao.getQuotes();
    }

    public void modifyTag( TagCreation creation) {
        tagDao.modifyTag( creation);
    }
    public void removeTag( TagCreation creation) {
        tagDao.removeTag( creation);
    }
    private static TagRepository instance;

    public static TagRepository getInstance(CreateTagDao tagDao) {

        if (instance == null) {
            synchronized (TagRepository.class) {
                instance = new TagRepository(tagDao);
            }
        }

        return instance;
    }

}
