package com.pucp.lab6_20175719;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserRegister extends AppCompatActivity {

    private FirebaseAuth Authentication;
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button createAccountButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_register);

        Authentication = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        createAccountButton = findViewById(R.id.createAccountButton);
        cancelButton = findViewById(R.id.cancelButton);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");
        emailEditText.setText(email);
        passwordEditText.setText(password);

        createAccountButton.setOnClickListener(v -> createAccount());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void createAccount() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Completar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        Authentication.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = Authentication.getCurrentUser();
                        Toast.makeText(this, "Se creo la cuenta", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserRegister.this, UserLogin.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Error en cuenta creada" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
