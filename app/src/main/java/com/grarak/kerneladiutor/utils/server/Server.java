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

/**
 * Created by willi on 18.09.17.
 */

public class Server {

    private String mAddress;

    Server(String address) {
        mAddress = address;
    }

    class Query {

        private String mKey;
        private String mValue;

        Query(String key, String value) {
            mKey = key;
            mValue = value;
        }
    }

    String getAddress(String url, Query... queries) {
        StringBuilder parsedUrl = new StringBuilder(mAddress + url + "?");
        for (Query query : queries) {
            if (query.mValue == null) continue;
            parsedUrl.append(query.mKey).append("=").append(query.mValue).append("&");
        }
        parsedUrl.setLength(parsedUrl.length() - 1);
        return parsedUrl.toString();
    }
}
