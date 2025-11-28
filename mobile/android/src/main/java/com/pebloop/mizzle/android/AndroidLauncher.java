package com.pebloop.mizzle.android;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.pebloop.mizzle.R;

public class AndroidLauncher extends AppCompatActivity implements AndroidFragmentApplication.Callbacks {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void exit() {

    }
}
