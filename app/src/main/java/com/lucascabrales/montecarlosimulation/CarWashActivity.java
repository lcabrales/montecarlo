package com.lucascabrales.montecarlosimulation;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.lucascabrales.montecarlosimulation.adapters.CarWashAdapter;
import com.lucascabrales.montecarlosimulation.helpers.AlertDialogHelper;
import com.lucascabrales.montecarlosimulation.helpers.LoadingDialogHelper;
import com.lucascabrales.montecarlosimulation.models.CarWash;
import com.lucascabrales.montecarlosimulation.models.CarWashResult;

import java.util.ArrayList;

public class CarWashActivity extends AppCompatActivity {

    private CarWashActivity mContext = this;
    private AlertDialogHelper mAlertDialog;
    private LoadingDialogHelper mLoading;
    private CarWashAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_wash);

        mAlertDialog = new AlertDialogHelper(mContext);
        mLoading = new LoadingDialogHelper(mContext);

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
                        runSimulation();

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
    private void runSimulation() {
        findViewById(R.id.ll_results).setVisibility(View.GONE);

        int time = Integer.parseInt(((EditText) findViewById(R.id.et_time)).getText().toString());
        int intervals = Integer.parseInt(((EditText) findViewById(R.id.et_intervals)).getText().toString());
        int intervalSize = Integer.parseInt(((EditText) findViewById(R.id.et_interval_size)).getText().toString());
        int iterations = Integer.parseInt(((EditText) findViewById(R.id.et_iterations)).getText().toString());

        CarWash carWash = new CarWash();
        carWash.time = time;
        carWash.intervals = intervals;
        carWash.intervalSize = intervalSize;
        carWash.iterations = iterations;

        new AsyncTask<CarWash, String, ArrayList<CarWashResult>>() {
            @Override
            protected ArrayList<CarWashResult> doInBackground(CarWash... carWashes) {
                CarWash carWash = carWashes[0];

                try {
                    carWash.vehicleAmount = new double[carWash.intervals];
                    carWash.vehicleWait = new double[carWash.intervals];

                    //Montecarlo
                    for (int i = 0; i < carWash.iterations; i++) {

                        for (int j = 0; j < carWash.intervals; j++) {
                            String randomString = String.valueOf(Math.random());
                            int vehiclesAmount = Integer.parseInt(randomString.substring(6, 7));
                            int vehiclesWait = (int) (Math.random() * vehiclesAmount);

                            carWash.vehicleAmount[j] += vehiclesAmount;
                            carWash.vehicleWait[j] += vehiclesWait;
                        }
                    }

                    ArrayList<CarWashResult> carWashResults = new ArrayList<>();

                    for (int i = 0; i < carWash.intervals; i++) {
                        double vehicleAmount = carWash.vehicleAmount[i] / carWash.iterations;
                        double vehicleWait = carWash.vehicleWait[i] / carWash.iterations;

                        CarWashResult obj = new CarWashResult();
                        obj.vehicleAmount = vehicleAmount;
                        obj.vehicleWait = vehicleWait;

                        carWashResults.add(obj);
                    }

                    return carWashResults;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ArrayList<CarWashResult> carWashResults) {
                super.onPostExecute(carWashResults);

                mLoading.dismiss();

                if (carWashResults == null) {
                    mAlertDialog.show("Error", "Ha ocurrido un error. Verifique la función introducida.");
                } else {
                    showResults(carWashResults);
                }
            }
        }.execute(carWash);
    }

    private void showResults(ArrayList<CarWashResult> carWashResults) {
        findViewById(R.id.ll_results).setVisibility(View.VISIBLE);

        if (mListView == null) mListView = findViewById(R.id.list_view);

        mAdapter = new CarWashAdapter(mContext, carWashResults);
        mListView.setAdapter(mAdapter);
    }
}
