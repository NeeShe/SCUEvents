package com.project.scuevents.model;

import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationClass {
    protected EditText emailEditText, passwordEditText;


    public ValidationClass(EditText emailEditText, EditText passwordEditText) {
        this.emailEditText = emailEditText;
        this.passwordEditText = passwordEditText;
    }

 public ValidationClass(EditText emailEditText){
     this.emailEditText = emailEditText;
 }

    public boolean validateEmail() {

        String str = emailEditText.getText().toString();
//            Pattern mPattern = Pattern.compile("([a-zA-Z0-9]+[@]scu[.]edu)");
//            Matcher matcher = mPattern.matcher(str.toString());
//            if(!matcher.find()) {
//                weightEditText.setText(); // Don't know what to place//                    
//                 }


        if (TextUtils.isEmpty(str)) {
            emailEditText.setError("This field is required.");
            emailEditText.requestFocus();
            return false;
        } else if (!(Patterns.EMAIL_ADDRESS.matcher(str).matches() && str.endsWith("@scu.edu"))) {
            emailEditText.setError("Invalid email address. Check again.");
            emailEditText.requestFocus();
            return false;
        }
        return true;
    }

//    !Patterns.EMAIL_ADDRESS.matcher(str).matches()

    public boolean validatePassword() {
        String str1 = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(str1)) {
            passwordEditText.setError("This field is required.");
            passwordEditText.requestFocus();
            return false;
        } else if (str1.length() < 6) {
            passwordEditText.setError("Too short password. Min 6 characters allowed.");
            passwordEditText.requestFocus();
            return false;
        }
        return true;
    }


}
