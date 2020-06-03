package com.project.scuevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.scuevents.model.FireBaseUtilClass;
import com.project.scuevents.model.UserDetails;

public class SignUpActivity extends AppCompatActivity {
    EditText fName;
    EditText lName;
    EditText email;
    EditText password;
    EditText confPassword;

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fName = findViewById(R.id.fName);
        lName = findViewById(R.id.lName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.pwd);
        confPassword=findViewById(R.id.cnfpwd);

        auth = FirebaseAuth.getInstance();
    }

    public void signup(View view) {
        hideKeyboard();
        String fname = fName.getText().toString().trim();
        String lname = lName.getText().toString().trim();
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString();
        String confirmPassword = confPassword.getText().toString();

        if (!validateEmail(emailStr)) {
            email.setError("Not a valid scu email");
            return;
        }
        if (!validatePassword(passwordStr)) {
            password.setError("Use at least 6 characters");
            return;
        }
        if (!validateConfirmPassword(passwordStr, confirmPassword)) {
            confPassword.setError("Password not match");
            return;
        }

        signUp(fname, lname, emailStr, passwordStr);

    }

    private void signUp(final String fname, final String lname, final String email, final String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                } else { //success
                    //verify user
                    verifyEmail();
                    updateToFirebase(fname, lname, email);
                }
            }
        });

    }
    private void verifyEmail(){
        final FirebaseUser user = auth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignUpActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        onBackPressed();
                    }
                });
    }
    private void updateToFirebase(final String fName,final String lName, final String email){
        String uid = auth.getCurrentUser().getUid();
        final UserDetails user = new UserDetails(fName,lName,email,uid);
        FireBaseUtilClass.getDatabaseReference().child("Users").child(auth.getCurrentUser().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.endsWith("@scu.edu");
    }
    public boolean validatePassword(String password) {
        return password.length() > 5;
    }

    public boolean validateConfirmPassword(String password, String password2) {
        return password.equals(password2) && password2.length() > 5;
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
