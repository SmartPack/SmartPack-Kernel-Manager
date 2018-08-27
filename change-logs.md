# Change-logs

## 26. August 27, 2018
*Release-tag: v4.7*
Changes: Added xda support page (OP5T). Fully re-made Advanced Charge Control Interface logic to avoid issues in other devices. LED: add a switch to disable charging light (Moto G 2015). Re-added faux sound with per-channel headphone gain support. Add device specific links for Moto G 2015. 

## 25. August 22, 2018
*Release-tag: v4.6*
Changes: Added control over Boeffla Mic and Earpiece boost (OP5T). Added Adreno boost control (OP5T). Update charge rate and maximum display backlight control to work properly in OP5T. Bunch of other changes to support OP5T.

## 24. August 01, 2018
*Release-tag: v4.5*
Changes: Clean and update Wakelock page (Now it is significantly fast). Made device-specific kernel download more accurate.

## 23. July 26, 2018
*Release-tag: v4.4*
Changes: Added "SmartPack" page, which will allow the device (and SDK level) specific download of the latest kernel all to the supported devices. Removed the old kernel update implementation. Clean-ups to remove a lot of unwanted and fancy stuff from the app.

## 22. July 03, 2018
*Release-tag: v4.3*
Changes: Added a new switch to instantly disable charging (can be used while changing charging rates). Merged lot of changes from official KA. Removed some unwanted options from settings page.

## 21. May 31, 2018
*Release-tag: v4.2*
Changes: Added version information to boeffla sound. Blue accent is now more intense, like *Pixel* blue. Added new SmartPack header image.

## 20. May 16, 2018
*Release-tag: v4.1*
Changes: Added new 'wakelock' page with full control over Boeffla generic wakelock blocker. Clean-ups and miscellaneous changes.

## 19. May 10, 2018
*Release-tag: v4.0*
Changes: Move auto update check to "Overall" page to initiate app update check at start-up. Update build grade dependancies. Merged a lot of important updates from official KA. Clean-ups and miscellaneous changes.

## 18. May 04, 2018
*Release-tag: v3.8*
Changes: Added LED blink/fade control. Added options to control LED (Green, Red and Blue LEDs separately) brightness. Moved Display backlight control from Misc to Display and LED page. Some internal changes in "Performance Tweaks". Added Boeffla Charge Level control (needed some changes in the charge_level code). Clean-ups and miscellaneous changes in Battery, FAQ and various other pages. 

## 17. April 30, 2018
*Release-tag: v3.7*
Changes: Fixed force closing app while trying to create a *New* Profile. Enabled icons for various pages. Removed Performance Tweaks from Profiles section to avoid conflicts. Removed some obsolete functions from the build environment.

## 16. April 29, 2018
*Release-tag: v3.6*
Changes: App-update: make it more automated and moved into About Page (Now it can download and prompt for the installation). Built on Android Studio 3.1.2, latest stable release. Miscellaneous changes.

## 15. April 27, 2018
*Release-tag: v3.5*
Changes: Boeffla Sound: implement per-channel control for headphone gain. Remove input boost freq control. Performance Tweaks: implemented Spectrum Kernel Manager based profile support. Added a switch to check for updates in Settings. Added a bunch of data in About fragment. Miscellaneous changes.

## 14. April 22, 2018
*Release-tag: v3.4*
Changes: Added support for Boeffla Sound engine (Sound). Added Real-time charging status (Battery). Switch to Blue accent colors. Rearrange all the Tabs (Updates is now at the bottom). Removed card view from various fragments (not all). Simplified GPU and CPU hotplug a bit (advanced hotplugging options will be visible only if the respective driver is enabled; please note, refreshing the respective pages are required). All-over clean-ups.

## 13. April 13, 2018
*Release-tag: v3.3*
Changes: Merged a lot of changes from official KA.(Misc) Added an option to control maximum display backlight.

## 12. March 22, 2018
*Release-tag: v3.2*
Changes: (Battery) Added an interface for advanced charge control (AC, USB, and Wireless). (CPU) Update CPU_Input_Boost with an option to set boost duration and frequency.

## 11. March 19, 2018
*Release-tag: v3.1*
Changes: Added a switch to change SELinux mode (Misc). Merged a lot (not all) of changes from official KA. Some improvements in the OTA section.

## 10. Feb 18, 2018
*Release-tag: v3.0*
Changes: Fully rebuild from the latest official KA source.

## 9. Feb 01, 2018
*Release-tag: v2.5*
Changes: Updated OTA supported device list (added devices: SM-G900M, SM-G900R4, SM-G900R7, SM-G900T & M-G900W8)

## 8. Jan 26, 2018
*Release-tag: v2.4*
Changes: Added full control over AC/USB/Wireless charge (Credits to @micky387). Added a switch to enable CPU input boost.

## 7. Jan 23, 2018
*Release-tag: v2.3*
Changes: Removed data sharing since it is annoying and useless for this modded app. Made Update page more device-specific (more user-friendly). Modified Donate app purchase dialogue. Up-to-date with official KA.

## 6. Jan 16, 2018
*Release-tag: v2.2*
Changes: Minor update: Re-implement OTA support to all the supported variants. Some minor changes.

## 5. Jan 7, 2018
*Release-tag: v2.1*
Changes: Built using Android studio v3 (new). A lot of unused (in SmartPack-Kernel) features (also translations) were simply removed to make the app lighter and faster. Made "Custom controls" as a free feature. Removed kernel download links and renamed “Download” section into “Check for Update” (now it is only for app update). Some simple modifications in “About” section.

## 4. Dec 20, 2018
*Release-tag: v2.1*
Changes: Change app id in-order to avoid conflicts with the official app. Adds, crashlytics, google services, etc. have been removed from the app. Removed some unused (by SmartPack-Kernel) stuff from the app to make it more lighter. Up-to-date with official KernelAdiutor.

## 3. Nov 27, 2018
*Releas4-tag: v1.4*
Changes: Up-to-date with official KernelAdiutor (which includes an important workaround to fix "apply on boot" in android O).

## 2. Nov 14, 2018
*Releas4-tag: v1.3*
Changes: Some simple modifications (mainly cosmetic). Updated FAQ section.

## 1. Nov 14, 2018
*Releas4-tag: v1.2*
The very first public release of SmartPack-Kernel Manger, which is a modified version of the official Kernel Adiutor, originally developed by Willi Ye aka Grarak. All the credits goes to the original developer.
