package com.project.scuevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    final String TAG="ChangePasswordModule";
    FirebaseAuth auth;
    FirebaseUser user;
    EditText emailId,oldPwd,newPwd,cnfPwd;
    Button changePwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        emailId = findViewById(R.id.etEmailId);
        oldPwd = findViewById(R.id.etOldPassword);
        newPwd = findViewById(R.id.etNewPassword);
        cnfPwd = findViewById(R.id.etConfirmPassword);

        changePwd = findViewById(R.id.btnChangePwd);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //getting the current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "user is " + user);
    }

    public void changePassword(View view){
       final String email = emailId.getText().toString().trim();
       final String  oldPswd = oldPwd.getText().toString().trim();
       final String newPswd = newPwd.getText().toString().trim();
       final String cnfPswd = cnfPwd.getText().toString().trim();

       //checking for empty input
        if(email.isEmpty() || oldPswd.isEmpty() || newPswd.isEmpty() || cnfPswd.isEmpty() ){
            Snackbar snackbar_su = Snackbar
                    .make(findViewById(android.R.id.content), "One or More field is empty, input fields cannot be empty", Snackbar.LENGTH_LONG);
            snackbar_su.show();
            return;
        }

        //password length validation
        if (!validatePassword(newPswd)) {
            newPwd.setError("Use at least 6 characters");
            return;
        }

        if (!validatePassword(cnfPswd) ) {
            cnfPwd.setError("Use at least 6 characters");
            return;
        }

        //if only new pswd equals to cnf pswd
        if(newPswd.equals(cnfPswd)) {

            //getting credential from firebase with use of email and old pswd
            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPswd);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPswd).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful()){
                                    Snackbar snackbar_fail = Snackbar
                                            .make(findViewById(android.R.id.content), "Something went wrong. Please try again later", Snackbar.LENGTH_LONG);
                                    snackbar_fail.show();
                                }else {
                                    Toast.makeText(ChangePasswordActivity.this, "Password Successfully Updated, Sign in with new Password", Toast.LENGTH_LONG).show();
                                    SharedPreferences sharedPreferences = ChangePasswordActivity.this.getSharedPreferences("USER_TOKENS", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                    myEdit.putString("USER_ID", "null");
                                    myEdit.putString("USER_NAME", "null");
                                    myEdit.commit();
                                    FirebaseAuth.getInstance().signOut();
                                    Intent intent = new Intent(ChangePasswordActivity.this, SignInActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                    }else{
                        Snackbar snackbar_su = Snackbar
                                .make(findViewById(android.R.id.content), "Authentication Failed", Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }
                }
            });
        }else{
            Snackbar snackbar_su = Snackbar
                    .make(findViewById(android.R.id.content), "Confirm Password, Password Mismatch", Snackbar.LENGTH_LONG);
            snackbar_su.show();
        }
    }

    public boolean validatePassword(String password) {
        return password.length() > 5;
    }
}
