package com.cb.softwares.doctorapp.fragment;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cb.softwares.doctorapp.R;
import com.cb.softwares.doctorapp.adapter.AdapterTagDateModification;
import com.cb.softwares.doctorapp.interfaces.DateModification;
import com.cb.softwares.doctorapp.model.CalendarModel;
import com.cb.softwares.doctorapp.model.TagCreation;
import com.cb.softwares.doctorapp.ui.Activity_TAG;
import com.cb.softwares.doctorapp.util.CalendarUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TagsModificationFragment extends Fragment implements DateModification {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tag_modification, container, false);
    }


    private String TAG = "TagModify";

    @BindView(R.id.datesRecyclerview)
    RecyclerView recyclerView;


    TagCreation creation;

    String type = "";


    String monthNumber;
    Date currentDate;
    SimpleDateFormat format;

    //  CalAdapter adapter;
    //  CalendarAdapter adapter;

    public boolean isDateClicks = false;
    Calendar cal;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setAdapter();
        if (getArguments() != null) {
            creation = getArguments().getParcelable("data");
            type = getArguments().getString("type");
        }


        if (type.equalsIgnoreCase("modifyDate")) {
            // loadData();
            isDateClicks = true;
            setLinearLayoutManager();
            setAdapter();
            loadDataModify();
        } else {
            setLayoutManager();
            setAdapter();
            initCalendar();

        }
    }

    private void setLinearLayoutManager() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
    }

    private void loadDataModify() {

        if (creation != null) {

            try {
                JSONArray array = new JSONArray(creation.getDates());
                for (int i = 0; i <= array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    map.put(obj.getString("dates"), obj.getString("time"));
                    list.add(new CalendarModel(obj.getString("dates"), obj.getString("time")));
                }
                if (list.size() > 0) {
                    adapterTagDateModification.notifyDataSetChanged();
                }
            } catch (JSONException e) {
            }

        }
    }


    private void setLayoutManager() {
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        recyclerView.setHasFixedSize(true);
    }

    public int SELECTED_MONTH;
    public int SELECTED_YEAR;
    public int SELECTED_DAY;

    private void initCalendar() {
        format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        currentDate = new Date();
        //   Calendar calendar = (Calendar) currentDate.clone();


        cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);


        String dayOfTheWeek = (String) DateFormat.format("EEEE", currentDate); // Thursday
        String day = (String) DateFormat.format("dd", currentDate); // 20
        String monthString = (String) DateFormat.format("MMM", currentDate); // Jun
        monthNumber = (String) DateFormat.format("MM", currentDate); // 06
        String year = (String) DateFormat.format("yyyy", currentDate); // 2013

        CalendarUtil.addFirstRow(list);


        try {

            SELECTED_DAY = Integer.parseInt(day);
            SELECTED_MONTH = Integer.parseInt(monthNumber) - 1;
            SELECTED_YEAR = Integer.parseInt(year);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.w(TAG, "date " + dayOfTheWeek + " day " + day + " monthSting " + monthString + " number " + monthNumber + " " + year);

        cal.set(Calendar.DAY_OF_MONTH, 1);

        addDataIntoCell();

    }

    private void addDataIntoCell() {

        int numberOfDays = CalendarUtil.getNumberOfDays(SELECTED_MONTH + 1);

        Log.w(TAG, "year " + SELECTED_YEAR);
        //check leaf year
        if (CalendarUtil.isLeafYear(SELECTED_YEAR)) {

            //if feb it 29 years
            if (SELECTED_MONTH == 1) {
                numberOfDays = 29;
            }
        }


        Log.w(TAG, "number of days : " + numberOfDays);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        Log.w(TAG, "Start of the month:       " + cal.getTime());


        int monthBeginningCell = cal.get(Calendar.DAY_OF_WEEK);
        //int monthBeginningCell = cal.get(Calendar.DAY_OF_MONTH);
        Log.w(TAG, "number of days : " + monthBeginningCell);
        int date = 1;
        for (int i = 1; i <= 42; i++) {

            if (i < monthBeginningCell) {
                list.add(new CalendarModel(""));
            } else {

                if (date <= numberOfDays) {

                    String fullDate = date + "-" + (SELECTED_MONTH + 1) + "-" + (SELECTED_YEAR);


                    try {
                        list.add(new CalendarModel(String.valueOf(date), format.format(format.parse(fullDate))));
                        Log.w(TAG, "date " + format.format(format.parse(fullDate)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    date++;
                } else {
                    //  Log.w(TAG, "i greater that number of days " + i);
                }
            }
        }

        Log.w(TAG, "month number " + monthNumber + " selected moth " + SELECTED_MONTH);

        if (Integer.parseInt(monthNumber) == (SELECTED_MONTH + 1)) {

            // setDateToTextView(format.format(currentDate));

        } else {

            CalendarModel details = list.get(0);
            if ((SELECTED_MONTH + 1) < 10) {
                String setDate = "01 - 0" + (SELECTED_MONTH + 1) + " - " + SELECTED_YEAR;
                //   setDateToTextView(setDate);
                //   txtCalendarDate.setText(setDate);
            } else {
                String setDate = "01 - " + (SELECTED_MONTH + 1) + " - " + SELECTED_YEAR;
                //   setDateToTextView(setDate);
            }


        }


        loadData();

        //  setAdapter();


        adapterTagDateModification.notifyDataSetChanged();

    }


    @OnClick(R.id.txtUpdate)
    public void update() {

        JSONArray array = new JSONArray();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                JSONObject object = new JSONObject();
                object.put("dates", entry.getKey());
                object.put("time", entry.getValue());
                array.put(object);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        creation.setDates(array.toString());
        creation.setFormatDate(creation.getAllDates());
        ((Activity_TAG) getActivity()).viewModel.modifyTags(creation);
        Toast.makeText(getActivity(), "Successfully updated", Toast.LENGTH_SHORT).show();
        ((Activity_TAG) getActivity()).navController.navigateUp();

    }

    @OnClick(R.id.txtDelete)
    public void delete() {

        JSONArray array = new JSONArray();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                JSONObject object = new JSONObject();
                object.put("dates", entry.getKey());
                object.put("time", entry.getValue());
                array.put(object);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        creation.setDates(array.toString());
        creation.setFormatDate(creation.getAllDates());

        ((Activity_TAG) getActivity()).viewModel.removeTags(creation);
        Toast.makeText(getActivity(), "Successfully removed", Toast.LENGTH_SHORT).show();
        ((Activity_TAG) getActivity()).navController.navigateUp();

    }
    HashMap<String, String> map = new HashMap<>();

    private void loadData() {

        if (creation != null) {

            try {
                JSONArray array = new JSONArray(creation.getDates());
                for (int i = 0; i <= array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    map.put(obj.getString("dates"), obj.getString("time"));
                    findDate(obj.getString("dates"));
                }
                if (list.size() > 0) {
                    adapterTagDateModification.notifyDataSetChanged();
                }
            } catch (JSONException e) {
            }

        }
    }


    private void findDate(String date) {
        for (int i = 0; i <= list.size() - 1; i++) {
            CalendarModel model = list.get(i);
            if (!model.getFullDate().equals("null") || !model.getFullDate().equals(null) || !model.getFullDate().isEmpty()) {
                if (model.getFullDate().equalsIgnoreCase(date)) {
                    model.setSelected(true);
                    break;
                }
            }
        }
    }

    private AdapterTagDateModification adapterTagDateModification;
    ArrayList<CalendarModel> list = new ArrayList<>();

    private void setAdapter() {
        adapterTagDateModification = new AdapterTagDateModification(list, getActivity(), TagsModificationFragment.this, map, isDateClicks);
        recyclerView.setAdapter(adapterTagDateModification);
    }

    @Override
    public void modifyDate(int pos) {
        list.remove(pos);
        adapterTagDateModification.notifyDataSetChanged();
    }

    private void doSelections(boolean val, int pos) {
        CalendarModel model = list.get(pos);


      /*  SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date.after(new Date())) {
            dateList.add(array.getJSONObject(i).getString("dates"));
        }*/
        String strDate = model.getFullDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = sdf.parse(strDate);
        }catch (ParseException e){
            e.printStackTrace();
        }
        if (date.after(new Date())) {
            model.setSelected(val);

            adapterTagDateModification.notifyDataSetChanged();
            try {
                if (val) {

                    String time = creation.getStartTime() + "-" + creation.getEndTime();
                    map.put(model.getFullDate(), time);
                } else {
                    map.remove(model.getFullDate());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        Log.w(TAG, "map size " + map.size());
    }

    @Override
    public void select(int pos) {
        doSelections(true, pos);
    }


    @Override
    public void unSelect(int pos) {
        doSelections(false, pos);
    }

    @Override
    public void enableSelection(int pos) {
        isInActionMode = true;
        doSelections(true, pos);

    }

    @Override
    public void modifyTime(int pos) {
        createAlertDialog(pos);
    }

    private void createAlertDialog(final int pos) {

        final CalendarModel model = list.get(pos);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = null;

        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alertLayout = inflater.inflate(R.layout.alertformodifytime, null);

        final EditText edtStartTime = alertLayout.findViewById(R.id.edtStartTime);
        final EditText edtEndTime = alertLayout.findViewById(R.id.edtEndTime);

        Button btnSave = (Button) alertLayout.findViewById(R.id.btnSave);


        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        dialog.show();


        try {

            String startTime = model.getFullDate().substring(0, model.getFullDate().indexOf("-"));
            String endTime = model.getFullDate().substring(model.getFullDate().indexOf("-") + 1, model.getFullDate().length());
            edtStartTime.setText(startTime);
            edtEndTime.setText(endTime);
        } catch (StringIndexOutOfBoundsException e) {

        }


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(edtStartTime.getText().toString().trim().isEmpty() || edtEndTime.getText().toString().trim().isEmpty())) {


                    try {

                        String time = edtStartTime.getText().toString().trim() + "-" + edtEndTime.getText().toString();
                        model.setFullDate(time);

                        Log.w(TAG,"date "+ model.getDate());
                        map.put(model.getDate(), time);

                        if (adapterTagDateModification != null) {
                            adapterTagDateModification.notifyDataSetChanged();
                        }

                        dialog.dismiss();


                    } catch (IndexOutOfBoundsException e) {

                    }

                }
            }
        });
    }

    public boolean isInActionMode = false;
}
