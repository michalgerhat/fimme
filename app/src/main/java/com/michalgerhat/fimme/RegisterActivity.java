package com.michalgerhat.fimme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    EditText txtUsername;
    EditText txtPassword;
    EditText txtRepeatPassword;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtUsername = findViewById(R.id.txtRegisterUsername);
        txtPassword = findViewById(R.id.txtRegisterPassword);
        txtRepeatPassword = findViewById(R.id.txtRepeatPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();
                String repeatPassword = txtRepeatPassword.getText().toString();

                if (password.equals(repeatPassword))
                {

                }
            }
        });
    }
}