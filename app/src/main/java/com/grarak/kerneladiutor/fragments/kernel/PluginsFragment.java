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

package com.grarak.kerneladiutor.fragments.kernel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.grarak.kerneladiutor.DownloadPluginsActivity;
import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.elements.DAdapter;
import com.grarak.kerneladiutor.elements.DDivider;
import com.grarak.kerneladiutor.elements.cards.CardViewItem;
import com.grarak.kerneladiutor.elements.cards.EditTextCardView;
import com.grarak.kerneladiutor.elements.cards.InformationCardView;
import com.grarak.kerneladiutor.elements.cards.PopupCardView;
import com.grarak.kerneladiutor.elements.cards.SeekBarCardView;
import com.grarak.kerneladiutor.elements.cards.SwitchCardView;
import com.grarak.kerneladiutor.elements.cards.UsageCardView;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.fragments.ViewPagerFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.WebpageReader;
import com.grarak.kerneladiutor.utils.json.Plugins;
import com.grarak.kerneladiutor.utils.root.Control;
import com.kerneladiutor.library.Item;
import com.kerneladiutor.library.PluginManager;
import com.kerneladiutor.library.Tab;
import com.kerneladiutor.library.items.Divider;
import com.kerneladiutor.library.items.EditText;
import com.kerneladiutor.library.items.Information;
import com.kerneladiutor.library.items.Popup;
import com.kerneladiutor.library.items.Progress;
import com.kerneladiutor.library.items.SeekBar;
import com.kerneladiutor.library.items.Simple;
import com.kerneladiutor.library.items.Switcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by willi on 11.08.15.
 */
public class PluginsFragment extends ViewPagerFragment {

    private final HashMap<Handler, Runnable> handlers = new HashMap<>();
    private final List<Tab> mTabs = new ArrayList<>();
    private boolean runOnResumeHandlers;
    private ProgressDialog mProgressDialog;
    private int count;

    private final BroadcastReceiver tabReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mProgressDialog != null) mProgressDialog.dismiss();
            PluginsFragment.super.mTabs.setVisibility(View.VISIBLE);
            final Tab tab = intent.getParcelableExtra(com.kerneladiutor.library.action.Intent.TAB);
            if (tab == null) return;

            int versionCode = intent.getIntExtra(com.kerneladiutor.library.action.Intent.VERSION_CODE, 1);
            PluginManager.setVersion(versionCode);
            if (tab.getTitle() != null) {
                mTabs.add(tab);
                addFragment(new ViewPagerItem(PluginFragment.newInstance(tab), tab.getTitle()));

                Utils.saveString("plugins", "", context);
                StringBuilder s = new StringBuilder();
                for (Tab t : mTabs)
                    if (s.length() == 0) s.append(t.getPackageName());
                    else s.append("wefewfewwgwe").append(t.getPackageName());
                Utils.saveString("plugins", s.toString(), context);

                if (tab.getOnRefreshListenerPending() != null) {
                    final Handler hand = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                tab.getOnRefreshListenerPending().send(getActivity(), 0, new Intent());
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                            hand.postDelayed(this, tab.getRefreshTime());
                        }
                    };
                    handlers.put(hand, runnable);
                    hand.postDelayed(runnable, tab.getRefreshTime());
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (runOnResumeHandlers) {
            if (count == 0 && supportedApps(sendIntent()) != 0) preInit(null);
            for (Handler handler : handlers.keySet().toArray(new Handler[handlers.size()]))
                handler.post(this.handlers.get(handler));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        runOnResumeHandlers = true;
        for (Handler hand : handlers.keySet().toArray(new Handler[handlers.size()]))
            hand.removeCallbacksAndMessages(null);
    }

    private final BroadcastReceiver commandReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String command = intent.getStringExtra(com.kerneladiutor.library.action.Intent.COMMAND);
            Tab tab = Item.getTabEvent(intent);
            String tag = Item.getTagEvent(intent);
            Control.runCommand(command, tag, Control.CommandType.CUSTOM, tab.getTitle(), context);
        }
    };

    private final BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Tab tab = intent.getParcelableExtra(com.kerneladiutor.library.action.Intent.TAB);
            Item item = intent.getParcelableExtra(com.kerneladiutor.library.action.Intent.ITEM);

            for (int i = 0; i < mTabs.size(); i++)
                if (mTabs.get(i).getPackageName().equals(tab.getPackageName()))
                    ((PluginFragment) getFragment(i)).addItem(item, tab, true);
        }
    };

    @Override
    public void preInit(Bundle savedInstanceState) {
        super.preInit(savedInstanceState);
        mTabs.clear();
        handlers.clear();
        runOnResumeHandlers = false;

        Intent i = sendIntent();
        if ((count = supportedApps(sendIntent())) == 0) {
            super.mTabs.setVisibility(View.GONE);
            Utils.toast(getString(R.string.no_plugins), getActivity());
        } else {
            try {
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage(getString(R.string.loading_plugins));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Activity activity;
                        if ((activity = getActivity()) != null)
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mProgressDialog != null) mProgressDialog.dismiss();
                                }
                            });
                    }
                }).start();

                getActivity().sendBroadcast(i);
                getActivity().registerReceiver(tabReceiver, new IntentFilter(com.kerneladiutor.library.action.Intent.RECEIVE_DATA));
                getActivity().registerReceiver(commandReceiver, new IntentFilter(com.kerneladiutor.library.action.Intent.EXECUTE_COMMAND));
                getActivity().registerReceiver(updateReceiver, new IntentFilter(com.kerneladiutor.library.action.Intent.RECEIVE_UPDATE));
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void postInit(Bundle savedInstanceState) {
        super.postInit(savedInstanceState);

        super.mTabs.setPaddingMiddle(false);
        super.mTabs.setSameWeightTabs(false);
        super.mTabs.setTabPaddingLeftRight(getResources().getDimensionPixelSize(R.dimen.toolbar_left_padding));

        if (Utils.getBoolean("showpluginwarning", true, getActivity()))
            new AlertDialog.Builder(getActivity()).setMessage(getString(R.string.plugin_warning))
                    .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utils.saveBoolean("showpluginwarning", false, getActivity());
                        }
                    }).show();
    }

    private int supportedApps(Intent i) {
        return getActivity().getPackageManager().queryBroadcastReceivers(i, PackageManager.GET_RESOLVED_FILTER).size();
    }

    private Intent sendIntent() {
        return new Intent(com.kerneladiutor.library.action.Intent.SEND_DATA);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(tabReceiver);
            getActivity().unregisterReceiver(commandReceiver);
            getActivity().unregisterReceiver(updateReceiver);
            mProgressDialog.dismiss();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.plugins_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_plugins_download:
                final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage(getString(R.string.looking_plugins));

                final WebpageReader webpageReader = new WebpageReader(new WebpageReader.WebpageCallback() {
                    @Override
                    public void onCallback(String raw, String html) {
                        mProgressDialog.dismiss();
                        Plugins plugins = new Plugins(raw);
                        if (!plugins.readable()) {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage(getString(R.string.no_plugins))
                                    .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).show();
                            return;
                        }

                        Intent i = new Intent(getActivity(), DownloadPluginsActivity.class);
                        Bundle args = new Bundle();
                        args.putString(DownloadPluginsActivity.JSON_ARG, raw);
                        i.putExtras(args);
                        getActivity().startActivity(i);
                    }
                });

                mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        webpageReader.cancel();
                    }
                });
                mProgressDialog.show();

                webpageReader.execute("https://raw.githubusercontent.com/Grarak/KernelAdiutor/master/plugins.json");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PluginFragment extends RecyclerViewFragment {

        public static PluginFragment newInstance(Tab tab) {
            PluginFragment pluginFragment = new PluginFragment();
            pluginFragment.tab = tab;
            return pluginFragment;
        }

        @Override
        public void applyOnBootChecked(boolean isChecked) {
            Utils.saveBoolean(tab.getPackageName() + "onboot", isChecked, getActivity());
            Utils.toast(getString(isChecked ? R.string.apply_on_boot_enabled : R.string.apply_on_boot_disabled,
                    tab.getTitle()), getActivity());
        }

        private Tab tab;
        private HashMap<Integer, DAdapter.DView> items = new HashMap<>();

        @Override
        public void init(Bundle savedInstanceState) {
            super.init(savedInstanceState);
            items.clear();

            for (Item item : tab.getAllItems())
                addItem(item, tab, false);
        }

        public void addItem(Item item, final Tab tab, boolean update) {

            DAdapter.DView dView = null;

            if (update && items.get(item.getId()) == null) return;
            if (item instanceof Simple) {

                final Simple simple = (Simple) item;
                if (simple.getTitle() != null || simple.getDescription() != null) {
                    CardViewItem.DCardView mCard = update ? (CardViewItem.DCardView) items.get(item.getId())
                            : new CardViewItem.DCardView();
                    if (simple.getTitle() != null && simple.getDescription() == null) {
                        mCard.setDescription(simple.getTitle());
                    } else {
                        if (simple.getTitle() != null) mCard.setTitle(simple.getTitle());
                        mCard.setDescription(simple.getDescription());
                    }
                    mCard.setFullSpan(simple.isFullSpan());
                    if (simple.getOnClickPendingListener() != null && simple.getTag() != null)
                        mCard.setOnDCardListener(new CardViewItem.DCardView.OnDCardListener() {
                            @Override
                            public void onClick(CardViewItem.DCardView dCardView) {
                                startPendingIntent(simple.getOnClickPendingListener(), new Intent(), simple.getTag(), tab);
                            }
                        });

                    dView = mCard;
                }

            } else if (item instanceof Divider) {

                Divider divider = (Divider) item;
                if (divider.getTitle() != null) {
                    DDivider mDividerCard = update ? (DDivider) items.get(item.getId()) : new DDivider();
                    mDividerCard.setText(divider.getTitle());
                    if (divider.getDescription() != null)
                        mDividerCard.setDescription(divider.getDescription());

                    dView = mDividerCard;
                }

            } else if (item instanceof EditText) {

                final EditText editText = (EditText) item;
                if (editText.getValue() != null) {
                    EditTextCardView.DEditTextCard mEditTextCard = update ? (EditTextCardView.DEditTextCard) items.get(item.getId())
                            : new EditTextCardView.DEditTextCard();
                    if (editText.getTitle() != null) mEditTextCard.setTitle(editText.getTitle());
                    mEditTextCard.setDescription(editText.getValue());
                    mEditTextCard.setValue(editText.getValue());
                    mEditTextCard.setInputType(editText.getInputType());
                    mEditTextCard.setFullSpan(editText.isFullSpan());
                    if (editText.getOnClickPendingListener() != null && editText.getTag() != null)
                        mEditTextCard.setOnDEditTextCardListener(new EditTextCardView.DEditTextCard.OnDEditTextCardListener() {
                            @Override
                            public void onApply(EditTextCardView.DEditTextCard dEditTextCard, String value) {
                                dEditTextCard.setDescription(value);

                                Intent intent = new Intent();
                                intent.putExtra(EditText.VALUE, value);
                                startPendingIntent(editText.getOnClickPendingListener(), intent, editText.getTag(), tab);
                            }
                        });

                    dView = mEditTextCard;
                }

            } else if (item instanceof Information) {

                final Information information = (Information) item;
                if (information.getText() != null) {
                    InformationCardView.DInformationCard mInformationCard = update ? (InformationCardView.DInformationCard) items
                            .get(item.getId()) : new InformationCardView.DInformationCard();
                    mInformationCard.setText(information.getText());

                    dView = mInformationCard;
                }

            } else if (item instanceof Popup) {

                final Popup popup = (Popup) item;
                if (popup.getItem() != null) {
                    PopupCardView.DPopupCard mPopupCard = update ? (PopupCardView.DPopupCard) items
                            .get(item.getId()) : new PopupCardView.DPopupCard(popup.getItems());
                    if (popup.getTitle() != null && popup.getDescription() == null) {
                        mPopupCard.setDescription(popup.getTitle());
                    } else {
                        if (popup.getTitle() != null) mPopupCard.setTitle(popup.getTitle());
                        mPopupCard.setDescription(popup.getDescription());
                    }
                    mPopupCard.setItem(popup.getItem());
                    mPopupCard.setFullSpan(popup.isFullSpan());
                    if (popup.getOnClickPendingListener() != null && popup.getTag() != null)
                        if (popup.getItems() != null) {
                            mPopupCard.setOnDPopupCardListener(new PopupCardView.DPopupCard.OnDPopupCardListener() {
                                @Override
                                public void onItemSelected(PopupCardView.DPopupCard dPopupCard, int position) {
                                    Intent intent = new Intent();
                                    intent.putExtra(Popup.POSITION, position);
                                    intent.putExtra(Popup.ITEM, popup.getItems().get(position));
                                    startPendingIntent(popup.getOnClickPendingListener(), intent, popup.getTag(), tab);
                                }
                            });
                        } else {
                            mPopupCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startPendingIntent(popup.getOnClickPendingListener(), new Intent(), popup.getTag(), tab);
                                }
                            });
                        }

                    dView = mPopupCard;
                }

            } else if (item instanceof SeekBar) {

                final SeekBar seekBar = (SeekBar) item;
                if (seekBar.getItems() != null) {
                    SeekBarCardView.DSeekBarCard mSeekBarCard = update ? (SeekBarCardView.DSeekBarCard) items
                            .get(item.getId()) : new SeekBarCardView.DSeekBarCard(seekBar.getItems());
                    if (seekBar.getTitle() != null) mSeekBarCard.setTitle(seekBar.getTitle());
                    if (seekBar.getDescription() != null)
                        mSeekBarCard.setDescription(seekBar.getDescription());
                    mSeekBarCard.setProgress(seekBar.getProgress());
                    mSeekBarCard.setFullSpan(seekBar.isFullSpan());
                    if ((seekBar.getOnClickPendingListener() != null || seekBar.getOnChangePendingListener() != null)
                            && (seekBar.getTag() != null || seekBar.getOnChangeTag() != null))
                        mSeekBarCard.setOnDSeekBarCardListener(new SeekBarCardView.DSeekBarCard.OnDSeekBarCardListener() {
                            @Override
                            public void onChanged(SeekBarCardView.DSeekBarCard dSeekBarCard, int position) {
                                if (seekBar.getOnChangePendingListener() != null && seekBar.getOnChangeTag() != null) {
                                    Intent intent = new Intent();
                                    intent.putExtra(SeekBar.ITEM, seekBar.getItems().get(position));
                                    intent.putExtra(SeekBar.POSITION, position);
                                    startPendingIntent(seekBar.getOnChangePendingListener(), intent, seekBar.getOnChangeTag(), tab);
                                }
                            }

                            @Override
                            public void onStop(SeekBarCardView.DSeekBarCard dSeekBarCard, int position) {
                                if (seekBar.getOnClickPendingListener() != null && seekBar.getTag() != null) {
                                    Intent intent = new Intent();
                                    intent.putExtra(SeekBar.ITEM, seekBar.getItems().get(position));
                                    intent.putExtra(SeekBar.POSITION, position);
                                    startPendingIntent(seekBar.getOnClickPendingListener(), intent, seekBar.getTag(), tab);
                                }
                            }
                        });

                    dView = mSeekBarCard;
                }

            } else if (item instanceof Switcher) {

                final Switcher switcher = (Switcher) item;
                if (switcher.getTitle() != null || switcher.getDescription() != null) {
                    SwitchCardView.DSwitchCard mSwitchCard = update ? (SwitchCardView.DSwitchCard) items
                            .get(item.getId()) : new SwitchCardView.DSwitchCard();
                    if (switcher.getTitle() != null && switcher.getDescription() == null) {
                        mSwitchCard.setDescription(switcher.getTitle());
                    } else {
                        if (switcher.getTitle() != null) mSwitchCard.setTitle(switcher.getTitle());
                        mSwitchCard.setDescription(switcher.getDescription());
                    }
                    mSwitchCard.setChecked(switcher.isChecked());
                    mSwitchCard.setFullSpan(switcher.isFullSpan());
                    if (switcher.getOnClickPendingListener() != null && switcher.getTag() != null)
                        mSwitchCard.setOnDSwitchCardListener(new SwitchCardView.DSwitchCard.OnDSwitchCardListener() {
                            @Override
                            public void onChecked(SwitchCardView.DSwitchCard dSwitchCard, boolean checked) {
                                Intent intent = new Intent();
                                intent.putExtra(Switcher.CHECKED, checked);
                                startPendingIntent(switcher.getOnClickPendingListener(), intent, switcher.getTag(), tab);
                            }
                        });

                    dView = mSwitchCard;
                }

            } else if (item instanceof Progress) {

                Progress progress = (Progress) item;
                if (progress.getText() != null) {
                    UsageCardView.DUsageCard mUsageCard = update ? (UsageCardView.DUsageCard) items.get(item.getId())
                            : new UsageCardView.DUsageCard();
                    mUsageCard.setText(progress.getText());
                    mUsageCard.setProgress(progress.getProgress());
                    if (progress.getMax() > 0) mUsageCard.setMax(progress.getMax());
                    mUsageCard.setFullSpan(progress.isFullSpan());

                    dView = mUsageCard;
                }

            }

            if (dView != null) {
                addView(dView);
                if (!update) items.put(item.getId(), dView);
            }
        }

        private void startPendingIntent(PendingIntent pendingIntent, Intent intent, String tag, Tab tab) {
            intent.putExtra(com.kerneladiutor.library.action.Intent.TAG, tag);
            intent.putExtra(com.kerneladiutor.library.action.Intent.TAB, tab);
            try {
                pendingIntent.send(getActivity(), 0, intent);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }

    }

}
