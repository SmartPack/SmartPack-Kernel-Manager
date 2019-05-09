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
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.smartpack.kernelmanager.utils.KLapse;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 02, 2019
 */

@TargetApi(Build.VERSION_CODES.N)
public class KLapseTile extends TileService {

    private int id = KLapse.getklapseEnable();
    private int nightR = KLapse.getklapseRed();
    private int nightG = KLapse.getklapseGreen();
    private int nightB = KLapse.getklapseBlue();

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
        String newLabel;
        int newState;

        // Update tile and set profile
        if (!(KLapse.supported())) {
            newLabel = "No K-Lapse support";
            newState = Tile.STATE_INACTIVE;
        } else {
            if (id == 1) {
		newLabel = "K-Lapse\nBrightness";
		newState = Tile.STATE_ACTIVE;
		id +=1;
		KLapse.setklapseEnable(2, this);
		KLapse.setklapseRed(nightR, this);
		KLapse.setklapseGreen(nightG, this);
		KLapse.setklapseBlue(nightB, this);
            } else if (id == 2) {
		newLabel = "K-Lapse\nTurned-Off";
		newState = Tile.STATE_ACTIVE;
		id +=1;
		KLapse.setklapseEnable(0, this);
            } else {
		newLabel = "K-Lapse\nTime";
		newState = Tile.STATE_ACTIVE;
		id = 1;
		KLapse.setklapseEnable(1, this);
		KLapse.setklapseRed(nightR, this);
		KLapse.setklapseGreen(nightG, this);
		KLapse.setklapseBlue(nightB, this);
            }
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setState(newState);
        tile.updateTile();
    }

    private void resetTileStatus() {
	int status = KLapse.getklapseEnable();
        Tile tile = this.getQsTile();
        String newLabel;
        int newState;

        // Update tile
        if (!(KLapse.supported())) {
            newLabel = "No K-Lapse support";
            newState = Tile.STATE_INACTIVE;
        } else {
            if (status == 2) {
		newLabel = "K-Lapse\nBrightness";
		newState = Tile.STATE_ACTIVE;
            } else if (status == 1){
		newLabel = "K-Lapse\nTime";
		newState = Tile.STATE_ACTIVE;
            } else {
		newLabel = "K-Lapse\nTurned-Off";
		newState = Tile.STATE_ACTIVE;
            }
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setState(newState);
        tile.updateTile();
    }
}
