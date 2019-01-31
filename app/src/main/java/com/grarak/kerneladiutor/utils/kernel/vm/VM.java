/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
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
package com.grarak.kerneladiutor.utils.kernel.vm;

import android.content.Context;

import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.root.Control;

/**
 * Created by willi on 29.06.16.
 */
public class VM {

    private static final String PATH = "/proc/sys/vm";
    private static final String[] SUPPORTED_VM = {"admin_reserve_kbytes", "block_dump", "compact_memory", "compact_unevictable_allowed",
	"dirty_ratio", "dirty_bytes", "dirty_background_ratio", "dirty_background_bytes", "dirty_expire_centisecs","dirty_writeback_centisecs",
	"dirtytime_expire_seconds", "drop_caches", "extra_free_kbytes", "extfrag_threshold", "highmem_is_dirtyable", "laptop_mode",
	"legacy_va_layout", "lowmem_reserve_ratio", "mmap_rnd_compat_bits", "max_map_count", "min_free_kbytes", "min_free_order_shift",
	"mmap_min_addr", "mmap_rnd_bits", "mobile_page_compaction", "nr_pdflush_threads", "oom_dump_tasks", "oom_kill_allocating_task",
	"overcommit_kbytes", "overcommit_memory", "overcommit_ratio", "page-cluster", "panic_on_oom", "percpu_pagelist_fraction",
	"scan_unevictable_pages", "swap_ratio_enable", "swappiness", "stat_interval", "user_reserve_kbytes", "vfs_cache_pressure"};

    public static void setValue(String value, int position, Context context) {
        run(Control.write(value, PATH + "/" + SUPPORTED_VM[position]), PATH + "/" +
                SUPPORTED_VM[position], context);
    }

    public static String getValue(int position) {
        return Utils.readFile(PATH + "/" + SUPPORTED_VM[position]);
    }

    public static String getName(int position) {
        return Utils.upperCaseEachWord(SUPPORTED_VM[position]).replace("_", " ");
    }

    public static boolean exists(int position) {
        return Utils.existFile(PATH + "/" + SUPPORTED_VM[position]);
    }

    public static int size() {
        return SUPPORTED_VM.length;
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.VM, id, context);
    }

}
