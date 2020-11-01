package com.example.textextraction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.languageid.IdentifiedLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.List;

public class Translation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText sourcetext,translatedtext;
    Spinner sourcespinner,targetspinner;
    Button dismiss, translate;
    String textvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);

        sourcetext=findViewById(R.id.sourcetext);
        sourcetext.setText(getIntent().getStringExtra("data"));
        Intent intent = getIntent();
        textvalue = sourcetext.getText().toString();

        sourcespinner=findViewById(R.id.sourcespinner);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.sourcetextarray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sourcespinner.setAdapter(adapter1);

        targetspinner=findViewById(R.id.targetspinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.targettextarray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        targetspinner.setAdapter(adapter2);

        //BUTTON
        dismiss=findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Translation.this.finish();
            }
        });

        //Translated text
        translatedtext=findViewById(R.id.translatedtext);

        translate=findViewById(R.id.translate);
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identifyLanguage();
            }
        });

    }

    private void identifyLanguage()
    {


        FirebaseLanguageIdentification languageIdentifier =
                FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
        languageIdentifier.identifyLanguage(textvalue)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                if (languageCode != "und") {
                                    int langCode = getLanguageCode(languageCode);
                                    translateText(langCode);
                                } else {
                                    Toast.makeText(getApplicationContext(),"Language not Identified", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be loaded or other internal error.
                                // ...
                            }
                        });
    }

    private int getLanguageCode(String language)
    {
        int langCode;
        switch (language)
        {
            case "en":
                langCode= FirebaseTranslateLanguage.EN;
                break;

            default:
                langCode=0;
        }

        return langCode;
    }

    private void translateText(int langCode)
    {
        //providing source language and target language
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(langCode)
                .setTargetLanguage(FirebaseTranslateLanguage.FR)
                .build();

        //Initiating
        final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance()
                .getTranslator(options);


        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();

        //downloading modle if needed
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {

                    }
                });

        translator.translate(textvalue).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s)
            {
                String str = s;
                translatedtext.setText(str);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
