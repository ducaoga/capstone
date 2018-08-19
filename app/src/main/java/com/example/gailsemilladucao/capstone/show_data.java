package com.example.gailsemilladucao.capstone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class show_data extends AppCompatActivity {

    public EditText englishText;
    public Button query_button;
    public TextView result_cebuano;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        englishText = (EditText)findViewById(R.id.englishText);
        query_button = (Button)findViewById(R.id.query_button);
        result_cebuano = (TextView)findViewById(R.id.result_text);

        //SETTING ONCLICK LISTENER

        query_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // CREATE INSTANCE OF DB ACCESS CLASS AND DB CONNECTION
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open();

                // GETTING VALUE FROM EDIT TEXT

                String engWord = englishText.getText().toString();
                String cebWord = databaseAccess.getAddress(engWord); // getAddress() method to get the english word

                // SETTING TEXT TO RESULT FIELD
                result_cebuano.setText(cebWord);
                databaseAccess.close();
            }
        });
    }
}
