package com.example.imusic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    @SuppressLint("InlinedApi")
    private static final String READ_MEDIA_AUDIO= Manifest.permission.READ_MEDIA_AUDIO;
    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // ✅ Fixed order issue
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView); // ✅ Proper placement
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestPermission();

    }

    private void requestPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                loadSongs();  // ✅ Already granted
            } else {
                // Request MANAGE_EXTERNAL_STORAGE permission for Android 11+
                new AlertDialog.Builder(this)
                        .setTitle("Permission Required")
                        .setMessage("This app needs access to manage your audio files.")
                        .setPositiveButton("Grant Permission", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .create().show();
            }
        } else {
            // For Android below version 11
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                loadSongs();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Permission Denied")
                        .setMessage("Please grant permission from settings to access audio files.")
                        .setPositiveButton("Settings", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            }
        }
    }

    private void loadSongs() {
        ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
        String[] items = new String[mySongs.size()];
        for (int i = 0; i < mySongs.size(); i++) {
            items[i] = mySongs.get(i).getName().replace(".mp3", "");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(arrayAdapter);
        
    }

    public ArrayList<File> fetchSongs(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] songs = file.listFiles();
        if (songs != null) {
            for (File myFile : songs) {
                if (myFile.isDirectory() && !myFile.isHidden()) {
                    // Recursively search subdirectories
                    arrayList.addAll(fetchSongs(myFile));
                }
                // ✅ Exclude ringtones and non-music files
                else if (myFile.getName().toLowerCase().endsWith(".mp3") ||
                        myFile.getName().toLowerCase().endsWith(".wav") ||
                        myFile.getName().toLowerCase().endsWith(".flac")) {

                    // ✅ Filter out default ringtones by path
                    if (!myFile.getAbsolutePath().contains("/Ringtones") &&
                            !myFile.getAbsolutePath().contains("/Notifications") &&
                            !myFile.getAbsolutePath().contains("/Alarms")) {
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }

}
