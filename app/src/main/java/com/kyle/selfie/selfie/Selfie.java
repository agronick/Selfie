package com.kyle.selfie.selfie;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


public class Selfie extends Activity  {

    public static final String TAG = "SELFIE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        EndlessAdapter adapter = new EndlessAdapter(getBaseContext(), gridview, (TextView) findViewById(R.id.statustext));
        gridview.setAdapter(adapter);

    }



}
