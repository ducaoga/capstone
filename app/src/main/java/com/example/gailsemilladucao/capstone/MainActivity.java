package com.example.gailsemilladucao.capstone;

// =========== API =========== //
import android.Manifest;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


// =========== PACKAGES =========== //
import com.example.gailsemilladucao.capstone.backend.AddData;
import com.example.gailsemilladucao.capstone.view.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public TextView txvResult;
    public String message ="Hello";
    Button searchy;
    EditText wordie;

    // Navigation Drawer
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;

    String jsonString;



    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txvResult = findViewById(R.id.txvResult);
        searchy = findViewById(R.id.searching);
        wordie = findViewById(R.id.wordie);
        dl = findViewById(R.id.dl);

        // Navigation Drawer
        abdt = new ActionBarDrawerToggle(MainActivity.this, dl, R.string.open, R.string.close);
        dl.addDrawerListener(abdt);
        abdt.syncState();

        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if(id == R.id.add_word){
                    Intent intent = new Intent(MainActivity.this,AddData.class);
                    startActivity(intent);
                }else if(id == R.id.download_package){
                    Intent intent = new Intent(MainActivity.this, DownloadCateg.class);
                    startActivity(intent);
                }else if(id == R.id.action_logout){
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                }else if(id == R.id.home){
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        //download json
        gayson();

        //part na na parse na and placed sa string
        //this also is used global in searchWord()
        jsonString = readFromFile();


        searchy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject res = null;
                res = searchWord("airplane");
                if(res != null){
                    Intent lol = new Intent(MainActivity.this,ShowData.class);
                    //converted the
                    lol.putExtra("Val",res.toString());
                    startActivity(lol);
                }else{
                    Toast.makeText(MainActivity.this, "Word is not in the dictionary", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    private String readFromFile() {
        String ret = "";

        try {
            InputStream inputStream = openFileInput("wordbank.json");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }



    private void gayson() {

        String storageUrl = "https://firebasestorage.googleapis.com/v0/b/bistalk-7833f.appspot.com/o/wordbank.json?alt=media&token=21f68d7f-7a1c-4b1d-aab1-0790bbe5644c";
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReferenceFromUrl(storageUrl);

        final File myFile = new File(getFilesDir(),"wordbank.json");
        reference.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                //  Toast.makeText(MainActivity.this, myFile.getName(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(MainActivity.this, "Download was unsuccessful", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }


    //called in xml
    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);

        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    message = result.get(0);
                    txvResult.setText(message);

                    //covert first letter to uppercase since database set it on uppercase
                    message = message.substring(0,1).toLowerCase() + message.substring(1).toLowerCase();
                    //access the db


                    JSONObject res = searchWord(message);
                    if(res != null){
                        Intent lol = new Intent(MainActivity.this,ShowData.class);
                        //converted the
                        lol.putExtra("Val",res.toString());
                        startActivity(lol);
                    }else{
                        Toast.makeText(this, "Word is not in the dictionary", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
        }
    }

    //called in getSpeechInput and searchings the word
    public JSONObject searchWord(String msg) {
        JSONObject match = null;

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(readFromFile());
            jsonArray = jsonObject.getJSONArray("wordbank");

            for (int i = 0; i < jsonArray.length(); i++) {
                //for (int i = 0; i < 6; i++) {
                JSONObject instance = jsonArray.getJSONObject(i);
                //Toast.makeText(this, instance.getString(msg), Toast.LENGTH_SHORT).show();
                Toast.makeText(this, msg + "   "+ instance.getString("English"), Toast.LENGTH_SHORT).show();
                if (msg.equals(instance.getString("English"))) {
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    return match = instance;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return match;
    }
}
