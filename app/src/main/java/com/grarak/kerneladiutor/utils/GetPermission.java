/*
 * Copyright (C) 2016 Willi Ye
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

package com.grarak.kerneladiutor.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by willi on 28.12.15.
 */
public class GetPermission {

    private Activity activity;
    private String[] permissions;
    private static PermissionCallBack permissionCallBack;

    public GetPermission(Activity activity, String... permissions) {
        this.activity = activity;
        this.permissions = permissions;
    }

    public interface PermissionCallBack {
        void granted(String permission);

        void denied(String permission);
    }

    public void ask(PermissionCallBack permissionCallBack) {
        GetPermission.permissionCallBack = permissionCallBack;
        PermissionActivity.permissions = permissions;
        activity.startActivity(new Intent(activity, PermissionActivity.class));
    }

    public static class PermissionActivity extends Activity {

        private static String[] permissions;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            for (int i = 0; i < permissions.length; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(permissions[i])) {
                            permissionCallBack.denied(permissions[i]);
                            if (permissions.length == i + 1) finish();
                        } else {
                            requestPermissions(permissions, i + 1);
                        }
                    } else {
                        permissionCallBack.granted(permissions[i]);
                        if (permissions.length == i + 1) finish();
                    }
                } else {
                    permissionCallBack.granted(permissions[i]);
                    finish();
                }
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (permissions.length >= requestCode) {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionCallBack.granted(permissions[requestCode - 1]);
                } else {
                    permissionCallBack.denied(permissions[requestCode - 1]);
                }
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }

            if (requestCode == permissions.length) finish();
        }

    }

}
