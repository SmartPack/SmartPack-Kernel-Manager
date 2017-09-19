/*
 * Copyright (C) 2017 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.grarak.kerneladiutor.utils.server;

import android.app.Activity;

import com.grarak.kerneladiutor.utils.WebpageReader;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by willi on 18.09.17.
 */

public class ServerSearchDevice extends Server {

    private static final String DEVICE_GET = "/kerneladiutor/api/v1/device/get";
    private static final String BOARD_GET = "/kerneladiutor/api/v1/board/get";

    public interface DeviceSearchListener {
        void onDevicesResult(List<DeviceInfo> devices, int page);

        void onDevicesFailure();
    }

    public interface BoardSearchListener {
        void onBoardResult(List<String> boards);

        void onBoardFailure();
    }

    private Activity mActivity;

    private WebpageReader mDeviceReader;
    private WebpageReader mBoardReader;

    public ServerSearchDevice(String address, Activity activity) {
        super(address);
        mActivity = activity;
    }

    public void getDevices(final DeviceSearchListener deviceSearchListener, final int page, String board) {
        mDeviceReader = new WebpageReader(mActivity, new WebpageReader.WebpageListener() {
            @Override
            public void onSuccess(String url, String raw, CharSequence html) {
                try {
                    JSONArray devices = new JSONArray(raw);
                    List<DeviceInfo> list = new ArrayList<>();

                    for (int i = 0; i < devices.length(); i++) {
                        list.add(new DeviceInfo(devices.getJSONObject(i)));
                    }
                    deviceSearchListener.onDevicesResult(list, page == 0 ? 1 : page);
                } catch (JSONException ignored) {
                    deviceSearchListener.onDevicesFailure();
                }
            }

            @Override
            public void onFailure(String url) {
                deviceSearchListener.onDevicesFailure();
            }
        });
        mDeviceReader.get(getAddress(DEVICE_GET,
                new Query("page", String.valueOf(page == 0 ? 1 : page)),
                new Query("board", board)));
    }

    public void getBoards(final BoardSearchListener boardSearchListener) {
        mBoardReader = new WebpageReader(mActivity, new WebpageReader.WebpageListener() {
            @Override
            public void onSuccess(String url, String raw, CharSequence html) {
                try {
                    JSONArray boards = new JSONArray(raw);
                    List<String> list = new ArrayList<>();

                    for (int i = 0; i < boards.length(); i++) {
                        list.add(boards.getString(i));
                    }
                    Collections.sort(list);
                    boardSearchListener.onBoardResult(list);
                } catch (JSONException ignored) {
                    boardSearchListener.onBoardFailure();
                }
            }

            @Override
            public void onFailure(String url) {
                boardSearchListener.onBoardFailure();
            }
        });
        mBoardReader.get(getAddress(BOARD_GET));
    }

    public void cancel() {
        if (mDeviceReader != null) mDeviceReader.cancel();
        if (mBoardReader != null) mBoardReader.cancel();
    }

}
