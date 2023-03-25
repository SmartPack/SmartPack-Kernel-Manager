/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is a heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SmartPack Kernel Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SmartPack Kernel Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.smartpack.kernelmanager.tiles;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.smartpack.kernelmanager.R;
import com.smartpack.kernelmanager.utils.Prefs;
import com.smartpack.kernelmanager.utils.Utils;
import com.smartpack.kernelmanager.utils.root.RootUtils;
import com.smartpack.kernelmanager.utils.tools.Scripts;
import com.topjohnwu.superuser.io.SuFile;

import java.io.File;

import in.sunilpaulmathew.sCommon.CommonUtils.sExecutor;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 13, 2021
 */
@TargetApi(Build.VERSION_CODES.N)
public class ScriptTile extends TileService {

    private boolean mSupported;
    private int mNewState;
    private String mNewLabel, mQuickTile;
    private Tile mTile;

    @Override
    public void onStartListening() {
        resetTileStatus();
    }

    @Override
    public void onClick() {
        applyScript(this);
    }

    @SuppressLint("StaticFieldLeak")
    private void applyScript(Context context) {
        mTile = this.getQsTile();

        mQuickTile = Prefs.getString("apply_tile", null, this);
        mSupported = RootUtils.rootAccess() && mQuickTile != null && Utils.existFile(new File(Scripts.scriptFile(),
                mQuickTile).getAbsolutePath());

        if (mSupported) {
            new sExecutor() {
                @Override
                public void onPreExecute() {
                    Utils.toast( getString(R.string.applying_profile, mQuickTile), context);
                    mNewLabel = getString(R.string.applying_profile, mQuickTile);
                    mNewState = Tile.STATE_INACTIVE;

                }
                @Override
                public void doInBackground() {
                    RootUtils.runCommand("sh " + SuFile.open(Scripts.scriptFile(), mQuickTile).getAbsolutePath());
                }
                @Override
                public void onPostExecute() {
                    Utils.toast(mQuickTile + " " + getString(R.string.applied), context);
                    mNewLabel = getString(R.string.script) + ": " + mQuickTile;
                    mNewState = Tile.STATE_ACTIVE;
                }
            }.execute();
        } else {
            Utils.toast(getString(R.string.quick_tile_empty_message), context);
            mNewLabel = getString(R.string.script);
            mNewState = Tile.STATE_INACTIVE;
        }
        mTile.setLabel(mNewLabel);
        mTile.setState(mNewState);
        mTile.updateTile();
    }

    private void resetTileStatus() {
        mTile = this.getQsTile();

        mQuickTile = Prefs.getString("apply_tile", null, this);
        mSupported = RootUtils.rootAccess() && mQuickTile != null && Utils.existFile(SuFile.open(Scripts.scriptFile(),
                mQuickTile).getAbsolutePath());

        if (mSupported) {
            mNewLabel = getString(R.string.script) + ": " + mQuickTile;
            mNewState = Tile.STATE_ACTIVE;
        } else {
            mNewLabel = getString(R.string.script);
            mNewState = Tile.STATE_INACTIVE;
        }
        mTile.setLabel(mNewLabel);
        mTile.setState(mNewState);
        mTile.updateTile();
    }

}