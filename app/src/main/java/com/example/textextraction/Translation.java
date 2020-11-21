package com.example.textextraction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import java.util.Locale;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Translation extends AppCompatActivity  {

    private String TAG = "TranslationClass";
    protected EditText sourceText, translatedText;
    protected Spinner sourceLanguageSpinner, targetLanguageSpinner;
    protected String currentSourceLangShortCode, currentTargetLangShortCode;
    protected Button dismissButton, translateButton,copy1, copy2, speech1, speech2;
    protected TextToSpeech textToSpeech1, textToSpeech2;  
    protected List<String> supportedSourceLanguages;
    protected List<String> supportedTargetLanguages;
    final protected Locale defaultLocale = Locale.US;
    final String defaultSourceLanguage = "English";
    final String defaultTargetLanguage = "Hindi";
    protected static Map<String,FirebaseTranslator> translatorOptions = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);

        //intilialize all the elements
        Resources resources = getResources();
        String textToTranslate = getIntent().getStringExtra("textToTranslate");
        sourceText = findViewById(R.id.sourcetext);
        sourceText.setText(textToTranslate);

        supportedSourceLanguages = Arrays.asList(resources.getStringArray(R.array.supportedSourceLanguages));
        supportedTargetLanguages = Arrays.asList(resources.getStringArray(R.array.supportedTargetLanguages));
        Log.i(TAG, supportedSourceLanguages.toString());
        Log.i(TAG, supportedTargetLanguages.toString());
        sourceLanguageSpinner = findViewById(R.id.sourcespinner);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.supportedSourceLanguages, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sourceLanguageSpinner.setAdapter(adapter1);
        //set the on Item selected listener , basically tells what to do when on item is selected.
        sourceLanguageSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedLanguage = parent.getItemAtPosition(position).toString();
                        currentSourceLangShortCode = getShortCodeFromLanguageName(selectedLanguage);
//                        String textToTranslate = sourceText.getText().toString();
//                        if (!textToTranslate.isEmpty())
//                        {
//                            translateText(currentSourceLangShortCode,currentTargetLangShortCode,textToTranslate);
//                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        currentSourceLangShortCode = getShortCodeFromLanguageName(defaultSourceLanguage);
                        int defaultLangPosition = ((ArrayAdapter<String>)parent.getAdapter()).getPosition(defaultSourceLanguage);
                        sourceLanguageSpinner.setSelection(defaultLangPosition);
                    }
                }
        );


        targetLanguageSpinner = findViewById(R.id.targetspinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.supportedTargetLanguages, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        targetLanguageSpinner.setAdapter(adapter2);

        //set the on Item selected listener , basically tells what to do when on item is selected.
        targetLanguageSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedLanguage = parent.getItemAtPosition(position).toString();
                        currentTargetLangShortCode = getShortCodeFromLanguageName(selectedLanguage);
//                        String textToTranslate = sourceText.getText().toString();
//                        if (!textToTranslate.isEmpty())
//                        {
//                            translateText(currentSourceLangShortCode,currentTargetLangShortCode,textToTranslate);
//                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        currentTargetLangShortCode = getShortCodeFromLanguageName(defaultTargetLanguage);
                        int defaultLangPosition = ((ArrayAdapter<String>)parent.getAdapter()).getPosition(defaultSourceLanguage);
                        targetLanguageSpinner.setSelection(defaultLangPosition);


                    }
                }
        );

        //BUTTON
        dismissButton = findViewById(R.id.dismiss);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Translation.this.finish();
            }
        });

        //Translated text
        translatedText =findViewById(R.id.translatedtext);

        translateButton =findViewById(R.id.translate);
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identifySourceLanguage();
            }
        });

        //copy1 button
        copy1 = findViewById(R.id.copy1);
        copy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("EditText", sourceText.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(Translation.this, "Text Copied", Toast.LENGTH_SHORT).show();
            }
        });

        //copy2 button
        copy2 = findViewById(R.id.copy2);
        copy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("EditText", translatedText.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(Translation.this, "Text Copied", Toast.LENGTH_SHORT).show();
            }
        });


        //Text to Speech
        textToSpeech1= new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS)
                {
                    textToSpeech1.setLanguage(Locale.ENGLISH);
                }
            }
        });


        //speech1 button
        speech1 = findViewById(R.id.speech1);
        speech1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        //speech2 button
        speech2 = findViewById(R.id.speech2);
        speech2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }

    private void identifySourceLanguage()
    {
        final FirebaseNaturalLanguage firebaseNaturalLanguage = FirebaseNaturalLanguage.getInstance();
        final FirebaseLanguageIdentification languageIdentifier = firebaseNaturalLanguage.getLanguageIdentification();
        String textToTranslate = sourceText.getText().toString();
        languageIdentifier.identifyLanguage(textToTranslate)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageShortCode) {
                                if (!languageShortCode.equals("und")) {
//                                    int langCode = getFirebaseLanguageCode(languageShortCode);
//                                    translateText(langCode, Translation.this.textToTranslate);
                                    String identifiedLanguageName = getLanguageNameFromShortCode(languageShortCode);
                                    Log.i(TAG,"Identified Language code: "+ languageShortCode);
                                    Log.i(TAG,"Identified Language Name: "+ identifiedLanguageName);
                                    if(supportedSourceLanguages.contains(identifiedLanguageName))
                                    {
                                        setSpinnerSelection(sourceLanguageSpinner,identifiedLanguageName);
                                        currentSourceLangShortCode = languageShortCode;
                                        String textToTranslate = sourceText.getText().toString();
                                        translateText(currentSourceLangShortCode,currentTargetLangShortCode,textToTranslate);

                                    }
                                    else{
                                        String msg = "Translation of Language: "+ identifiedLanguageName + "Not supported yet!";
                                        Log.i(TAG,msg);
                                        Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
                                        setSpinnerSelection(sourceLanguageSpinner,defaultSourceLanguage);
                                        setSpinnerSelection(targetLanguageSpinner,defaultTargetLanguage);
                                        String textToTranslate = sourceText.getText().toString();
                                        translateText(currentSourceLangShortCode,currentTargetLangShortCode,textToTranslate);
                                    }

//                                    int langCode = getFirebaseLanguageCode(languageShortCode);
//                                    translateText(langCode, , Translation.this.textToTranslate);

                                } else {
                                    Toast.makeText(getApplicationContext(),"Language not Identified!", Toast.LENGTH_SHORT).show();
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

    private int getFirebaseLanguageCode(String langShortCode)
    {
        return FirebaseTranslateLanguage.languageForLanguageCode(langShortCode);
    }

    private void translateText(String sourceLangShortCode, String targetLangShortCode, String textToTranslate)
    {
        //providing source language and target language
        final String sourceLangName = getLanguageNameFromShortCode(sourceLangShortCode);
        final String targetLangName = getLanguageNameFromShortCode(targetLangShortCode);
        int sourceLangFirebaseCode = getFirebaseLanguageCode(sourceLangShortCode);
        int targetLangFirebaseCode = getFirebaseLanguageCode(targetLangShortCode);
        FirebaseTranslator translator;
        String src_to_tgt = sourceLangName +"_TO_" + targetLangName;
        if (! translatorOptions.containsKey(src_to_tgt))
        {
           translator = initializeTranslator(sourceLangFirebaseCode,targetLangFirebaseCode);
           translatorOptions.put(src_to_tgt,translator);
        }
        else{
            translator = translatorOptions.get(src_to_tgt);
        }

        translator.translate(textToTranslate).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s)
            {
                String str = s;
                translatedText.setText(str);
                Log.i(TAG,"Translation was successful from Source Language : " + sourceLangName +" to Target Language : "+ targetLangName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Error occurred while Translating from Source Language : " + sourceLangName +" to Target Language : "+ targetLangName);
            }
        });
    }


    private FirebaseTranslator initializeTranslator(final int sourceLangFirebaseCode, final int targetLangFirebaseCode)
    {
        //setup Translator options
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(sourceLangFirebaseCode)
                .setTargetLanguage(targetLangFirebaseCode)
                .build();

        //Initiating the translator
        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance()
                .getTranslator(options);

        //setting download conditions
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
               // .requireWifi()
                .build();

        //downloading module if needed
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    Log.i(TAG,"Translator Model Downloaded successfully for Source code: " + sourceLangFirebaseCode +" and Target code: "+ targetLangFirebaseCode);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.e(TAG,"Translator Model Download failed for Source code: " + sourceLangFirebaseCode +" and Target code: "+ targetLangFirebaseCode);
                    }
                });
        return translator;
    }

    private String getLanguageNameFromShortCode( String languageCode) {
        return new Locale(languageCode).getDisplayName();
    }

    private String getShortCodeFromLanguageName ( String languageName )
    {
        String langShortCode = defaultLocale.getLanguage();
        for ( Locale locale : Locale.getAvailableLocales() )
        {
            if(locale.getDisplayLanguage().equalsIgnoreCase(languageName))
            {
                langShortCode = locale.getLanguage();
                break;
            }
        }
        return langShortCode;
    }

    protected void setSpinnerSelection(Spinner spinner, String selectionValue )
  {
      int index = ((ArrayAdapter<String>)spinner.getAdapter()).getPosition(selectionValue);
      Log.i(TAG,"Spinner selection position : "+index);
      spinner.setSelection(index);
  }

}
