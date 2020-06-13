package com.example.lab08;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.lab08.utils.Accelerometer;
import com.example.lab08.utils.Gyroscope;

public class GrabaVideo extends AppCompatActivity {

    private Accelerometer accelerometer;
    private Gyroscope gyroscope;
    private Button buttonRecord,cargarVideo;
    private VideoView vdVw;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graba_video);
        buttonRecord = findViewById(R.id.buttonRecord);
        vdVw = findViewById(R.id.vdVw);
        cargarVideo = findViewById(R.id.cargarVideo);
        mediaController= new MediaController(this);
        mediaController.setAnchorView(vdVw);
        //Location of Media File
        //Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.earth);
        //Starting VideView By Setting MediaController and URI

        accelerometer = new Accelerometer(this);
        gyroscope = new Gyroscope(this);

        accelerometer.setListener(new Accelerometer.Listener() {
            @Override
            public void onTranslation(float tx, float ty, float ts) {
                if(tx > 5.0f){
                    tx=0.0f;
                    cargarVideo();
                }
                else  if (tx < -5.0f) {
                    tx = 0.0f;
                    dispatchTakeVideoIntent();
                }
            }
        });

       /*gyroscope.setListener(new Gyroscope.Listener() {
           @Override
           public void onTranslation(float rx, float ry, float rs) {
               if(rs > 5.0f){
                   cargarVideo();
               }
               else  if (rs < -5.0f) {
                   dispatchTakeVideoIntent();
               }
           }
       });*/

        buttonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakeVideoIntent();
            }
        });

        cargarVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarVideo();
            }
        });

    }
    protected void onResume() {
        super.onResume();
        accelerometer.register();
        gyroscope.register();
    }

    protected void onPause() {
        super.onPause();
        accelerometer.unregister();
        gyroscope.unregister();
    }

    static final int REQUEST_VIDEO_CAPTURE = 1;

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void cargarVideo(){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        i.setType("video/");
        startActivityForResult(i.createChooser(i,"Selecciones la Aplicacion"),10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Uri path=data.getData();
            vdVw.setMediaController(mediaController);
            vdVw.setVideoURI(path);
            vdVw.requestFocus();
            vdVw.start();
        }
    }
}
