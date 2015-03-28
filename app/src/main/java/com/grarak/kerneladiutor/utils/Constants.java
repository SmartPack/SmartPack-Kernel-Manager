/*
 * Copyright (C) 2015 Willi Ye
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

import com.grarak.kerneladiutor.BuildConfig;
import com.grarak.kerneladiutor.elements.ListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 30.11.14.
 */
public interface Constants {

    String TAG = "Kernel Adiutor";
    String VERSION_NAME = BuildConfig.VERSION_NAME;
    String PREF_NAME = "prefs";
    List<ListAdapter.ListItem> ITEMS = new ArrayList<>();

    // Kernel Informations
    String PROC_VERSION = "/proc/version";
    String PROC_CPUINFO = "/proc/cpuinfo";
    String PROC_MEMINFO = "/proc/meminfo";

    // CPU
    String CPU_CUR_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq";
    String CPU_CORE_ONLINE = "/sys/devices/system/cpu/cpu%d/online";
    String CPU_MAX_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_max_freq";
    String CPU_MIN_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_min_freq";
    String CPU_MAX_SCREEN_OFF_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/screen_off_max_freq";
    String CPU_MSM_CPUFREQ_LIMIT = "/sys/kernel/msm_cpufreq_limit/cpufreq_limit";
    String CPU_AVAILABLE_FREQS = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
    String CPU_TIME_STATE = "/sys/devices/system/cpu/cpu0/cpufreq/stats/time_in_state";
    String CPU_SCALING_GOVERNOR = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_governor";
    String CPU_AVAILABLE_GOVERNORS = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";
    String CPU_GOVERNOR_TUNABLES = "/sys/devices/system/cpu/cpufreq";
    String CPU_MC_POWER_SAVING = "/sys/devices/system/cpu/sched_mc_power_savings";
    String CPU_AVAILABLE_CFS_SCHEDULERS = "/sys/devices/system/cpu/sched_balance_policy/available_sched_balance_policy";
    String CPU_CURRENT_CFS_SCHEDULER = "/sys/devices/system/cpu/sched_balance_policy/current_sched_balance_policy";

    String CPU_TEMPCONTROL_TEMP_LIMIT = "/sys/class/misc/tempcontrol/templimit";
    String CPU_MSM_TEMP_LIMIT = "/sys/module/msm_thermal/parameters/temp_threshold";

    String[] CPU_TEMP_LIMIT_ARRAY = {CPU_TEMPCONTROL_TEMP_LIMIT, CPU_MSM_TEMP_LIMIT};

    String CPU_BOOST = "/sys/module/cpu_boost/parameters";
    String CPU_BOOST_ENABLE = "/sys/module/cpu_boost/parameters/cpu_boost";
    String CPU_BOOST_ENABLE_2 = "/sys/module/cpu_boost/parameters/cpuboost_enable";
    String CPU_BOOST_DEBUG_MASK = "/sys/module/cpu_boost/parameters/debug_mask";
    String CPU_BOOST_MS = "/sys/module/cpu_boost/parameters/boost_ms";
    String CPU_BOOST_SYNC_THRESHOLD = "/sys/module/cpu_boost/parameters/sync_threshold";
    String CPU_BOOST_INPUT_MS = "/sys/module/cpu_boost/parameters/input_boost_ms";
    String CPU_BOOST_INPUT_BOOST_FREQ = "/sys/module/cpu_boost/parameters/input_boost_freq";

    String[] CPU_BOOST_ARRAY = {CPU_BOOST, CPU_BOOST_ENABLE, CPU_BOOST_ENABLE_2, CPU_BOOST_DEBUG_MASK, CPU_BOOST_MS,
            CPU_BOOST_SYNC_THRESHOLD, CPU_BOOST_INPUT_MS,
            CPU_BOOST_INPUT_BOOST_FREQ};

    String[][] CPU_ARRAY = {
            new String[]{CPU_CUR_FREQ, CPU_CORE_ONLINE, CPU_MAX_FREQ, CPU_MIN_FREQ, CPU_MAX_SCREEN_OFF_FREQ, CPU_MSM_CPUFREQ_LIMIT,
                    CPU_AVAILABLE_FREQS, CPU_TIME_STATE, CPU_SCALING_GOVERNOR, CPU_AVAILABLE_GOVERNORS, CPU_GOVERNOR_TUNABLES,
                    CPU_MC_POWER_SAVING, CPU_AVAILABLE_CFS_SCHEDULERS, CPU_CURRENT_CFS_SCHEDULER}, CPU_TEMP_LIMIT_ARRAY, CPU_BOOST_ARRAY};

    // CPU Voltage
    String CPU_VOLTAGE = "/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table";
    String CPU_FAUX_VOLTAGE = "/sys/devices/system/cpu/cpufreq/vdd_table/vdd_levels";

    String[] CPU_VOLTAGE_ARRAY = {CPU_VOLTAGE, CPU_FAUX_VOLTAGE};

    // CPU Hotplug
    String HOTPLUG_MPDECISION_BINARY = "/system/bin/mpdecision";
    String HOTPLUG_MPDEC = "mpdecision";

    String[] MPDECISION_ARRAY = {HOTPLUG_MPDECISION_BINARY, HOTPLUG_MPDEC};

    String HOTPLUG_INTELLI_PLUG = "/sys/module/intelli_plug/parameters";
    String HOTPLUG_INTELLI_PLUG_ENABLE = "/sys/module/intelli_plug/parameters/intelli_plug_active";
    String HOTPLUG_INTELLI_PLUG_PROFILE = "/sys/module/intelli_plug/parameters/nr_run_profile_sel";
    String HOTPLUG_INTELLI_PLUG_ECO = "/sys/module/intelli_plug/parameters/eco_mode_active";
    String HOTPLUG_INTELLI_PLUG_TOUCH_BOOST = "/sys/module/intelli_plug/parameters/touch_boost_active";
    String HOTPLUG_INTELLI_PLUG_HYSTERESIS = "/sys/module/intelli_plug/parameters/nr_run_hysteresis";
    String HOTPLUG_INTELLI_PLUG_THRESHOLD = "/sys/module/intelli_plug/parameters/cpu_nr_run_threshold";
    String HOTPLUG_INTELLI_PLUG_SCREEN_OFF_MAX = "/sys/module/intelli_plug/parameters/screen_off_max";

    String HOTPLUG_INTELLI_PLUG_5 = "/sys/kernel/intelli_plug";
    String HOTPLUG_INTELLI_PLUG_5_ENABLE = "/sys/kernel/intelli_plug/intelli_plug_active";
    String HOTPLUG_INTELLI_PLUG_5_DEBUG = "/sys/kernel/intelli_plug/debug_intelli_plug";
    String HOTPLUG_INTELLI_PLUG_5_PROFILE = "/sys/kernel/intelli_plug/full_mode_profile";
    String HOTPLUG_INTELLI_PLUG_5_SUSPEND = "/sys/kernel/intelli_plug/hotplug_suspend";
    String HOTPLUG_INTELLI_PLUG_5_CPUS_BOOSTED = "/sys/kernel/intelli_plug/cpus_boosted";
    String HOTPLUG_INTELLI_PLUG_5_HYSTERESIS = "/sys/kernel/intelli_plug/nr_run_hysteresis";
    String HOTPLUG_INTELLI_PLUG_5_MIN_CPUS_ONLINE = "/sys/kernel/intelli_plug/min_cpus_online";
    String HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE = "/sys/kernel/intelli_plug/max_cpus_online";
    String HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE_SUSP = "/sys/kernel/intelli_plug/max_cpus_online_susp";
    String HOTPLUG_INTELLI_PLUG_5_SUSPEND_DEFER_TIME = "/sys/kernel/intelli_plug/suspend_defer_time";
    String HOTPLUG_INTELLI_PLUG_5_DEFER_SAMPLING = "/sys/kernel/intelli_plug/def_sampling_ms";
    String HOTPLUG_INTELLI_PLUG_5_BOOST_LOCK_DURATION = "/sys/kernel/intelli_plug/boost_lock_duration";
    String HOTPLUG_INTELLI_PLUG_5_DOWN_LOCK_DURATION = "/sys/kernel/intelli_plug/down_lock_duration";
    String HOTPLUG_INTELLI_PLUG_5_THRESHOLD = "/sys/kernel/intelli_plug/cpu_nr_run_threshold";
    String HOTPLUG_INTELLI_PLUG_5_FSHIFT = "/sys/kernel/intelli_plug/nr_fshift";
    String HOTPLUG_INTELLI_PLUG_5_SCREEN_OFF_MAX = "/sys/kernel/intelli_plug/screen_off_max";

    String[] INTELLIPLUG_ARRAY = {HOTPLUG_INTELLI_PLUG, HOTPLUG_INTELLI_PLUG_ENABLE, HOTPLUG_INTELLI_PLUG_PROFILE,
            HOTPLUG_INTELLI_PLUG_ECO, HOTPLUG_INTELLI_PLUG_TOUCH_BOOST, HOTPLUG_INTELLI_PLUG_HYSTERESIS,
            HOTPLUG_INTELLI_PLUG_THRESHOLD, HOTPLUG_INTELLI_PLUG_SCREEN_OFF_MAX, HOTPLUG_INTELLI_PLUG_5_ENABLE,
            HOTPLUG_INTELLI_PLUG_5_DEBUG, HOTPLUG_INTELLI_PLUG_5, HOTPLUG_INTELLI_PLUG_5_PROFILE, HOTPLUG_INTELLI_PLUG_5_SUSPEND,
            HOTPLUG_INTELLI_PLUG_5_CPUS_BOOSTED, HOTPLUG_INTELLI_PLUG_5_HYSTERESIS, HOTPLUG_INTELLI_PLUG_5_MIN_CPUS_ONLINE,
            HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE, HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE_SUSP,
            HOTPLUG_INTELLI_PLUG_5_SUSPEND_DEFER_TIME, HOTPLUG_INTELLI_PLUG_5_DEFER_SAMPLING,
            HOTPLUG_INTELLI_PLUG_5_BOOST_LOCK_DURATION, HOTPLUG_INTELLI_PLUG_5_DOWN_LOCK_DURATION,
            HOTPLUG_INTELLI_PLUG_5_THRESHOLD, HOTPLUG_INTELLI_PLUG_5_FSHIFT, HOTPLUG_INTELLI_PLUG_5_SCREEN_OFF_MAX};

    String HOTPLUG_BLU_PLUG = "/sys/module/blu_plug/parameters";
    String HOTPLUG_BLU_PLUG_ENABLE = "/sys/module/blu_plug/parameters/enabled";
    String HOTPLUG_BLU_PLUG_POWERSAVER_MODE = "/sys/module/blu_plug/parameters/powersaver_mode";
    String HOTPLUG_BLU_PLUG_MIN_ONLINE = "/sys/module/blu_plug/parameters/min_online";
    String HOTPLUG_BLU_PLUG_MAX_ONLINE = "/sys/module/blu_plug/parameters/max_online";
    String HOTPLUG_BLU_PLUG_MAX_CORES_SCREEN_OFF = "/sys/module/blu_plug/parameters/max_cores_screenoff";
    String HOTPLUG_BLU_PLUG_MAX_FREQ_SCREEN_OFF = "/sys/module/blu_plug/parameters/max_freq_screenoff";
    String HOTPLUG_BLU_PLUG_UP_THRESHOLD = "/sys/module/blu_plug/parameters/up_threshold";
    String HOTPLUG_BLU_PLUG_UP_TIMER_CNT = "/sys/module/blu_plug/parameters/up_timer_cnt";
    String HOTPLUG_BLU_PLUG_DOWN_TIMER_CNT = "/sys/module/blu_plug/parameters/down_timer_cnt";

    String[] BLU_PLUG_ARRAY = {HOTPLUG_BLU_PLUG, HOTPLUG_BLU_PLUG_ENABLE, HOTPLUG_BLU_PLUG_POWERSAVER_MODE,
            HOTPLUG_BLU_PLUG_MIN_ONLINE, HOTPLUG_BLU_PLUG_MAX_ONLINE, HOTPLUG_BLU_PLUG_MAX_CORES_SCREEN_OFF,
            HOTPLUG_BLU_PLUG_MAX_FREQ_SCREEN_OFF, HOTPLUG_BLU_PLUG_UP_THRESHOLD, HOTPLUG_BLU_PLUG_UP_TIMER_CNT,
            HOTPLUG_BLU_PLUG_DOWN_TIMER_CNT};

    String HOTPLUG_MSM = "/sys/module/msm_hotplug";
    String HOTPLUG_MSM_ENABLE = "/sys/module/msm_hotplug/enabled";
    String HOTPLUG_MSM_ENABLE_2 = "/sys/module/msm_hotplug/msm_enabled";
    String HOTPLUG_MSM_DEBUG_MASK = "/sys/module/msm_hotplug/parameters/debug_mask";
    String HOTPLUG_MSM_MIN_CPUS_ONLINE = "/sys/module/msm_hotplug/min_cpus_online";
    String HOTPLUG_MSM_MAX_CPUS_ONLINE = "/sys/module/msm_hotplug/max_cpus_online";
    String HOTPLUG_MSM_CPUS_BOOSTED = "/sys/module/msm_hotplug/cpus_boosted";
    String HOTPLUG_MSM_MAX_CPUS_ONLINE_SUSP = "/sys/module/msm_hotplug/max_cpus_online_susp";
    String HOTPLUG_MSM_BOOST_LOCK_DURATION = "/sys/module/msm_hotplug/boost_lock_duration";
    String HOTPLUG_MSM_DOWN_LOCK_DURATION = "/sys/module/msm_hotplug/down_lock_duration";
    String HOTPLUG_MSM_HISTORY_SIZE = "/sys/module/msm_hotplug/history_size";
    String HOTPLUG_MSM_UPDATE_RATE = "/sys/module/msm_hotplug/update_rate";
    String HOTPLUG_MSM_UPDATE_RATES = "/sys/module/msm_hotplug/update_rates";
    String HOTPLUG_MSM_FAST_LANE_LOAD = "/sys/module/msm_hotplug/fast_lane_load";
    String HOTPLUG_MSM_FAST_LANE_MIN_FREQ = "/sys/module/msm_hotplug/fast_lane_min_freq";
    String HOTPLUG_MSM_OFFLINE_LOAD = "/sys/module/msm_hotplug/offline_load";
    String HOTPLUG_MSM_IO_IS_BUSY = "/sys/module/msm_hotplug/io_is_busy";
    String HOTPLUG_MSM_HP_IO_IS_BUSY = "/sys/module/msm_hotplug/hp_io_is_busy";
    String HOTPLUG_MSM_SUSPEND_MAX_CPUS = "/sys/module/msm_hotplug/suspend_max_cpus";
    String HOTPLUG_MSM_SUSPEND_FREQ = "/sys/module/msm_hotplug/suspend_freq";
    String HOTPLUG_MSM_SUSPEND_MAX_FREQ = "/sys/module/msm_hotplug/suspend_max_freq";
    String HOTPLUG_MSM_SUSPEND_DEFER_TIME = "/sys/module/msm_hotplug/suspend_defer_time";

    String[] HOTPLUG_MSM_ARRAY = {HOTPLUG_MSM, HOTPLUG_MSM_ENABLE, HOTPLUG_MSM_ENABLE_2, HOTPLUG_MSM_DEBUG_MASK,
            HOTPLUG_MSM_MIN_CPUS_ONLINE, HOTPLUG_MSM_MAX_CPUS_ONLINE, HOTPLUG_MSM_CPUS_BOOSTED, HOTPLUG_MSM_MAX_CPUS_ONLINE_SUSP,
            HOTPLUG_MSM_BOOST_LOCK_DURATION, HOTPLUG_MSM_DOWN_LOCK_DURATION, HOTPLUG_MSM_HISTORY_SIZE, HOTPLUG_MSM_UPDATE_RATE,
            HOTPLUG_MSM_UPDATE_RATES, HOTPLUG_MSM_FAST_LANE_LOAD, HOTPLUG_MSM_FAST_LANE_MIN_FREQ, HOTPLUG_MSM_OFFLINE_LOAD,
            HOTPLUG_MSM_IO_IS_BUSY, HOTPLUG_MSM_HP_IO_IS_BUSY, HOTPLUG_MSM_SUSPEND_MAX_CPUS, HOTPLUG_MSM_SUSPEND_FREQ,
            HOTPLUG_MSM_SUSPEND_MAX_FREQ, HOTPLUG_MSM_SUSPEND_DEFER_TIME};

    String MAKO_HOTPLUG = "/sys/class/misc/mako_hotplug_control";
    String MAKO_HOTPLUG_ENABLED = "/sys/class/misc/mako_hotplug_control/enabled";
    String MAKO_HOTPLUG_CORES_ON_TOUCH = "/sys/class/misc/mako_hotplug_control/cores_on_touch";
    String MAKO_HOTPLUG_CPUFREQ_UNPLUG_LIMIT = "/sys/class/misc/mako_hotplug_control/cpufreq_unplug_limit";
    String MAKO_HOTPLUG_FIRST_LEVEL = "/sys/class/misc/mako_hotplug_control/first_level";
    String MAKO_HOTPLUG_HIGH_LOAD_COUNTER = "/sys/class/misc/mako_hotplug_control/high_load_counter";
    String MAKO_HOTPLUG_LOAD_THRESHOLD = "/sys/class/misc/mako_hotplug_control/load_threshold";
    String MAKO_HOTPLUG_MAX_LOAD_COUNTER = "/sys/class/misc/mako_hotplug_control/max_load_counter";
    String MAKO_HOTPLUG_MIN_TIME_CPU_ONLINE = "/sys/class/misc/mako_hotplug_control/min_time_cpu_online";
    String MAKO_HOTPLUG_TIMER = "/sys/class/misc/mako_hotplug_control/timer";
    String MAKO_HOTPLUG_SUSPEND_FREQ = "/sys/class/misc/mako_hotplug_control/suspend_frequency";

    String[] MAKO_HOTPLUG_ARRAY = {MAKO_HOTPLUG, MAKO_HOTPLUG_ENABLED, MAKO_HOTPLUG_CORES_ON_TOUCH,
            MAKO_HOTPLUG_CPUFREQ_UNPLUG_LIMIT, MAKO_HOTPLUG_FIRST_LEVEL, MAKO_HOTPLUG_HIGH_LOAD_COUNTER,
            MAKO_HOTPLUG_LOAD_THRESHOLD, MAKO_HOTPLUG_MAX_LOAD_COUNTER, MAKO_HOTPLUG_MIN_TIME_CPU_ONLINE,
            MAKO_HOTPLUG_TIMER, MAKO_HOTPLUG_SUSPEND_FREQ};

    String MSM_MPDECISION_HOTPLUG = "/sys/kernel/msm_mpdecision/conf";
    String BRICKED_HOTPLUG = "/sys/kernel/bricked_hotplug/conf";
    String MB_ENABLED = "enabled";
    String MB_SCROFF_SINGLE_CORE = "scroff_single_core";
    String MB_MIN_CPUS = "min_cpus";
    String MB_MAX_CPUS = "max_cpus";
    String MB_MIN_CPUS_ONLINE = "min_cpus_online";
    String MB_MAX_CPUS_ONLINE = "max_cpus_online";
    String MB_CPUS_ONLINE_SUSP = "max_cpus_online_susp";
    String MB_IDLE_FREQ = "idle_freq";
    String MB_BOOST_ENABLED = "boost_enabled";
    String MB_BOOST_TIME = "boost_time";
    String MB_CPUS_BOOSTED = "cpus_boosted";
    String MB_BOOST_FREQS = "boost_freqs";
    String MB_STARTDELAY = "startdelay";
    String MB_DELAY = "delay";
    String MB_PAUSE = "pause";

    String[] MB_HOTPLUG_ARRAY = {MSM_MPDECISION_HOTPLUG, BRICKED_HOTPLUG};

    String ALUCARD_HOTPLUG = "/sys/kernel/alucard_hotplug";
    String ALUCARD_HOTPLUG_ENABLE = "/sys/kernel/alucard_hotplug/hotplug_enable";
    String ALUCARD_HOTPLUG_HP_IO_IS_BUSY = "/sys/kernel/alucard_hotplug/hp_io_is_busy";
    String ALUCARD_HOTPLUG_SAMPLING_RATE = "/sys/kernel/alucard_hotplug/hotplug_sampling_rate";
    String ALUCARD_HOTPLUG_SUSPEND = "/sys/kernel/alucard_hotplug/hotplug_suspend";
    String ALUCARD_HOTPLUG_MIN_CPUS_ONLINE = "/sys/kernel/alucard_hotplug/min_cpus_online";
    String ALUCARD_HOTPLUG_MAX_CORES_LIMIT = "/sys/kernel/alucard_hotplug/maxcoreslimit";
    String ALUCARD_HOTPLUG_MAX_CORES_LIMIT_SLEEP = "/sys/kernel/alucard_hotplug/maxcoreslimit_sleep";
    String ALUCARD_HOTPLUG_CPU_DOWN_RATE = "/sys/kernel/alucard_hotplug/cpu_down_rate";
    String ALUCARD_HOTPLUG_CPU_UP_RATE = "/sys/kernel/alucard_hotplug/cpu_up_rate";

    String[] ALUCARD_HOTPLUG_ARRAY = {ALUCARD_HOTPLUG, ALUCARD_HOTPLUG_ENABLE, ALUCARD_HOTPLUG_HP_IO_IS_BUSY,
            ALUCARD_HOTPLUG_SAMPLING_RATE, ALUCARD_HOTPLUG_SUSPEND, ALUCARD_HOTPLUG_MIN_CPUS_ONLINE, ALUCARD_HOTPLUG_MAX_CORES_LIMIT,
            ALUCARD_HOTPLUG_MAX_CORES_LIMIT_SLEEP, ALUCARD_HOTPLUG_CPU_DOWN_RATE, ALUCARD_HOTPLUG_CPU_UP_RATE};

    String[][] CPU_HOTPLUG_ARRAY = {MPDECISION_ARRAY, INTELLIPLUG_ARRAY, BLU_PLUG_ARRAY, HOTPLUG_MSM_ARRAY, MAKO_HOTPLUG_ARRAY,
            MB_HOTPLUG_ARRAY, ALUCARD_HOTPLUG_ARRAY};

    // GPU
    String GPU_GENERIC_GOVERNORS = "performance powersave ondemand simple conservative";

    String GPU_CUR_KGSL2D0_QCOM_FREQ = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/gpuclk";
    String GPU_MAX_KGSL2D0_QCOM_FREQ = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/max_gpuclk";
    String GPU_AVAILABLE_KGSL2D0_QCOM_FREQS = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/gpu_available_frequencies";
    String GPU_SCALING_KGSL2D0_QCOM_GOVERNOR = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/pwrscale/trustzone/governor";

    String GPU_CUR_KGSL3D0_QCOM_FREQ = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/gpuclk";
    String GPU_MAX_KGSL3D0_QCOM_FREQ = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/max_gpuclk";
    String GPU_AVAILABLE_KGSL3D0_QCOM_FREQS = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/gpu_available_frequencies";
    String GPU_SCALING_KGSL3D0_QCOM_GOVERNOR = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/pwrscale/trustzone/governor";

    String GPU_CUR_FDB00000_QCOM_FREQ = "/sys/devices/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/gpuclk";
    String GPU_MAX_FDB00000_QCOM_FREQ = "/sys/devices/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/max_gpuclk";
    String GPU_AVAILABLE_FDB00000_QCOM_FREQS = "/sys/devices/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/gpu_available_frequencies";
    String GPU_SCALING_FDB00000_QCOM_GOVERNOR = "/sys/devices/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/devfreq/governor";
    String GPU_AVAILABLE_FDB00000_QCOM_GOVERNORS = "/sys/devices/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/devfreq/available_governors";

    String GPU_CUR_OMAP_FREQ = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/frequency";
    String GPU_MAX_OMAP_FREQ = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/frequency_limit";
    String GPU_AVAILABLE_OMAP_FREQS = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/frequency_list";
    String GPU_SCALING_OMAP_GOVERNOR = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/governor";
    String GPU_AVAILABLE_OMAP_GOVERNORS = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/governor_list";

    String[] GPU_2D_CUR_FREQ_ARRAY = {GPU_CUR_KGSL2D0_QCOM_FREQ};

    String[] GPU_2D_MAX_FREQ_ARRAY = {GPU_MAX_KGSL2D0_QCOM_FREQ};

    String[] GPU_2D_AVAILABLE_FREQS_ARRAY = {GPU_AVAILABLE_KGSL2D0_QCOM_FREQS};

    String[] GPU_2D_SCALING_GOVERNOR_ARRAY = {GPU_SCALING_KGSL2D0_QCOM_GOVERNOR};

    String[] GPU_CUR_FREQ_ARRAY = {GPU_CUR_KGSL3D0_QCOM_FREQ, GPU_CUR_FDB00000_QCOM_FREQ,
            GPU_CUR_OMAP_FREQ};

    String[] GPU_MAX_FREQ_ARRAY = {GPU_MAX_KGSL3D0_QCOM_FREQ, GPU_MAX_FDB00000_QCOM_FREQ,
            GPU_MAX_OMAP_FREQ};

    String[] GPU_AVAILABLE_FREQS_ARRAY = {GPU_AVAILABLE_KGSL3D0_QCOM_FREQS,
            GPU_AVAILABLE_FDB00000_QCOM_FREQS, GPU_AVAILABLE_OMAP_FREQS};

    String[] GPU_SCALING_GOVERNOR_ARRAY = {GPU_SCALING_KGSL3D0_QCOM_GOVERNOR,
            GPU_SCALING_FDB00000_QCOM_GOVERNOR, GPU_SCALING_OMAP_GOVERNOR};

    String[] GPU_AVAILABLE_GOVERNORS_ARRAY = {GPU_AVAILABLE_FDB00000_QCOM_GOVERNORS,
            GPU_AVAILABLE_OMAP_GOVERNORS};

    // Simple GPU
    String SIMPLE_GPU_ACTIVATE = "/sys/module/simple_gpu_algorithm/parameters/simple_gpu_activate";
    String SIMPLE_GPU_LAZINESS = "/sys/module/simple_gpu_algorithm/parameters/simple_laziness";
    String SIMPLE_RAMP_THRESHOLD = "/sys/module/simple_gpu_algorithm/parameters/simple_ramp_threshold";

    String[][] GPU_ARRAY = {GPU_2D_CUR_FREQ_ARRAY,
            GPU_2D_MAX_FREQ_ARRAY, GPU_2D_AVAILABLE_FREQS_ARRAY,
            GPU_2D_SCALING_GOVERNOR_ARRAY, GPU_CUR_FREQ_ARRAY,
            GPU_MAX_FREQ_ARRAY, GPU_AVAILABLE_FREQS_ARRAY,
            GPU_SCALING_GOVERNOR_ARRAY,
            {SIMPLE_GPU_ACTIVATE, SIMPLE_GPU_LAZINESS, SIMPLE_RAMP_THRESHOLD}};

    // Screen
    String SCREEN_KCAL = "/sys/devices/platform/kcal_ctrl.0";
    String SCREEN_KCAL_CTRL = SCREEN_KCAL + "/kcal";
    String SCREEN_KCAL_CTRL_CTRL = SCREEN_KCAL + "/kcal_ctrl";
    String SCREEN_KCAL_CTRL_ENABLE = SCREEN_KCAL + "/kcal_enable";
    String SCREEN_KCAL_CTRL_MIN = SCREEN_KCAL + "/kcal_min";
    String SCREEN_KCAL_CTRL_INVERT = SCREEN_KCAL + "/kcal_invert";
    String SCREEN_KCAL_CTRL_SAT = SCREEN_KCAL + "/kcal_sat";
    String SCREEN_KCAL_CTRL_HUE = SCREEN_KCAL + "/kcal_hue";
    String SCREEN_KCAL_CTRL_VAL = SCREEN_KCAL + "/kcal_val";
    String SCREEN_KCAL_CTRL_CONT = SCREEN_KCAL + "/kcal_cont";

    String[] SCREEN_KCAL_CTRL_NEW_ARRAY = {SCREEN_KCAL_CTRL_ENABLE, SCREEN_KCAL_CTRL_INVERT, SCREEN_KCAL_CTRL_SAT,
            SCREEN_KCAL_CTRL_HUE, SCREEN_KCAL_CTRL_VAL, SCREEN_KCAL_CTRL_CONT};

    String SCREEN_DIAG0 = "/sys/devices/platform/DIAG0.0";
    String SCREEN_DIAG0_POWER = SCREEN_DIAG0 + "/power_rail";
    String SCREEN_DIAG0_POWER_CTRL = SCREEN_DIAG0 + "/power_rail_ctrl";

    String SCREEN_COLOR = "/sys/class/misc/colorcontrol";
    String SCREEN_COLOR_CONTROL = SCREEN_COLOR + "/multiplier";
    String SCREEN_COLOR_CONTROL_CTRL = SCREEN_COLOR + "/safety_enabled";

    String SCREEN_SAMOLED_COLOR = "/sys/class/misc/samoled_color";
    String SCREEN_SAMOLED_COLOR_RED = SCREEN_SAMOLED_COLOR + "/red_multiplier";
    String SCREEN_SAMOLED_COLOR_GREEN = SCREEN_SAMOLED_COLOR + "/green_multiplier";
    String SCREEN_SAMOLED_COLOR_BLUE = SCREEN_SAMOLED_COLOR + "/blue_multiplier";

    String[] SCREEN_KCAL_ARRAY = {SCREEN_KCAL_CTRL, SCREEN_DIAG0_POWER, SCREEN_COLOR_CONTROL,
            SCREEN_SAMOLED_COLOR_RED, SCREEN_SAMOLED_COLOR_GREEN, SCREEN_SAMOLED_COLOR_BLUE};

    String[] SCREEN_KCAL_CTRL_ARRAY = {SCREEN_KCAL_CTRL_CTRL, SCREEN_DIAG0_POWER_CTRL, SCREEN_COLOR_CONTROL_CTRL};

    String LM3630_BACKLIGHT_DIMMER = "/sys/module/lm3630_bl/parameters/backlight_dimmer";
    String LM3630_MIN_BRIGHTNESS = "/sys/module/lm3630_bl/parameters/min_brightness";
    String LM3630_BACKLIGHT_DIMMER_THRESHOLD = "/sys/module/lm3630_bl/parameters/backlight_threshold";
    String LM3630_BACKLIGHT_DIMMER_OFFSET = "/sys/module/lm3630_bl/parameters/backlight_offset";

    String MSM_BACKLIGHT_DIMMER = "/sys/module/msm_fb/parameters/backlight_dimmer";

    String[] MIN_BRIGHTNESS_ARRAY = {LM3630_MIN_BRIGHTNESS, MSM_BACKLIGHT_DIMMER};

    String[][] SCREEN_ARRAY = {SCREEN_KCAL_ARRAY, SCREEN_KCAL_CTRL_ARRAY, MIN_BRIGHTNESS_ARRAY, SCREEN_KCAL_CTRL_NEW_ARRAY,
            {SCREEN_KCAL_CTRL_MIN, LM3630_BACKLIGHT_DIMMER, LM3630_BACKLIGHT_DIMMER_THRESHOLD, LM3630_BACKLIGHT_DIMMER_OFFSET}};

    // Wake

    // DT2W
    String LGE_TOUCH_DT2W = "/sys/devices/virtual/input/lge_touch/dt_wake_enabled";
    String LGE_TOUCH_CORE_DT2W = "/sys/module/lge_touch_core/parameters/doubletap_to_wake";
    String LGE_TOUCH_GESTURE = "/sys/devices/virtual/input/lge_touch/touch_gesture";
    String DT2W = "/sys/android_touch/doubletap2wake";
    String TOUCH_PANEL_DT2W = "/proc/touchpanel/double_tap_enable";
    String DT2W_WAKEUP_GESTURE = "/sys/devices/virtual/input/input1/wakeup_gesture";

    String[] DT2W_ARRAY = {LGE_TOUCH_DT2W, LGE_TOUCH_CORE_DT2W, LGE_TOUCH_GESTURE, DT2W, TOUCH_PANEL_DT2W, DT2W_WAKEUP_GESTURE};

    // S2W
    String S2W_ONLY = "/sys/android_touch/s2w_s2sonly";
    String SW2 = "/sys/android_touch/sweep2wake";

    String[] S2W_ARRY = {S2W_ONLY, SW2};

    // T2W
    String TSP_T2W = "/sys/devices/f9966000.i2c/i2c-1/1-004a/tsp";
    String TOUCHWAKE_T2W = "/sys/class/misc/touchwake/enabled";

    String[] T2W_ARRAY = {TSP_T2W, TOUCHWAKE_T2W};

    String WAKE_TIMEOUT = "/sys/android_touch/wake_timeout";
    String POWER_KEY_SUSPEND = "/sys/module/qpnp_power_on/parameters/pwrkey_suspend";

    String[][] WAKE_ARRAY = {DT2W_ARRAY, S2W_ARRY, T2W_ARRAY, {WAKE_TIMEOUT, POWER_KEY_SUSPEND}};

    // Sound
    String SOUND_CONTROL_ENABLE = "/sys/module/snd_soc_wcd9320/parameters/enable_fs";
    String HEADPHONE_GAIN = "/sys/kernel/sound_control_3/gpl_headphone_gain";
    String HANDSET_MICROPONE_GAIN = "/sys/kernel/sound_control_3/gpl_mic_gain";
    String CAM_MICROPHONE_GAIN = "/sys/kernel/sound_control_3/gpl_cam_mic_gain";
    String SPEAKER_GAIN = "/sys/kernel/sound_control_3/gpl_speaker_gain";
    String HEADPHONE_POWERAMP_GAIN = "/sys/kernel/sound_control_3/gpl_headphone_pa_gain";

    String[] SOUND_ARRAY = {SOUND_CONTROL_ENABLE, HEADPHONE_GAIN, HANDSET_MICROPONE_GAIN, CAM_MICROPHONE_GAIN,
            SPEAKER_GAIN, HEADPHONE_POWERAMP_GAIN};

    // Battery
    String FORCE_FAST_CHARGE = "/sys/kernel/fast_charge/force_fast_charge";
    String BLX = "/sys/devices/virtual/misc/batterylifeextender/charging_limit";

    String[] BATTERY_ARRAY = {FORCE_FAST_CHARGE, BLX};

    // I/O
    String IO_INTERNAL_SCHEDULER = "/sys/block/mmcblk0/queue/scheduler";
    String IO_EXTERNAL_SCHEDULER = "/sys/block/mmcblk1/queue/scheduler";
    String IO_INTERNAL_SCHEDULER_TUNABLE = "/sys/block/mmcblk0/queue/iosched";
    String IO_EXTERNAL_SCHEDULER_TUNABLE = "/sys/block/mmcblk1/queue/iosched";
    String IO_INTERNAL_READ_AHEAD = "/sys/block/mmcblk0/queue/read_ahead_kb";
    String IO_EXTERNAL_READ_AHEAD = "/sys/block/mmcblk1/queue/read_ahead_kb";

    String[] IO_ARRAY = {IO_INTERNAL_SCHEDULER, IO_EXTERNAL_SCHEDULER, IO_INTERNAL_SCHEDULER_TUNABLE, IO_EXTERNAL_SCHEDULER_TUNABLE,
            IO_INTERNAL_READ_AHEAD, IO_EXTERNAL_READ_AHEAD};

    // Kernel Samepage Merging
    String KSM_FOLDER = "/sys/kernel/mm/ksm";
    String KSM_FULL_SCANS = KSM_FOLDER + "/full_scans";
    String KSM_PAGES_SHARED = KSM_FOLDER + "/pages_shared";
    String KSM_PAGES_SHARING = KSM_FOLDER + "/pages_sharing";
    String KSM_PAGES_UNSHARED = KSM_FOLDER + "/pages_unshared";
    String KSM_PAGES_VOLATILE = KSM_FOLDER + "/pages_volatile";
    String KSM_RUN = KSM_FOLDER + "/run";
    String KSM_DEFERRED_TIMER = KSM_FOLDER + "/deferred_timer";
    String KSM_PAGES_TO_SCAN = KSM_FOLDER + "/pages_to_scan";
    String KSM_SLEEP_MILLISECONDS = KSM_FOLDER + "/sleep_millisecs";

    String[] KSM_INFOS = {KSM_FULL_SCANS, KSM_PAGES_SHARED, KSM_PAGES_SHARING,
            KSM_PAGES_UNSHARED, KSM_PAGES_VOLATILE};

    // Low Memory Killer
    String LMK_MINFREE = "/sys/module/lowmemorykiller/parameters/minfree";

    // Virtual Memory
    String VM_PATH = "/proc/sys/vm";

    String[] SUPPORTED_VM = {"dirty_ratio", "dirty_background_ratio", "dirty_expire_centisecs",
            "dirty_writeback_centisecs", "min_free_kbytes", "overcommit_ratio", "swappiness",
            "vfs_cache_pressure", "laptop_mode"};

    // Misc
    // TCP
    String TCP_AVAILABLE_CONGESTIONS = "/proc/sys/net/ipv4/tcp_available_congestion_control";

    // Vibration
    String VIBRATION_ENABLE = "/sys/class/timed_output/vibrator/enable";
    String[] VIBRATION_ARRAY = {
            "/sys/vibrator/pwmvalue",
            "/sys/class/timed_output/vibrator/amp",
            "/sys/class/timed_output/vibrator/vtg_level",
            "/sys/devices/platform/tspdrv/nforce_timed",
            "/sys/class/timed_output/vibrator/pwm_value",
            "/sys/devices/i2c-3/3-0033/vibrator/vib0/vib_duty_cycle",
            "/sys/devices/virtual/timed_output/vibrator/voltage_level",
            "/sys/devices/virtual/timed_output/vibrator/pwm_value_1p"
    };

    int[][] VIBRATION_MAX_MIN_ARRAY = {
            {127, 0},
            {100, 0},
            {31, 12}, // Read MAX MIN from sys
            {127, 1},
            {100, 0}, // Read MAX MIN from sys
            {100, 25}, // Needs enable path
            {3199, 1200},
            {99, 53}
    };

    // Wakelock
    String SMB135X_WAKELOCK = "/sys/module/smb135x_charger/parameters/use_wlock";
    String SENSOR_IND_WAKELOCK = "/sys/module/wakeup/parameters/enable_si_ws";
    String MSM_HSIC_HOST_WAKELOCK = "/sys/module/wakeup/parameters/enable_msm_hsic_ws";

    // Logging
    String LOGGER_ENABLED = "/sys/module/logger/parameters/enabled";

    // SELinux
    String SELINUX = "selinux";

    // Dynamic Fsync
    String DYNAMIC_FSYNC = "/sys/kernel/dyn_fsync/Dyn_fsync_active";

    String[][] MISC_ARRAY = {{TCP_AVAILABLE_CONGESTIONS, SMB135X_WAKELOCK, SENSOR_IND_WAKELOCK, MSM_HSIC_HOST_WAKELOCK,
            LOGGER_ENABLED, SELINUX, DYNAMIC_FSYNC}, VIBRATION_ARRAY};

    // Build prop
    String BUILD_PROP = "/system/build.prop";

}
