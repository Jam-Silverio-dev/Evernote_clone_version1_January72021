package com.jamsilveriodev.evernote_clone1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class View5 extends AppCompatActivity {
    /*
    These are global variables
     */
    View3 myView3 = new View3();
    int arraySize = myView3.getNotePosition().size();
    int notePosition = myView3.getNotePosition().get(0);

    SQLiteDatabase db;
    private static final String FILE_EXTENSION = ".db";
    public static final String appFolder = "com.jamsilveriodev.eclnotes";
    public static final String sdcardPath = "/" + "Android/data" + "/" + appFolder + "/" + "databases" + "/";
    File sdcardPathFolder = new File(Environment.getExternalStorageDirectory(), sdcardPath);
    /*
    These are local variables
    */
    private static final int REQUEST_CODE = 112;

    TextView textViewTimeStamp2;
    EditText editTextTags2;
    EditText editTextTopic2;
    EditText editTextInfo2;
    String textTags2;
    String textTopic2;
    String textInfo2;

    public static final ArrayList<String> tags = new ArrayList<>();
    public static final ArrayList<String> timestamp = new ArrayList<>();
    public static final ArrayList<String> topic = new ArrayList<>();
    public static final ArrayList<String> info = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view5);
        setTitle("Edit note");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

        Log.i("Entering View5.java", "Entering View5.java");
        Log.i("arraySize", "arraySize is " + arraySize);
        Log.i("notePosition", "notePosition is " + notePosition);

        textViewTimeStamp2 = findViewById(R.id.textViewTimeStamp2);
        editTextTags2 = findViewById(R.id.editTextTags2);
        editTextTopic2 = findViewById(R.id.editTextTopic2);
        editTextInfo2 = findViewById(R.id.editTextInfo2);
        textTags2 = editTextTags2.getText().toString();
        textTopic2 = editTextTopic2.getText().toString();
        textInfo2 = editTextInfo2.getText().toString();

        editTextTags2.requestFocus();

        Log.i("notePosition","notePosition is " + notePosition);
        readingUsersInfoTable();
        setContent(notePosition);
    }

    void setContent(int notePosition) {
        editTextTags2.setText(tags.get(notePosition));
        textViewTimeStamp2.setText(timestamp.get(notePosition));
        editTextTopic2.setText(topic.get(notePosition));
        editTextInfo2.setText(info.get(notePosition));
        Log.i("notesInfoTableB1: ", tags + " , " + timestamp + " , " + topic + " , " + info);
        Log.i("notesInfoTableB2: ", "notePosition is " + notePosition);
    }

    private void readingUsersInfoTable() {

        try {
            db = getApplicationContext().openOrCreateDatabase(sdcardPathFolder + "/" + "eclnotesDB" + FILE_EXTENSION, MODE_PRIVATE, null); //Querying the latest database

            Cursor c1 = db.rawQuery("SELECT * FROM notesInfo", null);
            int tagsIndex = c1.getColumnIndex("tags");
            int timestampIndex = c1.getColumnIndex("timestamp");
            int topicIndex = c1.getColumnIndex("topic");
            int infoIndex = c1.getColumnIndex("info");

            /*
            Database updated query part
             */

            c1.moveToFirst();
            while (c1 != null) {
                tags.add(c1.getString(tagsIndex));
                timestamp.add(c1.getString(timestampIndex));
                topic.add(c1.getString(topicIndex));
                info.add(c1.getString(infoIndex));
                c1.moveToNext();
            }
            c1.close();
            db.close();
            Log.i("notesInfoTableA1: ", tags.get(0) + " , " + timestamp.get(0) + " , " + topic.get(0) + " , " + info.get(0));
        } catch (Exception e) {
            //TODO
        }
    }


    void localClearC() {
        textTags2 = "";
        textTopic2 = "";
        textInfo2 = "";
        tags.clear();
        timestamp.clear();
        topic.clear();
        info.clear();

    }

    private void updateRecord() {

        try {
            textTags2 = editTextTags2.getText().toString();
            textTopic2 = editTextTopic2.getText().toString();
            textInfo2 = editTextInfo2.getText().toString();

            String q ="'";
            String updateSql = "UPDATE notesInfo SET tags = " + q+textTags2+q + ", " + "topic = " + q+textTopic2+q + ", " + "info = " + q+textInfo2+q + "WHERE timestamp =  " + q+timestamp.get(notePosition)+q;//Timestamp is the primary key and the where clause
            db = getApplicationContext().openOrCreateDatabase(sdcardPathFolder + "/" + "eclnotesDB" + FILE_EXTENSION, MODE_PRIVATE, null);
            db.execSQL(updateSql);
            db.close();
            Log.i("updateRecord(): ", "updateRecord(): Successfully updated your info!");
            Toast.makeText(getApplicationContext(), "Successfully updated your info!", Toast.LENGTH_SHORT).show();

            Log.i("updateRecord(): ", "Intent: GOING BACK TO View3.java -> HOME!");
        } catch (Exception e3) {
            Log.i("updateRecord(): ", "TODO");
        }


    }

    /*Fragment code*/
    public void deleteNoteDialog(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Are you sure, you wanted to delete this note?");

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                deleteNoteRecord();
//                Toast.makeText(View5.this, "You clicked yes button", Toast.LENGTH_LONG).show();
                Intent myIntent1 = new Intent(View5.this,
                        View3.class);
                startActivity(myIntent1);
                localClearC();
                myView3.clearNotePosition();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    @SuppressLint("NonConstantResourceId")
    public void onClickMethod(View v) throws IOException {//Purpose: Independent method for button click. Needs android: onClick = "method"

        switch (v.getId()) {
            case R.id.btnHome:
                Intent myIntent2 = new Intent(View5.this,
                        View3.class);
                startActivity(myIntent2);
                localClearC();
                break;

            case R.id.btnUpdateNote:
                updateRecord();
                break;

        }

    }

    private void deleteNoteRecord() {

        try {
            String q ="'";
//            String deleteSql = DELETE FROM notesInfo WHERE timestamp = 'timestamp1';
            String deleteSql = "DELETE FROM notesInfo WHERE timestamp = " + q+timestamp.get(notePosition)+q;//Timestamp is the primary key and the where clause
            db = getApplicationContext().openOrCreateDatabase(sdcardPathFolder + "/" + "eclnotesDB" + FILE_EXTENSION, MODE_PRIVATE, null);
            db.execSQL(deleteSql);
            db.close();
            Log.i("deleteRecord(): ", "deleteRecord(): Successfully deleted the record!");
            Toast.makeText(getApplicationContext(), "Successfully deleted the record!", Toast.LENGTH_SHORT).show();

            Log.i("deleteRecord(): ", "Intent: GOING BACK TO View3.java -> HOME!");
        } catch (Exception e3) {
            Log.i("updateRecord(): ", "TODO");
        }

    }

}