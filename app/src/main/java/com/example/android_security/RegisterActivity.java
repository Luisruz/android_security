package com.example.android_security;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class RegisterActivity extends AppCompatActivity {
    //cantidad de bytes permitidos para la clave 16/24/32
    static String clave = "luisFelipe123456";
    private static SecretKeySpec secret;
    private EditText edtEmail, edtPassRegister, edtRepeatPassRegister;
    private Button btnRegister, btnReturn;
    String passUno, passDos, email;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDB;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edtEmail = findViewById(R.id.edtEmailRegister);
        edtPassRegister = findViewById(R.id.edtPassRegister);
        edtRepeatPassRegister = findViewById(R.id.edtRepeatPass);
        btnRegister = findViewById(R.id.btnRegister);
        btnReturn = findViewById(R.id.btnReturn);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseDatabase.getInstance();
        dbReference = firebaseDB.getReference();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtEmail.getText().toString().trim();
                passUno = edtPassRegister.getText().toString().trim();
                passDos = edtRepeatPassRegister.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "Ingrese email", Toast.LENGTH_SHORT).show();
                    edtEmail.requestFocus();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(RegisterActivity.this, "email Invalido", Toast.LENGTH_SHORT).show();
                    edtEmail.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(passUno)) {
                    Toast.makeText(RegisterActivity.this, "Ingrese la Contraseña", Toast.LENGTH_SHORT).show();
                    edtPassRegister.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(passDos)) {
                    Toast.makeText(RegisterActivity.this, "Confirme la Contraseña", Toast.LENGTH_SHORT).show();
                    edtRepeatPassRegister.requestFocus();
                    return;
                } else if (passUno.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "La Contraseña debe Contener Minimo 6 Caracteres", Toast.LENGTH_SHORT).show();
                    edtPassRegister.requestFocus();
                    return;
                } else if (!passUno.equals(passDos)) {
                    Toast.makeText(RegisterActivity.this, "Las Contraseñas NO Coinciden", Toast.LENGTH_SHORT).show();
                    edtRepeatPassRegister.requestFocus();
                    return;
                } else {
                    try {
                        byte[] passEncrypt = encryptMsg(passUno, generateKey());
                        String passDecrypt = decryptMsg(passEncrypt, generateKey());
                        firebaseAuth.createUserWithEmailAndPassword(email, passDecrypt)
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            //obtengo el uid del user
                                            String uidUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            //guardo en realtime
                                            User user = new User();
                                            user.setUID(uidUser);
                                            user.setEmail(email);
                                            dbReference.child("Users").child(user.getUID()).setValue(user);
                                            //guardo en SQLite
                                            DBHelper dbHelper = new DBHelper(RegisterActivity.this);
                                            dbHelper.insertData(email, uidUser, RegisterActivity.this);
                                            edtEmail.setText("");
                                            edtPassRegister.setText("");
                                            edtRepeatPassRegister.setText("");
                                            Toast.makeText(RegisterActivity.this, "Usuario Creado Exitosamente", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intentLogin = new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(intentLogin);
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Ha Ocurrido un Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } catch (NoSuchAlgorithmException e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (NoSuchPaddingException e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (InvalidKeyException e) {
                        Log.i("error", e.toString());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (IllegalBlockSizeException e) {
                        Log.i("error", e.toString());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (BadPaddingException e) {
                        Log.i("error", e.toString());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (UnsupportedEncodingException e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentReturn = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intentReturn);
            }
        });
    }

    public static SecretKey generateKey() throws NoSuchAlgorithmException, InvalidKeyException {
        return secret = new SecretKeySpec(clave.getBytes(), "AES");
    }

    public static byte[] encryptMsg(String message, SecretKey secret) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
        return cipherText;
    }

    public static String decryptMsg(byte[] cipherText, SecretKey secret) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret);
        String decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
        return decryptString;
    }
}
