/*
 * Copyright (c) 2018, The University of Memphis, MD2K Center of Excellence
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.md2k.mcerebrum.jinsmeme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeDouble;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.datatype.DataTypeFloat;
import org.md2k.datakitapi.datatype.DataTypeFloatArray;
import org.md2k.datakitapi.datatype.DataTypeInt;
import org.md2k.datakitapi.datatype.DataTypeIntArray;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.core.access.appinfo.AppInfo;
import org.md2k.mcerebrum.jinsmeme.configuration.ConfigurationManager;
import org.md2k.mcerebrum.jinsmeme.datakit.DataKitManager;
import org.md2k.mcerebrum.jinsmeme.permission.ActivityPermission;
import org.md2k.mcerebrum.jinsmeme.permission.Permission;
import org.md2k.mcerebrum.jinsmeme.plot.ActivityPlotChoice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * <code>ActivityMain</code> is the execution start of the application.
 */
public class ActivityMain extends AppCompatActivity {

    /** Used to signify that this activity should start normally. <p>Set to 0.</p> */
    public static final int OPERATION_RUN = 0;

    /** Used to signify this activity should start the settings view. <p>Set to 1.</p> */
    public static final int OPERATION_SETTINGS = 1;

    /** Used to signify this activity should start the plot view. <p>Set to 2.</p> */
    public static final int OPERATION_PLOT = 2;

    /** Used to signify this activity should start in the foreground. <p>Set to 5.</p> */
    public static final int OPERATION_START_FOREGROUND = 5;

    /** Used to signify this activity should start in the background. <p>Set to 3.</p> */
    public static final int OPERATION_START_BACKGROUND = 3;

    /** Used to signify this activity should stop in the background. <p>Set to 4.</p> */
    public static final int OPERATION_STOP_BACKGROUND = 4;

    /** Set to "operation" */
    public static final String OPERATION = "operation";

    /** Request code for permission request intents. */
    public static final int REQUEST_CODE = 1111;

    /** Delay in milliseconds. */
    public static final int DELAY_MILLIS = 1000;

    int operation;
    private DataKitManager dataKitManager;
    private Device device;
    SparseArray<Summary> summary;

    /**
     * Calls <code>super</code>, <code>loadCrashlytics()</code>, <code>readIntent()</code>, and
     * checks for permissions on this activity's creation.
     *
     * @param savedInstanceState This activity's previous state, is null if this activity has never
     *                           existed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadCrashlytics();
        readIntent();

        if (!Permission.hasPermission(ActivityMain.this)) {
            Intent intent = new Intent(this, ActivityPermission.class);
            startActivityForResult(intent, REQUEST_CODE);
        } else
            load();
    }

    /**
     * Sets the operation mode based on whether the intent has extras.
     */
    void readIntent() {
        if (getIntent().getExtras() != null) {
            operation = getIntent().getExtras().getInt(OPERATION, 0);
        } else operation = 0;
    }

    /**
     * Handles callback results for <code>checkRequirement()</code>.
     *
     * @param requestCode The code sent with the request.
     * @param resultCode The code returned with the result, used for request/result verification
     * @param data Android intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode != RESULT_OK)
                finish();
            else
                load();
        }
    }

    /**
     * Starts the UI
     */
    void initializeUI() {
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Button buttonService = (Button) findViewById(R.id.button_app_status);
        prepareTable();
        buttonService.setOnClickListener(v -> {
            //start();
            Intent intent = new Intent(getApplicationContext(), ServiceJinsMeme.class);
            if (AppInfo.isServiceRunning(getBaseContext(), ServiceJinsMeme.class.getName())) {
                stopService(intent);
            } else {
                startService(intent);
            }
        });

    }

    private HashMap<String, TextView> hashMapData = new HashMap<>();
    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        /**
         * Creates a new <code>Runnable()</code> object. This object creates a start button when run.
         */
        @Override
        public void run() {
            {
                long time = AppInfo.serviceRunningTime(ActivityMain.this, ServiceJinsMeme.class.getName());
                if (time < 0) {
                    ((Button) findViewById(R.id.button_app_status)).setText("START");
                    findViewById(R.id.button_app_status)
                            .setBackground(ContextCompat
                                    .getDrawable(ActivityMain.this, R.drawable.button_status_off));

                } else {
                    findViewById(R.id.button_app_status)
                            .setBackground(ContextCompat.
                                    getDrawable(ActivityMain.this, R.drawable.button_status_on));
                    ((Button) findViewById(R.id.button_app_status)).setText(DateTime.convertTimestampToTimeStr(time));

                }
                mHandler.postDelayed(this, DELAY_MILLIS);
            }
        }
    };
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        /**
         * Creates a new broadcast receiver that updates the table widget when it receives new data.
         * @param context Android context
         * @param intent Received intent.
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTable(intent);
        }
    };

    /**
     * Creates a <code>TableRow</code> widget using default settings.
     *
     * @return the <code>TableRow</code> widget
     */
    private TableRow createDefaultRow() {
        TableRow row = new TableRow(this);
        TextView tvSensor = new TextView(this);
        tvSensor.setText("sensor");
        tvSensor.setTypeface(null, Typeface.BOLD);
        tvSensor.setTextColor(getResources().getColor(R.color.teal_A700));
        TextView tvCount = new TextView(this);
        tvCount.setText("count");
        tvCount.setTypeface(null, Typeface.BOLD);
        tvCount.setTextColor(getResources().getColor(R.color.teal_A700));
        TextView tvFreq = new TextView(this);
        tvFreq.setText("freq.");
        tvFreq.setTypeface(null, Typeface.BOLD);
        tvFreq.setTextColor(getResources().getColor(R.color.teal_A700));
        TextView tvSample = new TextView(this);
        tvSample.setText("samples");
        tvSample.setTypeface(null, Typeface.BOLD);
        tvSample.setTextColor(getResources().getColor(R.color.teal_A700));
        row.addView(tvSensor);
        row.addView(tvCount);
        row.addView(tvFreq);
        row.addView(tvSample);
        return row;
    }

    /**
     * Returns the id of the given <code>DataSource</code>.
     * @param dataSource Given <code>DataSource</code>
     * @return The id of the given <code>DataSource</code>.
     */

    /**
     * Returns the name of the given <code>DataSource</code>.
     * @param dataSource Given <code>DataSource</code>
     * @return The name of the given <code>DataSource</code>.
     */

    /**
     *  Creates a table widget that displays the phone sensor data sources.
     */
    private void prepareTable() {
        TableLayout ll = (TableLayout) findViewById(R.id.tableLayout);
        ll.removeAllViews();
        ll.addView(createDefaultRow());
        ArrayList<DataSource> dataSources = ConfigurationManager.read();
        for (int i = 0; dataSources != null && i < dataSources.size(); i++) {
            String id = dataSources.get(i).getType();
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView tvSensor = new TextView(this);
            tvSensor.setPadding(5, 0, 0, 0);
            tvSensor.setText(dataSources.get(i).getType());
            TextView tvCount = new TextView(this);
            tvCount.setText("0");
            hashMapData.put(id + "_count", tvCount);
            TextView tvFreq = new TextView(this);
            tvFreq.setText("0");
            hashMapData.put(id + "_freq", tvFreq);
            TextView tvSample = new TextView(this);
            tvSample.setText("0");
            hashMapData.put(id + "_sample", tvSample);
            row.addView(tvSensor);
            row.addView(tvCount);
            row.addView(tvFreq);
            row.addView(tvSample);
            row.setBackgroundResource(R.drawable.border);
            ll.addView(row);
        }
    }

    /**
     * Updates the table widget with refreshed data from the sensors.
     *
     * @param intent Android intent
     */
    private void updateTable(Intent intent) {
        try {
            String dataSourceType = intent.getStringExtra(DataSource.class.getSimpleName());
//            Summary summary=intent.getParcelableExtra(Summary.class.getSimpleName());
            DataType dataType = (DataType) intent.getParcelableExtra(DataType.class.getSimpleName());
//            if (hashMapData.containsKey(id + "_count"))
//                hashMapData.get(id + "_count").setText(String.valueOf(summary.getCount()));
//            if (hashMapData.containsKey(id + "_freq"))
//                hashMapData.get(id + "_freq").setText(String.format(Locale.getDefault(),
//                        "%.1f", summary.getFrequency()));
            String sampleStr = "";
            if (dataType instanceof DataTypeFloat) {
                sampleStr = String.format(Locale.getDefault(), "%.1f", ((DataTypeFloat) dataType).getSample());
            } else if (dataType instanceof DataTypeFloatArray) {
                float[] sample = ((DataTypeFloatArray) dataType).getSample();
                for (int i = 0; i < sample.length; i++) {
                    if (i != 0)
                        sampleStr += ",";
                    if (i % 3 == 0 && i != 0)
                        sampleStr += "\n";
                    sampleStr = sampleStr + String.format(Locale.getDefault(), "%.1f", sample[i]);
                }
            } else if (dataType instanceof DataTypeDouble) {
                sampleStr = String.format(Locale.getDefault(), "%.1f", ((DataTypeDouble) dataType).getSample());
            } else if (dataType instanceof DataTypeDoubleArray) {
                double[] sample = ((DataTypeDoubleArray) dataType).getSample();
                for (int i = 0; i < sample.length; i++) {
                    if (i != 0)
                        sampleStr += ",";
                    if (i % 3 == 0 && i != 0)
                        sampleStr += "\n";
                    sampleStr = sampleStr + String.format(Locale.getDefault(), "%.1f", sample[i]);
                }
            } else if (dataType instanceof DataTypeInt) {
                sampleStr = String.format(Locale.getDefault(), "%d", ((DataTypeInt) dataType).getSample());
            } else if (dataType instanceof DataTypeIntArray) {
                int[] sample = ((DataTypeIntArray) dataType).getSample();
                for (int i = 0; i < sample.length; i++) {
                    if (i != 0)
                        sampleStr += ",";
                    if (i % 3 == 0 && i != 0)
                        sampleStr += "\n";
                    sampleStr = sampleStr + String.format(Locale.getDefault(), "%d", sample[i]);
                }
            }
            if (hashMapData.containsKey(dataSourceType + "_sample"))
                hashMapData.get(dataSourceType + "_sample").setText(sampleStr);
        } catch (Exception ignored) {}
    }

    /**
     * Registers receivers, prepares a new table widget and adds the relevant <code>runnable</code>
     * methods to the message queue upon resuming the activity.
     */
    @Override
    public void onResume() {

        initializeUI();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(ServiceJinsMeme.INTENT_DATA));
        mHandler.post(runnable);
        super.onResume();
    }

    /**
     * Removes <code>runnable</code> callbacks and unregisters <code>mMessageReciver</code> when the
     * activity is paused.
     */
    @Override
    public void onPause() {
        mHandler.removeCallbacks(runnable);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    /**
     * Creates the options menu.
     *
     * <p>
     *     Inflate the menu; this adds items to the action bar if it is present.
     * </p>
     *
     * @param menu Android Menu object
     * @return Always returns true.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    /**
     * Handles the selection of items on the action bar.
     *
     * <p>
     *     Handle action bar item clicks here. The action bar will automatically handle clicks on
     *     the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
     * </p>
     *
     * @param item Android MenuItem object
     * @return <code>super.onOptionsItemSelected(item)</code>
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_settings:
                intent = new Intent(this, ActivitySettings.class);
                startActivity(intent);
                break;
            case R.id.action_plot:
                intent = new Intent(this, ActivityPlotChoice.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method starts this activity in the given mode. It defaults to <code>OPERATION_RUN</code>.
     * <p>
     *     Available modes are:
     *     <ul>
     *         <li><code>OPERATION_START_BACKGROUND</code> - starts the service in the background</li>
     *         <li><code>OPERATION_START_FOREGROUND</code> - starts the UI</li>
     *         <li><code>OPERATION_STOP_BACKGROUND</code> - stops the service running in the background</li>
     *         <li><code>OPERATION_SETTINGS</code> - starts the settings activity</li>
     *     </ul>
     * </p>
     */
    void load() {
        Intent intent;
        switch (operation) {
            case OPERATION_RUN:
                initializeUI();
                break;
            case OPERATION_START_BACKGROUND:
                intent = new Intent(ActivityMain.this, ServiceJinsMeme.class);
                startService(intent);
                finish();
                break;
            case OPERATION_START_FOREGROUND:
                intent = new Intent(ActivityMain.this, ServiceJinsMeme.class);
                startService(intent);
                break;
            case OPERATION_STOP_BACKGROUND:
                intent = new Intent(ActivityMain.this, ServiceJinsMeme.class);
                stopService(intent);
                finish();
                break;
            case OPERATION_PLOT:
                intent = new Intent(this, ActivityPlotChoice.class);
                intent.putExtra("datasourcetype", getIntent().getStringExtra("datasourcetype"));
                startActivity(intent);
                finish();
                break;
            case OPERATION_SETTINGS:
                intent = new Intent(this, ActivitySettings.class);
                startActivity(intent);
                finish();
                break;
            default:
                initializeUI();
        }
    }

    /**
     * Creates a new <code>Crashlytics</code> object.
     */
    private void loadCrashlytics() {
//        Fabric.with(this, new Crashlytics());
    }
}
