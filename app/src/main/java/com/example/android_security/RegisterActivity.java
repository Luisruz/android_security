package com.example.android_security;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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

    static String clave = "luis123";
    private static SecretKeySpec secret;
    private EditText edtEmail, edtPassRegister, edtRepeatPassRegister;
    private Button btnRegister, btnReturn;
    String passUno, passDos, email;

    private FirebaseAuth firebaseAuth;

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
                        //SecretKey secretKey = generateKey();
                        byte[] passEncrypt = encryptMsg(passUno, generateKey());
                        String passDecrypt = decryptMsg(passEncrypt, generateKey());
                        firebaseAuth.createUserWithEmailAndPassword(email, passDecrypt)
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Usuario Creado Exitosamente", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (IllegalBlockSizeException e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (BadPaddingException e) {
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
