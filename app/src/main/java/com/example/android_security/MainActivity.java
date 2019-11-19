package com.example.android_security;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText edtEmail, edtPass;
    private Button btnlogin, btnRegister;
    private String user, pass;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnlogin = findViewById(R.id.btnLogin);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser!=null){
            Intent intentHome = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intentHome);
        }

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = edtEmail.getText().toString().trim();
                pass = edtPass.getText().toString().trim();
                if (TextUtils.isEmpty(user)) {
                    Toast.makeText(MainActivity.this, "Ingrese Correo", Toast.LENGTH_SHORT).show();
                    edtEmail.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(MainActivity.this, "Ingrese Contraseña", Toast.LENGTH_SHORT).show();
                    edtPass.requestFocus();
                    return;
                } else {
                    firebaseAuth.signInWithEmailAndPassword(user, pass)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent login = new Intent(MainActivity.this, HomeActivity.class);
                                        startActivity(login);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Correo y/o Contraseña Incorrectos", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegister = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intentRegister);
            }
        });

    }

}
