package com.pucp.lab6_20175719;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.pucp.lab6_20175719.Fragments.Fragment_0;
import com.pucp.lab6_20175719.Fragments.Fragment_1;
import com.pucp.lab6_20175719.Fragments.Fragment_2;

public class UserIn extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth Authoritation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_interface);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Entry");

        setSupportActionBar(toolbar);

        replaceFragment(new Fragment_1());
        Authoritation = FirebaseAuth.getInstance();

        BottomNavigationView bottomInterfaceView = findViewById(R.id.bottom_Interface);
        bottomInterfaceView.setSelectedItemId(R.id.interface_entry);
        toolbar.setTitle("Entry");
        replaceFragment(new Fragment_1());
        bottomInterfaceView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.interface_entry) {
                toolbar.setTitle("Entry");
                replaceFragment(new Fragment_1());
                return true;
            } else if (itemId == R.id.interface_out) {
                toolbar.setTitle("Out");
                replaceFragment(new Fragment_2());
                return true;
            } else if (itemId == R.id.interface_1) {
                toolbar.setTitle("Resumen");
                replaceFragment(new Fragment_0());
                return true;
            } else if (itemId == R.id.interface_2) {
                toolbar.setTitle("Cerrar sesión");
                logout();
                return true;
            }
            return false;
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        fragmentTransaction.commit();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(UserIn.this, UserLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
