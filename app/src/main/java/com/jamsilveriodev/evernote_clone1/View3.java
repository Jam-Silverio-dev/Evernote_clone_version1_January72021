package com.jamsilveriodev.evernote_clone1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class View3 extends AppCompatActivity {
    /*
    These are global variables
     */
    Context ctx;
    View4 myView4 = new View4();
    String timestamp;
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
    public static final String appName = "evernote_clone1";
    public static final String appFolder = "com.jamsilveriodev.eclnotes";
    public static final String sdcardPath = "/" + "Android/data" + "/" + appFolder + "/" + "databases" + "/";
    public static final String createPath = Environment.getExternalStorageDirectory() + sdcardPath;
    public static final String sdcardBackupPath = "/" + "a_paradise_soft" + "/" + "backup" + "/" + appName + "/";
    public static final String sdcardRestorePath = "/" + "a_paradise_soft" + "/" + "restore" + "/" + appName + "/";
    public static final String backupPath = Environment.getExternalStorageDirectory() + sdcardBackupPath;
    public static final String restorePath = Environment.getExternalStorageDirectory() + sdcardRestorePath;
    File sdcardPathFolder = new File(Environment.getExternalStorageDirectory(), sdcardPath);
    File sdcardBackupFolder = new File(Environment.getExternalStorageDirectory(), sdcardBackupPath);
    File sdcardRestoreFolder = new File(Environment.getExternalStorageDirectory(), sdcardRestorePath);
    public String backupFile = "/" + "a_paradise_soft" + "/" + "backup" + "/" + appName + "/" + "eclnotesDB.db";


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

        Log.i("Entering View3.java", "Entering View3.java");

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

        //Add header per List Item
//        View header = getLayoutInflater().inflate(R.layout.header, null);
//        notesListView.addHeaderView(header);

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
            myView4.getMyTimestamp();
            String getTimestamp = myView4.getMyTimestamp().get(0);
            Log.i("getTimestamp", "getTimestamp is "+ getTimestamp);
            timestamp = getTimestamp.replaceAll("[\\s/:]","");//Get timestamp
            backupDB();
            renameFile();
            myView4.clearMyTimestamp();
        });

        btnNewNote.setOnClickListener(v -> {
            Intent myIntent1 = new Intent(View3.this,
                    View4.class);
            startActivity(myIntent1);
        });

        btnRestore.setOnClickListener(v -> {
            restoreDB();
        });



    }


    void localClearA() {
        editFilter.setText("");
        filter = "";
        notesCount.clear();
        tags.clear();
        info.clear();
        notesInfoRecords.clear();
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
        boolean databaseExists = sdcardPathFolder.exists();
        if (!databaseExists) {
            try {
                sdcardPathFolder.mkdirs();
                db = getApplicationContext().openOrCreateDatabase(sdcardPathFolder + "/" + "eclnotesDB" + FILE_EXTENSION, MODE_PRIVATE, null);
//                db.execSQL("CREATE TABLE notesInfo (tags TEXT, timestamp TEXT, topic TEXT, info TEXT)");
                db.execSQL("CREATE TABLE IF NOT EXISTS notesInfo (tags TEXT, timestamp TEXT PRIMARY KEY, topic TEXT, info TEXT)");
                db.close();
                Log.i("createDB A", "Database created.");
                Log.i("Table: ", "Table and schema created successfully!");
//                Toast.makeText(getApplicationContext(), "Table and schema created successfully!", Toast.LENGTH_LONG).show();
                myView4.getMyTimestamp();
                String getTimestamp = myView4.getMyTimestamp().get(0);
                Log.i("getTimestamp", "getTimestamp is "+ getTimestamp);
                timestamp = getTimestamp.replaceAll("[\\s/:]","");
                myView4.clearMyTimestamp();
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

    private boolean doesSDCardAccessible() {
        try {

            return (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));

        } catch (Exception e) {
            Toast.makeText(this, "SD card is not writeable!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Log.i("Sdcard: ", "SD card permission to write disabled");
        }
        return false;
    }


    private void backupDB() {
        //Folder creation
        //APP_DIR : your PackageName
        try {
            if (doesSDCardAccessible()) {
                if (!sdcardBackupFolder.exists()) {
                    sdcardBackupFolder.mkdirs();
                    sdcardRestoreFolder.mkdirs();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to make Directory!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        //File creation

        //checks if lastbackup is existing
        //checks what number of lastbackup
        try {
            copyFile(createPath, "eclnotesDB.db", backupPath);
            Toast.makeText(this, "Backup successful!", Toast.LENGTH_LONG).show();
        } catch (Exception e1) {
            //TODO
        }

    }

    private void restoreDB() {
        //File uploading
        try {
            copyFile(restorePath, "eclnotesDB.db", createPath);
        } catch (Exception e) {
            Toast.makeText(this, "File not found. Nothing to Restore!", Toast.LENGTH_LONG).show();
        }
    }

    private void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

            //log of success
            Toast.makeText(this, "Successful!", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
            Toast.makeText(this, "Unsuccessful file creation!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
            Toast.makeText(this, "Unsuccessful folder creation!", Toast.LENGTH_LONG).show();
        }

    }


    private void renameFile() {
        String newBackupFile = "/" + "a_paradise_soft" + "/" + "backup" + "/" + appName + "/" + "eclnotesDB_" + timestamp + ".db";
        File oldfile = new File(Environment.getExternalStorageDirectory(), backupFile);
        File newfile = new File (Environment.getExternalStorageDirectory(), newBackupFile);
        oldfile.renameTo(newfile);

    }


}