package com.lucascabrales.montecarlosimulation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lucascabrales.montecarlosimulation.adapters.NavMenuAdapter;
import com.lucascabrales.montecarlosimulation.models.MainMenu;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private MainActivity mContext = this;
    private DrawerLayout mNavDrawer;

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
                    case R.id.btn_queue:
                        intent = new Intent(mContext, QueueActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        };

        findViewById(R.id.btn_buffon).setOnClickListener(listener);
        findViewById(R.id.btn_area).setOnClickListener(listener);
        findViewById(R.id.btn_random_walk).setOnClickListener(listener);
        findViewById(R.id.btn_queue).setOnClickListener(listener);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (toolbar != null) {
            toolbar.setTitle(getTitle());
        }

        setupNavDrawer(toolbar);
    }

    private void setupNavDrawer(Toolbar toolbar) {
        setupMenu();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                mContext, mNavDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mNavDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupMenu() {
        ListView menu = findViewById(R.id.lv_menu);
        mNavDrawer = findViewById(R.id.drawer_layout);

        mNavDrawer.setScrimColor(ContextCompat.getColor(mContext, R.color.nav_drawer_shadow));

        View header = LayoutInflater.from(mContext).inflate(R.layout.nav_menu_header, null, false);
        menu.addHeaderView(header);
        menu.setSelectionAfterHeaderView();
        NavMenuAdapter menuAdapter = new NavMenuAdapter(mContext);
        menu.setAdapter(menuAdapter);
        menu.setOnItemClickListener(mContext);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        ((ListView) parent).setItemChecked(position, true);
        callActivity(position - 1);
    }

    private void callActivity(int position) {
        //LLAMADO DE LA ACTIVIDAD DE ACUERDO AL ITEM DEL MENU SELECCIONADO
        MainMenu menu = MainMenu.getList().get(position);
        Intent intent;

        switch (position) {
            default:
                try {
                    intent = new Intent(mContext, Class.forName("com.lucascabrales.montecarlosimulation."
                            + menu.controller));
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
        }
    }
}
