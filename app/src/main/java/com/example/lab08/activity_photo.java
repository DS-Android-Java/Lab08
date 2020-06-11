package com.example.lab08;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class activity_photo extends AppCompatActivity {

    ImageView photoNote;
    String photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        photoNote = findViewById(R.id.photoNote);
        photo = getIntent().getStringExtra("photo");
        photoNote.setImageBitmap(getBitmapFromEncodedString(photo));
        //Aca se prepara el popup
        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);

        int ancho = medidasVentana.widthPixels;
        int alto = medidasVentana.heightPixels;

        getWindow().setLayout((int)(ancho * 0.90), (int)(alto * 0.70));
    }

    private Bitmap getBitmapFromEncodedString(String encodedString){

        byte[] arr = Base64.decode(encodedString, Base64.URL_SAFE);

        Bitmap img = BitmapFactory.decodeByteArray(arr, 0, arr.length);

        return img;

    }
}