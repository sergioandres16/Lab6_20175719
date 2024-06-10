package com.pucp.lab6_20175719.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pucp.lab6_20175719.Dto.DataDto;
import com.pucp.lab6_20175719.Entry.ListElementEntry;
import com.pucp.lab6_20175719.Out.ListElementOut;
import com.pucp.lab6_20175719.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Fragment_0 extends Fragment {

    private MaterialButton actionBtn;
    private Calendar currentCalendar = Calendar.getInstance();
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private FirebaseAuth firebaseAuth;
    private float totalEntryMth = 0f;
    private float totalOutMth = 0f;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_0, container, false);

        actionBtn = view.findViewById(R.id.action_Btn);
        firebaseAuth = FirebaseAuth.getInstance();

        actionBtn.setText(monthYearFormat.format(currentCalendar.getTime()));
        actionBtn.setOnClickListener(v -> espectMY());

        loadDataAndactulyC();

        return view;
    }

    private void loadDataAndactulyC() {
        fetchEntry(() -> fetchOut(this::actulyC));
    }


    private void espectMY() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (datePicker, year, month, dayOfMonth) -> {
                    currentCalendar.set(Calendar.YEAR, year);
                    currentCalendar.set(Calendar.MONTH, month);
                    actionBtn.setText(monthYearFormat.format(currentCalendar.getTime()));
                    loadDataAndactulyC();
                },
                currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));

        try {
            View dayView = datePickerDialog.getDatePicker().findViewById(
                    getResources().getIdentifier("android:id/day", null, null));
            if (dayView != null) {
                dayView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        datePickerDialog.show();
    }

    private void actulyC() {
        List<ListElementEntry> entries = DataDto.getInstance().getEntryList();
        List<ListElementOut> outs = DataDto.getInstance().getOutList();

        Map<Integer, Float> entryMthPerDay = new HashMap<>();
        Map<Integer, Float> outMthPerDay = new HashMap<>();

        for (ListElementEntry entry : entries) {
            if (eqMyY(entry.getDate(), currentCalendar)) {
                totalEntryMth += entry.getMount();
                int day = getDayM(entry.getDate());
                entryMthPerDay.put(day, entryMthPerDay.getOrDefault(day, 0f) + (float) entry.getMount());
            }
        }

        for (ListElementOut out : outs) {
            if (eqMyY(out.getDate(), currentCalendar)) {
                totalOutMth += out.getMount();
                int day = getDayM(out.getDate());
                outMthPerDay.put(day, outMthPerDay.getOrDefault(day, 0f) + (float) out.getMount());
            }
        }
    }

    private boolean eqMyY(String date, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar dateCalendar = Calendar.getInstance();
        try {
            dateCalendar.setTime(sdf.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                dateCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH);
    }

    private int getDayM(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar dateCalendar = Calendar.getInstance();
        try {
            dateCalendar.setTime(sdf.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateCalendar.get(Calendar.DAY_OF_MONTH);


    }

    private void fetchEntry(Runnable onSuccess) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        String path = "user/" + uid + "/entry";
        db.collection(path)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ListElementEntry> entries = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ListElementEntry entry = document.toObject(ListElementEntry.class);
                            entry.setId(document.getId());
                            entries.add(entry);
                        }
                        DataDto.getInstance().setEntryList(entries);

                        for (ListElementEntry entry : entries) {
                            Log.d("msg-test", "Entry: " + entry.getMount());
                        }

                        onSuccess.run();
                    } else {
                        Log.d("msg-test", "Error : ", task.getException());
                    }
                });
    }

    private void fetchOut(Runnable onSuccess) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        String path = "user/" + uid + "/out";
        db.collection(path)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ListElementOut> outs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ListElementOut out = document.toObject(ListElementOut.class);
                            out.setId(document.getId());
                            outs.add(out);
                        }
                        DataDto.getInstance().setOutList(outs);

                        for (ListElementOut out : outs) {
                            Log.d("msg-test", "Out: " + out.getMount());
                        }

                        onSuccess.run();
                    } else {
                        Log.d("msg-test", "Error: ", task.getException());
                    }
                });
    }
}
