package com.lucascabrales.montecarlosimulation;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class DistributionsActivity extends AppCompatActivity {

    private DistributionsActivity mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributions);

        setupToolbar();
        setOnClickListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (toolbar != null) {
            toolbar.setTitle(getTitle());
        }
    }

    private void setOnClickListeners() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.Theme_AlertDialog);
                builder.setView(R.layout.dialog_chart);

                builder.setPositiveButton(getString(R.string.accept), null);

                String title = "";
                int imageRes = 0;

                switch (view.getId()) {
                    case R.id.itv_graph_normal:
                        title = "Distribución Normal";
                        imageRes = R.drawable.normal;
                        break;
                    case R.id.itv_graph_poisson:
                        title = "Distribución de Poisson";
                        imageRes = R.drawable.poisson;
                        break;
                    case R.id.itv_graph_uniform:
                        title = "Distribución Uniforme";
                        imageRes = R.drawable.uniform;
                        break;
                }

                builder.setTitle(title);

                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);

                ((ImageView) dialog.findViewById(R.id.image_view)).setImageResource(imageRes);
            }
        };

        findViewById(R.id.itv_graph_normal).setOnClickListener(listener);
        findViewById(R.id.itv_graph_poisson).setOnClickListener(listener);
        findViewById(R.id.itv_graph_uniform).setOnClickListener(listener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
