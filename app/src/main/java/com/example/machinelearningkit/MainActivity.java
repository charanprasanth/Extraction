package com.example.machinelearningkit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    RelativeLayout button1;
    Button button2, button3;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        textView = findViewById(R.id.textView);

        button1.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Images"), 121);
        });
        button2.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Images"), 121);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 121) {
            if (data != null){
                imageView.setImageURI(data.getData());

                TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

                InputImage image;
                try {
                    image = InputImage.fromFilePath(getApplicationContext(), data.getData());

                    recognizer.process(image)
                            .addOnSuccessListener(visionText -> {
                                showDialog(visionText.getText());
                                button3.setVisibility(View.VISIBLE);
                                button3.setOnClickListener(v -> {
                                    showDialog(visionText.getText());
                                });
                            })
                            .addOnFailureListener(e -> Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show());

                } catch (IOException e) {
                    Log.d("Extraction", e.getMessage());
                    Toast.makeText(MainActivity.this, "Catch : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else{
                Toast.makeText(MainActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(MainActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    void showDialog(String text) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.text_sheet);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.sheet_bg));

        ImageView closeBtn, copyBtn;
        TextView textView;

        closeBtn = dialog.findViewById(R.id.closeBtn);
        copyBtn = dialog.findViewById(R.id.copyBtn);
        textView = dialog.findViewById(R.id.textView);

        textView.setText(text);

        closeBtn.setOnClickListener(v -> dialog.dismiss());

        copyBtn.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("label", text);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(getApplicationContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }
}

