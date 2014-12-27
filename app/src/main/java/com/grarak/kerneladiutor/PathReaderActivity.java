package com.grarak.kerneladiutor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.CPU;
import com.grarak.kerneladiutor.utils.root.Control;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 14.12.14.
 */
public class PathReaderActivity extends ActionBarActivity {

    private final Handler hand = new Handler();

    public static final String ARG_TITLE = "title";
    public static final String ARG_PATH = "path";
    public static final String ARG_ERROR = "error";
    private String path;
    private String error;

    private final List<File> files = new ArrayList<>();
    private final List<String> values = new ArrayList<>();

    private Adapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ListView list;

    private final String[] FREQ_FILE = new String[]{"hispeed_freq", "optimal_freq", "sync_freq"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        refreshLayout = new SwipeRefreshLayout(this);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                hand.postDelayed(run, 500);
            }
        });
        list = new ListView(this);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean freq = false;
                for (String freqFile : FREQ_FILE)
                    if (files.get(position).getName().equals(freqFile)) {
                        freq = true;
                        break;
                    }

                if (freq) {
                    String[] values = new String[CPU.getFreqs().size()];
                    for (int i = 0; i < values.length; i++)
                        values[i] = String.valueOf(CPU.getFreqs().get(i));
                    showPopupDialog(files.get(position).getAbsolutePath(), values);
                } else showDialog(files.get(position).getAbsolutePath(), values.get(position));
            }
        });
        refreshLayout.addView(list);

        setContentView(refreshLayout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get args
        getSupportActionBar().setTitle(getIntent().getExtras().getString(ARG_TITLE));
        path = getIntent().getExtras().getString(ARG_PATH);
        error = getIntent().getExtras().getString(ARG_ERROR);

        // Collecting all files and add them to Lists
        File[] fileArray = new File(path).listFiles();
        if (Utils.existFile(path) && fileArray != null) {
            for (File file : fileArray)
                if (file.isFile()) {
                    String value = Utils.readFile(file.getAbsolutePath());
                    if (value != null) {
                        files.add(file);
                        values.add(value);
                    }
                }
            // Setup adapter
            if (files.size() > 0) {
                adapter = new Adapter(PathReaderActivity.this, files, values);
                list.setAdapter(adapter);
            } else {
                Utils.toast(error, PathReaderActivity.this);
                finish();
            }
        } else {
            Utils.toast(error, PathReaderActivity.this);
            finish();
        }
    }

    private final Runnable run = new Runnable() {
        @Override
        public void run() {
            // Remove all items first otherwise we will get duplicated items
            files.clear();
            values.clear();

            File[] fileArray = new File(path).listFiles();
            if (fileArray != null) {
                for (File file : fileArray) {
                    String value = Utils.readFile(file.getAbsolutePath());
                    if (value != null) {
                        files.add(file);
                        values.add(value);
                    }
                }

                adapter.notifyDataSetChanged();
                list.invalidateViews();
                list.refreshDrawableState();
            }
            refreshLayout.setRefreshing(false);
        }
    };

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
                hand.postDelayed(run, 500);
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
                hand.postDelayed(run, 500);
            }
        }).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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

    private class Adapter extends ArrayAdapter<String> {

        private final List<File> files;
        private final List<String> values;

        public Adapter(Context context, List<File> files, List<String> values) {
            super(context, R.layout.path_read_view, values);
            this.files = files;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.path_read_view, null, false);

            TextView keyText = (TextView) view.findViewById(R.id.key);
            TextView valueText = (TextView) view.findViewById(R.id.value);

            keyText.setText(files.get(position).getName());
            valueText.setText(values.get(position) + "\n");

            return view;
        }
    }

}
