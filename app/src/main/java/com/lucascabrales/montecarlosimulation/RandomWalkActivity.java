package com.lucascabrales.montecarlosimulation;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.lucascabrales.montecarlosimulation.helpers.AlertDialogHelper;
import com.lucascabrales.montecarlosimulation.helpers.LoadingDialogHelper;
import com.lucascabrales.montecarlosimulation.models.RandomWalk;

import java.util.ArrayList;

public class RandomWalkActivity extends AppCompatActivity {

    private RandomWalkActivity mContext = this;
    private LoadingDialogHelper mLoading;
    private AlertDialogHelper mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_walk);

        mLoading = new LoadingDialogHelper(mContext);
        mAlertDialog = new AlertDialogHelper(mContext);

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
                switch (view.getId()) {
                    case R.id.btn_accept:
                        //TODO ADD CANCEL BUTTON TO LOADING
                        mLoading.show();
                        calculateRandomWalk();

                        try {
                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };

        findViewById(R.id.btn_accept).setOnClickListener(listener);
    }

    @SuppressLint("StaticFieldLeak")
    private void calculateRandomWalk() {
        Integer iterations = Integer.parseInt(((EditText) findViewById(R.id.et_iterations)).getText().toString());

        new AsyncTask<Integer, String, RandomWalk>() {
            @Override
            protected RandomWalk doInBackground(Integer... steps) {
                int iterations = steps[0];

                RandomWalk randomWalk = new RandomWalk(iterations);

                try {
                    //Montecarlo
                    for (int i = 0; i < iterations; i++) {
                        double dx = 1 - 2 * Math.random();
                        double dy = 1 - 2 * Math.random();

                        float x = (float) (randomWalk.xArray[i] + dx);
                        float y = (float) (randomWalk.yArray[i] + dy);

                        randomWalk.xArray[i + 1] = x;
                        randomWalk.yArray[i + 1] = y;
                    }

                    return randomWalk;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(RandomWalk randomWalk) {
                super.onPostExecute(randomWalk);

                mLoading.dismiss();

                if (randomWalk == null) {
                    mAlertDialog.show("Error", "Ha ocurrido un error");
                } else {
                    findViewById(R.id.ll_results).setVisibility(View.VISIBLE);

                    showGraph(randomWalk);
                }
            }
        }.execute(iterations);
    }

    private void showGraph(RandomWalk randomWalk) {
        LineChart chart = findViewById(R.id.chart);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setNoDataText("No hay datos que mostrar");
        chart.setNoDataTextColor(ContextCompat.getColor(mContext, R.color.color_accent));
        chart.setHighlightPerTapEnabled(true);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(true);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setDrawLabels(false);

        // add data
        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < randomWalk.xArray.length; i++) {
            values.add(new Entry(randomWalk.xArray[i], randomWalk.yArray[i]));
        }

        chart.setData(generateLineData(values));
        chart.invalidate();
    }

    private LineData generateLineData(ArrayList<Entry> values) {
        LineDataSet dataSet = new LineDataSet(values, "Random Walk");
        dataSet.setColor(ContextCompat.getColor(mContext, R.color.color_accent_dark));
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(true);

        return new LineData(dataSet);
    }
}