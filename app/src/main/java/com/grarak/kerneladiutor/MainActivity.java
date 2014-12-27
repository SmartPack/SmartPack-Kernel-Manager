package com.grarak.kerneladiutor;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.grarak.kerneladiutor.elements.ListAdapter;
import com.grarak.kerneladiutor.elements.ScrimInsetsFrameLayout;
import com.grarak.kerneladiutor.fragments.AboutUsFragment;
import com.grarak.kerneladiutor.fragments.CPUFragment;
import com.grarak.kerneladiutor.fragments.CPUVoltageFragment;
import com.grarak.kerneladiutor.fragments.FrequencyTableFragment;
import com.grarak.kerneladiutor.fragments.GPUFragment;
import com.grarak.kerneladiutor.fragments.IOFragment;
import com.grarak.kerneladiutor.fragments.KSMFragment;
import com.grarak.kerneladiutor.fragments.KernelInformationFragment;
import com.grarak.kerneladiutor.fragments.LMKFragment;
import com.grarak.kerneladiutor.fragments.ScreenFragment;
import com.grarak.kerneladiutor.fragments.VMFragment;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.CPUVoltage;
import com.grarak.kerneladiutor.utils.kernel.GPU;
import com.grarak.kerneladiutor.utils.kernel.KSM;
import com.grarak.kerneladiutor.utils.kernel.LMK;
import com.grarak.kerneladiutor.utils.kernel.Screen;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 01.12.14.
 */
public class MainActivity extends ActionBarActivity implements Constants {

    private ProgressDialog progressDialog;

    private String mTitle;

    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ScrimInsetsFrameLayout mScrimInsetsFrameLayout;
    private ListView mDrawerList;

    private final List<ListAdapter.ListItem> mList = new ArrayList<>();

    private int cur_position = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Initialize for " + MODEL);
        setContentView(R.layout.activity_main);

        new Task().execute();
    }

    private void selectItem(int position) {
        Fragment fragment = mList.get(position).getFragment();

        if (fragment == null) {
            mDrawerList.setItemChecked(cur_position, true);
            return;
        }

        cur_position = position;

        Log.i(TAG, "Open postion " + position + ": " + mList.get(position).getTitle());
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

        mDrawerLayout.closeDrawer(mScrimInsetsFrameLayout);

        setTitle(mList.get(position).getTitle());
        mDrawerList.setItemChecked(position, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title.toString();
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mScrimInsetsFrameLayout)) super.onBackPressed();
        else mDrawerLayout.openDrawer(mScrimInsetsFrameLayout);
    }

    private final Runnable init = new Runnable() {
        @Override
        public void run() {
            mList.clear();
            mList.add(new ListAdapter.Header(getString(R.string.information)));
            mList.add(new ListAdapter.Item(getString(R.string.kernel_information), new KernelInformationFragment()));
            mList.add(new ListAdapter.Item(getString(R.string.frequency_table), new FrequencyTableFragment()));
            mList.add(new ListAdapter.Header(getString(R.string.kernel)));
            mList.add(new ListAdapter.Item(getString(R.string.cpu), new CPUFragment()));
            if (CPUVoltage.hasCpuVoltage())
                mList.add(new ListAdapter.Item(getString(R.string.cpu_voltage), new CPUVoltageFragment()));
            if (GPU.hasGpuControl())
                mList.add(new ListAdapter.Item(getString(R.string.gpu), new GPUFragment()));
            if (Screen.hasScreen())
                mList.add(new ListAdapter.Item(getString(R.string.screen), new ScreenFragment()));
            mList.add(new ListAdapter.Item(getString(R.string.io_scheduler), new IOFragment()));
            if (KSM.hasKsm())
                mList.add(new ListAdapter.Item(getString(R.string.ksm), new KSMFragment()));
            if (LMK.hasMinFree())
                mList.add(new ListAdapter.Item(getString(R.string.low_memory_killer), new LMKFragment()));
            mList.add(new ListAdapter.Item(getString(R.string.virtual_machine), new VMFragment()));
            mList.add(new ListAdapter.Header(getString(R.string.other)));
            mList.add(new ListAdapter.Item(getString(R.string.about_us), new AboutUsFragment()));

            mScrimInsetsFrameLayout = (ScrimInsetsFrameLayout) findViewById(R.id.scrimInsetsFrameLayout);

            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.material_blue_grey_900));
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

            SwitchCompat mApplyOnBootSwitch = (SwitchCompat) findViewById(R.id.apply_on_boot_switch);
            mApplyOnBootSwitch.setChecked(Utils.getBoolean("applyonboot", false, MainActivity.this));
            mApplyOnBootSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.saveBoolean("applyonboot", ((SwitchCompat) v).isChecked(), MainActivity.this);
                }
            });

            mDrawerList = (ListView) findViewById(R.id.drawer_list);
            mDrawerList.setAdapter(new ListAdapter.Adapter(MainActivity.this, mList));
            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectItem(position);
                }
            });

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, toolbar, 0, 0) {
                @Override
                public void onDrawerClosed(View drawerView) {
                    getSupportActionBar().setTitle(mTitle);
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    getSupportActionBar().setTitle(getString(R.string.app_name));
                }
            };

            mDrawerLayout.setDrawerListener(mDrawerToggle);
        }
    };

    private class Task extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();

            // Check root access and busybox installation
            boolean hasRoot = false;
            boolean hasBusybox = false;
            if (RootUtils.rooted()) hasRoot = RootUtils.rootAccess();
            if (hasRoot) hasBusybox = RootUtils.busyboxInstalled();

            if (!hasRoot || !hasBusybox) {
                Intent i = new Intent(MainActivity.this, TextActivity.class);
                Bundle args = new Bundle();
                args.putString(TextActivity.ARG_TEXT, !hasRoot ? getString(R.string.no_root) : getString(R.string.no_busybox));
                Log.d(TAG, !hasRoot ? getString(R.string.no_root) : getString(R.string.no_busybox));
                i.putExtras(args);
                startActivity(i);

                cancel(true);
                finish();
                return;
            }

            String[] files = {String.format(CPU_MAX_FREQ, 0), String.format(CPU_MIN_FREQ, 0),
                    String.format(CPU_SCALING_GOVERNOR, 0), LMK_MINFREE};

            for (String file : files) RootUtils.runCommand("chmod 644 " + file);

            runOnUiThread(init);

        }

        @Override
        protected Void doInBackground(Void... params) {
            selectItem(cur_position);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();
        }
    }

}
