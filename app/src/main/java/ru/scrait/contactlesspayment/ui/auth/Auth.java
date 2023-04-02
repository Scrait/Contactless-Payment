package ru.scrait.contactlesspayment.ui.auth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ru.scrait.contactlesspayment.MainActivity;
import ru.scrait.contactlesspayment.R;
import ru.scrait.contactlesspayment.models.User;

public class Auth extends AppCompatActivity {
    private EditText edit_email, edit_password;
    private Intent intent;
    private final String PREFS_NAME = "MyPrefsLogs";
    private final String USER_KEY = "User";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference myDatabaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        myDatabaseReference = FirebaseDatabase.getInstance().getReference(USER_KEY);

        edit_email = findViewById(R.id.edit_email);
        edit_password = findViewById(R.id.edit_password);

        mAuth = FirebaseAuth.getInstance();

        if (ContextCompat.checkSelfPermission(Auth.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(Auth.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
        if (ContextCompat.checkSelfPermission(Auth.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(Auth.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 0);
        }
        if (ContextCompat.checkSelfPermission(Auth.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(Auth.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        Button btnLogin = findViewById(R.id.login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignIn();
            }
        });

        Button btnRegister = findViewById(R.id.register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignUp();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        intent = new Intent(Auth.this, MainActivity.class);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if(settings.getBoolean("hasLoggedIn", false)) {
            startActivity(intent);
        }

    }


    private void onClickSignIn() {
        String email = edit_email.getText().toString();
        String password = edit_password.getText().toString();
        if(!TextUtils.isEmpty(edit_email.getText().toString()) && !TextUtils.isEmpty(edit_password.getText().toString())) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast toast = Toast.makeText(getApplicationContext(), "Вы успешно вошли", Toast.LENGTH_SHORT);
                        toast.show();
                        Intent intent2 = new Intent(Auth.this, MainActivity.class);
                        startActivity(intent2);
                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("hasLoggedIn", true);
                        editor.apply();
                    } else {
                        Toast toast1 = Toast.makeText(getApplicationContext(), "Произошла ошибка при входе", Toast.LENGTH_SHORT);
                        toast1.show();
                    }
                }
            });
        } else {
            Toast toast2 = Toast.makeText(getApplicationContext(), "Введены пустые данные", Toast.LENGTH_SHORT);
            toast2.show();
        }
    }

    private void onClickSignUp() {
        String email = edit_email.getText().toString();
        String password = edit_password.getText().toString();
        if(!TextUtils.isEmpty(edit_email.getText().toString()) && !TextUtils.isEmpty(edit_password.getText().toString())) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = currentUser.getUid();
                        User newUser = new User(
                                email,
                                password
                        );
                        myDatabaseReference.child(uid).setValue(newUser);
                        Toast.makeText(getApplicationContext(), "Вы успешно зарегестрировались", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Произошла ошибка при регистрации", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
