package com.example.textextraction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
   CardView textDetection, textTranslation;
   DrawerLayout drawerLayout;
   NavigationView navigationView;
   Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        textDetection = (CardView) findViewById(R.id.text_detection_card_view);
        textTranslation = (CardView) findViewById(R.id.text_translation_card_view);
        textDetection.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DashboardActivity.this, TextDetectionActivity.class);
                        startActivity(intent);
                    }
                }
        );
        textTranslation.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DashboardActivity.this, TextTranslationActivity.class);
                        startActivity(intent);
                    }
                }
        );

        //HOOKS
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        toolbar=findViewById(R.id.toolbar);

        //TOOLBAR
        setSupportActionBar(toolbar);

        //Navigation Drawer Menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.nav_home:
                break;
            case R.id.nav_about:
                Intent intent_about= new Intent(DashboardActivity.this, About.class);
                startActivity(intent_about);
                break;
            case R.id.nav_contact:
                Intent intent_contact = new Intent(DashboardActivity.this, Contact.class);
                startActivity(intent_contact);
                break;
            case R.id.nav_share:
                Intent intent_share = new Intent(DashboardActivity.this, Share.class);
                startActivity(intent_share);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
