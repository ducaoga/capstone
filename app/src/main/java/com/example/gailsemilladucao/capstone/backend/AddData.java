package com.example.gailsemilladucao.capstone.backend;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gailsemilladucao.capstone.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AddData extends AppCompatActivity {

    ImageView mimage;
    Button recstart,recstop,play,pause,attach;
    String savepath = "",preset ="";
    File file;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    TextView info;

    final int REQUEST_PERMISSION_CODE = 1000;
    final int REQUEST_PERMISSION_GALLERY = 999;
    final static int RQS_OPEN_AUDIO_MP3 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        //request runtime permission
        if(!checkPermissionFromDevice())
            requestPermission();

        //binding
        info = findViewById(R.id.info);
        attach = findViewById(R.id.attach);
        mimage = findViewById(R.id.image);
        recstart = findViewById(R.id.recstart);
        recstop =  findViewById(R.id.recstop);
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);

        //from android m, you need request runtime permission

        recstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermissionFromDevice()){

                    savepath = Environment.getExternalStorageDirectory()
                            .getAbsolutePath()+"/"
                            + UUID.randomUUID().toString()+".3gp";
                    setupMediaRecorder();

                    info.setText(savepath);
                    try{
                        mediaPlayer = new MediaPlayer();
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    play.setEnabled(false);
                    pause.setEnabled(false);
                    recstop.setEnabled(true);
                    recstart.setEnabled(false);

                    Toast.makeText(AddData.this, "now recording", Toast.LENGTH_SHORT).show();

                }else{
                    requestPermission();
                }

            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lol = new Intent();
                lol.setType("audio/*");
                lol.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(
                        lol, "Open Audio (mp3) file"), RQS_OPEN_AUDIO_MP3);
            }
        });

        recstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                recstop.setEnabled(false);
                play.setEnabled(true);
                recstart.setEnabled(true);
                pause.setEnabled(false);
            }
        });

        //The Play Button is an event that plays the record
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause.setEnabled(true);
                recstop.setEnabled(false);
                recstart.setEnabled(false);
                play.setEnabled(false);

                mediaPlayer = new MediaPlayer();
                try {
                    //It checks wether the TextView->Where the file path of the attach file is shown, is empty or not
                    //if it is empty, then it will assume that it will play on the recorded audio
                    if(info.getText() == "" && savepath != ""){
                        mediaPlayer.setDataSource(savepath);
                        mediaPlayer.prepare();
                        Toast.makeText(AddData.this, "hahahhaah...", Toast.LENGTH_SHORT).show();
                    }
                    //else, it will read the filepath inn the textview and play the audio
                    else{
                        //code here is needed to play the attach audio file
                        //doesnt work because we didnt get the audio attach file yet
                        savepath = info.getText().toString();//this portion gets the text that was in the TextView
                        mediaPlayer.setDataSource(savepath);//will read the path
                        mediaPlayer.prepare();
                        Toast.makeText(AddData.this, "Playing...", Toast.LENGTH_SHORT).show();

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddData.this, "File attach is not found", Toast.LENGTH_SHORT).show();
                }

                mediaPlayer.start();
//                Toast.makeText(AddData.this, "Playing...", Toast.LENGTH_SHORT).show();

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recstart.setEnabled(true);
                recstop.setEnabled(false);
                play.setEnabled(true);
                pause.setEnabled(false);

                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    setupMediaRecorder();
                }
            }
        });


        mimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //external storage permission
                //runtime permision for android 6.0 and above
                ActivityCompat.requestPermissions(
                        AddData.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_GALLERY
                );
            }
        });



    }



    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(savepath);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_PERMISSION_CODE);
    }

    //press CTRL+O






    //for audio
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            case REQUEST_PERMISSION_GALLERY:
            {
                if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //gallery intent
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, REQUEST_PERMISSION_GALLERY);
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }



    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    //for gallery

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_PERMISSION_GALLERY && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON) //crop guide lines
                    .setAspectRatio(1, 1) //square croping
                    .start(this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                //set image choosen from gallery to image view
                mimage.setImageURI(resultUri);
            }else if( resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == RQS_OPEN_AUDIO_MP3) {
                Uri audioFileUri = data.getData();
                //vvv This lets you set the path sa TextView
                info.setText(audioFileUri.getPath());

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}