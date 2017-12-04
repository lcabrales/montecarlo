package com.lucascabrales.montecarlosimulation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.lucascabrales.montecarlosimulation.enums.Distribution;
import com.lucascabrales.montecarlosimulation.enums.TimeUnit;
import com.lucascabrales.montecarlosimulation.helpers.AlertDialogHelper;
import com.lucascabrales.montecarlosimulation.helpers.SimpleValidator;
import com.lucascabrales.montecarlosimulation.models.queue.SimParams;

import java.util.ArrayList;

public class QueueActivity extends AppCompatActivity {

    private QueueActivity mContext = this;

    private Spinner mQueueSpinner, mServerSpinner, mTimeUnitSpinner;
    private AlertDialogHelper mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        mAlertDialog = new AlertDialogHelper(mContext);

        setupToolbar();
        declareViews();
        setupOnClickListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (toolbar != null) {
            toolbar.setTitle(getTitle());
        }
    }

    //DEFINE EL COMPORTAMIENTO DE CADA VISTA
    private void declareViews() {
        mTimeUnitSpinner = findViewById(R.id.sp_time_unit);
        mQueueSpinner = findViewById(R.id.sp_queue_distribution);
        mServerSpinner = findViewById(R.id.sp_server_distribution);

        String[] timeUnits = {
                TimeUnit.SECONDS.getName(),
                TimeUnit.MINUTES.getName(),
                TimeUnit.HOURS.getName()
        };

        String[] queueDistributions = {
                Distribution.NORMAL.getName(),
                Distribution.POISSON.getName(),
                Distribution.UNIFORM.getName()
        };

        String[] serverDistributions = {
                Distribution.NORMAL.getName(),
                Distribution.POISSON.getName()
        };

        ArrayAdapter<String> timeUnitAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, timeUnits);
        timeUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTimeUnitSpinner.setAdapter(timeUnitAdapter);

        ArrayAdapter<String> queueAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, queueDistributions);
        queueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mQueueSpinner.setAdapter(queueAdapter);

        ArrayAdapter<String> serverAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, serverDistributions);
        serverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mServerSpinner.setAdapter(serverAdapter);

        mQueueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = (String) mQueueSpinner.getSelectedItem();
                Distribution distribution = Distribution.fromString(item);

                switch (distribution) {
                    case NORMAL:
                        findViewById(R.id.et_queue_mean).setVisibility(View.VISIBLE);
                        findViewById(R.id.et_queue_deviance).setVisibility(View.VISIBLE);
                        findViewById(R.id.et_max).setVisibility(View.GONE);
                        break;
                    case POISSON:
                        findViewById(R.id.et_queue_mean).setVisibility(View.VISIBLE);
                        findViewById(R.id.et_queue_deviance).setVisibility(View.GONE);
                        findViewById(R.id.et_max).setVisibility(View.GONE);
                        break;
                    case UNIFORM:
                        findViewById(R.id.et_queue_mean).setVisibility(View.GONE);
                        findViewById(R.id.et_queue_deviance).setVisibility(View.GONE);
                        findViewById(R.id.et_max).setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mServerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = (String) mServerSpinner.getSelectedItem();
                Distribution distribution = Distribution.fromString(item);

                switch (distribution) {
                    case NORMAL:
                        findViewById(R.id.et_server_mean).setVisibility(View.VISIBLE);
                        findViewById(R.id.et_server_deviance).setVisibility(View.VISIBLE);
                        break;
                    case POISSON:
                        findViewById(R.id.et_server_mean).setVisibility(View.VISIBLE);
                        findViewById(R.id.et_server_deviance).setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //ESTABLECE EL COMPORTAMIENTO DE LOS BOTONES
    private void setupOnClickListeners() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();

                switch (id) {
                    case R.id.btn_clean:
                        resetParams();
                        break;
                    case R.id.btn_start:
                        startSimulation();
                        break;
                    case R.id.itv_info:
                    case R.id.itv_info2:
                        Intent intent = new Intent(mContext, DistributionsActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        };

        findViewById(R.id.btn_clean).setOnClickListener(listener);
        findViewById(R.id.btn_start).setOnClickListener(listener);
        findViewById(R.id.itv_info).setOnClickListener(listener);
        findViewById(R.id.itv_info2).setOnClickListener(listener);
    }

    //REINICIA LOS PARAMETROS SELECCIONADOS
    private void resetParams() {
        mTimeUnitSpinner.setSelection(0);
        mQueueSpinner.setSelection(0);
        mServerSpinner.setSelection(0);

        ((EditText) findViewById(R.id.et_queue_mean)).setText("");
        ((EditText) findViewById(R.id.et_server_deviance)).setText("");
        ((EditText) findViewById(R.id.et_server_mean)).setText("");
        ((EditText) findViewById(R.id.et_queue_deviance)).setText("");
    }

    private void startSimulation() {
        ArrayList<String> validations = new ArrayList<>();

        SimParams queueParams = getQueueParams();
        SimParams serverParams = getServerParams();

        switch (queueParams.distribution) {
            case NORMAL:
                validations.add(((EditText) findViewById(R.id.et_queue_mean)).getText().toString());
                validations.add(((EditText) findViewById(R.id.et_queue_deviance)).getText().toString());
                break;
            case POISSON:
                validations.add(((EditText) findViewById(R.id.et_queue_mean)).getText().toString());
                break;
            case UNIFORM:
                validations.add(((EditText) findViewById(R.id.et_max)).getText().toString());
                break;
        }

        switch (serverParams.distribution) {
            case NORMAL:
                validations.add(((EditText) findViewById(R.id.et_server_mean)).getText().toString());
                validations.add(((EditText) findViewById(R.id.et_server_deviance)).getText().toString());
                break;
            case POISSON:
                validations.add(((EditText) findViewById(R.id.et_server_mean)).getText().toString());
                break;
        }

        boolean trust = true;

        for (int i = 0; i < validations.size(); i++) {
            String text = validations.get(i);
            if (!SimpleValidator.validate(SimpleValidator.NOT_EMPTY, text)) {
                trust = false;
                break;
            }
        }

        if (trust) {
            Intent intent = new Intent(mContext, SimActivity.class);
            intent.putExtra(SimParams.QUEUE_KEY, queueParams);
            intent.putExtra(SimParams.SERVER_KEY, serverParams);
            startActivity(intent);
        } else
            Toast.makeText(mContext, "Favor introducir todos los parámetros necesarios", Toast.LENGTH_LONG).show();
    }

    //RECUPERA LA INFORMACION INTRODUCIDA RELACIONADA A LA COLA
    private SimParams getQueueParams() {
        TimeUnit timeUnit = TimeUnit.fromString((String) mTimeUnitSpinner.getSelectedItem());
        Distribution distribution = Distribution.fromString((String) mQueueSpinner.getSelectedItem());

        SimParams params = new SimParams();
        params.timeUnit = timeUnit;
        params.distribution = distribution;

        String deviance = ((EditText) findViewById(R.id.et_queue_deviance)).getText().toString();
        params.deviance = deviance.length() > 0 ? Double.valueOf(deviance) : null;

        String mean = ((EditText) findViewById(R.id.et_queue_mean)).getText().toString();
        params.mean = mean.length() > 0 ? Double.valueOf(mean) : null;

        String max = ((EditText) findViewById(R.id.et_max)).getText().toString();
        params.max = max.length() > 0 ? Double.valueOf(max) : null;

        return params;
    }

    //RECUPERA LA INFORMACION INTRODUCIDA RELACIONADA AL SERVIDOR
    private SimParams getServerParams() {
        TimeUnit timeUnit = TimeUnit.fromString((String) mTimeUnitSpinner.getSelectedItem());
        Distribution distribution = Distribution.fromString((String) mServerSpinner.getSelectedItem());

        SimParams params = new SimParams();
        params.timeUnit = timeUnit;
        params.distribution = distribution;

        String deviance = ((EditText) findViewById(R.id.et_server_deviance)).getText().toString();
        params.deviance = deviance.length() > 0 ? Double.valueOf(deviance) : null;

        String mean = ((EditText) findViewById(R.id.et_server_mean)).getText().toString();
        params.mean = mean.length() > 0 ? Double.valueOf(mean) : null;

        return params;
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        menu.findItem(R.id.info)
                .setIcon(new IconDrawable(mContext, FontAwesomeIcons.fa_info_circle)
                        .colorRes(R.color.color_accent_dark).actionBarSize());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.info){
            mAlertDialog.show("Lava Autos (Colas)", String.valueOf(Html.fromHtml(getString(R.string.car_wash))));
        }

        return super.onOptionsItemSelected(item);
    }
}

