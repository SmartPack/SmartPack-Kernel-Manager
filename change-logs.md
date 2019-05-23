# Change-logs

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
