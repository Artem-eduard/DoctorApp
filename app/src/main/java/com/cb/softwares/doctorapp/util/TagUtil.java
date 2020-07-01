package com.cb.softwares.doctorapp.util;

import com.cb.softwares.doctorapp.datas.TagData;
import com.cb.softwares.doctorapp.repository.TagRepository;
import com.cb.softwares.doctorapp.viewmodel.TagViewModelFactory;

public class TagUtil {


    public TagViewModelFactory provideTagViewModelFactory() {
        TagRepository repository = TagRepository.getInstance(TagData.getInstance().tagDao);
        return new TagViewModelFactory(repository);
    }

}
