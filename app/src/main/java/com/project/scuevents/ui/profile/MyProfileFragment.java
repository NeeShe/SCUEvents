package com.project.scuevents.ui.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.project.scuevents.ChangePasswordActivity;
import com.project.scuevents.R;
import com.project.scuevents.SignInActivity;
import com.project.scuevents.model.FireBaseUtilClass;


public class MyProfileFragment extends Fragment implements View.OnClickListener {
    LinearLayout logoutButton,changePasswordButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_profile, container, false);
        logoutButton = view.findViewById(R.id.logoutTab);
        changePasswordButton = view.findViewById(R.id.changePassword);


        logoutButton.setOnClickListener(this);
        changePasswordButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logoutTab:
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Are you sure?");
                alertDialog.setMessage("Click yes to Logout!");
                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }});

                alertDialog.setButton( Dialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener()    {
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getActivity(), "Going to sign in activity", Toast.LENGTH_LONG).show();
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("USER_TOKENS", Context.MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("USER_ID", "null");
                        myEdit.putString("USER_NAME", "null");
                        myEdit.commit();
                        DatabaseReference db = FireBaseUtilClass.getDatabaseReference();
                        db.onDisconnect().cancel();
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getActivity(), SignInActivity.class);
                        startActivity(intent);
                    }});
                alertDialog.show();
                break;
            case R.id.changePassword:
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                Toast.makeText(getActivity(), "Going to change password activity", Toast.LENGTH_LONG).show();
                startActivity(intent);
                break;
        }
    }
}
