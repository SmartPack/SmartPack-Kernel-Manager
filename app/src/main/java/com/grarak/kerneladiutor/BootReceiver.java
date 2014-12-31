package com.grarak.kerneladiutor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.database.SysDB;
import com.grarak.kerneladiutor.utils.root.RootUtils;

import java.util.List;

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

        if (RootUtils.su == null) RootUtils.su = new RootUtils.SU();

        SysDB db = new SysDB(context);
        db.open();

        List<SysDB.Sys> list = db.getAllSys();
        db.close();

        for (SysDB.Sys sys : list) RootUtils.runCommand(sys.getValue());

        RootUtils.su.close();

        Utils.toast(context.getString(R.string.apply_on_boot_summary), context);

    }

}
