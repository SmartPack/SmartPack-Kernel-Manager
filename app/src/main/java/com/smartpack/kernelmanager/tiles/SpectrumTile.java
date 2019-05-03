/*
 * Copyright (C) 2018-2019 sunilpaulmathew <sunil.kde@gmail.com>
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

import android.annotation.TargetApi;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.utils.Prefs;
import com.grarak.kerneladiutor.utils.Utils;

import org.frap129.spectrum.Spectrum;

/*
 * Based on the original implementation of Spectrum Kernel Manager by frap129 <joe@frap129.org>
 *
 * Originally authored by Morogoku <morogoku@hotmail.com>
 *
 * Modified by sunilpaulmathew <sunil.kde@gmail.com>
 */

@TargetApi(Build.VERSION_CODES.N)
public class SpectrumTile extends TileService {

    private int id = Utils.strToInt(Spectrum.getProfile());

    @Override
    public void onStartListening() {
        resetTileStatus();
    }

    @Override
    public void onClick() {
        updateTile();
    }

    private void updateTile() {
        Tile tile = this.getQsTile();
        Icon newIcon;
        String newLabel;
        int newState;

        // Update tile and set profile
        if (!(Spectrum.supported())) {
            newLabel = "No Spectrum support";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_logo);
            newState = Tile.STATE_INACTIVE;
        } else {
            if (id == 0) {
		newLabel = "Performance";
		newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_game);
		newState = Tile.STATE_ACTIVE;
		id +=1;
		Spectrum.setProfile(1);
		Prefs.saveInt("spectrum_profile", 1, getApplicationContext());
            } else if (id == 1) {
		newLabel = "Battery";
		newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_battery);
		newState = Tile.STATE_ACTIVE;
		id +=1;
		Spectrum.setProfile(2);
		Prefs.saveInt("spectrum_profile", 2, getApplicationContext());
            } else if (id == 2) {
		newLabel = "Gaming";
		newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_performance);
		newState = Tile.STATE_ACTIVE;
		id +=1;
		Spectrum.setProfile(3);
		Prefs.saveInt("spectrum_profile", 3, getApplicationContext());
            } else {
		newLabel = "Balance";
		newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_balanced);
		newState = Tile.STATE_ACTIVE;
		id = 0;
		Spectrum.setProfile(0);
		Prefs.saveInt("spectrum_profile", 0, getApplicationContext());
            }
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setIcon(newIcon);
        tile.setState(newState);
        tile.updateTile();
    }

    private void resetTileStatus() {
        int profile = Utils.strToInt(Spectrum.getProfile());
        Tile tile = this.getQsTile();
        Icon newIcon;
        String newLabel;
        int newState;

        // Update tile
        if (!(Spectrum.supported())) {
            newLabel = "No Spectrum support";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_logo);
            newState = Tile.STATE_INACTIVE;
        }else {
            if (profile == 3) {
		newLabel = "Gaming";
		newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_game);
		newState = Tile.STATE_ACTIVE;
            } else if (profile == 2) {
		newLabel = "Battery";
		newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_battery);
		newState = Tile.STATE_ACTIVE;
            } else if (profile == 1) {
		newLabel = "Performance";
		newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_performance);
		newState = Tile.STATE_ACTIVE;
            } else {
		newLabel = "Balance";
		newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_spectrum_balanced);
		newState = Tile.STATE_ACTIVE;
            }
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setIcon(newIcon);
        tile.setState(newState);
        tile.updateTile();
    }
}
