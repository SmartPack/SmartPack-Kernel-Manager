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

package com.grarak.kerneladiutor.elements;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by willi on 21.12.14.
 */
public class DAdapter {

    public interface DView {

        void onBindViewHolder(RecyclerView.ViewHolder viewHolder);

        RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup);

        String getTitle();

        BaseFragment getFragment();

    }

    public static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        public List<DView> DViews;
        private OnItemClickListener onItemClickListener;
        private int selectedItem = 0;
        private boolean itemOnly;

        public Adapter(List<DView> DViews) {
            this.DViews = DViews;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            DViews.get(position).onBindViewHolder(holder);
        }

        @Override
        public int getItemCount() {
            return DViews.size();
        }

        public void setItemChecked(int position, boolean checked) {
            try {
                ((Item) DViews.get(position)).setChecked(checked);
                ((Item) DViews.get(selectedItem)).setChecked(false);
            } catch (ClassCastException ignored) {
            }
            selectedItem = position;
        }

        public void setItemOnly(boolean itemOnly) {
            this.itemOnly = itemOnly;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = DViews.get(viewType).onCreateViewHolder(parent);
            setOnClickListener(DViews.get(viewType), viewHolder.itemView);
            return viewHolder;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        private void setOnClickListener(final DView dView, View view) {
            boolean onClick = false;
            if (itemOnly)
                onClick = dView instanceof Item;
            else if (onItemClickListener != null) onClick = true;
            if (onClick) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null)
                            onItemClickListener.onItemClick(v, DViews.indexOf(dView));
                    }
                });
            }
        }

    }

    public static class Item implements DView {

        private final String title;
        private final BaseFragment fragment;
        private View view;
        private TextView text;
        private boolean checked;
        private int defaultTextColor;
        private int checkedTextColor;
        private int defaultBackgroundColor;
        private int checkedBackgroundColor;

        public Item(String title, BaseFragment fragment) {
            this.title = title;
            this.fragment = fragment;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public BaseFragment getFragment() {
            return fragment;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
            checkedTextColor = view.getResources().getColor(R.color.color_primary);
            defaultTextColor = view.getResources().getColor(Utils.DARKTHEME ? R.color.white : R.color.black);
            defaultBackgroundColor = view.getResources().getColor(android.R.color.transparent);
            checkedBackgroundColor = view.getResources().getColor(Utils.DARKTHEME ?
                    R.color.navigationdrawer_selected_background_dark : R.color.navigationdrawer_selected_background_light);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
            text = (TextView) viewHolder.itemView.findViewById(R.id.text);
            text.setText(title);
            view.setBackgroundColor(checked ? checkedBackgroundColor : defaultBackgroundColor);
            text.setTextColor(checked ? checkedTextColor : defaultTextColor);
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
            if (view != null && text != null) {
                view.setBackgroundColor(checked ? checkedBackgroundColor : defaultBackgroundColor);
                text.setTextColor(checked ? checkedTextColor : defaultTextColor);
            }
        }

    }

    public static class Header implements DView {

        private final String title;

        public Header(String title) {
            this.title = title;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public BaseFragment getFragment() {
            return null;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_header, viewGroup, false)) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
            ((TextView) viewHolder.itemView.findViewById(R.id.text)).setText(title.toUpperCase());
            if (Utils.DARKTHEME)
                viewHolder.itemView.findViewById(R.id.divider_view).setBackgroundColor(viewHolder.itemView.getResources()
                        .getColor(R.color.divider_background_dark));
        }

    }

    public static class MainHeader implements DView {

        private static ImageView image;
        private boolean noPic;

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public BaseFragment getFragment() {
            return null;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_main, viewGroup, false);
            image = (ImageView) view.findViewById(R.id.picture);
            try {
                String uri = Utils.getString("previewpicture", null, image.getContext());
                if (uri == null || uri.equals("nopicture")) noPic = true;
                else {
                    setImage(Uri.parse(uri));
                    noPic = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                noPic = true;
            }

            if (noPic) Utils.saveString("previewpicture", "nopicture", image.getContext());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    new AlertDialog.Builder(v.getContext()).setItems(v.getResources()
                            .getStringArray(R.array.main_header_picture_items), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    v.getContext().startActivity(new Intent(v.getContext(), MainHeaderActivity.class));
                                    break;
                                case 1:
                                    if (Utils.getString("previewpicture", null, v.getContext()).equals("nopicture"))
                                        return;
                                    Utils.saveString("previewpicture", "nopicture", v.getContext());
                                    image.setImageDrawable(null);
                                    animate();
                                    break;
                            }

                        }
                    }).show();
                }
            });
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        }

        public static class MainHeaderActivity extends Activity {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                Intent intent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                } else {
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                }
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), 0);
            }

            @Override
            protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if (resultCode == RESULT_OK && requestCode == 0)
                    try {
                        Uri selectedImageUri = data.getData();
                        setImage(selectedImageUri);
                        Utils.saveString("previewpicture", selectedImageUri.toString(), this);
                        animate();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.toast(getString(R.string.went_wrong), MainHeaderActivity.this);
                    }
                finish();
            }

        }

        public static void animate() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                image.setVisibility(View.INVISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            ((Activity) image.getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Utils.circleAnimate(image, image.getWidth() / 2, image.getHeight() / 2);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }

        public static void setImage(Uri uri) throws IOException, NullPointerException {
            String selectedImagePath = null;
            try {
                selectedImagePath = getPath(uri, image.getContext());
            } catch (Exception ignored) {
            }
            Bitmap bitmap;
            if ((bitmap = selectedImagePath != null ? BitmapFactory.decodeFile(selectedImagePath) :
                    uriToBitmap(uri, image.getContext())) != null)
                image.setImageBitmap(Utils.scaleDownBitmap(bitmap, 1024, 1024));
            else throw new NullPointerException("Getting Bitmap failed");
        }

        private static Bitmap uriToBitmap(Uri uri, Context context) throws IOException {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;
        }

        private static String getPath(Uri uri, Context context) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA},
                    null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(column_index);
                cursor.close();
                return path;
            } else return null;
        }

    }

}