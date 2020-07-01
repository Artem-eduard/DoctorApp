package com.cb.softwares.doctorapp.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cb.softwares.doctorapp.model.TagCreation;
import com.cb.softwares.doctorapp.repository.TagRepository;

import java.util.List;

public class TagViewModel extends ViewModel {


    private TagRepository repository;

    public TagViewModel(TagRepository repository) {
        this.repository = repository;
    }


    public void addTags(TagCreation creation) {
        repository.addTag(creation);
    }


    public MutableLiveData<List<TagCreation>> getTags() {
        return repository.getTags();
    }

    public void modifyTags( TagCreation creation) {
        repository.modifyTag( creation);

    }
    public void removeTags( TagCreation creation) {
        repository.removeTag( creation);

    }

}
