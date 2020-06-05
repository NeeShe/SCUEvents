package com.project.scuevents.ui.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.scuevents.ChangePasswordActivity;
import com.project.scuevents.CreateEventActivity;
import com.project.scuevents.NavigationActivity;
import com.project.scuevents.R;
import com.project.scuevents.SignInActivity;
import com.project.scuevents.model.FireBaseUtilClass;
import com.project.scuevents.model.UserDetails;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class MyProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG ="UserInfo";
    private static final int PICK_IMAGE_REQUEST =50 ;
    LinearLayout logoutButton;
    TextView changePassword;
    TextView firstn;
    TextView lastn;
    TextView em;
    SharedPreferences sh;
    DatabaseReference db;

    ImageButton add;

    ImageView imageView;
    Uri imageFilePath;

    Bitmap bitmap;

    FirebaseStorage storage;
    StorageReference storageReference;

    UserDetails user;
    private NavigationActivity YourActivity= new NavigationActivity();
    Context applicationContext = YourActivity.getContextOfApplication();
    ProgressDialog progressDialog;


    NavigationView navigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_profile, container, false);
        logoutButton = view.findViewById(R.id.logoutTab);
        changePassword = view.findViewById(R.id.changePassword);

        logoutButton.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        //Displaying user details
        firstn = view.findViewById(R.id.firstnval);
        lastn = view.findViewById(R.id.lastnval);
        em = view.findViewById(R.id.emailidval);
        imageView = view.findViewById(R.id.profilepic);
        add = view.findViewById(R.id.addimage);
        add.setOnClickListener(this);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        getUserInfo();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        navigationView = ((NavigationActivity)getActivity()).findViewById(R.id.nav_view);

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
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("USER_TOKENS", MODE_PRIVATE);
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
                //Toast.makeText(getActivity(), "Going to change password activity", Toast.LENGTH_LONG).show();
                startActivity(intent);
                break;
            case R.id.addimage:
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            imageFilePath = data.getData();
            try {
               bitmap = MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(), imageFilePath);
                imageView.setImageBitmap(bitmap);
                //add a delay
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Save Profile Picture").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                }, 2000);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //imageView.setImageBitmap(bitmap);
                    Log.d(TAG, "onClick: of YES");
                    if(imageFilePath!=null) {
                        uploadStorage(imageFilePath);
                    }
                    else
                        break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    private void saveToDevice(Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(applicationContext);
        File directory = cw.getDir("profile", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File mypath = new File(directory, "profilepic.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        //try to dynamically update
        View hView =  navigationView.getHeaderView(0);
        ImageView profImage = hView.findViewById(R.id.profimage);
        profImage.setImageBitmap(bitmap);
    }

    private void uploadStorage(Uri uri){
        Log.d(TAG, "uploadStorage: entered");
        Log.d(TAG, "uploadStorage: image file path "+imageFilePath);
        String folder="profile/";

       //FirebaseStorage storage = FirebaseStorage.getInstance();
        // ref = storage.getReferenceFromUrl(folder + UUID.randomUUID().toString());
        final StorageReference ref = storageReference.child(folder + UUID.randomUUID().toString());
        Log.d(TAG, "uploadStorage: assigning reference");
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("updating");
        progressDialog.show();
        ref.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Toast.makeText(CreateEventActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        StorageMetadata sMetaData= taskSnapshot.getMetadata();
                        Task<Uri> downloadUrl = ref.getDownloadUrl();
                        downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>(){
                            @Override
                            public void onSuccess(final Uri uri) {
                                user.setImageUri(uri.toString());
                                //call upload to database method
                                Log.d(TAG, "onSuccess: reference success");
                                uploadDatabase();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Upload Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void uploadDatabase(){
        SharedPreferences s = getActivity().getSharedPreferences("USER_TOKENS", MODE_PRIVATE);
        final String userId=s.getString("USER_ID", "");
        //Log.d(TAG, "uploadDatabase:accessing shared preferences "+userId);
        FireBaseUtilClass.getDatabaseReference().child("Users").child(userId).child("imageUri").setValue(user.getImageUri()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Uploaded Sucessfully",Toast.LENGTH_SHORT).show();
                saveToDevice(bitmap);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed to upload"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUserInfo(){
        db = FirebaseDatabase.getInstance().getReference();
        sh = getActivity().getSharedPreferences("USER_TOKENS", MODE_PRIVATE);
        final String userId=sh.getString("USER_ID", "");
        Log.d(TAG, "getUserInfo:"+userId);
        db = FirebaseDatabase.getInstance().getReference();
        Query query = db.child("Users").child(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: "+dataSnapshot.getValue());
               user= dataSnapshot.getValue(UserDetails.class);
               firstn.setText(user.getfName());
               lastn.setText(user.getlName());
               em.setText(user.getEmail());
                Log.d(TAG, "onDataChange: image uri "+user.getImageUri());
               if(user.getImageUri()!=null){
                   Glide.with(getActivity()).asBitmap().load(user.getImageUri()).into(imageView);
               }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }});
    }


}
