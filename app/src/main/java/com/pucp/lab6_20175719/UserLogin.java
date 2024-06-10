package com.pucp.lab6_20175719;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pucp.lab6_20175719.Dto.DataDto;
import com.pucp.lab6_20175719.Entry.ListElementEntry;
import com.pucp.lab6_20175719.Out.ListElementOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserLogin extends AppCompatActivity {
    private FirebaseAuth Authentication;
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;

    private EditText userEmailEditText, userPasswordEditText;
    private Button userLoginButton, userRegisterButton, googleSignInBtn;


    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        authGoogle(account);
                    } catch (ApiException e) {
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.blue)));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Authentication = FirebaseAuth.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("587949494264-c800gj240fng74dmj4g3fk6gdf3kr9ou.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if (Authentication.getCurrentUser() != null) {
            updateUI(Authentication.getCurrentUser());
        }

        userEmailEditText = findViewById(R.id.userEmailEditText);
        userPasswordEditText = findViewById(R.id.userPasswordEditText);
        userLoginButton = findViewById(R.id.userLoginButton);
        userRegisterButton = findViewById(R.id.userRegisterButton);
        googleSignInBtn = findViewById(R.id.googleSignInBtn);

        userLoginButton.setOnClickListener(v -> loginUser());
        userRegisterButton.setOnClickListener(v -> startSignIn());
        googleSignInBtn.setOnClickListener(v -> signInWithGoogle());
        mCallbackManager = CallbackManager.Factory.create();
        MaterialButton customFacebookLoginButton = findViewById(R.id.facebookSignInBtn);
        customFacebookLoginButton.setOnClickListener(v -> {
            LoginManager.getInstance().logInWithReadPermissions(UserLogin.this, Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {}

                @Override
                public void onError(FacebookException error) {
                }
            });
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = Authentication.getCurrentUser();
        updateUI(currentUser);
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        Authentication.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = Authentication.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(UserLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void startSignIn() {
        Intent intent = new Intent(UserLogin.this, UserRegister.class);
        startActivity(intent);
    }


    public void loadEntry(Runnable onSuccess) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = Authentication.getCurrentUser().getUid();
        String path = "user/" + uid + "/Entry";
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

                        onSuccess.run();
                    } else {
                        Log.d("msg-test", "Loading : ", task.getException());
                    }
                });
    }

    public void loadOut(Runnable onSuccess) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = Authentication.getCurrentUser().getUid();
        String path = "user/" + uid + "/Out";
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
                        for (ListElementOut Out : OutList) {
                            Log.d("msg-test", "Out: " + Out.getMount());
                        }

                        onSuccess.run();
                    } else {
                        Log.d("msg-test", "Loading: ", task.getException());
                    }
                });
    }

    private void loginUser() {
        String email = userEmailEditText.getText().toString().trim();
        String password = userPasswordEditText.getText().toString().trim();


        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingrese un usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        Authentication.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = Authentication.getCurrentUser();
                        if (user != null) {
                            DataDto.getInstance().setUserId(user.getUid());
                        }
                        updateUI(user);
                    } else {
                        Intent intent = new Intent(UserLogin.this, UserRegister.class);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        startActivity(intent);
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void authGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        Authentication.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = Authentication.getCurrentUser();
                        if (user != null) {
                            DataDto.getInstance().setUserId(user.getUid());
                        }
                        updateUI(user);
                    } else {
                        Log.w("UserLogin", "Loading: ", task.getException());
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            loadEntry(() -> {
                loadOut(() -> {
                    Intent intent = new Intent(UserLogin.this, UserIn.class);
                    startActivity(intent);
                    finish();
                });
            });
        } else {
            Toast.makeText(this, "Loading:", Toast.LENGTH_SHORT).show();
        }
    }

}
