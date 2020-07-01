package com.cb.softwares.doctorapp.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.cb.softwares.doctorapp.repository.TagRepository;

public class TagViewModelFactory extends ViewModelProvider.NewInstanceFactory {


    private TagRepository repository;

    public TagViewModelFactory(TagRepository repository) {
        this.repository = repository;
    }



    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TagViewModel(repository);
    }
}
