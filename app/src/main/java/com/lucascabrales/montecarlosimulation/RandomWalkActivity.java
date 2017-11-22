package com.lucascabrales.montecarlosimulation;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.lucascabrales.montecarlosimulation.helpers.AlertDialogHelper;
import com.lucascabrales.montecarlosimulation.helpers.LoadingDialogHelper;
import com.lucascabrales.montecarlosimulation.models.RandomWalk;
import com.lucascabrales.montecarlosimulation.models.Step;
import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;

import java.util.ArrayList;

public class RandomWalkActivity extends AppCompatActivity {

    private RandomWalkActivity mContext = this;
    private LoadingDialogHelper mLoading;
    private AlertDialogHelper mAlertDialog;
    private SparkView mSparkView;
    private CustomSparkAdapter mSparkAdapter;
    private CountDownTimer mTimer;
    private RandomWalk mRandomWalk;
    private int stepCounter;

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
        Integer steps = Integer.parseInt(((EditText) findViewById(R.id.et_iterations)).getText().toString());

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

//                    showGraph(randomWalk);

                    showResults(randomWalk);
                }
            }
        }.execute(steps);
    }

    private void showResults(RandomWalk randomWalk) {
        if (mTimer != null) mTimer.cancel();

        mRandomWalk = randomWalk;
        stepCounter = 0;

        ArrayList<Step> steps = new ArrayList<>();

        Step step = new Step();
        step.x = randomWalk.xArray[0];
        step.y = randomWalk.yArray[0];

        steps.add(step);

        if (mSparkView == null) {
            mSparkView = findViewById(R.id.spark_view);
        }

        mSparkAdapter = new CustomSparkAdapter(steps);

        mSparkView.setAdapter(mSparkAdapter);

        int duration = Integer.parseInt(((EditText) findViewById(R.id.et_duration)).getText().toString());
        duration *= 1000; //convert to milliseconds
        startAnimation(duration);
    }

    public void startAnimation(int duration) {
        long dummyTime = System.currentTimeMillis();

        int countdown = duration / mRandomWalk.steps;

        mTimer = new CountDownTimer(dummyTime, countdown) {

            public void onTick(long millis) {
                //OBTIENE LOS VALORES ALEATORIOS
                nextStep(stepCounter);

                stepCounter++;

                if (stepCounter == mRandomWalk.xArray.length) {
                    String coordinates = "(" + mRandomWalk.xArray[mRandomWalk.xArray.length - 1] + ", "
                            + mRandomWalk.yArray[mRandomWalk.yArray.length - 1] + ")";
                    ((TextView) findViewById(R.id.tv_final_coordinates)).setText(coordinates);

                    mTimer.cancel();
                }
            }

            public void onFinish() {

            }
        };

        mTimer.start();
    }

    private void nextStep(int index) {
        Step step = new Step();
        step.x = mRandomWalk.xArray[index];
        step.y = mRandomWalk.yArray[index];

        mSparkAdapter.addValue(step);
    }

    public class CustomSparkAdapter extends SparkAdapter {
        private ArrayList<Step> mDataset;

        public CustomSparkAdapter(ArrayList<Step> dataset) {
            mDataset = dataset;
        }

        public void addValue(Step step) {
            mDataset.add(step);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mDataset.size();
        }

        @Override
        public Object getItem(int index) {
            return mDataset.get(index);
        }

        @Override
        public float getY(int index) {
            return mDataset.get(index).y;
        }

        @Override
        public float getX(int index) {
            return mDataset.get(index).x;
        }
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