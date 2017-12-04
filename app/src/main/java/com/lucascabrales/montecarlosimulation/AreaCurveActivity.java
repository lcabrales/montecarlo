package com.lucascabrales.montecarlosimulation;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.lucascabrales.montecarlosimulation.helpers.AlertDialogHelper;
import com.lucascabrales.montecarlosimulation.helpers.LoadingDialogHelper;
import com.lucascabrales.montecarlosimulation.helpers.MathEval;
import com.lucascabrales.montecarlosimulation.helpers.SimpleValidator;
import com.lucascabrales.montecarlosimulation.models.AreaCurve;

public class AreaCurveActivity extends AppCompatActivity {

    private AreaCurveActivity mContext = this;
    private LoadingDialogHelper mLoading;
    private AlertDialogHelper mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_curve);

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
                        boolean trust = SimpleValidator.validate(SimpleValidator.NOT_EMPTY,
                                ((EditText) findViewById(R.id.et_function)).getText().toString())
                                && SimpleValidator.validate(SimpleValidator.NOT_EMPTY,
                                ((EditText) findViewById(R.id.et_xmin)).getText().toString())
                                && SimpleValidator.validate(SimpleValidator.NOT_EMPTY,
                                ((EditText) findViewById(R.id.et_xmax)).getText().toString())
                                && SimpleValidator.validate(SimpleValidator.NOT_EMPTY,
                                ((EditText) findViewById(R.id.et_iterations)).getText().toString());

                        if (trust) {
                            mLoading.show();
                            calculateArea();
                        } else
                            mAlertDialog.show("Error", "Los datos de entrada no son válidos.");

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
    private void calculateArea() {
        String function = ((EditText) findViewById(R.id.et_function)).getText().toString();
        double xmin = Double.parseDouble(((EditText) findViewById(R.id.et_xmin)).getText().toString());
        double xmax = Double.parseDouble(((EditText) findViewById(R.id.et_xmax)).getText().toString());
        String unit = ((EditText) findViewById(R.id.et_unit)).getText().toString();
        int iterations = Integer.parseInt(((EditText) findViewById(R.id.et_iterations)).getText().toString());

        AreaCurve areaCurve = new AreaCurve();
        areaCurve.function = function;
        areaCurve.xmin = xmin;
        areaCurve.xmax = xmax;
        areaCurve.unit = unit;
        areaCurve.iterations = iterations;

        new AsyncTask<AreaCurve, String, String>() {
            @Override
            protected String doInBackground(AreaCurve... areaCurves) {
                AreaCurve areaCurve = areaCurves[0];

                String function = areaCurve.function;

                double xmin = areaCurve.xmin; //given xmin
                double xmax = areaCurve.xmax; //given xmax

                try {
                    // Find ymin and ymax
                    int numSteps = areaCurve.iterations; //bigger the better but slower!

                    MathEval eval = new MathEval();
                    eval.setVariable("x", xmin);
                    double ymin = eval.evaluate(function);
                    double ymax = ymin;

                    double x, y;

                    for (int i = 0; i < numSteps; i++) {
                        x = (xmin + (xmax - xmin) * (double) i) / numSteps;
                        eval.setVariable("x", x);
                        y = eval.evaluate(function);
                        if (y < ymin) ymin = y;
                        if (y > ymax) ymax = y;
                    }

                    // Montecarlo
                    double rectArea = (xmax - xmin) * (ymax - ymin); // area of rectangle
                    int numPoints = areaCurve.iterations; //bigger the better but slower!
                    int success = 0;

                    for (int i = 0; i < numPoints; i++) {
                        // establish random coordinates
                        x = xmin + (xmax - xmin) * Math.random();
                        y = ymin + (ymax - ymin) * Math.random();

                        // checks if point is inside the area
                        eval.setVariable("x", x);
                        double fx = eval.evaluate(function);
                        if (fx > 0 && y > 0 && y <= fx)
                            success += 1;
                        if (fx < 0 && y < 0 && y >= fx)
                            success += 1;
                    }

                    double finalArea = rectArea * (double) success / numPoints;

                    Log.e("AREA", String.valueOf(finalArea));

                    return finalArea + " " + areaCurve.unit;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String area) {
                super.onPostExecute(area);

                mLoading.dismiss();

                if (area == null) {
                    mAlertDialog.show("Error", "Ha ocurrido un error. Verifique la función introducida.");
                } else {
                    findViewById(R.id.ll_results).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.tv_area)).setText(area);
                }
            }
        }.execute(areaCurve);
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
            mAlertDialog.show("Área debajo de la curva", String.valueOf(Html.fromHtml(getString(R.string.area_curve))));
        }

        return super.onOptionsItemSelected(item);
    }
}
