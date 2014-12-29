package com.grarak.kerneladiutor.elements;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.grarak.kerneladiutor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 22.12.14.
 */
public class RecyclerViewFragment extends Fragment {

    protected LayoutInflater inflater;
    protected ViewGroup container;

    private ProgressBar progressBar;
    private final List<DAdapter.DView> views = new ArrayList<>();
    private RecyclerView recyclerView;
    private DAdapter.Adapter adapter;
    private final Handler hand = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;

        views.clear();

        recyclerView = (RecyclerView) inflater.inflate(R.layout.recyclerview_vertical, container, false);
        recyclerView.setHasFixedSize(true);

        progressBar = new ProgressBar(getActivity());
        setProgressBar(progressBar);

        setRecyclerView(recyclerView);
        setProgressBar(progressBar);
        adapter = new DAdapter.Adapter(views);

        recyclerView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in));

        if (isAdded()) new Task().execute(savedInstanceState);

        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void setProgressBar(ProgressBar progressBar) {
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(progressBar, new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL | Gravity.END));
    }

    public void init(Bundle savedInstanceState) {

    }

    public void addView(DAdapter.DView view) {
        if (views.indexOf(view) < 0) {
            views.add(view);
            adapter.notifyDataSetChanged();
        }
    }

    public void removeView(DAdapter.DView view) {
        int position = views.indexOf(view);
        if (position > -1) {
            views.remove(view);
            adapter.notifyDataSetChanged();
        }
    }

    private class Task extends AsyncTask<Bundle, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(final Bundle... params) {
            if (isAdded()) init(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (adapter != null) recyclerView.setAdapter(adapter);
            hand.post(run);

            progressBar.setVisibility(View.GONE);
        }

    }

    public boolean onRefresh() {
        return false;
    }

    private final Runnable run = new Runnable() {
        @Override
        public void run() {
            if (isAdded())
                if (onRefresh()) {
                    hand.postDelayed(run, 1000);
                } else hand.removeCallbacks(run);
            else hand.postDelayed(run, 1000);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        hand.removeCallbacks(run);
    }

}
