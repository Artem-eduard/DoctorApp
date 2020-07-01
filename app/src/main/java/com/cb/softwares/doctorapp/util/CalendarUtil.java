package com.cb.softwares.doctorapp.util;

import com.cb.softwares.doctorapp.model.CalendarModel;

import java.util.ArrayList;

public class CalendarUtil {

    public static boolean isLeafYear(int year) {
        return year % 4 == 0;
    }

    public static ArrayList<String> getMonths() {
        ArrayList<String> monthList = new ArrayList<>();
        monthList.add("January");
        monthList.add("February");
        monthList.add("March");
        monthList.add("April");
        monthList.add("May");
        monthList.add("June");
        monthList.add("July");
        monthList.add("August");
        monthList.add("September");
        monthList.add("October");
        monthList.add("November");
        monthList.add("December");
        return monthList;
    }


    public static int getNumberOfDays(int month) {

        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            return 31;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        } else {
            return 29;
        }
    }


    public static void addFirstRow(ArrayList<CalendarModel> list) {
        list.add(new CalendarModel("S"));
        list.add(new CalendarModel("M"));
        list.add(new CalendarModel("T"));
        list.add(new CalendarModel("W"));
        list.add(new CalendarModel("T"));
        list.add(new CalendarModel("F"));
        list.add(new CalendarModel("S"));

    }


}
