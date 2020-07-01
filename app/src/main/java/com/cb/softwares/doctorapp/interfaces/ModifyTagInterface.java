package com.cb.softwares.doctorapp.interfaces;

import com.cb.softwares.doctorapp.model.TagCreation;

public interface ModifyTagInterface {

    void modifyToCustomDate( TagCreation creation);

    void modifyToToday( TagCreation creation);

    void modifyToTomorrow( TagCreation creation);

    void modifyDates(TagCreation creation);

    void addDates(TagCreation creation);
}
