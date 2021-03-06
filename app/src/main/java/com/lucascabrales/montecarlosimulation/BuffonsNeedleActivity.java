package com.lucascabrales.montecarlosimulation;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.lucascabrales.montecarlosimulation.helpers.AlertDialogHelper;
import com.lucascabrales.montecarlosimulation.helpers.LoadingDialogHelper;
import com.lucascabrales.montecarlosimulation.helpers.SimpleValidator;
import com.lucascabrales.montecarlosimulation.models.BuffonsNeedle;

public class BuffonsNeedleActivity extends AppCompatActivity {

    private BuffonsNeedleActivity mContext = this;
    private LoadingDialogHelper mLoading;
    private AlertDialogHelper mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buffons_needle);

        mLoading = new LoadingDialogHelper(mContext);
        mAlertDialog = new AlertDialogHelper(mContext);

        setupToolbar();
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_accept:
                        String size = ((EditText) findViewById(R.id.et_size)).getText().toString();
                        String space = ((EditText) findViewById(R.id.et_space)).getText().toString();

                        boolean trust = (SimpleValidator.validate(SimpleValidator.NOT_EMPTY,
                                space)
                                && SimpleValidator.validate(SimpleValidator.NOT_EMPTY,
                                size)
                                && SimpleValidator.validate(SimpleValidator.NOT_EMPTY,
                                ((EditText) findViewById(R.id.et_qty)).getText().toString())
                                && Integer.parseInt(space) > Integer.parseInt(size));

                        if (trust) {
                            mLoading.show();
                            calculatePiEstimate();
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
    private void calculatePiEstimate() {
        //TODO VALIDATE FIELDS

        // space between two lines
        int space = Integer.parseInt(((EditText) findViewById(R.id.et_space)).getText().toString());
        // size of the needle
        int size = Integer.parseInt(((EditText) findViewById(R.id.et_size)).getText().toString());
        // total amount of needles thrown
        int needlesQty = Integer.parseInt(((EditText) findViewById(R.id.et_qty)).getText().toString());

        BuffonsNeedle buffonsNeedle = new BuffonsNeedle();
        buffonsNeedle.space = space;
        buffonsNeedle.size = size;
        buffonsNeedle.qty = needlesQty;

        new AsyncTask<BuffonsNeedle, String, BuffonsNeedle>() {
            @Override
            protected BuffonsNeedle doInBackground(BuffonsNeedle... buffonsNeedles) {
                BuffonsNeedle buffonsNeedle = buffonsNeedles[0];

                int distance = buffonsNeedle.space;
                int needlesQty = buffonsNeedle.qty;
                int size = buffonsNeedle.size;

                double angle; // random angle of the thrown needle
                double position; // the position of the center of the pin (in the range of 0 to the distance between two lines)
                double piEstimate; // the result we will finally give (should be approximate to Pi)
                int intersections = 0; // number of times needle intersected with line, the number is a double due to calculation later

                for (int i = 0; i < needlesQty; i++) {
                    angle = Math.random() * Math.PI;
                    position = distance * Math.random();
                    // checking to see if there is indeed an intersection - using the intermediate value theorem.
                    if (((position + size * Math.sin(angle) / 2 >= distance) && (position - size * Math.sin(angle) / 2 <= distance)) ||
                            ((position + size * Math.sin(angle) / 2 >= 0) && (position - size * Math.sin(angle) / 2 <= 0))) {
                        intersections++;
                    }
                }

                piEstimate = (double) (2 * size * needlesQty) / (distance * intersections);

                buffonsNeedle.intersections = intersections;
                buffonsNeedle.piEstimate = piEstimate;

                return buffonsNeedle;
            }

            @Override
            protected void onPostExecute(BuffonsNeedle buffonsNeedle) {
                super.onPostExecute(buffonsNeedle);

                mLoading.dismiss();

                findViewById(R.id.ll_results).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.tv_intersections)).setText(String.valueOf(buffonsNeedle.intersections));
                ((TextView) findViewById(R.id.tv_pi)).setText(String.valueOf(buffonsNeedle.piEstimate));
            }
        }.execute(buffonsNeedle);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (toolbar != null) {
            toolbar.setTitle(getTitle());
        }
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
        } else if (id == R.id.info) {
            mAlertDialog.show("Aguja de Bufón", String.valueOf(Html.fromHtml(getString(R.string.buffons_needle))));
        }

        return super.onOptionsItemSelected(item);
    }
}
