package com.lucascabrales.montecarlosimulation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private MainActivity mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;

                switch (view.getId()) {
                    case R.id.btn_buffon:
                        intent = new Intent(mContext, BuffonsNeedleActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btn_area:
                        intent = new Intent(mContext, AreaCurveActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btn_random_walk:
                        intent = new Intent(mContext, RandomWalkActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        };

        findViewById(R.id.btn_buffon).setOnClickListener(listener);
        findViewById(R.id.btn_area).setOnClickListener(listener);
        findViewById(R.id.btn_random_walk).setOnClickListener(listener);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (toolbar != null) {
            toolbar.setTitle(getTitle());
        }
    }
}
