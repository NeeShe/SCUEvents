package com.project.scuevents;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import com.google.android.material.navigation.NavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class NavigationActivity extends AppCompatActivity {

    private static final String TAG ="NavigationActivity" ;
    private AppBarConfiguration mAppBarConfiguration;
    public static Context contextOfApplication;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        this.setImage();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_createModify,R.id.nav_register,R.id.nav_profile,R.id.nav_chat)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        contextOfApplication = getApplicationContext();
    }

    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    void setImage(){
        Bitmap bitmap = null;
        ContextWrapper cw = new ContextWrapper(this);
        File directory = cw.getDir("profile", Context.MODE_PRIVATE);
        if(directory.exists()){
            File mypath = new File(directory, "profilepic.png");
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(mypath);
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(bitmap != null){
            View hView =  navigationView.getHeaderView(0);
            ImageView profImage = hView.findViewById(R.id.profimage);
            profImage.setImageBitmap(bitmap);
        }
    }
}
