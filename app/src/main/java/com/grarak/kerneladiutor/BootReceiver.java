package com.grarak.kerneladiutor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.database.SysDB;
import com.grarak.kerneladiutor.utils.root.RootUtils;

/**
 * Created by willi on 27.12.14.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!Utils.getBoolean("applyonboot", false, context)) return;

        // Check root access and busybox installation
        boolean hasRoot = false;
        boolean hasBusybox = false;
        if (RootUtils.rooted()) hasRoot = RootUtils.rootAccess();
        if (hasRoot) hasBusybox = RootUtils.busyboxInstalled();

        if (!hasRoot || !hasBusybox) return;

        SysDB sysDB = new SysDB(context);
        sysDB.create();

        for (SysDB.SysItem sysItem : sysDB.getAllSys())
            RootUtils.runCommand(sysItem.getCommand());
        sysDB.close();

        if (RootUtils.su == null) RootUtils.su = new RootUtils.SU();
        RootUtils.su.close();
        Utils.toast(context.getString(R.string.apply_on_boot_summary), context);

    }

}
