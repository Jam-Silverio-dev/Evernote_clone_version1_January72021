package com.jamsilveriodev.evernote_clone1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class View4 extends AppCompatActivity {
    /*
    These are global variables
     */
    public static final String userID = "001";
    ArrayList<String> myTimestamp = new ArrayList<>();

    public ArrayList<String> getMyTimestamp() {
        getDate();
        return (ArrayList<String>) myTimestamp.clone();
    }

    public void clearMyTimestamp() {
        myTimestamp.clear();
    }

    SQLiteDatabase db;
    private static final String FILE_EXTENSION = ".db";
    public static final String appFolder = "com.jamsilveriodev.eclnotes";
    public static final String sdcardPath = "/" + "Android/data" + "/" + appFolder + "/" + "databases" + "/";
    File sdcardPathFolder = new File(Environment.getExternalStorageDirectory(), sdcardPath);

    /*
    These are local variables
     */
    private static final int REQUEST_CODE = 112;

    TextView textViewTimeStamp1;
    EditText editTextTags1;
    EditText editTextTopic1;
    EditText editTextInfo1;
    String textTags1;
    String timestamp;
    String textTopic1;
    String textInfo1;

    Button btnCancelNote;
    Button btnCreateNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view4);
        setTitle("Create note");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

        Log.i("Entering View4.java", "Entering View4.java");

        textViewTimeStamp1 = findViewById(R.id.textViewTimeStamp1);
        editTextTags1 = findViewById(R.id.editTextTags1);
        editTextTopic1 = findViewById(R.id.editTextTopic1);
        editTextInfo1 = findViewById(R.id.editTextInfo1);
        btnCancelNote = findViewById(R.id.btnCancelNote);
        btnCreateNote = findViewById(R.id.btnCreateNote);

        editTextTags1.requestFocus();

        getDate();
        setTimestamp();

        btnCancelNote.setOnClickListener(v -> {
            try {
                Intent myIntent1 = new Intent(View4.this,
                        View3.class);
                startActivity(myIntent1);
//                localClearB();
            } catch (Exception e) {
                //TODO
            }

        });

        btnCreateNote.setOnClickListener(v -> insertRecord());//insertRecord()


    }

    private void insertRecord() {

        try {
            textTags1 = editTextTags1.getText().toString();
            textTopic1 = editTextTopic1.getText().toString();
            textInfo1 = editTextInfo1.getText().toString();

            String q = "'";
            String insertSql1 = "INSERT INTO notesInfo (tags, timestamp, topic, info) VALUES (" + q + textTags1 + q + ", " + q + timestamp + q + ", " + q + textTopic1 + q + ", " + q + textInfo1 + q + ");";

            db = getApplicationContext().openOrCreateDatabase(sdcardPathFolder + "/" + "eclnotesDB" + userID + FILE_EXTENSION, MODE_PRIVATE, null);
            db.execSQL(insertSql1);
            db.close();
            Log.i("insertRecord(): ", "Log1 from try: Successfully saved your info!");
            Toast.makeText(getApplicationContext(), "Successfully saved your info!", Toast.LENGTH_SHORT).show();

            localClearB();
            Intent myIntent2 = new Intent(View4.this,
                    View3.class);
            startActivity(myIntent2);
            Log.i("insertRecord(): ", "Intent: GOING BACK TO View3.java -> HOME!");
        } catch (Exception e3) {
            Log.i("insertRecord(): ", "TODO");
            Toast.makeText(getApplicationContext(), "Unsuccessful", Toast.LENGTH_SHORT).show();
        }


    }


    private void getDate() {
        long time = System.currentTimeMillis() / 1000;
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        myTimestamp.add(0, DateFormat.format("MM/dd/yyyy hh:mm:ss", cal).toString());
    }

    private void setTimestamp() {
        timestamp = myTimestamp.get(0);
        textViewTimeStamp1.setText(timestamp);
    }

    void localClearB() {
        myTimestamp.clear();
        timestamp = "";
        textTags1 = "";
        textTopic1 = "";
        textInfo1 = "";

    }


}