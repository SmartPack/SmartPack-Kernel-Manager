/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grarak.kerneladiutor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.ViewPagerFragment;
import com.grarak.kerneladiutor.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by willi on 24.04.15.
 */
public class FileBrowserActivity extends BaseActivity {

    public static final String FILE_TYPE_ARG = "file_type";

    private static FileBrowserActivity fileBrowserActivity;
    private FileBrowserFragment fileBrowserFragment;
    private StorageFragment internalStorage;
    private StorageFragment externalStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileBrowserActivity = this;

        ActionBar actionBar;
        if ((actionBar = getSupportActionBar()) != null) actionBar.setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                fileBrowserFragment = FileBrowserFragment.newInstance(getIntent().getExtras().getString(FILE_TYPE_ARG)))
                .commitAllowingStateLoss();
    }

    @Override
    public int getParentViewId() {
        return R.layout.activity_filebrowser;
    }

    @Override
    public View getParentView() {
        return null;
    }

    @Override
    public Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    public static class FileBrowserFragment extends ViewPagerFragment {

        public static FileBrowserFragment newInstance(String filetype) {
            Bundle args = new Bundle();
            args.putString(FILE_TYPE_ARG, filetype);
            FileBrowserFragment fragment = new FileBrowserFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void init(Bundle savedInstanceState) {
            super.init(savedInstanceState);

            String fileType = getArguments().getString(FILE_TYPE_ARG);
            String externalStorage = Utils.getExternalStorage();

            fileBrowserActivity.internalStorage =
                    StorageFragment.newInstance(Environment.getExternalStorageDirectory().getPath(), fileType);
            addFragment(new ViewPagerItem(fileBrowserActivity.internalStorage, getString(R.string.internal_storage)));
            if (externalStorage != null) {
                fileBrowserActivity.externalStorage = StorageFragment.newInstance(externalStorage, fileType);
                addFragment(new ViewPagerItem(fileBrowserActivity.externalStorage, getString(R.string.external_storage)));
            }
        }

        @Override
        public boolean onBackPressed() {
            boolean finish;
            if (getCurrentPage() == 1) finish = fileBrowserActivity.externalStorage.onBackPressed();
            else finish = fileBrowserActivity.internalStorage.onBackPressed();
            return finish;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (fileBrowserFragment.onBackPressed()) super.onBackPressed();
    }

    protected void finished(String path) {
        Bundle args = new Bundle();
        args.putString("path", path);
        Intent intent = new Intent();
        intent.putExtras(args);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static class StorageFragment extends BaseFragment {

        private static final String PATH_ARG = "path";
        private static final String TYPE_ARG = "type";

        public static StorageFragment newInstance(String path, String type) {
            Bundle args = new Bundle();
            args.putString(PATH_ARG, path);
            if (type != null) args.putString(TYPE_ARG, type);
            StorageFragment storageFragment = new StorageFragment();
            storageFragment.setArguments(args);
            return storageFragment;
        }

        private AppCompatTextView pathText;
        private RecyclerView recyclerView;
        private String current_path;
        private String type;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            current_path = getArguments().getString(PATH_ARG);
            type = getArguments().getString(TYPE_ARG);

            View view = inflater.inflate(R.layout.fragment_storage, container, false);

            pathText = (AppCompatTextView) view.findViewById(R.id.path_text);
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            updateData();
            return view;
        }

        private void updateData() {
            File[] storageFiles = new File(current_path).listFiles();
            if (storageFiles == null) return;

            final List<DAdapter.DView> views = new ArrayList<>();
            List<File> files = new ArrayList<>();
            List<File> folders = new ArrayList<>();

            for (File file : storageFiles) {
                if (file.isDirectory()) folders.add(file);
                else if (type != null) {
                    if (file.getName().endsWith("." + type)) files.add(file);
                } else files.add(file);
            }

            Collections.sort(files);
            Collections.sort(folders);

            for (File folder : folders) views.add(new FileItem(folder));
            for (File file : files) views.add(new FileItem(file));

            if (views.size() > 0) {
                pathText.setText(current_path);

                DAdapter.Adapter adapter = new DAdapter.Adapter(views);
                adapter.setOnItemClickListener(new DAdapter.Adapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final File file = ((FileItem) views.get(position)).getFile();
                        if (file.isDirectory()) {
                            current_path = file.getAbsolutePath();
                            updateData();
                        } else {
                            new AlertDialog.Builder(getActivity()).setMessage(getString(R.string.select_file, file.getName()))
                                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            fileBrowserActivity.finished(file.getAbsolutePath());
                                        }
                                    }).show();
                        }
                    }
                });
                recyclerView.setAdapter(adapter);
            } else Utils.toast(getString(R.string.no_files), getActivity());
        }

        @Override
        public boolean onBackPressed() {
            if (!current_path.equals(getArguments().getString(PATH_ARG))) {
                current_path = getParentPath(current_path);
                updateData();
                return false;
            }
            return true;
        }

        private String getParentPath(String path) {
            return new File(path).getParentFile().toString();
        }

        private class FileItem implements DAdapter.DView {

            private final File file;
            private TextView text;

            public FileItem(File file) {
                this.file = file;
            }

            @Override
            public BaseFragment getFragment() {
                return null;
            }

            @Override
            public String getTitle() {
                return null;
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.filebrowser_item, viewGroup, false)) {
                };
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
                text = (TextView) viewHolder.itemView.findViewById(R.id.text);
                text.setText(file.getName());
                text.setCompoundDrawablesWithIntrinsicBounds(file.isDirectory() ? R.drawable.ic_folder : R.drawable.ic_file,
                        0, 0, 0);
            }

            public File getFile() {
                return file;
            }

        }
    }

}
