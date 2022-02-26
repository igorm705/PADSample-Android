package com.stickersmenuapp.readdatafromfiles;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Main mainClass = new Main();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}