package com.pucp.lab6_20175719.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pucp.lab6_20175719.Dto.DataDto;
import com.pucp.lab6_20175719.Entry.Entry;
import com.pucp.lab6_20175719.NewEntry;
import com.pucp.lab6_20175719.R;

public class Fragment_1 extends Fragment {

    private Entry entry;
    private RecyclerView recyclerViewUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_1, container, false);
        initializeViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateEntryList();
    }

    private void initializeViews(View view) {
        recyclerViewUser = view.findViewById(R.id.recyclerViewEntry);
        recyclerViewUser.setHasFixedSize(true);
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(getContext()));

        entry = new Entry(DataDto.getInstance().getEntryList(), item -> {
            Intent intent = new Intent(getActivity(), NewEntry.class);
            intent.putExtra("entry_type", "Entry");
            intent.putExtra("ListElement", item);
            startActivity(intent);
        });
        recyclerViewUser.setAdapter(entry);

        FloatingActionButton agregarUsuarioButton = view.findViewById(R.id.addEntry);
        agregarUsuarioButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewEntry.class);
            intent.putExtra("entry_type", "Entry");
            startActivity(intent);
        });
    }

    private void updateEntryList() {
        entry.notifyDataSetChanged();
    }


}
