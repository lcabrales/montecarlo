package com.lucascabrales.montecarlosimulation;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lucascabrales.montecarlosimulation.enums.TimeUnit;
import com.lucascabrales.montecarlosimulation.helpers.AlertDialogHelper;
import com.lucascabrales.montecarlosimulation.helpers.RandomGenerator;
import com.lucascabrales.montecarlosimulation.models.queue.Results;
import com.lucascabrales.montecarlosimulation.models.queue.SimParams;
import com.lucascabrales.montecarlosimulation.models.queue.Statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class SimActivity extends AppCompatActivity {

    private SimActivity mContext = this;
    private SimParams mQueueParams, mServerParams;
    private RandomGenerator mQueueRandom, mServerRandom;
    private ArrayList<Integer> mQueueValues, mServerValues, mArrivalTimes, mStartTimes, mEndTimes,
            mWaitTimes, mServerWaitTimes, mQueueSizes, mTotalTimes;
    private boolean isActive = true;
    private int countTotal;
    private CountDownTimer mTimer;
    private AlertDialogHelper mAlertDialog;
    private int countQueue;
    private TimeUnit mTimeUnit;
    private Animation mQueueAnimation, mServerAnimation;
    private android.view.Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sim);

        mAlertDialog = new AlertDialogHelper(mContext);

        setupToolbar();

        setOnClickListeners();
        prepSimulation();
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
        findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSimulation();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sim, menu);
        mMenu = menu;

        startSimulation();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            if (id == R.id.action) {
                //START OR STOP SIMULATION
                if (!isActive) {
                    startSimulation();
                } else {
                    stopSimulation();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    //PREPARA LA SIMULACION
    private void prepSimulation() {
        mQueueParams = getIntent().getParcelableExtra(SimParams.QUEUE_KEY);
        mServerParams = getIntent().getParcelableExtra(SimParams.SERVER_KEY);

        mQueueRandom = new RandomGenerator(
                mQueueParams.distribution.getName(),
                mQueueParams.mean,
                mQueueParams.deviance,
                mQueueParams.max);

        mServerRandom = new RandomGenerator(
                mServerParams.distribution.getName(),
                mServerParams.mean,
                mServerParams.deviance,
                mServerParams.max);

        mQueueValues = new ArrayList<>();
        mServerValues = new ArrayList<>();
        mArrivalTimes = new ArrayList<>();
        mStartTimes = new ArrayList<>();
        mEndTimes = new ArrayList<>();
        mWaitTimes = new ArrayList<>();
        mServerWaitTimes = new ArrayList<>();
        mQueueSizes = new ArrayList<>();
        mTotalTimes = new ArrayList<>();

        mTimeUnit = mQueueParams.timeUnit;
        String waitTime = "Tiempo de espera ";
        switch (mTimeUnit) {
            case SECONDS:
                waitTime += "(segundos)";
                break;
            case MINUTES:
                waitTime += "(minutos)";
                break;
            case HOURS:
                waitTime += "(horas)";
                break;
        }

        ((TextView) findViewById(R.id.tv_wait_time)).setText(waitTime);
    }

    //DETENER SIMULACIÓN
    private void stopSimulation() {
        isActive = false;
        mTimer.cancel();
        mMenu.findItem(R.id.action).setTitle("Empezar");
        mMenu.findItem(R.id.action).setEnabled(false);

        findViewById(R.id.view_queue).clearAnimation();
        findViewById(R.id.view_server).clearAnimation();
        findViewById(R.id.view_queue).setVisibility(View.GONE);

        Results results = new Results();
        results.averageWaitTime = String.valueOf(String.format(Locale.US, "%.2f", getMean(mWaitTimes)));
        double queueProbability = getQueueProbability(countQueue, countTotal);
        results.queueProbability = String.valueOf(String.format(Locale.US, "%.2f", queueProbability));
        results.averageQueueLenght = String.valueOf(String.format(Locale.US, "%.2f", getMean(mQueueSizes)));
        results.maxQueueLenght = String.valueOf(getMinMax(mQueueSizes).second);
        results.averageTotalTime = String.valueOf(String.format(Locale.US, "%.2f", getMean(mTotalTimes)));
        results.queueLenght = String.valueOf(mQueueSizes.get(mQueueSizes.size() - 1));
        results.totalQty = String.valueOf(countTotal);
        results.maxWaitTime = String.valueOf(getMinMax(mWaitTimes).second);

        //ENVIA LOS RESULTADOS DE LA SIMULACION A LA OTRA PANTALLA
        Intent intent = new Intent(mContext, ResultsActivity.class);
        intent.putExtra(SimParams.QUEUE_KEY, mQueueParams);
        intent.putExtra(SimParams.SERVER_KEY, mServerParams);
        intent.putExtra(Results.KEY, results);
        intent.putExtra(TimeUnit.KEY, mTimeUnit);
        mAlertDialog.showWithIntent(
                "Simulación Finalizada",
                "La simulación ha concluido con éxito, proceda a ver los resultados.",
                intent
        );
    }

    //INICIA LA SIMULACION
    private void startSimulation() {
        isActive = true;
        mMenu.findItem(R.id.action).setTitle("Detener");

        findViewById(R.id.view_queue).setVisibility(View.VISIBLE);

        countTotal = 0;
        countQueue = 0;

        final long dummyTime = System.currentTimeMillis();

        //TIMER PARA VISUALIZAR LOS CAMBIOS EN PANTALLA
        mTimer = new CountDownTimer(dummyTime, 200) {

            public void onTick(long millis) {
                //OBTIENE LOS VALORES ALEATORIOS
                int queueValue = mQueueRandom.getNextValue();
                int serverValue = mServerRandom.getNextValue();

                mQueueValues.add(queueValue);
                mServerValues.add(serverValue);

                //CALCULA LAS ESTADISTICAS
                calculateStatistics(countTotal);

                LinearLayout llQueue = findViewById(R.id.ll_queue_size);
                LinearLayout llServer = findViewById(R.id.ll_wait_time);

                //MUESTRA LAS ESTADISTICAS EN PANTALLA
                showStatistics(llQueue, getStatistics(mQueueSizes));
                showStatistics(llServer, getStatistics(mWaitTimes));

                ((TextView) findViewById(R.id.tv_queue_lenght)).setText(String.valueOf(mQueueSizes.get(countTotal)));

                countTotal++;

                //ANIMACION LUEGO DE PASAR AL SERVIDOR
                mServerAnimation = AnimationUtils.loadAnimation(mContext, R.anim.server);
                if (countTotal == 1)
                    findViewById(R.id.view_server).startAnimation(mServerAnimation);

                ((TextView) findViewById(R.id.tv_total_qty)).setText(String.valueOf(countTotal));
            }

            public void onFinish() {

            }
        };

        mTimer.start();

        //ANIMACION DE LA COLA
        mQueueAnimation = AnimationUtils.loadAnimation(mContext, R.anim.queue);
        findViewById(R.id.view_queue).startAnimation(mQueueAnimation);
    }

    private void calculateStatistics(int i) {
        //CALCULAR TIEMPO DE LLEGADA
        int arrivalTime = 0;

        if (mArrivalTimes.size() == 0)
            arrivalTime = mQueueValues.get(0);
        else {
            arrivalTime = mQueueValues.get(i) + mArrivalTimes.get(i - 1);
        }

        mArrivalTimes.add(arrivalTime);

        //CALCULAR TIEMPO DE INICIO DE SERVICIO
        int startTime = 0;

        if (mStartTimes.size() == 0)
            startTime = mQueueValues.get(0);
        else {
            if (mEndTimes.get(i - 1) < mArrivalTimes.get(i))
                startTime = mArrivalTimes.get(i);
            else
                startTime = mEndTimes.get(i - 1);
        }

        mStartTimes.add(startTime);

        //CALCULAR TIEMPO DE FIN DE SERVICIO
        int endTime = mServerValues.get(i) + mStartTimes.get(i);
        mEndTimes.add(endTime);

        //CALCULAR TIEMPO DE ESPERA
        int waitTime = mStartTimes.get(i) - mArrivalTimes.get(i);
        waitTime = waitTime > 0 ? waitTime : 0;

        if (waitTime > 0) countQueue++;

        mWaitTimes.add(waitTime);

        //CALCULAR TIEMPO DE SERVIDOR OCIOSO
        int serverWait = 0;

        if (mServerWaitTimes.size() == 0)
            serverWait = mArrivalTimes.get(0);
        else {
            if (mWaitTimes.get(i) > 0)
                serverWait = 0;
            else {
                if (mArrivalTimes.get(i) > mEndTimes.get(i - 1))
                    serverWait = mArrivalTimes.get(i) - mEndTimes.get(i - 1);
                else
                    serverWait = 0;
            }
        }
        mServerWaitTimes.add(serverWait);

        //CALCULAR LONGITUD DE COLA
        int queueSize;

        if (mQueueSizes.size() == 0) {
            if (mStartTimes.get(0) > 0)
                queueSize = 1;
            else
                queueSize = 0;
        } else {
            queueSize = 0;

            int currentArrivalTime = mArrivalTimes.get(i);
            /*if (currentArrivalTime <= mEndTimes.get(i - 1))
                queueSize++;*/

            for (int j = mEndTimes.size() - 1; j > 0; j--) {
                if (currentArrivalTime <= mEndTimes.get(j))
                    queueSize++;
            }
        }
        mQueueSizes.add(queueSize);

        //CALCULAR TIEMPO TOTAL EN EL SISTEMA
        int totalTime = mEndTimes.get(i) - mArrivalTimes.get(i);
        mTotalTimes.add(totalTime);
    }

    //MUESTRA LOS DATOS EN PANTALLA EN TIEMPO REAL
    private void showStatistics(LinearLayout layout, Statistics statistics) {
        ((TextView) layout.findViewById(R.id.tv_mean)).setText(String.format(Locale.US, "%.2f", statistics.mean));
        ((TextView) layout.findViewById(R.id.tv_median)).setText(String.format(Locale.US, "%.2f", statistics.median));
        ((TextView) layout.findViewById(R.id.tv_mode)).setText(String.valueOf(statistics.mode));
        ((TextView) layout.findViewById(R.id.tv_std_deviation)).setText(String.format(Locale.US, "%.2f", statistics.stdDev));
        ((TextView) layout.findViewById(R.id.tv_min)).setText(String.valueOf(statistics.min));
        ((TextView) layout.findViewById(R.id.tv_max)).setText(String.valueOf(statistics.max));
    }

    //CALCULA LOS DATOS ESTADISTICOS
    private Statistics getStatistics(ArrayList<Integer> list) {
        Statistics statistics = new Statistics();

        statistics.mean = getMean(list);
        statistics.median = getMedian(list);
        statistics.mode = getMode(list);
        statistics.variance = getVariance(list);
        statistics.stdDev = getStdDev(statistics.variance);

        Pair<Integer, Integer> minMax = getMinMax(list);
        statistics.min = minMax.first;
        statistics.max = minMax.second;

        return statistics;
    }

    //CALCULA EL PROMEDIO
    private double getMean(ArrayList<Integer> list) {
        double sum = 0;
        for (Integer i : list) {
            sum += i;
        }

        return sum / list.size();
    }

    //CALCULA LA MEDIANA
    private double getMedian(ArrayList<Integer> list) {
        ArrayList<Integer> listCopy = new ArrayList<>(list);

        Collections.sort(listCopy);

        double median;

        if (listCopy.size() % 2 == 0)
            median = ((double) listCopy.get(listCopy.size() / 2)
                    + (double) listCopy.get(listCopy.size() / 2 - 1)) / 2;
        else
            median = (double) listCopy.get(listCopy.size() / 2);

        return median;
    }

    //CALCULA LA MODA
    private int getMode(ArrayList<Integer> list) {
        int maxValue = 0, maxCount = 0;

        for (int i = 0; i < list.size(); ++i) {
            int count = 0;

            for (int j = 0; j < list.size(); ++j) {
                if (list.get(j).equals(list.get(i)))
                    ++count;
            }

            if (count > maxCount) {
                maxCount = count;
                maxValue = list.get(i);
            }
        }

        return maxValue;
    }

    //CALCULA LA VARIANZA
    private double getVariance(ArrayList<Integer> list) {
        double mean = getMean(list);
        double temp = 0;

        for (double a : list)
            temp += (a - mean) * (a - mean);
        return temp / (list.size() - 1);
    }

    //CALCULA LA DESVIACION ESTANDAR
    private double getStdDev(double variance) {
        return Math.sqrt(variance);
    }

    private Pair<Integer, Integer> getMinMax(ArrayList<Integer> list) {
        int min = list.get(0);
        int max = list.get(0);

        for (Integer i : list) {
            if (i < min) min = i;
            if (i > max) max = i;
        }

        return new Pair<>(min, max);
    }

    //CALCULA LA PROBABILIDAD DE QUE UN CLIENTE ESTE EN COLA
    private double getQueueProbability(double amountQueue, double total) {
        return amountQueue / total;
    }
}

