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
package com.grarak.kerneladiutor.services.boot;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.grarak.kerneladiutor.utils.Utils;

/**
 * Created by willi on 24.11.17.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class JobService extends android.app.job.JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Utils.setupStartActivity(this);
        ApplyOnBoot.apply(this, new ApplyOnBoot.ApplyOnBootListener() {
            @Override
            public void onFinish() {
                stopSelf();
            }
        });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void start(Context context) {
        ComponentName serviceComponent = new ComponentName(context, JobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1000);
        builder.setOverrideDeadline(3 * 1000);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

}
