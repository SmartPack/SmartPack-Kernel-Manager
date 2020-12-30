# Change-logs

## 118. December 30, 2020
*Release-tag: beta_v15.7*<br>
Updated CPU Cluster Control. Fixed layout issues on CPU Boost & Overall Activities. Slightly updated cardview corner. Added more options to Overall page. Visually improved FAQ page. Fixed Translator crashing on certain situations. Miscellaneous changes.

## 117. December 20, 2020
*Release-tag: beta_v15.3*<br>
Hopefully fixed crashes on some devices (sorry for the issue). Updated google.android.material library to latest beta.

## 116. December 19, 2020
*Release-tag: beta_v15.0*<br>
Enhance Overall page UI. Fixed (hopefully) Adreno idler status not showing properly on some devices. Improved Script and Profile details view. Improved Terminal and Translator pages. Miscellaneous changes.

## 115. December 05, 2020
*Release-tag: beta_v14.7*<br>
Updated to use Magisk BusyBox binaries (if available) as much as possible. Largely improved flasher (now show real-time output + correct flashing status). Scripts and Terminal now onwards display outputs in real-time. App now uses material elements as much as possible (please feel free to share your comments). Updated to use accent color (for texts) in many places. Fixed bug on sharing custom controller. Moved internal update (app update and kernel update) files into internal data storage folder. Miscellaneous changes.

## 114. November 11, 2020
*Release-tag: v14.2*<br>
All the premium features are now free. Removed Ads from app. Replace password with Biometric authentication. Simplified & moved Reset App to Settings menu. Replaced most Toast messages with SnackBar. Allowed disabling tasker toast message. Miscellaneous changes.

## 113. October 16, 2020
*Release-tag: v13.9*<br>
Replaced shell command option with a full Terminal page. Updated progress message everywhere (almost) on app. Updated polish translation (credits: @Fruity-0). Miscellaneous changes.

## 112. September 17, 2020
*Release-tag: v13.5*<br>
Various updates to avoid issues with google play policies. Miscellaneous changes.

## 111. September 13, 2020
*Release-tag: v13.4*<br>
Updated IO Readahead minimum & offset values. Updated polish translation (credits: @Fruity-0). Improved AdLayout to avoid overlapping with other items. Miscellaneous changes.

## 110. September 02, 2020
*Release-tag: v13.2*<br>
Updated Polish translations (credits: @Fruity-0). Removed bottom loading message. Improved Navigation Bar response on Overall page (credits: @mpschahal16). Miscellaneous changes.

## 109. August 21, 2020
*Release-tag: v13.0*<br>
Added a switch to disable on-boot service in Settings page. Removed copyright text. Added an initial version of Polish translations (credits: @Fruity-0). Updated Portuguese (Brazilian), Russian and Chinese (Simplified) translations translations. Updated libsu to v3.0.2. Updated build tools and dependencies. Miscellaneous changes.

## 108. July 04, 2020
*Release-tag: v12.7*<br>
Added an in-built Translator to help users to translate this app (About --> Translators). Updated file picker to fix issues with some file manager apps. Improved flexibility of Update Channel Url. Updated CPU control for Sailfish (Pixel) & Marlin (Pixel XL). Added Spanish translation (Credits: Alejandro YT). Miscellaneous changes.

## 107. June 21, 2020
*Release-tag: v12.4*<br>
Fixed Build Prop Editor on SAR devices. CPU and GPU pages are now linked to the OverAll statistics. Improved page switching from overall page (It won't fall back to overall page on screen rotation anymore). Added german translation (Credits: @free-bots). Update Portuguese (Brazilian) translation (Credits: @Lennoard). Miscellaneous changes.

## 106. May 31, 2020
*Release-tag: v12.0*<br>
Added support to ThunderPlug CPU hotplug driver. Moved all CPU Boost settings into a new page (link in CPU). Moved Create Update Channel option into a new activity (improved & simplified). Updated build tools to latest. Updated Script Manager to show success dialog even if the output is empty. Removed circular image view from app (reduced app size). Added a debug mode in Flasher (capture full log if enabled). Added LMK fast run control switch. Miscellaneous changes.

## 105. May 09, 2020
*Release-tag: v11.7*<br>
Updated GPU Min Freq path and offset for some Xiaomi devices (Credits: @elpaablo). Enabled on boot indicator for profiles. Increased zRAM max limit to 4GB. Replaced most toast messages with a snackbar. Miscellaneous changes.

## 104. May 06, 2020
*Release-tag: v11.5*<br>
Largely improved Root handling. Added option to select default app page (Credits: @Lennoard). Updated icon colors on Hide Banner mode. Added option to set individual scripts on boot (Credits: @Lennoard). Added time stamp to exported log (logcat, dmesg, etc.) files. Show flashing failed message if flashing output is empty. Updated Portuguese (Credits: @Lennoard) and Korean translations (Credits: @SmgKhOaRn).

## 103. April 26, 2020
*Release-tag: v11.0*<br>
Added Brand New Flashing view. Migrated to libsu (Credits: @Lennoard). Largely improved manual flashing (includes fix for syntax error). Updated to display stderr for Run Shell Command & apply Script options. Updated main headings and vector graphics (overall page and FAB's) to use accent colors (Credits: @Lennoard). Added GPU System Lib version to Device page. Updated nav header background to follow app theme (Credits: @Lennoard). Added few more virtual memory tunables. Added Amharic translations (Credits: @Mikesew1320) and Korean translations (Credits: @SmgKhOaRn). Added links to other SmartPack apps (if not installed) on related pages. Updated Portuguese (Brazilian) translations (Credits: @Lennoard). Miscellaneous changes.

## 102. April 14, 2020
*Release-tag: v10.5*<br>
CPUFreq: Properly distinguish more 2 big/6 little devices. SmartPack: Another workaround to use Magisk BusyBox. Misc: Allow changing copyright text by long-clicking on it. Miscellaneous changes.

## 101. April 09, 2020
*Release-tag: v10.4*<br>
Added support to devices with Prime CPU cluster. Added Stune Boost On Input to CPU Boost. Added CPU Set support in Misc page. Moved Dynamic and Stune Boost settings into a new Card. Fixed crashing Settings menu on some devices. Added a bottom copyright text (mainly to  cover Ads). Updated gradle build tools. Miscellaneous changes.

## 100. April 03, 2020
*Release-tag: v10.3*<br>
Partially removed recent changes (including flashing view) to fix foreground issues. Added a Cancel button on foreground page. Fixed issues with Tasker integration. Miscellaneous changes.

## 99. April 01, 2020
*Release-tag: v10.2*<br>
Added a recovery-like flashing view. Added option to view Scripts. Fixed creating app folder on Android 10. Switched to Teal Accent color. More UI updates. Miscellaneous changes.

## 98. March 23, 2020
*Release-tag: v10.1*<br>
Changes: Depreciate below lollipop (API < 21) support. Fixed profile pop-up menu not showing. Fixed going back to overall page upon screen rotation. Miscellaneous changes.

## 97. March 20, 2020
*Release-tag: v10.0*<br>
Changes: Overall cleaning & modernization of code. Updated kernel downloader to work without curl/wget binaries. Significantly improved auto-flashing. Prevent users from picking file paths contain unsupported characters. Update pt-rBR translations. Updated Scripts & Custom Controls view. Miscellaneous changes.

## 96. March 01, 2020
*Release-tag: v9.16*<br>
Changes: Main: Fully move to com.smartpack.kernelmanager (avoid conflicting with forks). Misc: Fixed rebooting from app not working on most cases. Misc: Added option to tweak auto-sync data. Misc: Improved sharing files. Gradle: Updated build tools and dependencies. Miscellaneous changes.

## 95. February 20, 2020
*Release-tag: v9.15*<br>
Changes: Build Prop: Improved search UI (mainly on light theme). IO: Add support to Adoptable storage (DM-0). Misc: Updated some titles (suggested by KA developer). Miscellaneous changes.

## 94. February 17, 2020
*Release-tag: v9.14*<br>
Changes: File picking: Overall updates to work with Documents UI. Custom controls: Various improvements. K-lapse: Export settings: Added option to share exported item. Miscellaneous changes.

## 93. February 15, 2020
*Release-tag: v9.13*<br>
Changes: Custom Control & Backup: Fixed crashes on some devices. Miscellaneous changes.

## 92. February 08, 2020
*Release-tag: v9.12*<br>
Changes: Main: Added a simple banner Ad view at the bottom (can be disabled in Settings). CPU Freq: Reverted some recent changes made for 6 little/2 big devices. res: Icon: Increased icon size a bit. Translations: Added Ukrainian translations. Translations: Updated Russian translations. Miscellaneous changes.

## 91. February 05, 2020
*Release-tag: v9.11*<br>
Changes: SmartPack: Manual Flashing: Improve flashing. Backup, Custom Controls, Profiles, & Script Manager: Apply file manager updates. Miscellaneous changes.

## 90. February 04, 2020
*Release-tag: v9.10*<br>
Changes: SmartPack: Manual flasher: Support more file manager. About: Link to Play-Store page. Miscellaneous changes.

## 89. February 03, 2020
*Release-tag: v9.9*<br>
Changes: Main: Updated app theme, especially the Dark one. Main: Updates for Play-Store release (removed all the conflicting features). res: Updated app icon (Credits to telegram user toxinpiper). CPUFreq: Updated to properly distinguish 4 Little/2 big core devices (need testing). Miscellaneous changes.

## 88. February 02, 2020
*Release-tag: v9.8*<br>
Changes: CPUFreq: Added proper support for 2 big/6 LITTLE core CPUs. Kernel Updater: Added option to remove and share channel. Misc: Updated reboot options to use svc power reboot if available. SmartPack: Simplified reboot and reset options. build: update some dependencies (e.g. picasso; Credits @pashamcr). Miscellaneous changes.

## 87. January 29, 2020
*Release-tag: v9.7*<br>
Changes: SmartPack: Introduced new page to create Update channel. Main: Overall UI updates. Translations. Improved Chinese simplified translations (Credits: @guh0613).

## 86. January 23, 2020
*Release-tag: v9.6*<br>
Changes: SmartPack: Implemented an FK model kernel update feature. Custom Controller: Added option to share and import controllers. Script Manager: Added option to share scripts. Profiles, Scripts & Backup: Added images. SmartPack: Overall Updates in alignment. Miscellaneous changes.

## 85. January 16, 2020
*Release-tag: v9.5*<br>
Changes: SmartPack: Workaround to use Magisk BusyBox. SmartPack: Add option to run a shell commands and view output. SmartPack: Updated manual flashing. App updater: Switch to own implementation. K-lapse: Add option to export current settings as shell script. Update Russian translations. Miscellaneous changes.

## 84. January 05, 2020
*Release-tag: v9.4*<br>
Changes: SmartPack: Manual Flash: Directly catch and show the flashing output. SmartPack: Manual Flash: Removed file size limit with a warning message. VM: Hide VM tunables by default. Scripts & Backups: Workaround to avoid failing create scripts/backups in certain cases. Custom Controller: Switch: Updates for supporting more boolean values. Misc: Fully redesigned about page. Miscellaneous changes.

## 83. December 22, 2019
*Release-tag: v9.3*<br>
Changes: Tools: Introduced a *Brand New* Script Manager page (largely based on, and as a replacement of the Init.d page). Custom Controls: Add ability to remove a controller. Custom Controls: Add a warning alert dialogue on opening page. Navigation layout: Fix navigation drawer highlight color (@atarek1). Misc: Fix on boot notification icon (@atarek1). About: Add option to share app. Miscellaneous changes.

## 82. December 16, 2019
*Release-tag: v9.2*<br>
Changes: Tools: Introduced a *Brand New* Custom controls page (simple and user-friendly) as a replcement of the old one. SmartPack: Flasher: Show reboot menu after flashing (with an option to view  flasher log). Backup: Also show reboot menu after a flash/restore. res: Introduced adaptive icons (credits: @Lennoard). gradle: Updated build tools to 3.5.3. Miscellaneous changes.

## 81. December 08, 2019
*Release-tag: v9.1*<br>
Changes: K-Lapse: Scheduler: Updated to use TimePicker. Misc: Brand new SmartPack icons, and also updated some other drawable. Misc: Shorten app name on android launcher for better user experience. Settings: Make section icons user configurable and enabled dark theme by default. Overall: Display device uptime including total, awake and deep sleep. Battery and Virtual Memory: Updated live status views. Miscellaneous changes.

## 80. November 27, 2019
*Release-tag: v9.0*<br>
Changes: Main: Overall updates according to material style guidelines. Main: Fixed crashing if Root access not available/granted. Battery: Charging Status: Report OnePlus Dash charging. SmartPack: Maintain flashing history inside '/sdcard/SP/'. VM: RAM & Swap view: Switched to a simple Status View. Main: File Picker: Show a message to prevent using default DocumentsUI. ApplyOnBoot: Initialize Spectrum and Boeffla Wakelock blocker upon boot. Miscellaneous changes.

## 79. November 21, 2019
*Release-tag: v8.12*<br>
Changes: SmartPack: Add option to execute shell script. Misc: Updated file picker using external file manager. Settings: Fix Force English option not showing. build: update gradle to 3.5.2. Sweep2Wake: Add support to Lord Boeffla's s2w. Back-Up & Restore: Update to use proper titles. Miscellaneous changes.

## 78. November 10, 2019
*Release-tag: v8.11*<br>
Changes: SmartPack: Overall update to recovery zip flasher. SmartPack: Flasher: Added an option to reboot after successful flashing. VM: zRAM: Updated zRAM algorithm. SmartPack: Moved logs in a separate folder.

## 77. November 04, 2019
*Release-tag: v8.10*<br>
Changes: gradle: Updated SDK to 29 (Android Q). gradle: Updated build dependencies. SmartPack: Added option to ShutDown & Normal Reboot. SmartPack: Removed SmartPack-Kernel support and related features.

## 76. October 23, 2019
*Release-tag: v8.9*<br>
Changes: CPU: CPU Quiet: Corrected some mistakes. About: Integrated a licence view with SP icon. Translations: Updated Brazilian Portuguese (Credits: @Lennoard) Chinese traditional (Credits: @jason5545). gradle: Updated build tools.

## 75. September 20, 2019
*Release-tag: v8.8*<br>
Changes: CPU Hotplug: Added AIO hotplug. CPU: CPU Quiet: Added Min and Max CPUs. Translations: Added Brazilian Portuguese (Credits: @Lennoard). Translations: Updated Russian (Credits: @Andrey) and Chinese simplified (Credits: @Roiyaru).

## 74. August 15, 2019
*Release-tag: v8.7*<br>
Changes: About: Add an option to disable auto-update check (Enabled by default). Battery: Add switch to disable charging on support OnePlus phones. build: Updated gradle to 3.5.0-rc03.

## 73. July 16, 2019
*Release-tag: v8.6*<br>
Changes: GPU: Added GPU Power Level Control. Display & LED: Added support to LED Flash Intensity. Misc: Updated haptic feedback control (Credits to @khusika). Gestures: Updated dt2w for xiaomi devices (Credits to @khusika). build: Updated gradle to 3.5.0-beta05.

## 72. June 24, 2019
*Release-tag: v8.5*<br>
Changes: SmartPack: Flasher: Updated file Size check. Custom Control: Fixed "Create" not working. SmartPack: Added option to view (last) Flasher log. Misc: Added haptic feedback control. build: Updated gradle to 3.5.0-beta04. Overall: Simplified file extension check. SmartPack/About: Check for internet availability.

## 71. June 18, 2019
*Release-tag: v8.4*<br>
Changes: Display & LED: Added support to LED Max Brightness (Red/Green/Blue/White). SmartPack: Flasher: Simplified manual flashing menu. SmartPack: Auto/Manual Flashing: Internal updates. SmartPack/About: Updated alert dialogues.

## 70. June 11, 2019
*Release-tag: v8.3*<br>
Changes: SmartPack: Flasher: Updated to work with some some file manager. SmartPack: Updated permission request. Back-up, Profiles & custom Control: Updated for some file manager. SmartPack: Flasher: Introduced an additional file extension check.

## 69. June 07, 2019
*Release-tag: v8.2*<br>
Changes: Over-all: Updated to use external file manager. Wake: Added initial support to Smart Wake. CPU: Added initial support to Power HAL tunables. K-lapse: Added version info (only for v5.0 onwards). K-lapse: Fixed auto-dimmer not working properly. build: Updated gradle to 3.5.0-beta03. Updated Chinese Traditional translations. Miscellaneous changes, please check my GitHub page for more info.

## 68. May 31, 2019
*Release-tag: v8.1*<br>
Changes: Virtual Memory: zRAM: Increased maximum limit to 2560. SmartPack: Flasher: Added a file size limit of 100 MB. K-lapse: Moved to own page. K-lapse: Updates for v5.0. Misc. Updated Russian & Chinese Traditional translations. Miscellaneous changes, please check my GitHub page for more info.

## 67. May 23, 2019
*Release-tag: v8.0*<br>
Changes: SmartPack: Added an option to flash any recovery zip file without reboot. SmartPack: Implemented real "AutoFlash" for SmartPack Kernel. build: Updated gradle to 3.5.0-beta02. Misc. Updated Russian & Chinese Traditional translations. Miscellaneous changes everywhere, please check my GitHub page for more info.

## 66. May 10, 2019
*Release-tag: v7.14*<br>
Changes: Screen: KLapse: Workaround to avoid the time delay while switching modes. Hotplug: AutoSMP: Added Minimum Boost Frequency and Hotplug Suspend. Misc: Updated icons for SmartPack & Gestures page. Settings: Prevent banner resize option (only for donated version) if screen is landscape to avoid app crashing. build: Updated gradle to latest beta release. Misc: Removed a bunch of unused resources from the app. Miscellaneous changes on FAQ, About and more.

## 65. May 06, 2019
*Release-tag: v7.13*<br>
Changes: Build: Optimized app size (now it is much  smaller). Introduced new donation package. Miscellaneous changes.

## 64. May 03, 2019
*Release-tag: v7.12*<br>
Changes: Spectrum: Fixed spectrum support on some newer devices. Quick tile: Added new quick tile to control K-Lapse. Miscellaneous changes.

## 63. April 24, 2019
*Release-tag: v7.11*<br>
Changes: CPU Hotplug: Added AutoSMP. CPU: Separated stune boost setting from dynamic stune boost and made available for all the supported devices.

## 62. April 19, 2019
*Release-tag: v7.10*<br>
Changes: CPU Hotplug: Added MSM MPDecision/Bricked hotplug. Misc. Added Russian translations (credits to @andrey167). Misc: Updated Chinese translations (thanks to @Roiyaru & @jason5545). build: Updated gradle to latest.

## 61. April 10, 2019
*Release-tag: v7.9*<br>
Changes: CPU Input Boost: Addrd support to Cluster Return Frequency. GPU: devfreq_boost: Added control to wake_boost_duration. SmartPack: Integrated auto kernel flash with the latest version check. SmartPack: Show support status only for OOS. Misc: Updated Chinese translations (thanks again to @jason5545). build: Updated gradle to latest.

## 60. April 05, 2019
*Release-tag: v7.8*<br>
Changes: Gestures: add full control over flar's sweep2wake gestures. Gestures: dt2w: Restricted to enable or disable options (in-line with most of the modern devices). Gestures: sweep2sleep: Added support to new sysf's. Gestures: Switch to card view for a better visual experience. Misc: added an option to disable android's native doze mode. Translations: Updated Chinese traditional (thanks again to @jason5545)

## 59. April 02, 2019
*Release-tag: v7.7*<br>
Changes: CPU Input Boost: Switched little and big cluster input frequency control to more convenient select view. CPU Input boost: Updated input boost frequency control for devices using old sysfs paths to avoid random issues. CPU Input Boost: Extended control over new sysfs paths (includes wake_boost_duration). Translations: update Chinese traditional (credits: jason5545). Display & LED: Updated and switched Max and Min display control to input style so that users can choose any value. Overall: Applied *refresh* function 500 ms after applying many tasks to make sure that it's really working. Miscellaneous changes.

## 58. March 27, 2019
*Release-tag: v7.6*<br>
Changes: CPU Hotplug & Sound: Overall changes in the appearance. KSM: Updated for UKSM. GPU: Adreno idler & Simple GPU Algo: Visual enhancements. GPU: Added devfreq boost control. Screen: KLapse: Updated brightness control. Screen: KLapse: Brightness factor: Switched to seekbar view. SmartPack: Properly updated support check logic to avoid issues in discontinued devices. Thermal: MSM Simple: Updated thermal zone display style. LMK: Fixed some options not showing.

## 57. March 15, 2019
*Release-tag: v7.5*<br>
Changes: I/O: Add support to No-Merges & NR Requests. Other (minor) changes.

## 56. March 09, 2019
*Release-tag: v7.4*<br>
Changes: Screen: K-Lapse: added control over brightness-based scaling mode. Screen: K-Lapse: Separated backlight range Min & Max. CPU: CPU Boost: added support to Dynamic Stune Boost duration.

## 55. March 06, 2019
*Release-tag: v7.3*<br>
Changes: Screen: Added support to K-Lapse, A kernel level live-display module. Updated gradle to 3.3.2. Updated spectrum support checking method.

## 54. February 25, 2019
*Release-tag: v7.2*<br>
Changes: Moved profiles and backup's into a new folder "SP" inside internal storage. Misc: added control over Leases Breaks & Lease Break Time. Translations: added Chinese simplified (credits: Roiyaru). SmartPack: removed Moto G 2015 from the supported devices list.

## 53. February 01, 2019
*Release-tag: v7.1*<br>
Changes: Battery: Added thunder charge controller. Partially reverted the recent UI changes by considering user feedbacks (The app should now launch much fast). Wakelocks: Initialized boeffla wakelock blocker at launch for that the wakelocks blocked by default will be listed properly. Wakelocks: Fixed some wakelocks not showing up. Wakelocks: Added support to more wakelocks. Virtual memory: Updated title display style. Misc: Updated Chinese traditional translations.

## 52. January 26, 2019
*Release-tag: v7.0*<br>
Changes: User Interface is largely modified (e.g. Overall, CPU, GPU, Battery, LMK, Misc etc). Moved almost all the information services (Battery, charging & RAM) into the overall page. CPU: cleaned up & updated MSM Limiter. build: updated gradle build tools. About: added Translators. Battery: added OTG Switch to permanently enable USB On-The-Go (OTG) mode. AppUpdater: merged updates from the upstream repo. Misc: Added Chinese (Traditional) translation (Credits: jason5545). Miscellaneous other changes. Please visit GitHub for more information.

## 51. January 17, 2019
*Release-tag: v6.16*<br>
Changes: Misc: added an option to disable PrintK logging via printk_mode interface by Lord Boeffla. Battery: added support to Lord Boeffla's USB fast charge. VM: added control over zRAM compression algorithm. Display & LED: Unified minimum backlight control. Misc changes and some internal improvements.

## 50. January 08, 2019
*Release-tag: v6.15*<br>
Changes: CPU Input Boost: added dynamic stune boost configuration. SmartPack: removed klte* devices from the supported list. Sound: Boeffla Sound: updated Mic Volume to work without any special patch. Virtual Memory: Fixed ZRam Max Value.

## 49. January 03, 2019
*Release-tag: v6.14*<br>
Changes: CPU: CPU Boost: added advanced parameters of Dynamic Stune Boost and moved position to bottom. Screen: KCAL: update a bunch of parameters for msm_drm.

## 48. December 31, 2018
*Release-tag: v6.13*<br>
Changes: Display & LED: Fixed Max Backlight Control for some devices. Display & LED: Updated Min Backlight Control for msm_drm devices (e.g. Pocophone). CPU: Added control over Dynamic Stune Boost. CPU: Added initial support for MSM Limiter (only newer versions). Battery: Aligned everything under one card. SmartPack: Disabled everything related to klte SmartPack-Kernel. SmartPack header image: Updated to work in Dark Mode. Misc changes.

## 47. December 22, 2018
*Release-tag: v6.12*<br>
Changes: SmartPack: Latest version: Improved update information. build: promote target SDK Version to 28 (Pie). Switch Thermal, IO, Entropy, Display & LED pages into card view (visual improvements). Moved almost all the SmartPack related stuff to a new place "com/smartpack/kernelmanager" (internal improvement).

## 46. December 15, 2018
*Release-tag: v6.11*<br>
Changes: Sound: Microphone Flar: Fix negative values not showing correctly. Sound: cleaned a bunch of useless code. SmartPack: logs: fix some logs not saving. SmartPack: Update Auto-flash, Version check and change-logs for Android 9 (Pie). Device: add information about Project Treble Support.

## 45. December 11, 2018
*Release-tag: v6.10*<br>
Changes: Sound: Boeffla & Flar Headphone: Properly re-implement per-channel gain. Sound: Flar Sound: add earpiece gain. SmartPack: move all the log (logcat, dmesg, last_kmsg etc.) related features into Advanced Options. SmartPack: added options to get dmesg-ramoops, console-ramoops (if available) etc. Misc changes, mainly in Sound page.

## 44. December 04, 2018
*Release-tag: v6.9*<br>
Changes: Virtual Memory: Add RAM & Swap info. Virtual Memory: Switch to card view (visual enhancements). Sound: Faux & Boeflfa Headphone: Remove buggy per-channel gain. Sound: Overall changes including a bunch of internal improvements. Misc changes.

## 43. November 29, 2018
*Release-tag: v6.8*<br>
Changes: CPU Input Boost: Bunch of internal changes. CPU Input Boost Duration: switch to Input type. SmartPack: implement the latest kernel version notification (update check). SmartPack: overall (internal) improvements.

## 42. November 21, 2018
*Release-tag: v6.7*<br>
Changes: Performance tweaks: show un-supported status in quick tile if no spectrum support. CPU Hotplug: Core Control: add Task Threshold. Profile: remove editing profiles from the donated feature. SmartPack: Auto-flash: Internal changes for OnePlus 5/5T. Miscellaneous changes.

## 41. November 15, 2018
*Release-tag: v6.6*<br>
Changes: App signature: Updates to work on all devices including Samsung TW ROMs. Battery: update charge level interface to support original boeffla driver (no more modifications are needed). Display & Led: update LED brightness to work with more devices. LED Notification Intensity: allow to set any value. Settings: fix option to hide sections. build: update to stable AndroidX (and also update dependencies). SmartPack: Auto-flash: Internal changes for Pie ROMs.

## 40. November 06, 2018
*Release-tag: v6.5*<br>
Changes: Dispay & LED: update notification led control to for klte. Dispay & LED: properly update and unified some titles. Settings: remove unnecessary margin (visual improvements). Misc: Update androidx appcompat library.

## 39. November 03, 2018
*Release-tag: v6.4*<br>
Changes: Dispay & LED: switch charging light control to seekbar view (osprey). Wakelocks: Visual improvements. SmartPack: Update weblinks. Miscellaneous cleanings.

## 38. October 29, 2018
*Release-tag: v6.3*<br>
Changes: CPU Hotplug: add back MSM Hotplug. Alucard Hotplug: hide Advanced tunables from normal view.

## 37. October 23, 2018
*Release-tag: v6.2*<br>
Changes: Performance Tweaks: add Gaming profile. Performance Tweaks. Fix battery quick tile's not working properly. Update build grade tools (v3.2.1) and Android Studio.

## 36. October 18, 2018
*Release-tag: v6.1*<br>
Changes: Wakelocks: Minor bug fixes and improvements. About: link to new support page at xda. SmartPack: add one more klte variant (SM-G900FQ). SmartPack: Advanced (reboot & wipe) Options are now available for all the devices. 

## 35. October 13, 2018
*Release-tag: v6.0*<br>
Changes: Migrate to AndroidX support library. SDK: Update to 28 (Pie). Thermel: MSM thermal: add support to a bunch of new parameters. SmartPack: Preparations for Pie (SDK == 28) kernel release. SmartPack: Auto-flash: add an alert dialogue with a lot of important information. Wakelocks: update and add a bunch of new wakelocks. About: update support library list. Misc changes.

## 34. October 07, 2018
*Release-tag: v5.4*<br>
Changes: CPU: Update CPU Input Boost parameters. Misc: SmartPack: update Auto-flash and other advanced options to ensure data integrity while reboot. SDK: revert to 27 to fix apply on boot on some android versions.

## 33. September 29, 2018
*Release-tag: v5.3*<br>
Changes: Misc: fix Software CRC check not showing correct status. Tools: Remove all the profile related functions from the donated list. CPU Hotplug: Alucard: Add advanced parameters. Remove Recovery page and add all the necessary functions into SmartPack Page (Advanced Options). Update Gradle builds tools and dependencies. Miscellaneous cleanups and fixes.

## 32. September 27, 2018
*Release-tag: v5.2*<br>
Changes: Thermal: add full control over Simple MSM Thermal driver  by Sultanxda. Virtual Memmory: Add a bunch of parameters. Core Control: fix some issues.

## 31. September 19, 2018
*Release-tag: v5.1*<br>
Changes: SmartPack: Fully re-construct kernel auto-flash (now much more simple & accurate). Live charging status: Add charging source (Cash/AC/USB) information. Misc: add WireGuard version information. Initial support for OnePlus 5 (since it share a common kernel source with 5T). A lot of under-the-hood changes in Battery, CPU & Sound pages.

## 30. September 10, 2018
*Release-tag: v5.0*<br>
Changes: CPU Boost: add control over CPU Input Boost Freq. Overall UI enhancements. SmartPack: Improve auto-flash. appupdater: fetch updates from upstream repo.

## 29. September 08, 2018
*Release-tag: v4.10*<br>
Changes: Add full control to MSM Sleeper. Update Gradle build dependencies. Add all the CPU boost functions (CPU Boost, CPU Input Boost & Touch Boost) into a single card. Misc changes.

## 28. September 05, 2018
*Release-tag: v4.9*<br>
Changes: SmartPack: add alert messages to Reboot and Reset options. OP5T: add OnePlus version info in Device page and ad support to download Open Beta kernels. Add an option to adjust minimum backlight (brightness). Re-done live charging status and maximum display backlight so that it will work on other devices as well. Misc changes.

## 27. August 29, 2018
*Release-tag: v4.8*<br>
Changes: Added xda support page (Moto G 2015). Update vibration intensity for OP5T. Add an option to directly flash the new kernel (along with download button). Add an option to reset app settings (SmartPack page). Restrict SmartPack specific stuff to SDK >= 27 (Oreo). Misc changes.

## 26. August 27, 2018
*Release-tag: v4.7*<br>
Changes: Added xda support page (OP5T). Fully re-made Advanced Charge Control Interface logic to avoid issues in other devices. LED: add a switch to disable charging light (Moto G 2015). Re-added faux sound with per-channel headphone gain support. Add device specific links for Moto G 2015. 

## 25. August 22, 2018
*Release-tag: v4.6*<br>
Changes: Added control over Boeffla Mic and Earpiece boost (OP5T). Added Adreno boost control (OP5T). Update charge rate and maximum display backlight control to work properly in OP5T. Bunch of other changes to support OP5T.

## 24. August 01, 2018
*Release-tag: v4.5*<br>
Changes: Clean and update Wakelock page (Now it is significantly fast). Made device-specific kernel download more accurate.

## 23. July 26, 2018
*Release-tag: v4.4*<br>
Changes: Added "SmartPack" page, which will allow the device (and SDK level) specific download of the latest kernel all to the supported devices. Removed the old kernel update implementation. Clean-ups to remove a lot of unwanted and fancy stuff from the app.

## 22. July 03, 2018
*Release-tag: v4.3*<br>
Changes: Added a new switch to instantly disable charging (can be used while changing charging rates). Merged lot of changes from official KA. Removed some unwanted options from settings page.

## 21. May 31, 2018
*Release-tag: v4.2*<br>
Changes: Added version information to boeffla sound. Blue accent is now more intense, like *Pixel* blue. Added new SmartPack header image.

## 20. May 16, 2018
*Release-tag: v4.1*<br>
Changes: Added new 'wakelock' page with full control over Boeffla generic wakelock blocker. Clean-ups and miscellaneous changes.

## 19. May 10, 2018
*Release-tag: v4.0*<br>
Changes: Move auto update check to "Overall" page to initiate app update check at start-up. Update build grade dependancies. Merged a lot of important updates from official KA. Clean-ups and miscellaneous changes.

## 18. May 04, 2018
*Release-tag: v3.8*<br>
Changes: Added LED blink/fade control. Added options to control LED (Green, Red and Blue LEDs separately) brightness. Moved Display backlight control from Misc to Display and LED page. Some internal changes in "Performance Tweaks". Added Boeffla Charge Level control (needed some changes in the charge_level code). Clean-ups and miscellaneous changes in Battery, FAQ and various other pages. 

## 17. April 30, 2018
*Release-tag: v3.7*<br>
Changes: Fixed force closing app while trying to create a *New* Profile. Enabled icons for various pages. Removed Performance Tweaks from Profiles section to avoid conflicts. Removed some obsolete functions from the build environment.

## 16. April 29, 2018
*Release-tag: v3.6*<br>
Changes: App-update: make it more automated and moved into About Page (Now it can download and prompt for the installation). Built on Android Studio 3.1.2, latest stable release. Miscellaneous changes.

## 15. April 27, 2018
*Release-tag: v3.5*<br>
Changes: Boeffla Sound: implement per-channel control for headphone gain. Remove input boost freq control. Performance Tweaks: implemented Spectrum Kernel Manager based profile support. Added a switch to check for updates in Settings. Added a bunch of data in About fragment. Miscellaneous changes.

## 14. April 22, 2018
*Release-tag: v3.4*<br>
Changes: Added support for Boeffla Sound engine (Sound). Added Real-time charging status (Battery). Switch to Blue accent colors. Rearrange all the Tabs (Updates is now at the bottom). Removed card view from various fragments (not all). Simplified GPU and CPU hotplug a bit (advanced hotplugging options will be visible only if the respective driver is enabled; please note, refreshing the respective pages are required). All-over clean-ups.

## 13. April 13, 2018
*Release-tag: v3.3*<br>
Changes: Merged a lot of changes from official KA.(Misc) Added an option to control maximum display backlight.

## 12. March 22, 2018
*Release-tag: v3.2*<br>
Changes: (Battery) Added an interface for advanced charge control (AC, USB, and Wireless). (CPU) Update CPU_Input_Boost with an option to set boost duration and frequency.

## 11. March 19, 2018
*Release-tag: v3.1*<br>
Changes: Added a switch to change SELinux mode (Misc). Merged a lot (not all) of changes from official KA. Some improvements in the OTA section.

## 10. Feb 18, 2018
*Release-tag: v3.0*<br>
Changes: Fully rebuild from the latest official KA source.

## 9. Feb 01, 2018
*Release-tag: v2.5*<br>
Changes: Updated OTA supported device list (added devices: SM-G900M, SM-G900R4, SM-G900R7, SM-G900T & M-G900W8)

## 8. Jan 26, 2018
*Release-tag: v2.4*<br>
Changes: Added full control over AC/USB/Wireless charge (Credits to @micky387). Added a switch to enable CPU input boost.

## 7. Jan 23, 2018
*Release-tag: v2.3*<br>
Changes: Removed data sharing since it is annoying and useless for this modded app. Made Update page more device-specific (more user-friendly). Modified Donate app purchase dialogue. Up-to-date with official KA.

## 6. Jan 16, 2018
*Release-tag: v2.2*<br>
Changes: Minor update: Re-implement OTA support to all the supported variants. Some minor changes.

## 5. Jan 7, 2018
*Release-tag: v2.1*<br>
Changes: Built using Android studio v3 (new). A lot of unused (in SmartPack-Kernel) features (also translations) were simply removed to make the app lighter and faster. Made "Custom controls" as a free feature. Removed kernel download links and renamed “Download” section into “Check for Update” (now it is only for app update). Some simple modifications in “About” section.

## 4. Dec 20, 2017
*Release-tag: v2.1*<br>
Changes: Change app id in-order to avoid conflicts with the official app. Adds, crashlytics, google services, etc. have been removed from the app. Removed some unused (by SmartPack-Kernel) stuff from the app to make it more lighter. Up-to-date with official KernelAdiutor.

## 3. Nov 27, 2017
*Release-tag: v1.4*<br>
Changes: Up-to-date with official KernelAdiutor (which includes an important workaround to fix "apply on boot" in android O).

## 2. Nov 14, 2017
*Release-tag: v1.3*<br>
Changes: Some simple modifications (mainly cosmetic). Updated FAQ section.

## 1. Nov 14, 2017
*Release-tag: v1.2*<br>
The very first public release of SmartPack-Kernel Manger, which is a modified version of the official Kernel Adiutor, originally developed by Willi Ye aka Grarak. All the credits goes to the original developer.
