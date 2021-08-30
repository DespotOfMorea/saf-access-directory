package org.vnuk.safaccessdirectory;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String PATH_KEY = "path";
    private static final String PATH_DEF_VALUE = "Default value";
    private static final String FILE_NAME = "chant.ogs";

// Request code, when using startActivityForResult() method, which is deprecated.
    private static final int PATH_REQUEST_CODE = 17;
    private ActivityResultLauncher<Intent> launcherForDirectoryPathResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        launcherForDirectoryPathResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                this::onActivityResult);
        setupListeners();
    }

    private void setupListeners() {
        Button btnPickPath = findViewById(R.id.btnPickPath);
        btnPickPath.setOnClickListener(this::onButtonPickPathClick);
        Button btnReadFile = findViewById(R.id.btnReadFile);
        btnReadFile.setOnClickListener(this::onButtonReadFileClick);
    }

    private void onButtonPickPathClick(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

// Old way, now deprecated.
        //startActivityForResult(intent, PATH_REQUEST_CODE);
        launcherForDirectoryPathResult.launch(intent);
    }

    private void onButtonReadFileClick(View view) {
        SharedPreferences preferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        Uri uri;
        try {
            uri = Uri.parse(preferences.getString(PATH_KEY, PATH_DEF_VALUE));
            createFileUri(uri);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
// Now deprecated way, using startActivityForResult() method and specific request code.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == PATH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            getUriFromIntent(resultData);
        } else {
            super.onActivityResult(requestCode, resultCode, resultData);
        }
    }
// Now recommended way, using Activity Result API.
    private void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            getUriFromIntent(result.getData());
        }
    }

    private void getUriFromIntent(Intent resultData) {
        Uri uri = null;
        if (resultData != null) {
            uri = resultData.getData();
            getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
            SharedPreferences preferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            preferences.edit().putString(PATH_KEY, uri.toString()).apply();
            createFileUri(uri);
        }
    }

    private void createFileUri(Uri directoryUri) {
        DocumentFile documentFile = DocumentFile.fromTreeUri(this, directoryUri);
        DocumentFile file = documentFile.findFile(FILE_NAME);
        TextView tvContent = findViewById(R.id.tvContent);
        if (file != null) {
            try {
                String text = readTextFromUri(file.getUri());
                tvContent.setText(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            tvContent.setText(R.string.file_not_found);
    }

    private String readTextFromUri(Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }
}