package com.example.lab08.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab08.GrabaVideo;
import com.example.lab08.R;
import com.example.lab08.utils.DataBaseHandler;
import com.example.lab08.utils.LocalDataBaseAdapter;
import com.example.lab08.utils.LocalResponse;


import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CameraFragment extends Fragment {
    private static final int CAMERA_REQUEST = 1888;
    TextView text;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    //Bitmap photo;
    String photo;
    DataBaseHandler databaseHandler;
    private SQLiteDatabase db;
    Bitmap theImage;
    int contadorNotas;

    RecyclerView recyclerView;
    private ArrayList<LocalResponse> singleRowArrayList;
    private LocalResponse singleRow;
    String image;
    int uid;
    Cursor cursor;

    private MediaRecorder grabacion;
    private String archivoSalida = null;
    private ImageView btr_rec;
    LocalDataBaseAdapter localDataBaseResponse;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.camera_fragment,container,false);
        localDataBaseResponse = new LocalDataBaseAdapter();
        contadorNotas = 0;
        recyclerView = view.findViewById(R.id.recyclerview);
        databaseHandler = new DataBaseHandler(getContext());
        db = databaseHandler.getWritableDatabase();
        setData();

        text = view.findViewById(R.id.text);
        btr_rec = (ImageView) view.findViewById(R.id.btn_rec);
        btr_rec.setVisibility(View.INVISIBLE);

        text.setOnClickListener(
                new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        btr_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recorder();
            }
        });
       return view;
    }


    private void setDataToDataBase() {
        db = databaseHandler.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(databaseHandler.KEY_IMG_URL,getEncodedString(theImage));

        long id = db.insert(databaseHandler.TABLE_NAME, null, cv);
        if (id < 0) {
            Toast.makeText(getContext(), "Something went wrong. Please try again later...", Toast.LENGTH_LONG).show();
        } else {//Aca empezara la grabacion hasta que se pause el boton
            Toast.makeText(getContext(),"Hable se esta grabando pendeja",Toast.LENGTH_LONG).show();
            Recorder();
            Toast.makeText(getContext(), "Add successful", Toast.LENGTH_LONG).show();
        }
    }

    public void Recorder() {
        if(localDataBaseResponse.getSingleRowArrayList() == null) {//Si la lista esta vacia
            contadorNotas = 0;
        }else{//Si no cuente con los del tamanio de la misma
            contadorNotas = localDataBaseResponse.getItemCount();
        }
        if (grabacion == null) {//Quiere decir que no se esta grabando nada
            archivoSalida = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Grabacion"+contadorNotas+".mp3";
            grabacion = new MediaRecorder();
            grabacion.setAudioSource(MediaRecorder.AudioSource.MIC);
            grabacion.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            grabacion.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            grabacion.setOutputFile(archivoSalida);

            try {
                grabacion.prepare();
                grabacion.start();//Aca comienza a grabar
            } catch (IOException e) {
            }

            btr_rec.setBackgroundResource(R.drawable.rec);
            btr_rec.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Grabando...", Toast.LENGTH_SHORT).show();
        }else if(grabacion != null){//se esta ejecutando la grabacion
            grabacion.stop();
            grabacion.release();
            grabacion = null;
            btr_rec.setBackgroundResource(R.drawable.stop_rec);
            Toast.makeText(getContext(),"Grabacion Finalizada...",Toast.LENGTH_SHORT).show();
            btr_rec.setVisibility(View.INVISIBLE);
            setData();
        }
    }
    /**
     * Reuqesting for premissons
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getActivity(), "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(getActivity(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Start an activity for result
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
           theImage = (Bitmap) data.getExtras().get("data");
          photo=getEncodedString(theImage);
                setDataToDataBase();
        }
    }


    private String getEncodedString(Bitmap bitmap){

        ByteArrayOutputStream os = new ByteArrayOutputStream();

               bitmap.compress(Bitmap.CompressFormat.JPEG,100, os);

       /* or use below if you want 32 bit images

        bitmap.compress(Bitmap.CompressFormat.PNG, (0–100 compression), os);*/
        byte[] imageArr = os.toByteArray();

        return Base64.encodeToString(imageArr, Base64.URL_SAFE);

    }


    //LOCAL_FRAGMENT
    private void setData() {
        db = databaseHandler.getWritableDatabase();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        singleRowArrayList = new ArrayList<>();
        String[] columns = {DataBaseHandler.KEY_ID, DataBaseHandler.KEY_IMG_URL};
        cursor = db.query(DataBaseHandler.TABLE_NAME, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(DataBaseHandler.KEY_ID);
            int index2 = cursor.getColumnIndex(DataBaseHandler.KEY_IMG_URL);
            uid = cursor.getInt(index1);
            image = cursor.getString(index2);
            singleRow = new LocalResponse(image,uid);
            singleRowArrayList.add(singleRow);
        }
        if (singleRowArrayList.size()==0){
            //empty.setVisibility(View.VISIBLE);
            //recyclerView.setVisibility(View.GONE);
        }else {
            localDataBaseResponse = new LocalDataBaseAdapter(getContext(), singleRowArrayList, db, databaseHandler);
            recyclerView.setAdapter(localDataBaseResponse);
        }
    }

}
