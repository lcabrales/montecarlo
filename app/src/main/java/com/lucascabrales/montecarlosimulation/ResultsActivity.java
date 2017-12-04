package com.lucascabrales.montecarlosimulation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lucascabrales.montecarlosimulation.enums.TimeUnit;
import com.lucascabrales.montecarlosimulation.models.queue.Results;
import com.lucascabrales.montecarlosimulation.models.queue.SimParams;

import java.util.Locale;

public class ResultsActivity extends AppCompatActivity {

    private ResultsActivity mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        setupToolbar();

        SimParams queueParams = getIntent().getParcelableExtra(SimParams.QUEUE_KEY);
        SimParams serverParams = getIntent().getParcelableExtra(SimParams.SERVER_KEY);

        Results results = getIntent().getParcelableExtra(Results.KEY);
        TimeUnit timeUnit = (TimeUnit) getIntent().getSerializableExtra(TimeUnit.KEY);

        showResults(results, timeUnit, queueParams, serverParams);

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
        findViewById(R.id.btn_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showResults(Results results, TimeUnit timeUnit, SimParams queueParams, SimParams serverParams) {
        String time = "";

        switch (timeUnit) {
            case SECONDS:
                time = "s";
                break;
            case MINUTES:
                time = "min";
                break;
            case HOURS:
                time = "h";
                break;
        }

        ((TextView) findViewById(R.id.tv_average_wait_time)).setText(
                getString(R.string.time_unit, results.averageWaitTime, time));
        ((TextView) findViewById(R.id.tv_probability_queue)).setText(results.queueProbability);
        ((TextView) findViewById(R.id.tv_queue_lenght)).setText(results.queueLenght);
        ((TextView) findViewById(R.id.tv_queue_average)).setText(results.averageQueueLenght);
        ((TextView) findViewById(R.id.tv_queue_max)).setText(results.maxQueueLenght);
        ((TextView) findViewById(R.id.tv_total_time)).setText(
                getString(R.string.time_unit, results.averageTotalTime, time));
        ((TextView) findViewById(R.id.tv_total_qty)).setText(results.totalQty);
        ((TextView) findViewById(R.id.tv_wait_time_max)).setText(
                getString(R.string.time_unit, results.maxWaitTime, time));

        String queue = "Distribución " + queueParams.distribution.getName();
        switch (queueParams.distribution) {
            case NORMAL:
                queue += " - Media: " + String.format(Locale.US, "%.2f", queueParams.mean)
                        + System.getProperty("line.separator") + "Desviación Estándar: "
                        + String.format(Locale.US, "%.2f", queueParams.deviance);
                break;
            case POISSON:
                queue += " - Media: " + String.format(Locale.US, "%.2f", queueParams.mean);
                break;
            case UNIFORM:
                queue += " - Valor Máximo: " + queueParams.max;
                break;
        }

        ((TextView) findViewById(R.id.tv_queue_params)).setText(queue);

        String server = "Distribución " + serverParams.distribution.getName();
        switch (serverParams.distribution) {
            case NORMAL:
                server += " - Media: " + String.format(Locale.US, "%.2f", serverParams.mean)
                        + System.getProperty("line.separator") + "Desviación Estándar: "
                        + String.format(Locale.US, "%.2f", serverParams.deviance);
                break;
            case POISSON:
                server += " - Media: " + String.format(Locale.US, "%.2f", serverParams.mean);
                break;
        }

        ((TextView) findViewById(R.id.tv_server_params)).setText(server);

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
