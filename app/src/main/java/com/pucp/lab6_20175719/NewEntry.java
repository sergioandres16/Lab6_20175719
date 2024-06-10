package com.pucp.lab6_20175719;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pucp.lab6_20175719.Dto.DataDto;
import com.pucp.lab6_20175719.Entry.ListElementEntry;
import com.pucp.lab6_20175719.Out.ListElementOut;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NewEntry extends AppCompatActivity {

    private EditText etTitle, etMount, etDescription, etDate;
    private Button btnSave;
    private FloatingActionButton fabEdit;
    private FirebaseAuth Authentication;

    private FirebaseFirestore db;
    private MaterialToolbar topAppBar;
    private ListElementEntry currentEntry;
    private ListElementOut currentOut;
    private String entryType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_new_entry);

        etTitle = findViewById(R.id.title);
        etMount = findViewById(R.id.mount);
        etDescription = findViewById(R.id.description);
        etDate = findViewById(R.id.date);
        btnSave = findViewById(R.id.createButton);
        fabEdit = findViewById(R.id.fabEdit);
        topAppBar = findViewById(R.id.topAppBar);

        db = FirebaseFirestore.getInstance();
        Authentication = FirebaseAuth.getInstance();

        entryType = getIntent().getStringExtra("entry_type");
        if ("Entry".equals(entryType)) {
            currentEntry = (ListElementEntry) getIntent().getSerializableExtra("ListElement");
        } else if ("Out".equals(entryType)) {
            currentOut = (ListElementOut) getIntent().getSerializableExtra("ListElement");
        }

        if (currentEntry != null && "Entry".equals(entryType)) {
            configureViewForDetails(currentEntry.getTitle(), currentEntry.getMount(), currentEntry.getDescription(), currentEntry.getDate(), "Entry");
            btnSave.setOnClickListener(v -> updateEntry());
        } else if (currentOut != null && "Out".equals(entryType)) {
            configureViewForDetails(currentOut.getTitle(), currentOut.getMount(), currentOut.getDescription(), currentOut.getDate(), "Out");
            btnSave.setOnClickListener(v -> updateOut());
        } else {
            topAppBar.setTitle("Nuevo " + entryType);
            fabEdit.setVisibility(View.GONE);
            btnSave.setOnClickListener(v -> saveEntry(entryType));
        }

        topAppBar.setNavigationOnClickListener(v -> finish());

        etDate.setOnClickListener(v -> showDatePicker());
        etDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePicker();
            }
        });
    }

    private void configureViewForDetails(String title, double Mount, String description, String date, String type) {
        topAppBar.setTitle("Detalles de " + type);
        etTitle.setText(title);
        etMount.setText(String.valueOf(Mount));
        etDescription.setText(description);
        etDate.setText(date);

        etTitle.setEnabled(false);
        etMount.setEnabled(false);
        etDescription.setEnabled(false);
        etDate.setEnabled(false);
        btnSave.setText("Actualizar");
        btnSave.setVisibility(View.INVISIBLE);

        fabEdit.setVisibility(View.VISIBLE);
        fabEdit.setOnClickListener(v -> {
            etMount.setEnabled(true);
            etDescription.setEnabled(true);
            btnSave.setVisibility(View.VISIBLE);
            fabEdit.setVisibility(View.GONE);
            topAppBar.setTitle("Editar " + type);
        });


    }

    private void showDatePicker() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Selecciona una fecha");
        final MaterialDatePicker<Long> picker = builder.build();
        picker.show(getSupportFragmentManager(), picker.toString());

        picker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            etDate.setText(sdf.format(selection));
        });
    }


    private void saveEntry(String entryType) {
        String title = etTitle.getText().toString().trim();
        String MountString = etMount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(MountString) || TextUtils.isEmpty(date)) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double Mount;
        try {
            Mount = Double.parseDouble(MountString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> entry = new HashMap<>();
        entry.put("title", title);
        entry.put("Mount", Mount);
        entry.put("description", description);
        entry.put("date", date);

        String uid = Authentication.getCurrentUser().getUid();
        String path = "users/" + uid + "/" + entryType + "s";
        Log.d("msg-test", path);

        db.collection(path)
                .add(entry)
                .addOnSuccessListener(documentReference -> {
                    String id = documentReference.getId();
                    if ("Entry".equals(entryType)) {
                        ListElementEntry newEntry = new ListElementEntry(id, title, Mount, description, date);
                        loadEntryFromFirestore();

                    } else {
                        ListElementOut newEntry = new ListElementOut(id, title, Mount, description, date);
                        loadOutFromFirestore();

                    }
                    Toast.makeText(NewEntry.this, "Entrada guardada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NewEntry.this, "Error al guardar entrada", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateEntry() {
        String MountString = etMount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(MountString) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double Mount;
        try {
            Mount = Double.parseDouble(MountString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> entry = new HashMap<>();
        entry.put("Mount", Mount);
        entry.put("description", description);

        String uid = Authentication.getCurrentUser().getUid();
        String path = "users/" + uid + "/" + entryType + "s";
        Log.d("msg-test", path);

        db.collection(path).document(currentEntry.getId())
                .update(entry)
                .addOnSuccessListener(aVoid -> {
                    currentEntry.setMount(Mount);
                    currentEntry.setDescription(description);
                    Toast.makeText(NewEntry.this, "Entry actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NewEntry.this, "Error al actualizar Entry", Toast.LENGTH_SHORT).show();
                });
        loadEntryFromFirestore();

    }

    private void updateOut() {
        String MountString = etMount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(MountString) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double Mount;
        try {
            Mount = Double.parseDouble(MountString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> entry = new HashMap<>();
        entry.put("Mount", Mount);
        entry.put("description", description);

        String uid = Authentication.getCurrentUser().getUid();
        String path = "users/" + uid + "/" + entryType + "s";
        Log.d("msg-test", path);
        db.collection(path).document(currentOut.getId())
                .update(entry)
                .addOnSuccessListener(aVoid -> {
                    currentOut.setMount(Mount);
                    currentOut.setDescription(description);
                    Toast.makeText(NewEntry.this, "Out actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NewEntry.this, "Error al actualizar Out", Toast.LENGTH_SHORT).show();
                });
        loadOutFromFirestore();
    }

    private void loadOutFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = Authentication.getCurrentUser().getUid();
        String path = "users/" + uid + "/Out";
        db.collection(path)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ListElementOut> OutList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ListElementOut Out = document.toObject(ListElementOut.class);
                            Out.setId(document.getId());
                            OutList.add(Out);
                        }
                        DataDto.getInstance().setOutList(OutList);
                    } else {
                        Log.d("msg-test", "Error: ", task.getException());
                    }
                });

    }

    public void loadEntryFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = Authentication.getCurrentUser().getUid();
        String path = "users/" + uid + "/Entry";
        db.collection(path)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ListElementEntry> EntryList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ListElementEntry Entry = document.toObject(ListElementEntry.class);
                            Entry.setId(document.getId());
                            EntryList.add(Entry);
                        }
                        DataDto.getInstance().setEntryList(EntryList);
                        for (ListElementEntry Entry : EntryList) {
                            Log.d("msg-test", "Entry: " + Entry.getMount());
                        }
                    } else {
                        Log.d("msg-test", "Error: ", task.getException());
                    }
                });
    }

}
