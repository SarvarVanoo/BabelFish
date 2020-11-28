package com.example.textextraction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DashboardActivity extends AppCompatActivity {
   CardView textDetection, textTranslation;
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
    }
}
