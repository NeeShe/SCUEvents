package com.project.scuevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.project.scuevents.model.ValidationClass;

public class ForgotPasswordActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button resetButton;
    EditText emailEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.email_textview);
        auth = FirebaseAuth.getInstance();
        resetButton = findViewById(R.id.resetButton);


        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String email = emailEditText.getText().toString().trim();
                ValidationClass validationClass = new ValidationClass(emailEditText);
                if (validationClass.validateEmail()) {
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPasswordActivity.this, "Reset Link has been sent to the mail", Toast.LENGTH_LONG).show();
                                onBackPressed();
                            }
                            else{
                                //Toast.makeText(ForgotPasswordActivity.this, "Email Id not registered", Toast.LENGTH_LONG).show();
                                Snackbar snackbar_success = Snackbar
                                        .make(findViewById(android.R.id.content), "Email Id not registered", Snackbar.LENGTH_LONG);
                                snackbar_success.show();
                            }


                        }
                    });
                }

            }

        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
