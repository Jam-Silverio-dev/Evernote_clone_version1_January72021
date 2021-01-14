package com.jamsilveriodev.evernote_clone1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class View3 extends AppCompatActivity {
    /*
    These are global variables
     */
    ListView notesListView;
    ArrayAdapter<String> adapter;
    public static ArrayList<String> tags = new ArrayList<>();
    public static ArrayList<String> info = new ArrayList<>();
    public static ArrayList<String> notesInfoRecords = new ArrayList<>();
    public static ArrayList<Integer> notesCount = new ArrayList<>();
    public static ArrayList<Integer> notePosition = new ArrayList<>();


    public ArrayList<Integer> getNotePosition() {
        return (ArrayList<Integer>) notePosition.clone();
    }

    public void clearNotePosition() {
        notePosition.clear();
    }


    SQLiteDatabase db;
    private static final String FILE_EXTENSION = ".db";
    public static final String appFolder = "com.jamsilveriodev.eclnotes";
    public static final String sdcardPath = "/" + "Android/data" + "/" + appFolder + "/" + "databases" + "/";
    public static final String databasePath = "/" + "Android/data" + "/" + appFolder + "/" + "databases" + "/" + "eclnotes.db";
    File sdcardPathFolder = new File(Environment.getExternalStorageDirectory(), sdcardPath);
    File databasePathFolder = new File(Environment.getExternalStorageDirectory(), databasePath);

    /*
   These are local variables
    */
    private static final int REQUEST_CODE = 112;

    EditText editFilter;
    String filter;

    Button btnBackup;
    Button btnNewNote;
    Button btnRestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view3);
        setTitle("Evernote clone (offline)");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

        editFilter = findViewById(R.id.editFilter);
        filter = editFilter.getText().toString();
        btnBackup = findViewById(R.id.btnBackup);
        btnNewNote = findViewById(R.id.btnNewNote);
        btnRestore = findViewById(R.id.btnRestore);

        localClearA();
        notePosition.clear();
        editFilter.requestFocus();

        createDBAndTableIfNotExists();
        readingTagsAndInfoTables();
        insertIntoNotesInfoRecords();
        notesListView = findViewById(R.id.notesListView);
        adapter = new ArrayAdapter<String>(this, R.layout.simplerow, notesInfoRecords);
        notesListView.setAdapter(adapter);
        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(), "Note Position " + position, Toast.LENGTH_SHORT).show();//Important: This shows the position of the note
                Log.i("Note Position ", "Note Position is " + position);
                notePosition.add(position);
                Log.i("Note Position ", "Note Position is " + notePosition);

                Intent myIntent1 = new Intent(View3.this,
                        View5.class);
                startActivity(myIntent1);

                Log.i("arraySize", "arraySize is " + notePosition.size());
                Log.i("onItemClick", "GOING TO View5.java -> 'Editing view'.");
                localClearA();

            }
        });

        btnBackup.setOnClickListener(v -> {
            //TODO
            Toast.makeText(this, "//TODO", Toast.LENGTH_SHORT).show();
        });

        btnNewNote.setOnClickListener(v -> {
            Intent myIntent1 = new Intent(View3.this,
                    View4.class);
            startActivity(myIntent1);
        });

        btnRestore.setOnClickListener(v -> {
            //TODO
            Toast.makeText(this, "//TODO", Toast.LENGTH_SHORT).show();
        });

    }


    void localClearA() {
        editFilter.setText("");
        filter = "";
        notesCount.clear();
        tags.clear();
        info.clear();
    }

    private void readingTagsAndInfoTables() {
        try {
            db = getApplicationContext().openOrCreateDatabase(sdcardPathFolder + "/" + "eclnotesDB" + FILE_EXTENSION, MODE_PRIVATE, null); //Querying the latest database

            Cursor c1 = db.rawQuery("SELECT * FROM notesInfo", null);
            int tagsIndex = c1.getColumnIndex("tags");
            int infoIndex = c1.getColumnIndex("info");

            c1.moveToFirst();
            while (c1 != null) {
                tags.add(c1.getString(tagsIndex));
                info.add(c1.getString(infoIndex));

                Log.i("userInfoTableA1: ", tags + " , " + info);
                c1.moveToNext();
            }

            c1.close();
            db.close();
            notesCount.add(0, info.size());
            Log.i("notesCount", "notesCount =" + notesCount.get(0));
        } catch (Exception e) {
            //TODO
        }
    }

    private void createDBAndTableIfNotExists() {
        //Start of createTable() method
        //Database create table = users and column names (id | name | age)
        boolean databaseExists = databasePathFolder.exists();
        if (!databaseExists) {
            try {
                sdcardPathFolder.mkdirs();
                db = getApplicationContext().openOrCreateDatabase(sdcardPathFolder + "/" + "eclnotesDB" + FILE_EXTENSION, MODE_PRIVATE, null);
                db.execSQL("CREATE TABLE notesInfo (tags TEXT, timestamp TEXT, topic TEXT, info TEXT)");
                db.close();
                Log.i("createDB A", "Database created.");
                Log.i("Table: ", "Table and schema created successfully!");
//                Toast.makeText(getApplicationContext(), "Table and schema created successfully!", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                //TODO
            }


        } else {
            Log.i("createDB B", "Database already exists.");
        }
    }

    private void insertIntoNotesInfoRecords() {
        for (int i = 0; i < info.size(); i++) {
            String combined = tags.get(i) + "    " + info.get(i);
            notesInfoRecords.add(combined);
        }

    }


}