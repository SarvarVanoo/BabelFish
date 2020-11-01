package com.example.textextraction;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Button camera_button,done_button, translate_button;
    ImageView image;
    TextView extractedtext,beforetextview;
    public String text;
    static final int REQUEST_IMAGE_CAPTURE = 1;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera_button=findViewById(R.id.btn_camera);
        done_button=findViewById(R.id.btn_done);
        translate_button=findViewById(R.id.btn_translate);
        image=findViewById(R.id.image_view);
        extractedtext=findViewById(R.id.aftertextview);
        beforetextview=findViewById(R.id.beforetextview);


        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dispatchTakePictureIntent();
                //done_button.setVisibility(View.VISIBLE);
            }
        });

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                camera_button.setVisibility(View.VISIBLE);
                done_button.setVisibility(View.GONE);
                translate_button.setVisibility(View.GONE);
                extractedtext.setText("Extracted text will be shown here!");
                image.setVisibility(View.GONE);
                beforetextview.setVisibility(View.VISIBLE);

            }
        });


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
            camera_button.setVisibility(View.GONE);
            done_button.setVisibility(View.VISIBLE);
            translate_button.setVisibility(View.VISIBLE);
            image.setVisibility(View.VISIBLE);
            beforetextview.setVisibility(View.GONE);


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
            task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    String str = firebaseVisionText.getText();
                    extractedtext.setText(str);
                    //extractedtext.setMovementMethod(new ScrollingMovementMethod());
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });

            translate_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    text=extractedtext.getText().toString();
                    Intent intent = new Intent(MainActivity.this, Translation.class);
                    intent.putExtra("data",text);
                    startActivity(intent);
                }
            });
        }
    }
}
