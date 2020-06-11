package com.example.lab08.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lab08.R;
import com.example.lab08.activity_photo;

import java.io.IOException;
import java.util.ArrayList;

public class LocalDataBaseAdapter extends RecyclerView.Adapter<LocalDataBaseAdapter.MyViewHolder> {
    Context context;
    ArrayList<LocalResponse> singleRowArrayList;
    SQLiteDatabase db;
    DataBaseHandler myDatabase;
    private String archivoSalida = null;

    public LocalDataBaseAdapter(Context context, ArrayList<LocalResponse> singleRowArrayList, SQLiteDatabase db, DataBaseHandler myDatabase) {
        this.context = context;
        this.singleRowArrayList = singleRowArrayList;
        this.db = db;
        this.myDatabase = myDatabase;
    }
    public LocalDataBaseAdapter(){};

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.local_database_items, null);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, @SuppressLint("RecyclerView") final int i) {
        myViewHolder.newsImage.setImageBitmap(getBitmapFromEncodedString(singleRowArrayList.get(i).image));
      //  myViewHolder.id.setText(singleRowArrayList.get(i).uid);
        myViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletedata(i,singleRowArrayList);
            }
        });
        myViewHolder.btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reproducir(i);
            }
        });
        myViewHolder.btn_max.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, activity_photo.class);
                intent.putExtra("photo",singleRowArrayList.get(i).image);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return singleRowArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView newsImage,delete,btn_play,btn_max;
        TextView id;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImage = (ImageView) itemView.findViewById(R.id.newsImage);
            delete = (ImageView) itemView.findViewById(R.id.delete);
            btn_play = itemView.findViewById(R.id.btn_play);
            btn_max = itemView.findViewById(R.id.btn_max);
        }
    }

    public void deletedata(final int position, final ArrayList<LocalResponse> singleRowArrayList){
        new AlertDialog.Builder(context)
                .setIcon(R.drawable.defaultimage)
                .setTitle("Delete result")
                .setMessage("Are you sure you want delete this result?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //This is where deletions should be handled
                        myDatabase.deleteEntry(singleRowArrayList.get(position).getUid());
                        singleRowArrayList.remove(position);
                        notifyItemRemoved(position);
                        notifyDataSetChanged();
                        myDatabase.close();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    public void Reproducir(int indiceGrabacion){
        MediaPlayer mediaPlayer = new MediaPlayer();

        try{
            archivoSalida = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Grabacion"+indiceGrabacion+".mp3";
            mediaPlayer.setDataSource(archivoSalida);
            mediaPlayer.prepare();
        }catch (IOException e){
        }

        mediaPlayer.start();
        //Toast.makeText(,"Reproduciendo audio",Toast.LENGTH_SHORT).show();
    }

    private Bitmap getBitmapFromEncodedString(String encodedString){

        byte[] arr = Base64.decode(encodedString, Base64.URL_SAFE);

        Bitmap img = BitmapFactory.decodeByteArray(arr, 0, arr.length);

        return img;

    }

    public ArrayList<LocalResponse> getSingleRowArrayList() {
        return singleRowArrayList;
    }

    public void setSingleRowArrayList(ArrayList<LocalResponse> singleRowArrayList) {
        this.singleRowArrayList = singleRowArrayList;
    }
}
