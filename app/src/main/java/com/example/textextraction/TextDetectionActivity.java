package com.example.textextraction;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

public class TextDetectionActivity extends AppCompatActivity {

    Button cameraButton, doneButton, translateButton;
    ImageView image;
    TextView extractedText, beforeTextView;
    Animation topAnimation;
    static final int REQUEST_IMAGE_CAPTURE = 1;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_detection);

        //Initializing all the elements
        cameraButton =findViewById(R.id.btn_camera);
        doneButton =findViewById(R.id.btn_done);
        translateButton =findViewById(R.id.btn_translate);
        image=findViewById(R.id.image_view);
        extractedText =findViewById(R.id.aftertextview);
        beforeTextView =findViewById(R.id.beforetextview);

        //Adding OnClickListeners for Buttons
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dispatchTakePictureIntent();
                //doneButton.setVisibility(View.VISIBLE);
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
//                cameraButton.setVisibility(View.VISIBLE);
//                doneButton.setVisibility(View.GONE);
//                translateButton.setVisibility(View.GONE);
//                extractedText.setText("Extracted text will be shown here!");
//                image.setVisibility(View.GONE);
//                beforeTextView.setVisibility(View.VISIBLE);
                Intent intent = new Intent(TextDetectionActivity.this, DashboardActivity.class);
                startActivity(intent);
                TextDetectionActivity.this.finish();

            }
        });

        topAnimation = AnimationUtils.loadAnimation(TextDetectionActivity.this, R.anim.top_animation);
        beforeTextView.setAnimation(topAnimation);

    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            //get image from bundle
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //set image in imageview
            image.setImageBitmap(imageBitmap);
            cameraButton.setVisibility(View.GONE);
            doneButton.setVisibility(View.VISIBLE);
            translateButton.setVisibility(View.VISIBLE);
            image.setVisibility(View.VISIBLE);
            beforeTextView.setVisibility(View.GONE);


            //Create firebasevisionimage object from a bitmap object
            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
            //get firebase instance
            FirebaseVision firebaseVision= FirebaseVision.getInstance();
            //firebase visiontext recognizer
            FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getOnDeviceTextRecognizer();

//            FirebaseVisionCloudTextRecognizerOptions options =
//                    new FirebaseVisionCloudTextRecognizerOptions.Builder()
//                            .setLanguageHints(Arrays.asList("en", "hi"))
//                            .build();
//            FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
//                    .getCloudTextRecognizer(options);



            //process image
            Task<FirebaseVisionText> task=firebaseVisionTextRecognizer.processImage(firebaseVisionImage);
            task.addOnSuccessListener( new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    String firebaseExtractedText = firebaseVisionText.getText();
                    if(firebaseExtractedText.isEmpty())
                    {
                        extractedText.setText("Hey, Seems we were not able to extract the Text from the Image you provided! :( Could you try clicking the Picture again?");
                    }
                    else {
                        extractedText.setText(firebaseExtractedText);
                    }
                    //extractedText.setMovementMethod(new ScrollingMovementMethod());
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    extractedText.setText("Hey, Seems we were not able to extract the Text from the Image you provided! :( Could you try again please?");
                    String errorMsg = "Failure while extracting Text from Image! Error Message: "+ e.getMessage();
                    Toast.makeText(getApplicationContext(),errorMsg,Toast.LENGTH_LONG).show();
                }
            });

            translateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    String textToTranslate = extractedText.getText().toString();
                    Intent intent = new Intent(TextDetectionActivity.this, TextTranslationActivity.class);
                    intent.putExtra("textToTranslate",textToTranslate);
                    startActivity(intent);
                }
            });
        }
    }
}
