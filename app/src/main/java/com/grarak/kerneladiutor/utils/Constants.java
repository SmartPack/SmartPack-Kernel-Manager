package com.grarak.kerneladiutor.utils;

/**
 * Created by willi on 30.11.14.
 */
public interface Constants {

    public final String MODEL = android.os.Build.MODEL;

    public final String TAG = "Kernel Adiutor";
    public final String PREF_NAME = "prefs";

    // Kernel Informations
    public final String PROC_VERSION = "/proc/version";
    public final String PROC_CPUINFO = "/proc/cpuinfo";
    public final String PROC_MEMINFO = "/proc/meminfo";

    // CPU
    public final String CPU_CUR_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq";
    public final String CPU_CORE_ONLINE = "/sys/devices/system/cpu/cpu%d/online";
    public final String CPU_MAX_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_max_freq";
    public final String CPU_MIN_FREQ = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_min_freq";
    public final String CPU_AVAILABLE_FREQS = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
    public final String CPU_TIME_STATE = "/sys/devices/system/cpu/cpu0/cpufreq/stats/time_in_state";
    public final String CPU_SCALING_GOVERNOR = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_governor";
    public final String CPU_AVAILABLE_GOVERNORS = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";
    public final String CPU_GOVERNOR_TUNABLES = "/sys/devices/system/cpu/cpufreq/%s";
    public final String CPU_MC_POWER_SAVING = "/sys/devices/system/cpu/sched_mc_power_savings";
    public final String CPU_MPDECISION_BINARY = "/system/bin/mpdecision";
    public final String CPU_MPDEC = "mpdecision";
    public final String CPU_INTELLI_PLUG = "/sys/module/intelli_plug/parameters/intelli_plug_active";
    public final String CPU_INTELLI_PLUG_ECO = "/sys/module/intelli_plug/parameters/eco_mode_active";

    // CPU Voltage
    public final String CPU_VOLTAGE = "/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table";

    // GPU
    public final String GPU_GENERIC_GOVERNORS = "performance powersave ondemand simple";

    public final String GPU_CUR_KGSL2D0_QCOM_FREQ = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/gpuclk";
    public final String GPU_MAX_KGSL2D0_QCOM_FREQ = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/max_gpuclk";
    public final String GPU_AVAILABLE_KGSL2D0_QCOM_FREQS = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/gpu_available_frequencies";
    public final String GPU_SCALING_KGSL2D0_QCOM_GOVERNOR = "/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/pwrscale/trustzone/governor";

    public final String GPU_CUR_KGSL3D0_QCOM_FREQ = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/gpuclk";
    public final String GPU_MAX_KGSL3D0_QCOM_FREQ = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/max_gpuclk";
    public final String GPU_AVAILABLE_KGSL3D0_QCOM_FREQS = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/gpu_available_frequencies";
    public final String GPU_SCALING_KGSL3D0_QCOM_GOVERNOR = "/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/pwrscale/trustzone/governor";

    public final String GPU_CUR_FDB00000_QCOM_FREQ = "/sys/devices/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/devfreq/cur_freq";
    public final String GPU_MAX_FDB00000_QCOM_FREQ = "/sys/devices/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/max_gpuclk";
    public final String GPU_AVAILABLE_FDB00000_QCOM_FREQS = "/sys/devices/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/gpu_available_frequencies";
    public final String GPU_SCALING_FDB00000_QCOM_GOVERNOR = "/sys/devices/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/devfreq/governor";
    public final String GPU_AVAILABLE_FDB00000_QCOM_GOVERNORS = "/sys/devices/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/devfreq/available_governors";

    public final String GPU_CUR_OMAP_FREQ = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/frequency";
    public final String GPU_MAX_OMAP_FREQ = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/frequency_limit";
    public final String GPU_AVAILABLE_OMAP_FREQS = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/frequency_list";
    public final String GPU_SCALING_OMAP_GOVERNOR = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/governor";
    public final String GPU_AVAILABLE_OMAP_GOVERNORS = "/sys/devices/platform/omap/pvrsrvkm.0/sgxfreq/governor_list";

    public final String[] GPU_2D_CUR_FREQ_ARRAY = new String[]{GPU_CUR_KGSL2D0_QCOM_FREQ};

    public final String[] GPU_2D_MAX_FREQ_ARRAY = new String[]{GPU_MAX_KGSL2D0_QCOM_FREQ};

    public final String[] GPU_2D_AVAILABLE_FREQS_ARRAY = new String[]{GPU_AVAILABLE_KGSL2D0_QCOM_FREQS};

    public final String[] GPU_2D_SCALING_GOVERNOR_ARRAY = new String[]{GPU_SCALING_KGSL2D0_QCOM_GOVERNOR};

    public final String[] GPU_CUR_FREQ_ARRAY = new String[]{GPU_CUR_KGSL3D0_QCOM_FREQ, GPU_CUR_FDB00000_QCOM_FREQ,
            GPU_CUR_OMAP_FREQ};

    public final String[] GPU_MAX_FREQ_ARRAY = new String[]{GPU_MAX_KGSL3D0_QCOM_FREQ, GPU_MAX_FDB00000_QCOM_FREQ,
            GPU_MAX_OMAP_FREQ};

    public final String[] GPU_AVAILABLE_FREQS_ARRAY = new String[]{GPU_AVAILABLE_KGSL3D0_QCOM_FREQS,
            GPU_AVAILABLE_FDB00000_QCOM_FREQS, GPU_AVAILABLE_OMAP_FREQS};

    public final String[] GPU_SCALING_GOVERNOR_ARRAY = new String[]{GPU_SCALING_KGSL3D0_QCOM_GOVERNOR,
            GPU_SCALING_FDB00000_QCOM_GOVERNOR, GPU_SCALING_OMAP_GOVERNOR};

    public final String[] GPU_AVAILABLE_GOVERNORS_ARRAY = new String[]{GPU_AVAILABLE_FDB00000_QCOM_GOVERNORS,
            GPU_AVAILABLE_OMAP_GOVERNORS};

    public final String[][] GPU_ARRAY = new String[][]{GPU_2D_CUR_FREQ_ARRAY,
            GPU_2D_MAX_FREQ_ARRAY, GPU_2D_AVAILABLE_FREQS_ARRAY,
            GPU_2D_SCALING_GOVERNOR_ARRAY, GPU_CUR_FREQ_ARRAY,
            GPU_MAX_FREQ_ARRAY, GPU_AVAILABLE_FREQS_ARRAY,
            GPU_SCALING_GOVERNOR_ARRAY};

    // Screen
    public final String SCREEN_KCAL_CTRL = "/sys/devices/platform/kcal_ctrl.0/kcal";
    public final String SCREEN_KCAL_CTRL_CTRL = "/sys/devices/platform/kcal_ctrl.0/kcal_ctrl";

    public final String SCREEN_DIAG0_POWER = "/sys/devices/platform/DIAG0.0/power_rail";
    public final String SCREEN_DIAG0_POWER_CTRL = "/sys/devices/platform/DIAG0.0/power_rail_ctrl";

    public final String SCREEN_COLOR_CONTROL = "/sys/class/misc/colorcontrol/multiplier";
    public final String SCREEN_COLOR_CONTROL_CTRL = "/sys/class/misc/colorcontrol/safety_enabled";

    public final String SCREEN_SAMOLED_COLOR_RED = "/sys/class/misc/samoled_color/red_multiplier";
    public final String SCREEN_SAMOLED_COLOR_GREEN = "/sys/class/misc/samoled_color/green_multiplier";
    public final String SCREEN_SAMOLED_COLOR_BLUE = "/sys/class/misc/samoled_color/blue_multiplier";

    public final String[] SCREEN_KCAL_ARRAY = {SCREEN_KCAL_CTRL, SCREEN_DIAG0_POWER, SCREEN_COLOR_CONTROL,
            SCREEN_SAMOLED_COLOR_RED};

    public final String[] SCREEN_KCAL_CTRL_ARRAY = {SCREEN_KCAL_CTRL_CTRL, SCREEN_DIAG0_POWER_CTRL,
            SCREEN_COLOR_CONTROL_CTRL};

    public final String[][] SCREEN_ARRAY = {SCREEN_KCAL_ARRAY, SCREEN_KCAL_CTRL_ARRAY};

    // I/O
    public final String IO_INTERNAL_SCHEDULER = "/sys/block/mmcblk0/queue/scheduler";
    public final String IO_EXTERNAL_SCHEDULER = "/sys/block/mmcblk1/queue/scheduler";
    public final String IO_INTERNAL_SCHEDULER_TUNABLE = "/sys/block/mmcblk0/queue/iosched";
    public final String IO_EXTERNAL_SCHEDULER_TUNABLE = "/sys/block/mmcblk1/queue/iosched";
    public final String IO_INTERNAL_READ_AHEAD = "/sys/block/mmcblk0/queue/read_ahead_kb";
    public final String IO_EXTERNAL_READ_AHEAD = "/sys/block/mmcblk1/queue/read_ahead_kb";

    // Kernel Samepage Merging
    public final String KSM_FOLDER = "/sys/kernel/mm/ksm";
    public final String KSM_FULL_SCANS = KSM_FOLDER + "/full_scans";
    public final String KSM_PAGES_SHARED = KSM_FOLDER + "/pages_shared";
    public final String KSM_PAGES_SHARING = KSM_FOLDER + "/pages_sharing";
    public final String KSM_PAGES_UNSHARED = KSM_FOLDER + "/pages_unshared";
    public final String KSM_PAGES_VOLATILE = KSM_FOLDER + "/pages_volatile";
    public final String KSM_RUN = KSM_FOLDER + "/run";
    public final String KSM_PAGES_TO_SCAN = KSM_FOLDER + "/pages_to_scan";
    public final String KSM_SLEEP_MILLISECONDS = KSM_FOLDER + "/sleep_millisecs";

    public final String[] KSM_INFOS = new String[]{KSM_FULL_SCANS, KSM_PAGES_SHARED, KSM_PAGES_SHARING, KSM_PAGES_UNSHARED,
            KSM_PAGES_VOLATILE};

    // Low Memory Killer
    public final String LMK_MINFREE = "/sys/module/lowmemorykiller/parameters/minfree";

    // Virtual Machine
    public final String VM_PATH = "/proc/sys/vm";

}
