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

    public final String TAG = "Kernel Adiutor";
    public final String VERSION_NAME = BuildConfig.VERSION_NAME;
    public final String PREF_NAME = "prefs";
    public static final List<ListAdapter.ListItem> mList = new ArrayList<>();

    // Kernel Informations
    public final String PROC_VERSION = "/proc/version";
    public final String PROC_CPUINFO = "/proc/cpuinfo";
    public final String PROC_MEMINFO = "/proc/meminfo";

    // CPU
    public final String CPU_CUR_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq";
    public final String CPU_CORE_ONLINE = "/sys/devices/system/cpu/cpu%d/online";
    public final String CPU_MAX_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_max_freq";
    public final String CPU_MIN_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_min_freq";
    public final String CPU_MAX_SCREEN_OFF_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/screen_off_max_freq";
    public final String CPU_MSM_CPUFREQ_LIMIT = "/sys/kernel/msm_cpufreq_limit/cpufreq_limit";
    public final String CPU_AVAILABLE_FREQS = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
    public final String CPU_TIME_STATE = "/sys/devices/system/cpu/cpu0/cpufreq/stats/time_in_state";
    public final String CPU_SCALING_GOVERNOR = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_governor";
    public final String CPU_AVAILABLE_GOVERNORS = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";
    public final String CPU_GOVERNOR_TUNABLES = "/sys/devices/system/cpu/cpufreq";
    public final String CPU_MC_POWER_SAVING = "/sys/devices/system/cpu/sched_mc_power_savings";

    public final String CPU_TEMPCONTROL_TEMP_LIMIT = "/sys/class/misc/tempcontrol/templimit";
    public final String CPU_MSM_TEMP_LIMIT = "/sys/module/msm_thermal/parameters/temp_threshold";

    public final String[] CPU_TEMP_LIMIT_ARRAY = {CPU_TEMPCONTROL_TEMP_LIMIT, CPU_MSM_TEMP_LIMIT};

    public final String CPU_BOOST = "/sys/module/cpu_boost/parameters";
    public final String CPU_BOOST_ENABLE = "/sys/module/cpu_boost/parameters/cpu_boost";
    public final String CPU_BOOST_ENABLE_2 = "/sys/module/cpu_boost/parameters/cpuboost_enable";
    public final String CPU_BOOST_DEBUG_MASK = "/sys/module/cpu_boost/parameters/debug_mask";
    public final String CPU_BOOST_MS = "/sys/module/cpu_boost/parameters/boost_ms";
    public final String CPU_BOOST_SYNC_THRESHOLD = "/sys/module/cpu_boost/parameters/sync_threshold";
    public final String CPU_BOOST_INPUT_MS = "/sys/module/cpu_boost/parameters/input_boost_ms";
    public final String CPU_BOOST_INPUT_BOOST_FREQ = "/sys/module/cpu_boost/parameters/input_boost_freq";

    public final String[] CPU_BOOST_ARRAY = {CPU_BOOST, CPU_BOOST_ENABLE, CPU_BOOST_ENABLE_2,
            CPU_BOOST_DEBUG_MASK, CPU_BOOST_MS, CPU_BOOST_SYNC_THRESHOLD, CPU_BOOST_INPUT_MS};

    public final String[][] CPU_ARRAY = {
            new String[]{CPU_CUR_FREQ, CPU_CORE_ONLINE, CPU_MAX_FREQ, CPU_MIN_FREQ,
                    CPU_MAX_SCREEN_OFF_FREQ, CPU_MSM_CPUFREQ_LIMIT, CPU_AVAILABLE_FREQS, CPU_TIME_STATE,
                    CPU_SCALING_GOVERNOR, CPU_AVAILABLE_GOVERNORS, CPU_GOVERNOR_TUNABLES, CPU_MC_POWER_SAVING},
            CPU_TEMP_LIMIT_ARRAY, CPU_BOOST_ARRAY};

    // CPU Voltage
    public final String CPU_VOLTAGE = "/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table";
    public final String CPU_FAUX_VOLTAGE = "/sys/devices/system/cpu/cpufreq/vdd_table/vdd_levels";

    public final String[] CPU_VOLTAGE_ARRAY = {CPU_VOLTAGE, CPU_FAUX_VOLTAGE};

    // CPU Hotplug
    public final String HOTPLUG_MPDECISION_BINARY = "/system/bin/mpdecision";
    public final String HOTPLUG_MPDEC = "mpdecision";

    public final String[] MPDECISION_ARRAY = {HOTPLUG_MPDECISION_BINARY, HOTPLUG_MPDEC};

    public final String HOTPLUG_INTELLI_PLUG = "/sys/module/intelli_plug/parameters";
    public final String HOTPLUG_INTELLI_PLUG_ENABLE = "/sys/module/intelli_plug/parameters/intelli_plug_active";
    public final String HOTPLUG_INTELLI_PLUG_PROFILE = "/sys/module/intelli_plug/parameters/nr_run_profile_sel";
    public final String HOTPLUG_INTELLI_PLUG_ECO = "/sys/module/intelli_plug/parameters/eco_mode_active";
    public final String HOTPLUG_INTELLI_PLUG_TOUCH_BOOST = "/sys/module/intelli_plug/parameters/touch_boost_active";
    public final String HOTPLUG_INTELLI_PLUG_HYSTERESIS = "/sys/module/intelli_plug/parameters/nr_run_hysteresis";
    public final String HOTPLUG_INTELLI_PLUG_THRESHOLD = "/sys/module/intelli_plug/parameters/cpu_nr_run_threshold";
    public final String HOTPLUG_INTELLI_PLUG_SCREEN_OFF_MAX = "/sys/module/intelli_plug/parameters/screen_off_max";

    public final String HOTPLUG_INTELLI_PLUG_5 = "/sys/kernel/intelli_plug";
    public final String HOTPLUG_INTELLI_PLUG_5_ENABLE = "/sys/kernel/intelli_plug/intelli_plug_active";
    public final String HOTPLUG_INTELLI_PLUG_5_DEBUG = "/sys/kernel/intelli_plug/debug_intelli_plug";
    public final String HOTPLUG_INTELLI_PLUG_5_PROFILE = "/sys/kernel/intelli_plug/full_mode_profile";
    public final String HOTPLUG_INTELLI_PLUG_5_SUSPEND = "/sys/kernel/intelli_plug/hotplug_suspend";
    public final String HOTPLUG_INTELLI_PLUG_5_CPUS_BOOSTED = "/sys/kernel/intelli_plug/cpus_boosted";
    public final String HOTPLUG_INTELLI_PLUG_5_HYSTERESIS = "/sys/kernel/intelli_plug/nr_run_hysteresis";
    public final String HOTPLUG_INTELLI_PLUG_5_MIN_CPUS_ONLINE = "/sys/kernel/intelli_plug/min_cpus_online";
    public final String HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE = "/sys/kernel/intelli_plug/max_cpus_online";
    public final String HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE_SUSP = "/sys/kernel/intelli_plug/max_cpus_online_susp";
    public final String HOTPLUG_INTELLI_PLUG_5_SUSPEND_DEFER_TIME = "/sys/kernel/intelli_plug/suspend_defer_time";
    public final String HOTPLUG_INTELLI_PLUG_5_DEFER_SAMPLING = "/sys/kernel/intelli_plug/def_sampling_ms";
    public final String HOTPLUG_INTELLI_PLUG_5_BOOST_LOCK_DURATION = "/sys/kernel/intelli_plug/boost_lock_duration";
    public final String HOTPLUG_INTELLI_PLUG_5_DOWN_LOCK_DURATION = "/sys/kernel/intelli_plug/down_lock_duration";
    public final String HOTPLUG_INTELLI_PLUG_5_THRESHOLD = "/sys/kernel/intelli_plug/cpu_nr_run_threshold";
    public final String HOTPLUG_INTELLI_PLUG_5_FSHIFT = "/sys/kernel/intelli_plug/nr_fshift";
    public final String HOTPLUG_INTELLI_PLUG_5_SCREEN_OFF_MAX = "/sys/kernel/intelli_plug/screen_off_max";

    public final String[] INTELLIPLUG_ARRAY = {HOTPLUG_INTELLI_PLUG, HOTPLUG_INTELLI_PLUG_ENABLE, HOTPLUG_INTELLI_PLUG_PROFILE,
            HOTPLUG_INTELLI_PLUG_ECO, HOTPLUG_INTELLI_PLUG_TOUCH_BOOST, HOTPLUG_INTELLI_PLUG_HYSTERESIS,
            HOTPLUG_INTELLI_PLUG_THRESHOLD, HOTPLUG_INTELLI_PLUG_SCREEN_OFF_MAX, HOTPLUG_INTELLI_PLUG_5_ENABLE,
            HOTPLUG_INTELLI_PLUG_5_DEBUG, HOTPLUG_INTELLI_PLUG_5, HOTPLUG_INTELLI_PLUG_5_PROFILE, HOTPLUG_INTELLI_PLUG_5_SUSPEND,
            HOTPLUG_INTELLI_PLUG_5_CPUS_BOOSTED, HOTPLUG_INTELLI_PLUG_5_HYSTERESIS, HOTPLUG_INTELLI_PLUG_5_MIN_CPUS_ONLINE,
            HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE, HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE_SUSP,
            HOTPLUG_INTELLI_PLUG_5_SUSPEND_DEFER_TIME, HOTPLUG_INTELLI_PLUG_5_DEFER_SAMPLING,
            HOTPLUG_INTELLI_PLUG_5_BOOST_LOCK_DURATION, HOTPLUG_INTELLI_PLUG_5_DOWN_LOCK_DURATION,
            HOTPLUG_INTELLI_PLUG_5_THRESHOLD, HOTPLUG_INTELLI_PLUG_5_FSHIFT, HOTPLUG_INTELLI_PLUG_5_SCREEN_OFF_MAX};

    public final String HOTPLUG_BLU_PLUG = "/sys/module/blu_plug/parameters";
    public final String HOTPLUG_BLU_PLUG_ENABLE = "/sys/module/blu_plug/parameters/enabled";
    public final String HOTPLUG_BLU_PLUG_POWERSAVER_MODE = "/sys/module/blu_plug/parameters/powersaver_mode";
    public final String HOTPLUG_BLU_PLUG_MIN_ONLINE = "/sys/module/blu_plug/parameters/min_online";
    public final String HOTPLUG_BLU_PLUG_MAX_ONLINE = "/sys/module/blu_plug/parameters/max_online";
    public final String HOTPLUG_BLU_PLUG_MAX_CORES_SCREEN_OFF = "/sys/module/blu_plug/parameters/max_cores_screenoff";
    public final String HOTPLUG_BLU_PLUG_MAX_FREQ_SCREEN_OFF = "/sys/module/blu_plug/parameters/max_freq_screenoff";
    public final String HOTPLUG_BLU_PLUG_UP_THRESHOLD = "/sys/module/blu_plug/parameters/up_threshold";
    public final String HOTPLUG_BLU_PLUG_UP_TIMER_CNT = "/sys/module/blu_plug/parameters/up_timer_cnt";
    public final String HOTPLUG_BLU_PLUG_DOWN_TIMER_CNT = "/sys/module/blu_plug/parameters/down_timer_cnt";

    public final String[] BLU_PLUG_ARRAY = {HOTPLUG_BLU_PLUG, HOTPLUG_BLU_PLUG_ENABLE, HOTPLUG_BLU_PLUG_POWERSAVER_MODE,
            HOTPLUG_BLU_PLUG_MIN_ONLINE, HOTPLUG_BLU_PLUG_MAX_ONLINE, HOTPLUG_BLU_PLUG_MAX_CORES_SCREEN_OFF,
            HOTPLUG_BLU_PLUG_MAX_FREQ_SCREEN_OFF, HOTPLUG_BLU_PLUG_UP_THRESHOLD, HOTPLUG_BLU_PLUG_UP_TIMER_CNT,
            HOTPLUG_BLU_PLUG_DOWN_TIMER_CNT};

    public final String HOTPLUG_MSM = "/sys/module/msm_hotplug";
    public final String HOTPLUG_MSM_ENABLE = "/sys/module/msm_hotplug/enabled";
    public final String HOTPLUG_MSM_ENABLE_2 = "/sys/module/msm_hotplug/msm_enabled";
    public final String HOTPLUG_MSM_DEBUG_MASK = "/sys/module/msm_hotplug/parameters/debug_mask";
    public final String HOTPLUG_MSM_MIN_CPUS_ONLINE = "/sys/module/msm_hotplug/min_cpus_online";
    public final String HOTPLUG_MSM_MAX_CPUS_ONLINE = "/sys/module/msm_hotplug/max_cpus_online";
    public final String HOTPLUG_MSM_CPUS_BOOSTED = "/sys/module/msm_hotplug/cpus_boosted";
    public final String HOTPLUG_MSM_MAX_CPUS_ONLINE_SUSP = "/sys/module/msm_hotplug/max_cpus_online_susp";
    public final String HOTPLUG_MSM_BOOST_LOCK_DURATION = "/sys/module/msm_hotplug/boost_lock_duration";
    public final String HOTPLUG_MSM_DOWN_LOCK_DURATION = "/sys/module/msm_hotplug/down_lock_duration";
    public final String HOTPLUG_MSM_HISTORY_SIZE = "/sys/module/msm_hotplug/history_size";
    public final String HOTPLUG_MSM_UPDATE_RATE = "/sys/module/msm_hotplug/update_rate";
    public final String HOTPLUG_MSM_UPDATE_RATES = "/sys/module/msm_hotplug/update_rates";
    public final String HOTPLUG_MSM_FAST_LANE_LOAD = "/sys/module/msm_hotplug/fast_lane_load";
    public final String HOTPLUG_MSM_FAST_LANE_MIN_FREQ = "/sys/module/msm_hotplug/fast_lane_min_freq";
    public final String HOTPLUG_MSM_OFFLINE_LOAD = "/sys/module/msm_hotplug/offline_load";
    public final String HOTPLUG_MSM_IO_IS_BUSY = "/sys/module/msm_hotplug/io_is_busy";
    public final String HOTPLUG_MSM_HP_IO_IS_BUSY = "/sys/module/msm_hotplug/hp_io_is_busy";
    public final String HOTPLUG_MSM_SUSPEND_MAX_CPUS = "/sys/module/msm_hotplug/suspend_max_cpus";
    public final String HOTPLUG_MSM_SUSPEND_FREQ = "/sys/module/msm_hotplug/suspend_freq";
    public final String HOTPLUG_MSM_SUSPEND_MAX_FREQ = "/sys/module/msm_hotplug/suspend_max_freq";
    public final String HOTPLUG_MSM_SUSPEND_DEFER_TIME = "/sys/module/msm_hotplug/suspend_defer_time";

    public final String[] HOTPLUG_MSM_ARRAY = {HOTPLUG_MSM, HOTPLUG_MSM_ENABLE, HOTPLUG_MSM_ENABLE_2, HOTPLUG_MSM_DEBUG_MASK,
            HOTPLUG_MSM_MIN_CPUS_ONLINE, HOTPLUG_MSM_MAX_CPUS_ONLINE, HOTPLUG_MSM_CPUS_BOOSTED, HOTPLUG_MSM_MAX_CPUS_ONLINE_SUSP,
            HOTPLUG_MSM_BOOST_LOCK_DURATION, HOTPLUG_MSM_DOWN_LOCK_DURATION, HOTPLUG_MSM_HISTORY_SIZE, HOTPLUG_MSM_UPDATE_RATE,
            HOTPLUG_MSM_UPDATE_RATES, HOTPLUG_MSM_FAST_LANE_LOAD, HOTPLUG_MSM_FAST_LANE_MIN_FREQ, HOTPLUG_MSM_OFFLINE_LOAD,
            HOTPLUG_MSM_IO_IS_BUSY, HOTPLUG_MSM_HP_IO_IS_BUSY, HOTPLUG_MSM_SUSPEND_MAX_CPUS, HOTPLUG_MSM_SUSPEND_FREQ,
            HOTPLUG_MSM_SUSPEND_MAX_FREQ, HOTPLUG_MSM_SUSPEND_DEFER_TIME};

    public final String[][] CPU_HOTPLUG_ARRAY = {MPDECISION_ARRAY, INTELLIPLUG_ARRAY, BLU_PLUG_ARRAY, HOTPLUG_MSM_ARRAY};

    // GPU
    public final String GPU_GENERIC_GOVERNORS = "performance powersave ondemand simple conservative";

    public final String GPU_CUR_KGSL2D0_QCOM_FREQ = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/gpuclk";
    public final String GPU_MAX_KGSL2D0_QCOM_FREQ = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/max_gpuclk";
    public final String GPU_AVAILABLE_KGSL2D0_QCOM_FREQS = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/gpu_available_frequencies";
    public final String GPU_SCALING_KGSL2D0_QCOM_GOVERNOR = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/pwrscale/trustzone/governor";

    public final String GPU_CUR_KGSL3D0_QCOM_FREQ = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/gpuclk";
    public final String GPU_MAX_KGSL3D0_QCOM_FREQ = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/max_gpuclk";
    public final String GPU_AVAILABLE_KGSL3D0_QCOM_FREQS = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/gpu_available_frequencies";
    public final String GPU_SCALING_KGSL3D0_QCOM_GOVERNOR = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/pwrscale/trustzone/governor";

    public final String GPU_CUR_FDB00000_QCOM_FREQ = "/sys/devices/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/gpuclk";
    public final String GPU_MAX_FDB00000_QCOM_FREQ = "/sys/devices/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/max_gpuclk";
    public final String GPU_AVAILABLE_FDB00000_QCOM_FREQS = "/sys/devices/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/gpu_available_frequencies";
    public final String GPU_SCALING_FDB00000_QCOM_GOVERNOR = "/sys/devices/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/devfreq/governor";
    public final String GPU_AVAILABLE_FDB00000_QCOM_GOVERNORS = "/sys/devices/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/devfreq/available_governors";

    public final String GPU_CUR_OMAP_FREQ = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/frequency";
    public final String GPU_MAX_OMAP_FREQ = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/frequency_limit";
    public final String GPU_AVAILABLE_OMAP_FREQS = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/frequency_list";
    public final String GPU_SCALING_OMAP_GOVERNOR = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/governor";
    public final String GPU_AVAILABLE_OMAP_GOVERNORS = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/governor_list";

    public final String[] GPU_2D_CUR_FREQ_ARRAY = {GPU_CUR_KGSL2D0_QCOM_FREQ};

    public final String[] GPU_2D_MAX_FREQ_ARRAY = {GPU_MAX_KGSL2D0_QCOM_FREQ};

    public final String[] GPU_2D_AVAILABLE_FREQS_ARRAY = {GPU_AVAILABLE_KGSL2D0_QCOM_FREQS};

    public final String[] GPU_2D_SCALING_GOVERNOR_ARRAY = {GPU_SCALING_KGSL2D0_QCOM_GOVERNOR};

    public final String[] GPU_CUR_FREQ_ARRAY = {GPU_CUR_KGSL3D0_QCOM_FREQ, GPU_CUR_FDB00000_QCOM_FREQ,
            GPU_CUR_OMAP_FREQ};

    public final String[] GPU_MAX_FREQ_ARRAY = {GPU_MAX_KGSL3D0_QCOM_FREQ, GPU_MAX_FDB00000_QCOM_FREQ,
            GPU_MAX_OMAP_FREQ};

    public final String[] GPU_AVAILABLE_FREQS_ARRAY = {GPU_AVAILABLE_KGSL3D0_QCOM_FREQS,
            GPU_AVAILABLE_FDB00000_QCOM_FREQS, GPU_AVAILABLE_OMAP_FREQS};

    public final String[] GPU_SCALING_GOVERNOR_ARRAY = {GPU_SCALING_KGSL3D0_QCOM_GOVERNOR,
            GPU_SCALING_FDB00000_QCOM_GOVERNOR, GPU_SCALING_OMAP_GOVERNOR};

    public final String[] GPU_AVAILABLE_GOVERNORS_ARRAY = {GPU_AVAILABLE_FDB00000_QCOM_GOVERNORS,
            GPU_AVAILABLE_OMAP_GOVERNORS};

    // Simple GPU
    public final String SIMPLE_GPU_ACTIVATE = "/sys/module/simple_gpu_algorithm/parameters/simple_gpu_activate";
    public final String SIMPLE_GPU_LAZINESS = "/sys/module/simple_gpu_algorithm/parameters/simple_laziness";
    public final String SIMPLE_RAMP_THRESHOLD = "/sys/module/simple_gpu_algorithm/parameters/simple_ramp_threshold";

    public final String[][] GPU_ARRAY = {GPU_2D_CUR_FREQ_ARRAY,
            GPU_2D_MAX_FREQ_ARRAY, GPU_2D_AVAILABLE_FREQS_ARRAY,
            GPU_2D_SCALING_GOVERNOR_ARRAY, GPU_CUR_FREQ_ARRAY,
            GPU_MAX_FREQ_ARRAY, GPU_AVAILABLE_FREQS_ARRAY,
            GPU_SCALING_GOVERNOR_ARRAY,
            {SIMPLE_GPU_ACTIVATE, SIMPLE_GPU_LAZINESS, SIMPLE_RAMP_THRESHOLD}};

    // Screen
    public final String SCREEN_KCAL = "/sys/devices/platform/kcal_ctrl.0";
    public final String SCREEN_KCAL_CTRL = SCREEN_KCAL + "/kcal";
    public final String SCREEN_KCAL_CTRL_CTRL = SCREEN_KCAL + "/kcal_ctrl";
    public final String SCREEN_KCAL_CTRL_ENABLE = SCREEN_KCAL + "/kcal_enable";
    public final String SCREEN_KCAL_CTRL_MIN = SCREEN_KCAL + "/kcal_min";
    public final String SCREEN_KCAL_CTRL_INVERT = SCREEN_KCAL + "/kcal_invert";
    public final String SCREEN_KCAL_CTRL_SAT = SCREEN_KCAL + "/kcal_sat";
    public final String SCREEN_KCAL_CTRL_HUE = SCREEN_KCAL + "/kcal_hue";
    public final String SCREEN_KCAL_CTRL_VAL = SCREEN_KCAL + "/kcal_val";
    public final String SCREEN_KCAL_CTRL_CONT = SCREEN_KCAL + "/kcal_cont";

    public final String[] SCREEN_KCAL_CTRL_NEW_ARRAY = {SCREEN_KCAL_CTRL_ENABLE, SCREEN_KCAL_CTRL_INVERT, SCREEN_KCAL_CTRL_SAT,
            SCREEN_KCAL_CTRL_HUE, SCREEN_KCAL_CTRL_VAL, SCREEN_KCAL_CTRL_CONT};

    public final String SCREEN_DIAG0 = "/sys/devices/platform/DIAG0.0";
    public final String SCREEN_DIAG0_POWER = SCREEN_DIAG0 + "/power_rail";
    public final String SCREEN_DIAG0_POWER_CTRL = SCREEN_DIAG0 + "/power_rail_ctrl";

    public final String SCREEN_COLOR = "/sys/class/misc/colorcontrol";
    public final String SCREEN_COLOR_CONTROL = SCREEN_COLOR + "/multiplier";
    public final String SCREEN_COLOR_CONTROL_CTRL = SCREEN_COLOR + "/safety_enabled";

    public final String SCREEN_SAMOLED_COLOR = "/sys/class/misc/samoled_color";
    public final String SCREEN_SAMOLED_COLOR_RED = SCREEN_SAMOLED_COLOR + "/red_multiplier";
    public final String SCREEN_SAMOLED_COLOR_GREEN = SCREEN_SAMOLED_COLOR + "/green_multiplier";
    public final String SCREEN_SAMOLED_COLOR_BLUE = SCREEN_SAMOLED_COLOR + "/blue_multiplier";

    public final String[] SCREEN_KCAL_ARRAY = {SCREEN_KCAL_CTRL, SCREEN_DIAG0_POWER, SCREEN_COLOR_CONTROL,
            SCREEN_SAMOLED_COLOR_RED};

    public final String[] SCREEN_KCAL_CTRL_ARRAY = {SCREEN_KCAL_CTRL_CTRL, SCREEN_KCAL_CTRL_MIN,
            SCREEN_DIAG0_POWER_CTRL, SCREEN_COLOR_CONTROL_CTRL};

    public final String LM3630_BACKLIGHT_DIMMER = "/sys/module/lm3630_bl/parameters/backlight_dimmer";
    public final String LM3630_MIN_BRIGHTNESS = "/sys/module/lm3630_bl/parameters/min_brightness";
    public final String LM3630_BACKLIGHT_DIMMER_THRESHOLD = "/sys/module/lm3630_bl/parameters/backlight_threshold";
    public final String LM3630_BACKLIGHT_DIMMER_OFFSET = "/sys/module/lm3630_bl/parameters/backlight_offset";

    public final String MSM_BACKLIGHT_DIMMER = "/sys/module/msm_fb/parameters/backlight_dimmer";

    public final String[] MIN_BRIGHTNESS_ARRAY = {LM3630_MIN_BRIGHTNESS, MSM_BACKLIGHT_DIMMER};

    public final String[][] SCREEN_ARRAY = {SCREEN_KCAL_ARRAY, SCREEN_KCAL_CTRL_ARRAY, MIN_BRIGHTNESS_ARRAY,
            SCREEN_KCAL_CTRL_NEW_ARRAY,
            {LM3630_BACKLIGHT_DIMMER, LM3630_BACKLIGHT_DIMMER_THRESHOLD, LM3630_BACKLIGHT_DIMMER_OFFSET}};

    // Wake

    // DT2W
    public final String LGE_TOUCH_DT2W = "/sys/devices/virtual/input/lge_touch/dt_wake_enabled";
    public final String LGE_TOUCH_CORE_DT2W = "/sys/module/lge_touch_core/parameters/doubletap_to_wake";
    public final String DT2W = "/sys/android_touch/doubletap2wake";
    public final String TOUCH_PANEL_DT2W = "/proc/touchpanel/double_tap_enable";
    public final String DT2W_WAKEUP_GESTURE = "/sys/devices/virtual/input/input1/wakeup_gesture";

    public final String[] DT2W_ARRAY = {LGE_TOUCH_DT2W, LGE_TOUCH_CORE_DT2W, DT2W, TOUCH_PANEL_DT2W, DT2W_WAKEUP_GESTURE};

    // S2W
    public final String S2W_ONLY = "/sys/android_touch/s2w_s2sonly";
    public final String SW2 = "/sys/android_touch/sweep2wake";

    public final String[] S2W_ARRY = {S2W_ONLY, SW2};

    // T2W
    public final String TSP_T2W = "/sys/devices/f9966000.i2c/i2c-1/1-004a/tsp";
    public final String TOUCHWAKE_T2W = "/sys/class/misc/touchwake/enabled";

    public final String[] T2W_ARRAY = {TSP_T2W, TOUCHWAKE_T2W};

    public final String WAKE_TIMEOUT = "/sys/android_touch/wake_timeout";
    public final String POWER_KEY_SUSPEND = "/sys/module/qpnp_power_on/parameters/pwrkey_suspend";

    public final String[][] WAKE_ARRAY = {DT2W_ARRAY, S2W_ARRY, T2W_ARRAY, {WAKE_TIMEOUT, POWER_KEY_SUSPEND}};

    // Sound
    public final String SOUND_CONTROL_ENABLE = "/sys/module/snd_soc_wcd9320/parameters/enable_fs";
    public final String HEADPHONE_GAIN = "/sys/kernel/sound_control_3/gpl_headphone_gain";
    public final String HANDSET_MICROPONE_GAIN = "/sys/kernel/sound_control_3/gpl_mic_gain";
    public final String CAM_MICROPHONE_GAIN = "/sys/kernel/sound_control_3/gpl_cam_mic_gain";
    public final String SPEAKER_GAIN = "/sys/kernel/sound_control_3/gpl_speaker_gain";
    public final String HEADPHONE_POWERAMP_GAIN = "/sys/kernel/sound_control_3/gpl_headphone_pa_gain";

    public final String[] SOUND_ARRAY = {SOUND_CONTROL_ENABLE, HEADPHONE_GAIN, HANDSET_MICROPONE_GAIN,
            CAM_MICROPHONE_GAIN, SPEAKER_GAIN, HEADPHONE_POWERAMP_GAIN};

    // Battery
    public final String FORCE_FAST_CHARGE = "/sys/kernel/fast_charge/force_fast_charge";
    public final String BLX = "/sys/devices/virtual/misc/batterylifeextender/charging_limit";

    public final String[] BATTERY_ARRAY = {FORCE_FAST_CHARGE, BLX};

    // I/O
    public final String IO_INTERNAL_SCHEDULER = "/sys/block/mmcblk0/queue/scheduler";
    public final String IO_EXTERNAL_SCHEDULER = "/sys/block/mmcblk1/queue/scheduler";
    public final String IO_INTERNAL_SCHEDULER_TUNABLE = "/sys/block/mmcblk0/queue/iosched";
    public final String IO_EXTERNAL_SCHEDULER_TUNABLE = "/sys/block/mmcblk1/queue/iosched";
    public final String IO_INTERNAL_READ_AHEAD = "/sys/block/mmcblk0/queue/read_ahead_kb";
    public final String IO_EXTERNAL_READ_AHEAD = "/sys/block/mmcblk1/queue/read_ahead_kb";

    public final String[] IO_ARRAY = {IO_INTERNAL_SCHEDULER, IO_EXTERNAL_SCHEDULER, IO_INTERNAL_SCHEDULER_TUNABLE,
            IO_EXTERNAL_SCHEDULER_TUNABLE, IO_INTERNAL_READ_AHEAD, IO_EXTERNAL_READ_AHEAD};

    // Kernel Samepage Merging
    public final String KSM_FOLDER = "/sys/kernel/mm/ksm";
    public final String KSM_FULL_SCANS = KSM_FOLDER + "/full_scans";
    public final String KSM_PAGES_SHARED = KSM_FOLDER + "/pages_shared";
    public final String KSM_PAGES_SHARING = KSM_FOLDER + "/pages_sharing";
    public final String KSM_PAGES_UNSHARED = KSM_FOLDER + "/pages_unshared";
    public final String KSM_PAGES_VOLATILE = KSM_FOLDER + "/pages_volatile";
    public final String KSM_RUN = KSM_FOLDER + "/run";
    public final String KSM_DEFERRED_TIMER = KSM_FOLDER + "/deferred_timer";
    public final String KSM_PAGES_TO_SCAN = KSM_FOLDER + "/pages_to_scan";
    public final String KSM_SLEEP_MILLISECONDS = KSM_FOLDER + "/sleep_millisecs";

    public final String[] KSM_INFOS = {KSM_FULL_SCANS, KSM_PAGES_SHARED, KSM_PAGES_SHARING,
            KSM_PAGES_UNSHARED, KSM_PAGES_VOLATILE};

    // Low Memory Killer
    public final String LMK_MINFREE = "/sys/module/lowmemorykiller/parameters/minfree";

    // Virtual Memory
    public final String VM_PATH = "/proc/sys/vm";

    public final String[] SUPPORTED_VM = {"dirty_ratio", "dirty_background_ratio", "dirty_expire_centisecs",
            "dirty_writeback_centisecs", "min_free_kbytes", "overcommit_ratio", "swappiness",
            "vfs_cache_pressure", "laptop_mode"};

    // Misc
    // TCP
    public final String TCP_AVAILABLE_CONGESTIONS = "/proc/sys/net/ipv4/tcp_available_congestion_control";

    // Vibration
    public final String VIBRATION_ENABLE = "/sys/class/timed_output/vibrator/enable";
    public final String[] VIBRATION_ARRAY = {
            "/sys/vibrator/pwmvalue",
            "/sys/class/timed_output/vibrator/amp",
            "/sys/class/timed_output/vibrator/vtg_level",
            "/sys/devices/platform/tspdrv/nforce_timed",
            "/sys/class/timed_output/vibrator/pwm_value",
            "/sys/devices/i2c-3/3-0033/vibrator/vib0/vib_duty_cycle",
            "/sys/devices/virtual/timed_output/vibrator/voltage_level",
            "/sys/devices/virtual/timed_output/vibrator/pwm_value_1p"
    };

    public final int[][] VIBRATION_MAX_MIN_ARRAY = {
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
    public final String SMB135X_WAKELOCK = "/sys/module/smb135x_charger/parameters/use_wlock";
    public final String SENSOR_IND_WAKELOCK = "/sys/module/wakeup/parameters/enable_si_ws";
    public final String MSM_HSIC_HOST_WAKELOCK = "/sys/module/wakeup/parameters/enable_msm_hsic_ws";

    // Logging
    public final String LOGGER_ENABLED = "/sys/module/logger/parameters/enabled";

    public final String[][] MISC_ARRAY = {{TCP_AVAILABLE_CONGESTIONS, SMB135X_WAKELOCK, SENSOR_IND_WAKELOCK,
            MSM_HSIC_HOST_WAKELOCK, LOGGER_ENABLED}, VIBRATION_ARRAY};

    // Build prop
    public final String BUILD_PROP = "/system/build.prop";

}
