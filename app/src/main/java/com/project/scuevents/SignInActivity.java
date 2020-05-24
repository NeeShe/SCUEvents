package com.project.scuevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.project.scuevents.model.FireBaseUtilClass;
import com.project.scuevents.model.ValidationClass;
import com.project.scuevents.service.MyFirebaseInstanceService;
import com.project.scuevents.ui.home.HomeFragment;


public class SignInActivity extends AppCompatActivity {

    FirebaseAuth auth;
    private ProgressDialog progressDialog;
    TextView createAccount;
    Button signinButton;
    EditText emailEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        emailEditText = findViewById(R.id.email_textview);

        passwordEditText = findViewById(R.id.password_textview);

        auth = FirebaseAuth.getInstance();

        createAccount = findViewById(R.id.createAccount);
        signinButton = findViewById(R.id.signinButton);

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, NavigationActivity.class));
        }

        progressDialog = new ProgressDialog(SignInActivity.this);


    }


    public void createaccount(View view) {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }


    public void Signin(View view) {

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        ValidationClass validationClass = new ValidationClass(emailEditText, passwordEditText);
        if (validationClass.validateEmail() && validationClass.validatePassword()) {




            progressDialog.setMessage("Signing In Please Wait...");
            progressDialog.show();

            //logging in the user
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            //if the task is successfull
                            if (task.isSuccessful()) {
                                if (auth.getCurrentUser().isEmailVerified()) {

                                    startActivity(new Intent(SignInActivity.this, NavigationActivity.class));
                                } else {
                                    Toast.makeText(SignInActivity.this, "Email not verified", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(SignInActivity.this, "Sign In Unsuccessful", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public void forgotpassword(View view) {

        Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);

    }
}



