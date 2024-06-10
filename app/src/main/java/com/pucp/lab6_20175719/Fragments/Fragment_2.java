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
import com.pucp.lab6_20175719.Out.Out;
import com.pucp.lab6_20175719.NewEntry;
import com.pucp.lab6_20175719.R;

public class Fragment_2 extends Fragment {
    private Out out;
    private RecyclerView recyclerViewUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2, container, false);
        initializeViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateOutList();
    }

    private void initializeViews(View view) {
        recyclerViewUsers = view.findViewById(R.id.recyclerViewOut);
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        out = new Out(DataDto.getInstance().getOutList(), out -> {
            Intent intent = new Intent(getActivity(), NewEntry.class);
            intent.putExtra("entry_type", "out");
            intent.putExtra("ListElement", out);
            startActivity(intent);
        });
        recyclerViewUsers.setAdapter(out);

        FloatingActionButton agregarUsuarioButton = view.findViewById(R.id.addOut);
        agregarUsuarioButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewEntry.class);
            intent.putExtra("entry_type", "out");
            startActivity(intent);
        });
    }

    private void updateOutList() {
        out.notifyDataSetChanged();
    }

}
