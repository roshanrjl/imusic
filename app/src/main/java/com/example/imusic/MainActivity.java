package com.example.imusic;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class MainActivity extends AppCompatActivity {
    private static final String READ_MEDIA_AUDIO= Manifest.permission.READ_MEDIA_AUDIO;
    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        requestPermission();
    }
    private void requestPermission() {
        // Check if permission is already granted
        if (checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted for audio files", Toast.LENGTH_SHORT).show();
        }
        // Check if an explanation is needed before requesting permission
        else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_AUDIO)) {
            Toast.makeText(this, "Permission is needed to access audio files.", Toast.LENGTH_LONG).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("app required the  READ_MEDIA_AUDIO to perform smoothly as expected")
                    .setTitle("permission Required")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(
                                    new String[]{Manifest.permission.READ_MEDIA_AUDIO},
                                    REQUEST_CODE
                            );
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();



        }
        // Request the permission
        else {
            requestPermissions(
                    new String[]{Manifest.permission.READ_MEDIA_AUDIO},
                    REQUEST_CODE
            );
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if the request code matches the defined request code
        if (requestCode == REQUEST_CODE) {

            // Check if the permission was granted
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted. You can now access audio files.", Toast.LENGTH_SHORT).show();
            }
            else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_AUDIO)) {
                Toast.makeText(this, "Permission is needed to access audio files.", Toast.LENGTH_LONG).show();
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("This feature is unaviable because it requires the app to get the permission which you denied")
                        .setTitle("permission Required")
                        .setCancelable(false)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("Package", getPackageName(),null);
                                intent.setData(uri);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                dialog.show();



            }
            else {
                // Explain why the feature is unavailable due to denied permission
                Toast.makeText(this, "Permission denied. Cannot access audio files.", Toast.LENGTH_LONG).show();
            }
        }
    }

}