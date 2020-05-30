package com.project.scuevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import android.text.TextUtils;
import android.util.Log;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.scuevents.model.FireBaseUtilClass;
import com.project.scuevents.model.UserDetails;
import com.project.scuevents.model.ValidationClass;
import com.project.scuevents.service.MyFirebaseInstanceService;
import com.project.scuevents.ui.home.HomeFragment;


public class SignInActivity extends AppCompatActivity {
    final String TAG="LogoutModule";
    FirebaseAuth auth;
    private ProgressDialog progressDialog;
    TextView createAccount;
    Button signinButton;
    EditText emailEditText;
    EditText passwordEditText;
    DatabaseReference db;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        SharedPreferences prefsUser = getSharedPreferences("USER_TOKENS", Context.MODE_PRIVATE);
        String userID= prefsUser.getString("USER_ID", "null");
        Log.d(TAG ,"logged userID in SignIn Acivity " + userID);
        String userName = prefsUser.getString("USER_NAME", "null");
        Log.d(TAG ,"logged userName in SignIn Acivity " + userName);

        emailEditText = findViewById(R.id.email_textview);

        passwordEditText = findViewById(R.id.password_textview);

        auth = FirebaseAuth.getInstance();

        createAccount = findViewById(R.id.createAccount);
        signinButton = findViewById(R.id.signinButton);

        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(SignInActivity.this, NavigationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
        }

        progressDialog = new ProgressDialog(SignInActivity.this);

    }


    public void createaccount(View view) {
        Log.d(TAG, "create account clicked");
        Toast.makeText(SignInActivity.this, "going to create account activity", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }


    public void Signin(View view) {


       final String email = emailEditText.getText().toString().trim();
       final String password = passwordEditText.getText().toString().trim();
//        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        ValidationClass validationClass = new ValidationClass(emailEditText, passwordEditText);
        if (validationClass.validateEmail() && validationClass.validatePassword()) {




            progressDialog.setMessage("Signing In Please Wait...");
            progressDialog.show();

            //logging in the user
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //progressDialog.dismiss();

                            //FirebaseDatabase database = FireBaseUtilClass.getDatabase();
                            //if the task is successfull
                            if (task.isSuccessful()) {
                                if (auth.getCurrentUser().isEmailVerified()) {
                                    //saving the device tokens in database
                                    SharedPreferences pref = getSharedPreferences(MyFirebaseInstanceService.PREFERENCE_NAME, Activity.MODE_PRIVATE);
                                    String s = pref.getString("UserToken", "null");
                                    Log.d(TAG, "user fresh token upon Installation/reinstallation "+ s);
                                    FireBaseUtilClass.getDatabaseReference().child("UserTokens").child(s).setValue(true);
                                    //after email verification , need to save user id and user name over here, or else after uninstalling
                                    //and installing we won't get the user id and name as it is never going to the sign up activity as
                                    //user is already authenticated

                                    db= FireBaseUtilClass.getDatabaseReference().child("Users").child(auth.getCurrentUser().getUid());
                                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            UserDetails user = dataSnapshot.getValue(UserDetails.class);
                                            Log.d(TAG, "userID " + user.getUserID());
                                            Log.d(TAG, "userName " + user.getfName() + " " + user.getlName());

                                            if (email != null && email.equals(user.getEmail())) {
                                                SharedPreferences sharedPreferences = getSharedPreferences("USER_TOKENS", MODE_PRIVATE);
                                                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                                myEdit.putString("USER_ID", user.getUserID());
                                                myEdit.putString("USER_NAME", user.getfName() + " " + user.getlName());
                                                myEdit.commit();
                                                //starting the activity here due to the async nature of data fetching from db, but helpful only when
                                                //installing the app first time, as somehow first time its going to the navigation activity first and the
                                                //shared preference getting saved once data is retrieved
                                                //startActivity(new Intent(SignInActivity.this, NavigationActivity.class));
                                               /* Intent intent = new Intent(SignInActivity.this, NavigationActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                finish();
                                                startActivity(intent);
                                                progressDialog.hide();*/
                                            }else{
                                                Toast.makeText(SignInActivity.this, "Incorrect Email provided", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(SignInActivity.this, "Sorry Something went wrong ", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    //providing a delay to start the activity , so that shared preference gets saved
                                    mHandler.postDelayed(mUpdateTimeTask, 500);
                                   /* Intent intent = new Intent(SignInActivity.this, NavigationActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();
                                    startActivity(intent);
                                    progressDialog.hide();*/
                                    //startActivity(new Intent(SignInActivity.this, NavigationActivity.class));
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

    //starting the activity after delay
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            // do what you need to do here after the delay
            Log.d(TAG, "delaying the start of Navigation activity ");
            Intent intent = new Intent(SignInActivity.this, NavigationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
            progressDialog.hide();
        }
    };

    public void forgotpassword(View view) {

        Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);

    }
}



