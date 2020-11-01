package com.example.babelfish;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

public class MainActivity extends AppCompatActivity {

    Button cameraButton, doneButton, translateButton;
    ImageView image;
    TextView extractedText, beforeTextView;
    public String text;
    static final int REQUEST_IMAGE_CAPTURE = 1;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraButton =findViewById(R.id.btn_camera);
        doneButton =findViewById(R.id.btn_done);
        translateButton =findViewById(R.id.btn_translate);
        image=findViewById(R.id.image_view);
        extractedText =findViewById(R.id.aftertextview);
        beforeTextView =findViewById(R.id.beforetextview);


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
                cameraButton.setVisibility(View.VISIBLE);
                doneButton.setVisibility(View.GONE);
                translateButton.setVisibility(View.GONE);
                extractedText.setText("Extracted text will be shown here!");
                image.setVisibility(View.GONE);
                beforeTextView.setVisibility(View.VISIBLE);

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
            task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    String str = firebaseVisionText.getText();
                    extractedText.setText(str);
                    //extractedText.setMovementMethod(new ScrollingMovementMethod());
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });

            translateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    text= extractedText.getText().toString();
                    Intent intent = new Intent(MainActivity.this, Translation.class);
                    intent.putExtra("data",text);
                    startActivity(intent);
                }
            });
        }
    }
}
