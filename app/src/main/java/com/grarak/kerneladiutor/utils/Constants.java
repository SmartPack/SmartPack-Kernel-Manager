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
import com.grarak.kerneladiutor.elements.DAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 30.11.14.
 */
public interface Constants {

    String TAG = "Kernel Adiutor";
    String VERSION_NAME = BuildConfig.VERSION_NAME;
    int VERSION_CODE = BuildConfig.VERSION_CODE;
    String PREF_NAME = "prefs";
    String GAMMA_URL = "https://raw.githubusercontent.com/Grarak/KernelAdiutor/master/gamma_profiles.json";
    List<DAdapter.DView> ITEMS = new ArrayList<>();
    List<DAdapter.DView> VISIBLE_ITEMS = new ArrayList<>();

    // Kernel Informations
    String PROC_VERSION = "/proc/version";
    String PROC_CPUINFO = "/proc/cpuinfo";
    String PROC_MEMINFO = "/proc/meminfo";

    // CPU
    String CPU_CUR_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq";
    String CPU_TEMP_ZONE0 = "/sys/class/thermal/thermal_zone0/temp";
    String CPU_TEMP_ZONE1 = "/sys/class/thermal/thermal_zone1/temp";
    String CPU_CORE_ONLINE = "/sys/devices/system/cpu/cpu%d/online";
    String CPU_MAX_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_max_freq";
    String CPU_MAX_FREQ_KT = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_max_freq_kt";
    String CPU_ENABLE_OC = "/sys/devices/system/cpu/cpu%d/cpufreq/enable_oc";
    String CPU_MIN_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_min_freq";
    String CPU_MAX_SCREEN_OFF_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/screen_off_max_freq";
    String CPU_MSM_CPUFREQ_LIMIT = "/sys/kernel/msm_cpufreq_limit/cpufreq_limit";
    String CPU_AVAILABLE_FREQS = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_available_frequencies";
    String CPU_TIME_STATE = "/sys/devices/system/cpu/cpufreq/stats/cpu%d/time_in_state";
    String CPU_TIME_STATE_2 = "/sys/devices/system/cpu/cpu%d/cpufreq/stats/time_in_state";
    String CPU_SCALING_GOVERNOR = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_governor";
    String CPU_AVAILABLE_GOVERNORS = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";

    String CPU_GOVERNOR_TUNABLES = "/sys/devices/system/cpu/cpufreq";
    String CPU_GOVERNOR_TUNABLES_CORE = "/sys/devices/system/cpu/cpu%d/cpufreq";

    String CPU_MC_POWER_SAVING = "/sys/devices/system/cpu/sched_mc_power_savings";
    String CPU_WQ_POWER_SAVING = "/sys/module/workqueue/parameters/power_efficient";
    String CPU_AVAILABLE_CFS_SCHEDULERS = "/sys/devices/system/cpu/sched_balance_policy/available_sched_balance_policy";
    String CPU_CURRENT_CFS_SCHEDULER = "/sys/devices/system/cpu/sched_balance_policy/current_sched_balance_policy";

    String CPU_QUIET = "/sys/devices/system/cpu/cpuquiet";
    String CPU_QUIET_ENABLE = CPU_QUIET + "/cpuquiet_driver/enabled";
    String CPU_QUIET_AVAILABLE_GOVERNORS = CPU_QUIET + "/available_governors";
    String CPU_QUIET_CURRENT_GOVERNOR = CPU_QUIET + "/current_governor";

    String CPU_BOOST = "/sys/module/cpu_boost/parameters";
    String CPU_BOOST_ENABLE = CPU_BOOST + "/cpu_boost";
    String CPU_BOOST_ENABLE_2 = CPU_BOOST + "/cpuboost_enable";
    String CPU_BOOST_DEBUG_MASK = CPU_BOOST + "/debug_mask";
    String CPU_BOOST_MS = CPU_BOOST + "/boost_ms";
    String CPU_BOOST_SYNC_THRESHOLD = CPU_BOOST + "/sync_threshold";
    String CPU_BOOST_INPUT_MS = CPU_BOOST + "/input_boost_ms";
    String CPU_BOOST_INPUT_BOOST_FREQ = CPU_BOOST + "/input_boost_freq";

    String[] CPU_ARRAY = {CPU_CUR_FREQ, CPU_TEMP_ZONE0, CPU_TEMP_ZONE1, CPU_CORE_ONLINE, CPU_MAX_FREQ, CPU_MAX_FREQ_KT, CPU_ENABLE_OC,
            CPU_MIN_FREQ, CPU_MAX_SCREEN_OFF_FREQ, CPU_MSM_CPUFREQ_LIMIT, CPU_AVAILABLE_FREQS, CPU_TIME_STATE, CPU_SCALING_GOVERNOR,
            CPU_AVAILABLE_GOVERNORS, CPU_GOVERNOR_TUNABLES, CPU_GOVERNOR_TUNABLES_CORE, CPU_MC_POWER_SAVING, CPU_WQ_POWER_SAVING,
            CPU_AVAILABLE_CFS_SCHEDULERS, CPU_CURRENT_CFS_SCHEDULER, CPU_QUIET, CPU_BOOST};

    // CPU Voltage
    String CPU_VOLTAGE = "/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table";
    String CPU_VDD_VOLTAGE = "/sys/devices/system/cpu/cpu0/cpufreq/vdd_levels";
    String CPU_FAUX_VOLTAGE = "/sys/devices/system/cpu/cpufreq/vdd_table/vdd_levels";

    String CPU_OVERRIDE_VMIN = "/sys/devices/system/cpu/cpu0/cpufreq/override_vmin";

    String[] CPU_VOLTAGE_ARRAY = {CPU_VOLTAGE, CPU_VDD_VOLTAGE, CPU_FAUX_VOLTAGE, CPU_OVERRIDE_VMIN};

    // CPU Hotplug
    String HOTPLUG_MPDEC = "mpdecision";

    String HOTPLUG_INTELLI_PLUG = "/sys/module/intelli_plug/parameters";
    String HOTPLUG_INTELLI_PLUG_ENABLE = HOTPLUG_INTELLI_PLUG + "/intelli_plug_active";
    String HOTPLUG_INTELLI_PLUG_PROFILE = HOTPLUG_INTELLI_PLUG + "/nr_run_profile_sel";
    String HOTPLUG_INTELLI_PLUG_ECO = HOTPLUG_INTELLI_PLUG + "/eco_mode_active";
    String HOTPLUG_INTELLI_PLUG_TOUCH_BOOST = HOTPLUG_INTELLI_PLUG + "/touch_boost_active";
    String HOTPLUG_INTELLI_PLUG_HYSTERESIS = HOTPLUG_INTELLI_PLUG + "/nr_run_hysteresis";
    String HOTPLUG_INTELLI_PLUG_THRESHOLD = HOTPLUG_INTELLI_PLUG + "/cpu_nr_run_threshold";
    String HOTPLUG_INTELLI_PLUG_SCREEN_OFF_MAX = HOTPLUG_INTELLI_PLUG + "/screen_off_max";

    String HOTPLUG_INTELLI_PLUG_5 = "/sys/kernel/intelli_plug";
    String HOTPLUG_INTELLI_PLUG_5_ENABLE = HOTPLUG_INTELLI_PLUG_5 + "/intelli_plug_active";
    String HOTPLUG_INTELLI_PLUG_5_DEBUG = HOTPLUG_INTELLI_PLUG_5 + "/debug_intelli_plug";
    String HOTPLUG_INTELLI_PLUG_5_PROFILE = HOTPLUG_INTELLI_PLUG_5 + "/full_mode_profile";
    String HOTPLUG_INTELLI_PLUG_5_SUSPEND = HOTPLUG_INTELLI_PLUG_5 + "/hotplug_suspend";
    String HOTPLUG_INTELLI_PLUG_5_CPUS_BOOSTED = HOTPLUG_INTELLI_PLUG_5 + "/cpus_boosted";
    String HOTPLUG_INTELLI_PLUG_5_HYSTERESIS = HOTPLUG_INTELLI_PLUG_5 + "/nr_run_hysteresis";
    String HOTPLUG_INTELLI_PLUG_5_MIN_CPUS_ONLINE = HOTPLUG_INTELLI_PLUG_5 + "/min_cpus_online";
    String HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE = HOTPLUG_INTELLI_PLUG_5 + "/max_cpus_online";
    String HOTPLUG_INTELLI_PLUG_5_MAX_CPUS_ONLINE_SUSP = HOTPLUG_INTELLI_PLUG_5 + "/max_cpus_online_susp";
    String HOTPLUG_INTELLI_PLUG_5_SUSPEND_DEFER_TIME = HOTPLUG_INTELLI_PLUG_5 + "/suspend_defer_time";
    String HOTPLUG_INTELLI_PLUG_5_DEFER_SAMPLING = HOTPLUG_INTELLI_PLUG_5 + "/def_sampling_ms";
    String HOTPLUG_INTELLI_PLUG_5_BOOST_LOCK_DURATION = HOTPLUG_INTELLI_PLUG_5 + "/boost_lock_duration";
    String HOTPLUG_INTELLI_PLUG_5_DOWN_LOCK_DURATION = HOTPLUG_INTELLI_PLUG_5 + "/down_lock_duration";
    String HOTPLUG_INTELLI_PLUG_5_THRESHOLD = HOTPLUG_INTELLI_PLUG_5 + "/cpu_nr_run_threshold";
    String HOTPLUG_INTELLI_PLUG_5_FSHIFT = HOTPLUG_INTELLI_PLUG_5 + "/nr_fshift";
    String HOTPLUG_INTELLI_PLUG_5_SCREEN_OFF_MAX = HOTPLUG_INTELLI_PLUG_5 + "/screen_off_max";

    String[] INTELLIPLUG_ARRAY = {HOTPLUG_INTELLI_PLUG, HOTPLUG_INTELLI_PLUG_5};

    String HOTPLUG_BLU_PLUG = "/sys/module/blu_plug/parameters";
    String HOTPLUG_BLU_PLUG_ENABLE = HOTPLUG_BLU_PLUG + "/enabled";
    String HOTPLUG_BLU_PLUG_POWERSAVER_MODE = HOTPLUG_BLU_PLUG + "/powersaver_mode";
    String HOTPLUG_BLU_PLUG_MIN_ONLINE = HOTPLUG_BLU_PLUG + "/min_online";
    String HOTPLUG_BLU_PLUG_MAX_ONLINE = HOTPLUG_BLU_PLUG + "/max_online";
    String HOTPLUG_BLU_PLUG_MAX_CORES_SCREEN_OFF = HOTPLUG_BLU_PLUG + "/max_cores_screenoff";
    String HOTPLUG_BLU_PLUG_MAX_FREQ_SCREEN_OFF = HOTPLUG_BLU_PLUG + "/max_freq_screenoff";
    String HOTPLUG_BLU_PLUG_UP_THRESHOLD = HOTPLUG_BLU_PLUG + "/up_threshold";
    String HOTPLUG_BLU_PLUG_UP_TIMER_CNT = HOTPLUG_BLU_PLUG + "/up_timer_cnt";
    String HOTPLUG_BLU_PLUG_DOWN_TIMER_CNT = HOTPLUG_BLU_PLUG + "/down_timer_cnt";

    String[] BLU_PLUG_ARRAY = {HOTPLUG_BLU_PLUG};

    String HOTPLUG_MSM = "/sys/module/msm_hotplug";
    String HOTPLUG_MSM_ENABLE = HOTPLUG_MSM + "/enabled";
    String HOTPLUG_MSM_ENABLE_2 = HOTPLUG_MSM + "/msm_enabled";
    String HOTPLUG_MSM_DEBUG_MASK = HOTPLUG_MSM + "/parameters/debug_mask";
    String HOTPLUG_MSM_MIN_CPUS_ONLINE = HOTPLUG_MSM + "/min_cpus_online";
    String HOTPLUG_MSM_MAX_CPUS_ONLINE = HOTPLUG_MSM + "/max_cpus_online";
    String HOTPLUG_MSM_CPUS_BOOSTED = HOTPLUG_MSM + "/cpus_boosted";
    String HOTPLUG_MSM_MAX_CPUS_ONLINE_SUSP = HOTPLUG_MSM + "/max_cpus_online_susp";
    String HOTPLUG_MSM_BOOST_LOCK_DURATION = HOTPLUG_MSM + "/boost_lock_duration";
    String HOTPLUG_MSM_DOWN_LOCK_DURATION = HOTPLUG_MSM + "/down_lock_duration";
    String HOTPLUG_MSM_HISTORY_SIZE = HOTPLUG_MSM + "/history_size";
    String HOTPLUG_MSM_UPDATE_RATE = HOTPLUG_MSM + "/update_rate";
    String HOTPLUG_MSM_UPDATE_RATES = HOTPLUG_MSM + "/update_rates";
    String HOTPLUG_MSM_FAST_LANE_LOAD = HOTPLUG_MSM + "/fast_lane_load";
    String HOTPLUG_MSM_FAST_LANE_MIN_FREQ = HOTPLUG_MSM + "/fast_lane_min_freq";
    String HOTPLUG_MSM_OFFLINE_LOAD = HOTPLUG_MSM + "/offline_load";
    String HOTPLUG_MSM_IO_IS_BUSY = HOTPLUG_MSM + "/io_is_busy";
    String HOTPLUG_MSM_HP_IO_IS_BUSY = HOTPLUG_MSM + "/hp_io_is_busy";
    String HOTPLUG_MSM_SUSPEND_MAX_CPUS = HOTPLUG_MSM + "/suspend_max_cpus";
    String HOTPLUG_MSM_SUSPEND_FREQ = HOTPLUG_MSM + "/suspend_freq";
    String HOTPLUG_MSM_SUSPEND_MAX_FREQ = HOTPLUG_MSM + "/suspend_max_freq";
    String HOTPLUG_MSM_SUSPEND_DEFER_TIME = HOTPLUG_MSM + "/suspend_defer_time";

    String[] HOTPLUG_MSM_ARRAY = {HOTPLUG_MSM};

    String MAKO_HOTPLUG = "/sys/class/misc/mako_hotplug_control";
    String MAKO_HOTPLUG_ENABLED = MAKO_HOTPLUG + "/enabled";
    String MAKO_HOTPLUG_CORES_ON_TOUCH = MAKO_HOTPLUG + "/cores_on_touch";
    String MAKO_HOTPLUG_CPUFREQ_UNPLUG_LIMIT = MAKO_HOTPLUG + "/cpufreq_unplug_limit";
    String MAKO_HOTPLUG_FIRST_LEVEL = MAKO_HOTPLUG + "/first_level";
    String MAKO_HOTPLUG_HIGH_LOAD_COUNTER = MAKO_HOTPLUG + "/high_load_counter";
    String MAKO_HOTPLUG_LOAD_THRESHOLD = MAKO_HOTPLUG + "/load_threshold";
    String MAKO_HOTPLUG_MAX_LOAD_COUNTER = MAKO_HOTPLUG + "/max_load_counter";
    String MAKO_HOTPLUG_MIN_TIME_CPU_ONLINE = MAKO_HOTPLUG + "/min_time_cpu_online";
    String MAKO_HOTPLUG_MIN_CORES_ONLINE = MAKO_HOTPLUG + "/min_cores_online";
    String MAKO_HOTPLUG_TIMER = MAKO_HOTPLUG + "/timer";
    String MAKO_HOTPLUG_SUSPEND_FREQ = MAKO_HOTPLUG + "/suspend_frequency";

    String[] MAKO_HOTPLUG_ARRAY = {MAKO_HOTPLUG};

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
    String ALUCARD_HOTPLUG_ENABLE = ALUCARD_HOTPLUG + "/hotplug_enable";
    String ALUCARD_HOTPLUG_HP_IO_IS_BUSY = ALUCARD_HOTPLUG + "/hp_io_is_busy";
    String ALUCARD_HOTPLUG_SAMPLING_RATE = ALUCARD_HOTPLUG + "/hotplug_sampling_rate";
    String ALUCARD_HOTPLUG_SUSPEND = ALUCARD_HOTPLUG + "/hotplug_suspend";
    String ALUCARD_HOTPLUG_MIN_CPUS_ONLINE = ALUCARD_HOTPLUG + "/min_cpus_online";
    String ALUCARD_HOTPLUG_MAX_CORES_LIMIT = ALUCARD_HOTPLUG + "/maxcoreslimit";
    String ALUCARD_HOTPLUG_MAX_CORES_LIMIT_SLEEP = ALUCARD_HOTPLUG + "/maxcoreslimit_sleep";
    String ALUCARD_HOTPLUG_CPU_DOWN_RATE = ALUCARD_HOTPLUG + "/cpu_down_rate";
    String ALUCARD_HOTPLUG_CPU_UP_RATE = ALUCARD_HOTPLUG + "/cpu_up_rate";

    String[] ALUCARD_HOTPLUG_ARRAY = {ALUCARD_HOTPLUG};

    String HOTPLUG_THUNDER_PLUG = "/sys/kernel/thunderplug";
    String HOTPLUG_THUNDER_PLUG_ENABLE = HOTPLUG_THUNDER_PLUG + "/hotplug_enabled";
    String HOTPLUG_THUNDER_PLUG_SUSPEND_CPUS = HOTPLUG_THUNDER_PLUG + "/suspend_cpus";
    String HOTPLUG_THUNDER_PLUG_ENDURANCE_LEVEL = HOTPLUG_THUNDER_PLUG + "/endurance_level";
    String HOTPLUG_THUNDER_PLUG_SAMPLING_RATE = HOTPLUG_THUNDER_PLUG + "/sampling_rate";
    String HOTPLUG_THUNDER_PLUG_LOAD_THRESHOLD = HOTPLUG_THUNDER_PLUG + "/load_threshold";
    String HOTPLUG_THUNDER_PLUG_TOUCH_BOOST = HOTPLUG_THUNDER_PLUG + "/touch_boost";

    String[] HOTPLUG_THUNDER_PLUG_ARRAY = {HOTPLUG_THUNDER_PLUG};

    String HOTPLUG_ZEN_DECISION = "/sys/kernel/zen_decision";
    String HOTPLUG_ZEN_DECISION_ENABLE = HOTPLUG_ZEN_DECISION + "/enabled";
    String HOTPLUG_ZEN_DECISION_WAKE_WAIT_TIME = HOTPLUG_ZEN_DECISION + "/wake_wait_time";
    String HOTPLUG_ZEN_DECISION_BAT_THRESHOLD_IGNORE = HOTPLUG_ZEN_DECISION + "/bat_threshold_ignore";

    String[] HOTPLUG_ZEN_DECISION_ARRAY = {HOTPLUG_ZEN_DECISION};

    String HOTPLUG_AUTOSMP_PARAMETERS = "/sys/module/autosmp/parameters";
    String HOTPLUG_AUTOSMP_CONF = "/sys/module/autosmp/conf";
    String HOTPLUG_AUTOSMP_ENABLE = HOTPLUG_AUTOSMP_PARAMETERS + "/enabled";
    String HOTPLUG_AUTOSMP_CPUFREQ_DOWN = HOTPLUG_AUTOSMP_CONF + "/cpufreq_down";
    String HOTPLUG_AUTOSMP_CPUFREQ_UP = HOTPLUG_AUTOSMP_CONF + "/cpufreq_up";
    String HOTPLUG_AUTOSMP_CYCLE_DOWN = HOTPLUG_AUTOSMP_CONF + "/cycle_down";
    String HOTPLUG_AUTOSMP_CYCLE_UP = HOTPLUG_AUTOSMP_CONF + "/cycle_up";
    String HOTPLUG_AUTOSMP_DELAY = HOTPLUG_AUTOSMP_CONF + "/delay";
    String HOTPLUG_AUTOSMP_MAX_CPUS = HOTPLUG_AUTOSMP_CONF + "/max_cpus";
    String HOTPLUG_AUTOSMP_MIN_CPUS = HOTPLUG_AUTOSMP_CONF + "/min_cpus";
    String HOTPLUG_AUTOSMP_SCROFF_SINGLE_CORE = HOTPLUG_AUTOSMP_CONF + "/scroff_single_core";

    String[] HOTPLUG_AUTOSMP_ARRAY = {HOTPLUG_AUTOSMP_PARAMETERS, HOTPLUG_AUTOSMP_CONF};

    String[][] CPU_HOTPLUG_ARRAY = {{HOTPLUG_MPDEC}, INTELLIPLUG_ARRAY, BLU_PLUG_ARRAY, HOTPLUG_MSM_ARRAY, MAKO_HOTPLUG_ARRAY,
            MB_HOTPLUG_ARRAY, ALUCARD_HOTPLUG_ARRAY, HOTPLUG_THUNDER_PLUG_ARRAY, HOTPLUG_ZEN_DECISION_ARRAY, HOTPLUG_AUTOSMP_ARRAY};

    // Thermal
    String THERMALD = "thermald";

    String MSM_THERMAL = "/sys/module/msm_thermal";
    String MSM_THERMAL_V2 = "/sys/module/msm_thermal_v2";
    String PARAMETERS_ENABLED = "parameters/enabled";
    String PARAMETERS_INTELLI_ENABLED = "parameters/intelli_enabled";
    String PARAMETERS_THERMAL_DEBUG_MODE = "parameters/thermal_debug_mode";
    String CORE_CONTROL_ENABLED = "core_control/enabled";
    String CORE_CONTROL_ENABLED_2 = "core_control/core_control";
    String VDD_RESTRICTION_ENABLED = "vdd_restriction/enabled";
    String PARAMETERS_LIMIT_TEMP_DEGC = "parameters/limit_temp_degC";
    String PARAMETERS_CORE_LIMIT_TEMP_DEGC = "parameters/core_limit_temp_degC";
    String PARAMETERS_CORE_TEMP_HYSTERESIS_DEGC = "parameters/core_temp_hysteresis_degC";
    String PARAMETERS_FREQ_STEP = "parameters/freq_step";
    String PARAMETERS_IMMEDIATELY_LIMIT_STOP = "parameters/immediately_limit_stop";
    String PARAMETERS_POLL_MS = "parameters/poll_ms";
    String PARAMETERS_TEMP_HYSTERESIS_DEGC = "parameters/temp_hysteresis_degC";
    String PARAMETERS_THERMAL_LIMIT_LOW = "parameters/thermal_limit_low";
    String PARAMETERS_THERMAL_LIMIT_HIGH = "parameters/thermal_limit_high";
    String PARAMETERS_TEMP_SAFETY = "parameters/temp_safety";
    String MSM_THERMAL_TEMP_THROTTLE = MSM_THERMAL + "/" + PARAMETERS_ENABLED;
    String MSM_THERMAL_THROTTLE_TEMP = MSM_THERMAL + "/parameters/throttle_temp";
    String MSM_THERMAL_TEMP_MAX = MSM_THERMAL + "/parameters/temp_max";
    String MSM_THERMAL_TEMP_THRESHOLD = MSM_THERMAL + "/parameters/temp_threshold";
    String MSM_THERMAL_FREQ_LIMIT_DEBUG = MSM_THERMAL + "/parameters/freq_limit_debug";
    String MSM_THERMAL_MIN_FREQ_INDEX = MSM_THERMAL + "/parameters/min_freq_index";
    String TEMPCONTROL_TEMP_LIMIT = "/sys/class/misc/tempcontrol/templimit";

    String[] TEMP_LIMIT_ARRAY = {MSM_THERMAL_THROTTLE_TEMP, MSM_THERMAL_TEMP_MAX, MSM_THERMAL_TEMP_THRESHOLD,
            MSM_THERMAL_FREQ_LIMIT_DEBUG, MSM_THERMAL_MIN_FREQ_INDEX, TEMPCONTROL_TEMP_LIMIT};

    String MSM_THERMAL_CONF = "/sys/kernel/msm_thermal/conf";
    String CONF_ALLOWED_LOW_LOW = MSM_THERMAL_CONF + "/allowed_low_low";
    String CONF_ALLOWED_LOW_HIGH = MSM_THERMAL_CONF + "/allowed_low_high";
    String CONF_ALLOWED_LOW_FREQ = MSM_THERMAL_CONF + "/allowed_low_freq";
    String CONF_ALLOWED_MID_LOW = MSM_THERMAL_CONF + "/allowed_mid_low";
    String CONF_ALLOWED_MID_HIGH = MSM_THERMAL_CONF + "/allowed_mid_high";
    String CONF_ALLOWED_MID_FREQ = MSM_THERMAL_CONF + "/allowed_mid_freq";
    String CONF_ALLOWED_MAX_LOW = MSM_THERMAL_CONF + "/allowed_max_low";
    String CONF_ALLOWED_MAX_HIGH = MSM_THERMAL_CONF + "/allowed_max_high";
    String CONF_ALLOWED_MAX_FREQ = MSM_THERMAL_CONF + "/allowed_max_freq";
    String CONF_CHECK_INTERVAL_MS = MSM_THERMAL_CONF + "/check_interval_ms";
    String CONF_SHUTDOWN_TEMP = MSM_THERMAL_CONF + "/shutdown_temp";

    String[] THERMAL_ARRAY = {MSM_THERMAL, MSM_THERMAL_V2};

    String[][] THERMAL_ARRAYS = {THERMAL_ARRAY, TEMP_LIMIT_ARRAY, {MSM_THERMAL_CONF}};

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
    String GPU_SCALING_PWRSCALE_GOVERNOR = "/sys/class/kgsl/kgsl-3d0/pwrscale/trustzone/governor";
    String GPU_AVAILABLE_FDB00000_QCOM_GOVERNORS = "/sys/devices/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/devfreq/available_governors";

    String GPU_CUR_FDC00000_QCOM_FREQ = "/sys/devices/fdc00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/gpuclk";
    String GPU_MAX_FDC00000_QCOM_FREQ = "/sys/devices/fdc00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/max_gpuclk";
    String GPU_AVAILABLE_FDC00000_QCOM_FREQS = "/sys/devices/fdc00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/gpu_available_frequencies";
    String GPU_SCALING_FDC00000_QCOM_GOVERNOR = "/sys/devices/fdc00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/devfreq/governor";
    String GPU_AVAILABLE_FDC00000_QCOM_GOVERNORS = "/sys/devices/fdc00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/devfreq/available_governors";

    String GPU_CUR_SOC0_FDB00000_QCOM_FREQ = "/sys/devices/soc.0/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/gpuclk";
    String GPU_MAX_SOC0_FDB00000_QCOM_FREQ = "/sys/devices/soc.0/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/max_gpuclk";
    String GPU_AVAILABLE_SOC0_FDB00000_QCOM_FREQS = "/sys/devices/soc.0/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/gpu_available_frequencies";
    String GPU_SCALING_SOC0_FDB00000_QCOM_GOVERNOR = "/sys/devices/soc.0/fdb00000.qcom,kgsl-3d0/devfreq/fdb00000.qcom,kgsl-3d0/governor";
    String GPU_AVAILABLE_SOC0_FDB00000_QCOM_GOVERNORS = "/sys/devices/soc.0/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/devfreq/available_governors";

    String GPU_CUR_1C00000_QCOM_FREQ = "/sys/devices/soc.0/1c00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/max_gpuclk";
    String GPU_MAX_1C00000_QCOM_FREQ = "/sys/devices/soc.0/1c00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/max_gpuclk";
    String GPU_AVAILABLE_1C00000_QCOM_FREQ = "/sys/devices/soc.0/1c00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/gpu_available_frequencies";
    String GPU_SCALING_1C00000_QCOM_GOVERNOR = "/sys/devices/soc.0/1c00000.qcom,kgsl-3d0/devfreq/1c00000.qcom,kgsl-3d0/governor";
    String GPU_AVAILABLE_1C00000_QCOM_GOVERNORS = "/sys/devices/soc.0/1c00000.qcom,kgsl-3d0/devfreq/1c00000.qcom,kgsl-3d0/available_governors";

    String GPU_CUR_OMAP_FREQ = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/frequency";
    String GPU_MAX_OMAP_FREQ = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/frequency_limit";
    String GPU_AVAILABLE_OMAP_FREQS = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/frequency_list";
    String GPU_SCALING_OMAP_GOVERNOR = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/governor";
    String GPU_AVAILABLE_OMAP_GOVERNORS = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/governor_list";

    String[] GPU_2D_CUR_FREQ_ARRAY = {GPU_CUR_KGSL2D0_QCOM_FREQ};

    String[] GPU_2D_MAX_FREQ_ARRAY = {GPU_MAX_KGSL2D0_QCOM_FREQ};

    String[] GPU_2D_AVAILABLE_FREQS_ARRAY = {GPU_AVAILABLE_KGSL2D0_QCOM_FREQS};

    String[] GPU_2D_SCALING_GOVERNOR_ARRAY = {GPU_SCALING_KGSL2D0_QCOM_GOVERNOR};

    String[] GPU_CUR_FREQ_ARRAY = {GPU_CUR_KGSL3D0_QCOM_FREQ, GPU_CUR_FDB00000_QCOM_FREQ, GPU_CUR_FDC00000_QCOM_FREQ,
            GPU_CUR_SOC0_FDB00000_QCOM_FREQ, GPU_CUR_1C00000_QCOM_FREQ, GPU_CUR_OMAP_FREQ};

    String[] GPU_MAX_FREQ_ARRAY = {GPU_MAX_KGSL3D0_QCOM_FREQ, GPU_MAX_FDB00000_QCOM_FREQ, GPU_MAX_FDC00000_QCOM_FREQ,
            GPU_MAX_SOC0_FDB00000_QCOM_FREQ, GPU_MAX_1C00000_QCOM_FREQ, GPU_MAX_OMAP_FREQ};

    String[] GPU_AVAILABLE_FREQS_ARRAY = {GPU_AVAILABLE_KGSL3D0_QCOM_FREQS, GPU_AVAILABLE_FDB00000_QCOM_FREQS,
            GPU_AVAILABLE_SOC0_FDB00000_QCOM_FREQS, GPU_AVAILABLE_FDC00000_QCOM_FREQS, GPU_AVAILABLE_1C00000_QCOM_FREQ,
            GPU_AVAILABLE_OMAP_FREQS};

    String[] GPU_SCALING_GOVERNOR_ARRAY = {GPU_SCALING_KGSL3D0_QCOM_GOVERNOR, GPU_SCALING_FDB00000_QCOM_GOVERNOR,
            GPU_SCALING_PWRSCALE_GOVERNOR, GPU_SCALING_FDC00000_QCOM_GOVERNOR, GPU_SCALING_SOC0_FDB00000_QCOM_GOVERNOR,
            GPU_SCALING_1C00000_QCOM_GOVERNOR, GPU_SCALING_OMAP_GOVERNOR};

    String[] GPU_AVAILABLE_GOVERNORS_ARRAY = {GPU_AVAILABLE_FDB00000_QCOM_GOVERNORS, GPU_AVAILABLE_FDC00000_QCOM_GOVERNORS,
            GPU_AVAILABLE_SOC0_FDB00000_QCOM_GOVERNORS, GPU_AVAILABLE_1C00000_QCOM_GOVERNORS, GPU_AVAILABLE_OMAP_GOVERNORS};

    // Simple GPU
    String SIMPLE_GPU_PARAMETERS = "/sys/module/simple_gpu_algorithm/parameters";
    String SIMPLE_GPU_ACTIVATE = SIMPLE_GPU_PARAMETERS + "/simple_gpu_activate";
    String SIMPLE_GPU_LAZINESS = SIMPLE_GPU_PARAMETERS + "/simple_laziness";
    String SIMPLE_RAMP_THRESHOLD = SIMPLE_GPU_PARAMETERS + "/simple_ramp_threshold";

    // Adreno Idler
    String ADRENO_IDLER_PARAMETERS = "/sys/module/adreno_idler/parameters/";
    String ADRENO_IDLER_ACTIVATE = ADRENO_IDLER_PARAMETERS + "adreno_idler_active";
    String ADRENO_IDLER_DOWNDIFFERENTIAL = ADRENO_IDLER_PARAMETERS + "/adreno_idler_downdifferential";
    String ADRENO_IDLER_IDLEWAIT = ADRENO_IDLER_PARAMETERS + "/adreno_idler_idlewait";
    String ADRENO_IDLER_IDLEWORKLOAD = ADRENO_IDLER_PARAMETERS + "/adreno_idler_idleworkload";

    String[][] GPU_ARRAY = {GPU_2D_CUR_FREQ_ARRAY,
            GPU_2D_MAX_FREQ_ARRAY, GPU_2D_AVAILABLE_FREQS_ARRAY,
            GPU_2D_SCALING_GOVERNOR_ARRAY, GPU_CUR_FREQ_ARRAY,
            GPU_MAX_FREQ_ARRAY, GPU_AVAILABLE_FREQS_ARRAY,
            GPU_SCALING_GOVERNOR_ARRAY, {SIMPLE_GPU_PARAMETERS, ADRENO_IDLER_PARAMETERS}};

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

    String SCREEN_FB0_RGB = "/sys/class/graphics/fb0/rgb";

    String[] SCREEN_RGB_ARRAY = {SCREEN_KCAL_CTRL, SCREEN_DIAG0_POWER, SCREEN_COLOR_CONTROL, SCREEN_SAMOLED_COLOR, SCREEN_FB0_RGB};

    String[] SCREEN_RGB_CTRL_ARRAY = {SCREEN_KCAL_CTRL_ENABLE, SCREEN_KCAL_CTRL_CTRL,
            SCREEN_DIAG0_POWER_CTRL, SCREEN_COLOR_CONTROL_CTRL};

    String SCREEN_HBM = "/sys/devices/virtual/graphics/fb0/hbm";

    // Gamma
    String K_GAMMA_R = "/sys/devices/platform/mipi_lgit.1537/kgamma_r";
    String K_GAMMA_G = "/sys/devices/platform/mipi_lgit.1537/kgamma_g";
    String K_GAMMA_B = "/sys/devices/platform/mipi_lgit.1537/kgamma_b";

    String K_GAMMA_RED = "/sys/devices/platform/mipi_lgit.1537/kgamma_red";
    String K_GAMMA_GREEN = "/sys/devices/platform/mipi_lgit.1537/kgamma_green";
    String K_GAMMA_BLUE = "/sys/devices/platform/mipi_lgit.1537/kgamma_blue";

    String[] K_GAMMA_ARRAY = {K_GAMMA_R, K_GAMMA_G, K_GAMMA_B, K_GAMMA_RED, K_GAMMA_GREEN, K_GAMMA_BLUE};

    String GAMMACONTROL = "/sys/class/misc/gammacontrol";
    String GAMMACONTROL_RED_GREYS = GAMMACONTROL + "/red_greys";
    String GAMMACONTROL_RED_MIDS = GAMMACONTROL + "/red_mids";
    String GAMMACONTROL_RED_BLACKS = GAMMACONTROL + "/red_blacks";
    String GAMMACONTROL_RED_WHITES = GAMMACONTROL + "/red_whites";
    String GAMMACONTROL_GREEN_GREYS = GAMMACONTROL + "/green_greys";
    String GAMMACONTROL_GREEN_MIDS = GAMMACONTROL + "/green_mids";
    String GAMMACONTROL_GREEN_BLACKS = GAMMACONTROL + "/green_blacks";
    String GAMMACONTROL_GREEN_WHITES = GAMMACONTROL + "/green_whites";
    String GAMMACONTROL_BLUE_GREYS = GAMMACONTROL + "/blue_greys";
    String GAMMACONTROL_BLUE_MIDS = GAMMACONTROL + "/blue_mids";
    String GAMMACONTROL_BLUE_BLACKS = GAMMACONTROL + "/blue_blacks";
    String GAMMACONTROL_BLUE_WHITES = GAMMACONTROL + "/blue_whites";
    String GAMMACONTROL_CONTRAST = GAMMACONTROL + "/contrast";
    String GAMMACONTROL_BRIGHTNESS = GAMMACONTROL + "/brightness";
    String GAMMACONTROL_SATURATION = GAMMACONTROL + "/saturation";

    String[] GAMMACONTROL_ARRAY = {GAMMACONTROL};

    String DSI_PANEL_RP = "/sys/module/dsi_panel/kgamma_rp";
    String DSI_PANEL_RN = "/sys/module/dsi_panel/kgamma_rn";
    String DSI_PANEL_GP = "/sys/module/dsi_panel/kgamma_gp";
    String DSI_PANEL_GN = "/sys/module/dsi_panel/kgamma_gn";
    String DSI_PANEL_BP = "/sys/module/dsi_panel/kgamma_bp";
    String DSI_PANEL_BN = "/sys/module/dsi_panel/kgamma_bn";
    String DSI_PANEL_W = "/sys/module/dsi_panel/kgamma_w";

    String[] DSI_PANEL_ARRAY = {DSI_PANEL_RP, DSI_PANEL_RN, DSI_PANEL_GP, DSI_PANEL_GN, DSI_PANEL_BP, DSI_PANEL_BN, DSI_PANEL_W};

    // LCD Backlight
    String LM3530_BRIGTHNESS_MODE = "/sys/devices/i2c-0/0-0038/lm3530_br_mode";
    String LM3530_MIN_BRIGHTNESS = "/sys/devices/i2c-0/0-0038/lm3530_min_br";
    String LM3530_MAX_BRIGHTNESS = "/sys/devices/i2c-0/0-0038/lm3530_max_br";

    // Backlight Dimmer
    String LM3630_BACKLIGHT_DIMMER = "/sys/module/lm3630_bl/parameters/backlight_dimmer";
    String LM3630_MIN_BRIGHTNESS = "/sys/module/lm3630_bl/parameters/min_brightness";
    String LM3630_BACKLIGHT_DIMMER_THRESHOLD = "/sys/module/lm3630_bl/parameters/backlight_threshold";
    String LM3630_BACKLIGHT_DIMMER_OFFSET = "/sys/module/lm3630_bl/parameters/backlight_offset";

    String MSM_BACKLIGHT_DIMMER = "/sys/module/msm_fb/parameters/backlight_dimmer";

    String[] MIN_BRIGHTNESS_ARRAY = {LM3630_MIN_BRIGHTNESS, MSM_BACKLIGHT_DIMMER};

    String NEGATIVE_TOGGLE = "/sys/module/cypress_touchkey/parameters/mdnie_shortcut_enabled";
    String REGISTER_HOOK = "/sys/class/misc/mdnie/hook_intercept";
    String MASTER_SEQUENCE = "/sys/class/misc/mdnie/sequence_intercept";
    String GLOVE_MODE = "/sys/devices/virtual/touchscreen/touchscreen_dev/mode";

    String[][] SCREEN_ARRAY = {SCREEN_RGB_ARRAY, SCREEN_RGB_CTRL_ARRAY, SCREEN_KCAL_CTRL_NEW_ARRAY, K_GAMMA_ARRAY,
            GAMMACONTROL_ARRAY, DSI_PANEL_ARRAY, MIN_BRIGHTNESS_ARRAY,
            {SCREEN_KCAL_CTRL_MIN, SCREEN_HBM, LM3530_BRIGTHNESS_MODE, LM3530_MIN_BRIGHTNESS, LM3530_MAX_BRIGHTNESS,
                    LM3630_BACKLIGHT_DIMMER, LM3630_BACKLIGHT_DIMMER_THRESHOLD, LM3630_BACKLIGHT_DIMMER_OFFSET,
                    NEGATIVE_TOGGLE, REGISTER_HOOK, MASTER_SEQUENCE, GLOVE_MODE}};

    // Wake

    // DT2W
    String LGE_TOUCH_DT2W = "/sys/devices/virtual/input/lge_touch/dt_wake_enabled";
    String LGE_TOUCH_CORE_DT2W = "/sys/module/lge_touch_core/parameters/doubletap_to_wake";
    String LGE_TOUCH_GESTURE = "/sys/devices/virtual/input/lge_touch/touch_gesture";
    String DT2W = "/sys/android_touch/doubletap2wake";
    String TOUCH_PANEL_DT2W = "/proc/touchpanel/double_tap_enable";
    String DT2W_WAKEUP_GESTURE = "/sys/devices/virtual/input/input1/wakeup_gesture";
    String DT2W_ENABLE = "/sys/devices/platform/s3c2440-i2c.3/i2c-3/3-004a/dt2w_enable";
    String DT2W_WAKE_GESTURE = "/sys/devices/platform/spi-tegra114.2/spi_master/spi2/spi2.0/input/input0/wake_gesture";

    String[] DT2W_ARRAY = {LGE_TOUCH_DT2W, LGE_TOUCH_CORE_DT2W, LGE_TOUCH_GESTURE, DT2W, TOUCH_PANEL_DT2W,
            DT2W_WAKEUP_GESTURE, DT2W_ENABLE, DT2W_WAKE_GESTURE};

    // S2W
    String S2W_ONLY = "/sys/android_touch/s2w_s2sonly";
    String SW2 = "/sys/android_touch/sweep2wake";

    String[] S2W_ARRY = {S2W_ONLY, SW2};

    // T2W
    String TSP_T2W = "/sys/devices/f9966000.i2c/i2c-1/1-004a/tsp";
    String TOUCHWAKE_T2W = "/sys/class/misc/touchwake/enabled";

    String[] T2W_ARRAY = {TSP_T2W, TOUCHWAKE_T2W};

    // Wake Misc
    String SCREEN_WAKE_OPTIONS = "/sys/devices/f9924000.i2c/i2c-2/2-0020/input/input2/screen_wake_options";

    String[] WAKE_MISC_ARRAY = {SCREEN_WAKE_OPTIONS};

    // Sleep Misc
    String S2S = "/sys/android_touch/sweep2sleep";
    String SCREEN_SLEEP_OPTIONS = "/sys/devices/f9924000.i2c/i2c-2/2-0020/input/input2/screen_sleep_options";

    String[] SLEEP_MISC_ARRAY = {S2S, SCREEN_SLEEP_OPTIONS};

    // Gesture
    String GESTURE_CRTL = "/sys/devices/virtual/touchscreen/touchscreen_dev/gesture_ctrl";

    Integer[] GESTURE_HEX_VALUES = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512};
    String[] GESTURE_STRING_VALUES = {"up", "down", "left", "right", "e", "o", "w", "c", "m", "double_click"};

    String WAKE_TIMEOUT = "/sys/android_touch/wake_timeout";
    String POWER_KEY_SUSPEND = "/sys/module/qpnp_power_on/parameters/pwrkey_suspend";

    String[][] WAKE_ARRAY = {DT2W_ARRAY, S2W_ARRY, T2W_ARRAY, WAKE_MISC_ARRAY, SLEEP_MISC_ARRAY,
            {GESTURE_CRTL, WAKE_TIMEOUT, POWER_KEY_SUSPEND}};

    // Sound
    String SOUND_CONTROL_ENABLE = "/sys/module/snd_soc_wcd9320/parameters/enable_fs";
    String HEADPHONE_GAIN = "/sys/kernel/sound_control_3/gpl_headphone_gain";
    String HANDSET_MICROPONE_GAIN = "/sys/kernel/sound_control_3/gpl_mic_gain";
    String CAM_MICROPHONE_GAIN = "/sys/kernel/sound_control_3/gpl_cam_mic_gain";
    String SPEAKER_GAIN = "/sys/kernel/sound_control_3/gpl_speaker_gain";
    String HEADPHONE_POWERAMP_GAIN = "/sys/kernel/sound_control_3/gpl_headphone_pa_gain";

    String MIC_BOOST = "/sys/devices/virtual/misc/soundcontrol/mic_boost";
    String SPEAKER_BOOST = "/sys/devices/virtual/misc/soundcontrol/speaker_boost";
    String VOLUME_BOOST = "/sys/devices/virtual/misc/soundcontrol/volume_boost";

    String[] SPEAKER_GAIN_ARRAY = {SPEAKER_GAIN, SPEAKER_BOOST};

    String[][] SOUND_ARRAY = {SPEAKER_GAIN_ARRAY, {SOUND_CONTROL_ENABLE, HEADPHONE_GAIN, HANDSET_MICROPONE_GAIN,
            CAM_MICROPHONE_GAIN, HEADPHONE_POWERAMP_GAIN, MIC_BOOST, VOLUME_BOOST}};

    // Battery
    String FORCE_FAST_CHARGE = "/sys/kernel/fast_charge/force_fast_charge";
    String BLX = "/sys/devices/virtual/misc/batterylifeextender/charging_limit";

    String CHARGE_RATE = "sys/kernel/thundercharge_control";
    String CHARGE_RATE_ENABLE = CHARGE_RATE + "/enabled";
    String CUSTOM_CHARGING_RATE = CHARGE_RATE + "/custom_current";

    String[] BATTERY_ARRAY = {FORCE_FAST_CHARGE, BLX, CHARGE_RATE};

    // I/O
    String IO_INTERNAL_SCHEDULER = "/sys/block/mmcblk0/queue/scheduler";
    String IO_EXTERNAL_SCHEDULER = "/sys/block/mmcblk1/queue/scheduler";
    String IO_INTERNAL_SCHEDULER_TUNABLE = "/sys/block/mmcblk0/queue/iosched";
    String IO_EXTERNAL_SCHEDULER_TUNABLE = "/sys/block/mmcblk1/queue/iosched";
    String IO_INTERNAL_READ_AHEAD = "/sys/block/mmcblk0/queue/read_ahead_kb";
    String IO_EXTERNAL_READ_AHEAD = "/sys/block/mmcblk1/queue/read_ahead_kb";

    String[] IO_ARRAY = {IO_INTERNAL_SCHEDULER, IO_EXTERNAL_SCHEDULER, IO_INTERNAL_SCHEDULER_TUNABLE,
            IO_EXTERNAL_SCHEDULER_TUNABLE, IO_INTERNAL_READ_AHEAD, IO_EXTERNAL_READ_AHEAD};

    // Kernel Samepage Merging
    String KSM_FOLDER = "/sys/kernel/mm/ksm";
    String UKSM_FOLDER = "/sys/kernel/mm/uksm";
    String FULL_SCANS = "full_scans";
    String PAGES_SHARED = "pages_shared";
    String PAGES_SHARING = "pages_sharing";
    String PAGES_UNSHARED = "pages_unshared";
    String PAGES_VOLATILE = "pages_volatile";
    String RUN = "run";
    String DEFERRED_TIMER = "deferred_timer";
    String PAGES_TO_SCAN = "pages_to_scan";
    String SLEEP_MILLISECONDS = "sleep_millisecs";

    String[] KSM_INFOS = {FULL_SCANS, PAGES_SHARED, PAGES_SHARING, PAGES_UNSHARED, PAGES_VOLATILE};

    String[] KSM_ARRAY = {KSM_FOLDER, UKSM_FOLDER};

    // Low Memory Killer
    String LMK_MINFREE = "/sys/module/lowmemorykiller/parameters/minfree";

    // Virtual Memory
    String VM_PATH = "/proc/sys/vm";

    String[] SUPPORTED_VM = {"dirty_ratio", "dirty_background_ratio", "dirty_expire_centisecs",
            "dirty_writeback_centisecs", "min_free_kbytes", "overcommit_ratio", "swappiness",
            "vfs_cache_pressure", "laptop_mode", "extra_free_kbytes"};

    String ZRAM = "/sys/block/zram0";
    String ZRAM_BLOCK = "/dev/block/zram0";
    String ZRAM_DISKSIZE = "/sys/block/zram0/disksize";
    String ZRAM_RESET = "/sys/block/zram0/reset";

    String[] VM_ARRAY = {VM_PATH, ZRAM_BLOCK, ZRAM_DISKSIZE, ZRAM_RESET};

    // Entropy
    String PROC_RANDOM = "/proc/sys/kernel/random";
    String PROC_RANDOM_ENTROPY_AVAILABLE = PROC_RANDOM + "/entropy_avail";
    String PROC_RANDOM_ENTROPY_POOLSIZE = PROC_RANDOM + "/poolsize";
    String PROC_RANDOM_ENTROPY_READ = PROC_RANDOM + "/read_wakeup_threshold";
    String PROC_RANDOM_ENTROPY_WRITE = PROC_RANDOM + "/write_wakeup_threshold";

    String[] ENTROPY_ARRAY = {PROC_RANDOM};

    // Misc

    // Vibration
    String[] VIBRATION_ARRAY = {
            "/sys/vibrator/pwmvalue",
            "/sys/class/timed_output/vibrator/amp",
            "/sys/class/timed_output/vibrator/vtg_level",
            "/sys/devices/platform/tspdrv/nforce_timed",
            "/sys/class/timed_output/vibrator/pwm_value",
            "/sys/devices/i2c-3/3-0033/vibrator/vib0/vib_duty_cycle",
            "/sys/devices/virtual/timed_output/vibrator/voltage_level",
            "/sys/devices/virtual/timed_output/vibrator/pwm_value_1p",
            "/sys/devices/virtual/timed_output/vibrator/vmax_mv"
    };

    int[][] VIBRATION_MAX_MIN_ARRAY = {
            {127, 0},
            {100, 0},
            {31, 12}, // Read MAX MIN from sys
            {127, 1},
            {100, 0}, // Read MAX MIN from sys
            {100, 25}, // Needs enable path
            {3199, 1200},
            {99, 53},
            {3596, 116}
    };

    String VIB_ENABLE = "/sys/devices/i2c-3/3-0033/vibrator/vib0/vib_enable";

    // Wakelock
    String[] SMB135X_WAKELOCKS = {
            "/sys/module/smb135x_charger/parameters/use_wlock",
            "/sys/module/wakeup/parameters/enable_smb135x_wake_ws"
    };

    String SENSOR_IND_WAKELOCK = "/sys/module/wakeup/parameters/enable_si_ws";
    String MSM_HSIC_HOST_WAKELOCK = "/sys/module/wakeup/parameters/enable_msm_hsic_ws";

    String[] WLAN_RX_WAKELOCKS = {
            "/sys/module/wakeup/parameters/wlan_rx_wake",
            "/sys/module/wakeup/parameters/enable_wlan_rx_wake_ws"
    };

    String[] WLAN_CTRL_WAKELOCKS = {
            "/sys/module/wakeup/parameters/wlan_ctrl_wake",
            "/sys/module/wakeup/parameters/enable_wlan_ctrl_wake_ws"
    };

    String[] WLAN_WAKELOCKS = {
            "/sys/module/wakeup/parameters/wlan_wake",
            "/sys/module/wakeup/parameters/enable_wlan_wake_ws"
    };

    String WLAN_RX_WAKELOCK_DIVIDER = "/sys/module/bcmdhd/parameters/wl_divide";
    String MSM_HSIC_WAKELOCK_DIVIDER = "/sys/module/xhci_hcd/parameters/wl_divide";

    // Logging
    String LOGGER_MODE = "/sys/kernel/logger_mode/logger_mode";
    String LOGGER_ENABLED = "/sys/module/logger/parameters/enabled";

    String[] LOGGER_ARRAY = {LOGGER_MODE, LOGGER_ENABLED};

    // Fsync
    String FSYNC = "/sys/devices/virtual/misc/fsynccontrol/fsync_enabled";
    String DYNAMIC_FSYNC = "/sys/kernel/dyn_fsync/Dyn_fsync_active";

    // Power suspend
    String POWER_SUSPEND = "/sys/kernel/power_suspend";
    String POWER_SUSPEND_MODE = POWER_SUSPEND + "/power_suspend_mode";
    String POWER_SUSPEND_STATE = POWER_SUSPEND + "/power_suspend_state";
    String POWER_SUSPEND_VERSION = POWER_SUSPEND + "/power_suspend_version";

    // Network
    String TCP_AVAILABLE_CONGESTIONS = "/proc/sys/net/ipv4/tcp_available_congestion_control";
    String HOSTNAME_KEY = "net.hostname";

    String[][] MISC_ARRAY = {{VIB_ENABLE, SENSOR_IND_WAKELOCK, MSM_HSIC_HOST_WAKELOCK, WLAN_RX_WAKELOCK_DIVIDER,
            MSM_HSIC_WAKELOCK_DIVIDER, LOGGER_ENABLED, FSYNC, DYNAMIC_FSYNC, POWER_SUSPEND_MODE, POWER_SUSPEND_STATE,
            TCP_AVAILABLE_CONGESTIONS, HOSTNAME_KEY},
            SMB135X_WAKELOCKS, WLAN_RX_WAKELOCKS, WLAN_CTRL_WAKELOCKS, WLAN_WAKELOCKS, VIBRATION_ARRAY};

    // Build prop
    String BUILD_PROP = "/system/build.prop";

    // Init.d
    String INITD = "/system/etc/init.d";

}
