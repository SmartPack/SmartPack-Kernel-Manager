package com.grarak.kerneladiutor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.elements.PopupCardItem;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.CPU;
import com.grarak.kerneladiutor.utils.root.Control;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 14.12.14.
 */
public class PathReaderActivity extends ActionBarActivity implements View.OnClickListener {

    public enum PATH_TYPE {
        GOVERNOR, IO
    }

    private final Handler hand = new Handler();

    public static final String ARG_TYPE = "type";
    public static final String ARG_TITLE = "title";
    public static final String ARG_PATH = "path";
    public static final String ARG_ERROR = "error";
    private String path;

    private List<DAdapter.DView> dViewList = new ArrayList<>();

    private RecyclerView recyclerView;
    private DAdapter.Adapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private View backgroundView;

    private final String[] FREQ_FILE = new String[]{"hispeed_freq", "optimal_freq", "sync_freq",
            "max_freq_blank", "high_freq_zone"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        setContentView(R.layout.path_read_view);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.color_primary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                hand.postDelayed(refresh, 500);
            }
        });

        backgroundView = findViewById(R.id.background_view);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        int padding = getSidePadding();
        recyclerView.setPadding(padding, 0, padding, 0);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get args
        getSupportActionBar().setTitle(getIntent().getExtras().getString(ARG_TITLE));
        path = getIntent().getExtras().getString(ARG_PATH);
        String error = getIntent().getExtras().getString(ARG_ERROR);

        File[] fileArray = new File(path).listFiles();
        if (Utils.existFile(path) && fileArray != null) {
            for (File file : fileArray)
                if (file.isFile()) {
                    String value = Utils.readFile(file.getAbsolutePath());
                    if (value != null) {
                        PopupCardItem.DPopupCard dPopupCard = new PopupCardItem.DPopupCard(null);
                        dPopupCard.setDescription(file.getName());
                        dPopupCard.setItem(value);
                        dPopupCard.setOnClickListener(this);

                        dViewList.add(dPopupCard);
                    }
                }

            // Setup adapter
            if (dViewList.size() > 0) {
                adapter = new DAdapter.Adapter(dViewList);
                recyclerView.setAdapter(adapter);
            } else {
                Utils.toast(error, PathReaderActivity.this);
                finish();
            }
        } else {
            Utils.toast(error, PathReaderActivity.this);
            finish();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int padding = getSidePadding();
        recyclerView.setPadding(padding, 0, padding, 0);
    }

    private int getSidePadding() {
        if (backgroundView != null)
            if (Utils.getScreenOrientation(this) == Configuration.ORIENTATION_LANDSCAPE) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) backgroundView.getLayoutParams();
                params.height = getViewHeight();
                backgroundView.setLayoutParams(params);
                backgroundView.setVisibility(View.VISIBLE);
            } else backgroundView.setVisibility(View.GONE);

        double padding = getResources().getDisplayMetrics().widthPixels * 0.08361204013;
        return Utils.getScreenOrientation(this) == Configuration.ORIENTATION_LANDSCAPE ? (int) padding : 0;
    }

    public int getViewHeight() {
        TypedArray ta = obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int actionBarSize = ta.getDimensionPixelSize(0, 100);
        int height = getResources().getDisplayMetrics().heightPixels;
        return height / 3 - actionBarSize;
    }

    private final Runnable refresh = new Runnable() {
        @Override
        public void run() {
            dViewList.clear();

            File[] fileArray = new File(path).listFiles();
            if (fileArray != null) {
                for (File file : fileArray) {
                    String value = Utils.readFile(file.getAbsolutePath());
                    if (value != null) {
                        PopupCardItem.DPopupCard dPopupCard = new PopupCardItem.DPopupCard(null);
                        dPopupCard.setDescription(file.getName());
                        dPopupCard.setItem(value);
                        dPopupCard.setOnClickListener(PathReaderActivity.this);

                        dViewList.add(dPopupCard);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            refreshLayout.setRefreshing(false);
        }
    };

    @Override
    public void onClick(View v) {
        boolean freq = false;
        for (String freqFile : FREQ_FILE)
            if (((PopupCardItem) v).getDescription().equals(freqFile)) {
                freq = true;
                break;
            }

        if (freq && getIntent().getExtras().getInt(ARG_TYPE) == PATH_TYPE.GOVERNOR.ordinal()) {
            String[] values = new String[CPU.getFreqs().size()];
            for (int i = 0; i < values.length; i++)
                values[i] = String.valueOf(CPU.getFreqs().get(i));
            showPopupDialog(path + "/" + ((PopupCardItem) v).getDescription(), values);
        } else
            showDialog(path + "/" + ((PopupCardItem) v).getDescription(),
                    ((PopupCardItem) v).getItem());
    }

    private void showDialog(final String file, String value) {
        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(30, 30, 30, 30);

        final EditText editText = new EditText(this);
        editText.setGravity(Gravity.CENTER);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        editText.setText(value);

        layout.addView(editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout)
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Control.runCommand(editText.getText().toString(), file, Control.CommandType.GENERIC,
                        PathReaderActivity.this);
                refreshLayout.setRefreshing(true);
                hand.postDelayed(refresh, 500);
            }
        }).show();
    }

    private void showPopupDialog(final String file, final String[] values) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(values, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Control.runCommand(values[which], file, Control.CommandType.GENERIC, PathReaderActivity.this);
                refreshLayout.setRefreshing(true);
                hand.postDelayed(refresh, 500);
            }
        }).show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

}
