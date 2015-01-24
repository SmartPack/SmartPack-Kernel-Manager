package com.grarak.kerneladiutor;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.LightingColorFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.grarak.kerneladiutor.elements.ListAdapter;
import com.grarak.kerneladiutor.elements.ScrimInsetsFrameLayout;
import com.grarak.kerneladiutor.fragments.information.FrequencyTableFragment;
import com.grarak.kerneladiutor.fragments.information.KernelInformationFragment;
import com.grarak.kerneladiutor.fragments.kernel.BatteryFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUFragment;
import com.grarak.kerneladiutor.fragments.kernel.CPUVoltageFragment;
import com.grarak.kerneladiutor.fragments.kernel.GPUFragment;
import com.grarak.kerneladiutor.fragments.kernel.IOFragment;
import com.grarak.kerneladiutor.fragments.kernel.KSMFragment;
import com.grarak.kerneladiutor.fragments.kernel.LMKFragment;
import com.grarak.kerneladiutor.fragments.kernel.MiscControlsFragment;
import com.grarak.kerneladiutor.fragments.kernel.ScreenFragment;
import com.grarak.kerneladiutor.fragments.kernel.SoundFragment;
import com.grarak.kerneladiutor.fragments.kernel.VMFragment;
import com.grarak.kerneladiutor.fragments.kernel.WakeFragment;
import com.grarak.kerneladiutor.fragments.other.AboutUsFragment;
import com.grarak.kerneladiutor.fragments.tools.BuildpropFragment;
import com.grarak.kerneladiutor.utils.Constants;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.CPUVoltage;
import com.grarak.kerneladiutor.utils.kernel.GPU;
import com.grarak.kerneladiutor.utils.kernel.KSM;
import com.grarak.kerneladiutor.utils.kernel.Screen;
import com.grarak.kerneladiutor.utils.kernel.Sound;
import com.grarak.kerneladiutor.utils.kernel.Wake;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 01.12.14.
 */
public class MainActivity extends ActionBarActivity implements Constants {

    private boolean hasRoot;
    private boolean hasBusybox;

    private ProgressBar progressBar;
    private Toolbar toolbar;

    private String mTitle;

    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ScrimInsetsFrameLayout mScrimInsetsFrameLayout;
    private ListView mDrawerList;

    private final List<ListAdapter.ListItem> mList = new ArrayList<>();

    private int cur_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = new ProgressBar(this);
        progressBar.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000,
                getResources().getColor(android.R.color.white)));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(progressBar, new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL | Gravity.END));

        new Task().execute();
    }

    private void selectItem(int position) {
        Fragment fragment = mList.get(position).getFragment();

        if (fragment == null || cur_position == position) {
            mDrawerList.setItemChecked(cur_position, true);
            return;
        }

        mDrawerLayout.closeDrawer(mScrimInsetsFrameLayout);

        cur_position = position;

        try {
            Log.i(TAG, "Open postion " + position + ": " + mList.get(position).getTitle());
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle(mList.get(position).getTitle());
        mDrawerList.setItemChecked(position, true);
    }

    private void setList() {
        mList.clear();
        mList.add(new ListAdapter.MainHeader());
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
        if (Wake.hasWake())
            mList.add(new ListAdapter.Item(getString(R.string.wake), new WakeFragment()));
        if (Sound.hasSound())
            mList.add(new ListAdapter.Item(getString(R.string.sound), new SoundFragment()));
        mList.add(new ListAdapter.Item(getString(R.string.battery), new BatteryFragment()));
        mList.add(new ListAdapter.Item(getString(R.string.io_scheduler), new IOFragment()));
        if (KSM.hasKsm())
            mList.add(new ListAdapter.Item(getString(R.string.ksm), new KSMFragment()));
        mList.add(new ListAdapter.Item(getString(R.string.low_memory_killer), new LMKFragment()));
        mList.add(new ListAdapter.Item(getString(R.string.virtual_memory), new VMFragment()));
        mList.add(new ListAdapter.Item(getString(R.string.misc_controls), new MiscControlsFragment()));
        mList.add(new ListAdapter.Header(getString(R.string.tools)));
        mList.add(new ListAdapter.Item(getString(R.string.build_prop_editor), new BuildpropFragment()));
        mList.add(new ListAdapter.Header(getString(R.string.other)));
        mList.add(new ListAdapter.Item(getString(R.string.about_us), new AboutUsFragment()));
    }

    private void setView() {
        mScrimInsetsFrameLayout = (ScrimInsetsFrameLayout) findViewById(R.id.scrimInsetsFrameLayout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.color_primary_dark));
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
    }

    private void setInterface() {
        mScrimInsetsFrameLayout.setLayoutParams(getDrawerParams());
        mDrawerList.setAdapter(new ListAdapter.Adapter(this, mList));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

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

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                if (mDrawerToggle != null) mDrawerToggle.syncState();
            }
        });
    }

    private class Task extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            setView();
        }

        @Override
        protected String doInBackground(Void... params) {

            // Check root access and busybox installation
            if (RootUtils.rooted()) hasRoot = RootUtils.rootAccess();
            if (hasRoot) hasBusybox = RootUtils.busyboxInstalled();

            if (hasRoot && hasBusybox) {
                RootUtils.su = new RootUtils.SU();
                RootUtils.runCommand("chmod 644 " + LMK_MINFREE);

                setList();

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (!hasRoot || !hasBusybox) {
                Intent i = new Intent(MainActivity.this, TextActivity.class);
                Bundle args = new Bundle();
                args.putString(TextActivity.ARG_TEXT, !hasRoot ? getString(R.string.no_root)
                        : getString(R.string.no_busybox));
                Log.d(TAG, !hasRoot ? getString(R.string.no_root) : getString(R.string.no_busybox));
                i.putExtras(args);
                startActivity(i);

                cancel(true);
                finish();
                return;
            }

            setInterface();
            try {
                ((ViewGroup) progressBar.getParent()).removeView(progressBar);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            selectItem(2);
        }
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mScrimInsetsFrameLayout != null)
            mScrimInsetsFrameLayout.setLayoutParams(getDrawerParams());
        if (mDrawerToggle != null) mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (!mDrawerLayout.isDrawerOpen(mScrimInsetsFrameLayout)) super.onBackPressed();
        else mDrawerLayout.closeDrawer(mScrimInsetsFrameLayout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (RootUtils.su != null) RootUtils.su.close();
        RootUtils.su = null;
    }

    private DrawerLayout.LayoutParams getDrawerParams() {
        boolean tablet = Utils.isTablet(this);

        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mScrimInsetsFrameLayout.getLayoutParams();
        int width = getResources().getDisplayMetrics().widthPixels;

        TypedArray ta = obtainStyledAttributes(new int[]{R.attr.actionBarSize});
        int actionBarSize = ta.getDimensionPixelSize(0, 100);
        ta.recycle();
        if (Utils.getScreenOrientation(this) == Configuration.ORIENTATION_LANDSCAPE) {
            params.width = width / 2;
            if (tablet) params.width -= actionBarSize + 30;
        } else {
            params.width = tablet ? width / 2 : width - actionBarSize;
        }

        return params;
    }

}
