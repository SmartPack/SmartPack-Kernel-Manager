# SmartPack-Kernel Manager

## SmartPack-Kernel Manager is a modified version of official Kernel Adiutor, which is originally developed by Willi Ye aka Grarak. All the credits goes to the original developer.

## Download
### ! [Download latest build](https://github.com/SmartPack/SmartPack-Kernel-Manager/releases/latest) ! [All builds](https://github.com/SmartPack/SmartPack-Kernel-Manager/releases) !
## Added features over official KA

* Advanced Charge Control Interface for yank555.lu's Fast Charge
* CPU Input Boost (Sultanxda) Control
* Boeffla Sound (Please note: Currently requires some changes in boeffla_sound.c. Please refer [this](https://github.com/SmartPack/SmartPack-Kernel-Project_kltexxx/commit/d2cb5948f31997070e334d60523c86d24139d051) commit).
* Boeffla Charge Level Interface (Please note: Currently requires some changes in charge_level.c. Please refer [this](https://github.com/SmartPack/Boeffla-Kernel-unofficial-kltexxx/commit/4e22b13b8f1b0c22dff9600e7c1c93e3cc5dad9b) commit).
* Spectrum support in-built
* Display Backlight Control
* SELinux Switch
* Auto-updates (in *About* page)
* Real-time Charging Status
* LED Blink/Fade support
* Significantly modified (for SmartPack-Kernel) OTA implementation
* Dedicated Frequently Asked Questions (FAQ) section for SmartPack-Kernel (Manager)

## Relevant links

[SmartPack-Kernel for Lineage-OS 14.1/15.1 (kltexxx)](https://forum.xda-developers.com/galaxy-s5/unified-development/kernel-project-kltexxx-t3564206)

[SmartPack-Kernel for Stock Marshmallow (kltexxx)](https://forum.xda-developers.com/galaxy-s5/development/kernel-smartpack-project-stock-t3568810)

***** ***** ***** ***** ***** ***** ***** ***** ***** ***** *****

# About official Kernel Adiutor

[![Build Status](https://travis-ci.org/Grarak/KernelAdiutor.svg?branch=master)](https://travis-ci.org/Grarak/KernelAdiutor)
[![Crowdin](https://d322cqt584bo4o.cloudfront.net/kernel-adiutor/localized.png)](https://crowdin.com/project/kernel-adiutor)

* [Join Google+ Community](https://plus.google.com/communities/108445529270785762340) (Beta Testing)

[![Google Play](http://developer.android.com/images/brand/en_generic_rgb_wo_60.png)](https://play.google.com/store/apps/details?id=com.grarak.kerneladiutor)
[![PayPal](https://www.paypalobjects.com/webstatic/mktg/Logo/pp-logo-200px.png)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=G3643L52LJQ7G)

## Kernel Downloader

This is a feature which allows the user to download different custom kernels for their device. To add more kernel the developers have to add support by their own. I wrote up a [documentation](https://github.com/Grarak/KernelAdiutor/wiki/Adding-Download-Support) which explains how to do this.

## Report a bug or request a feature

You can report a bug or request a feature by [opening an issue](https://github.com/Grarak/KernelAdiutor/issues/new).

#### How to report a bug
* A detailed description of the bug
* Logcat
* Make sure there are no similar bug reports already

#### How to request a feature
* A detailed description of the feature
* All kind of information
* Paths to sys interface
* What's the content if the sys file
* How to apply a new value
* Make sure there are no similar feature requests already

## Download & Build

Clone the project and come in:

``` bash
$ git clone git://github.com/Grarak/KernelAdiutor.git
$ cd KernelAdiutor
$ ./gradlew build
```

## Credits

I used following libraries:

* Google: [v4 Support Library](https://developer.android.com/topic/libraries/support-library/features.html#v4)
* Google: [v7 appcompat library](https://developer.android.com/topic/libraries/support-library/features.html#v7)
* Google: [v7 cardview library](https://developer.android.com/topic/libraries/support-library/features.html#v7)
* Google: [Design Support Library](https://developer.android.com/topic/libraries/support-library/features.html#design)
* Google: [v7 recyclerview library](https://developer.android.com/topic/libraries/support-library/features.html#v7)
* Ozodrukh: [CircularReveal](https://github.com/ozodrukh/CircularReveal)
* Roman Nurik: [dashclock](https://github.com/romannurik/dashclock)
* Google: [Firebase](https://firebase.google.com)
* Matthew Precious: [swirl](https://github.com/mattprecious/swirl)
* Lopez Mikhael: [CircularImageView](https://github.com/lopspower/CircularImageView)
* Square: [picasso](https://github.com/square/picasso)
* CyanogenMod: [CyanogenMod Platform SDK](https://github.com/CyanogenMod/cm_platform_sdk)

Also codes from different people:

#### Andrei F.

* [RootUtils](https://github.com/Grarak/KernelAdiutor/blob/master/app/src/main/java/com/grarak/kerneladiutor/utils/root/RootUtils.java)

#### apbaxel

_(Many sys interface paths has been taken from his [UKM-Project](https://github.com/apbaxel/UKM))_

#### Brandon Valosek

* [CpuSpyApp](https://github.com/Grarak/KernelAdiutor/blob/master/app/src/main/java/com/bvalosek/cpuspy/CpuSpyApp.java)
* [CpuStateMonitor](https://github.com/Grarak/KernelAdiutor/blob/master/app/src/main/java/com/bvalosek/cpuspy/CpuStateMonitor.java)
* [OverallFragment](https://github.com/Grarak/KernelAdiutor/blob/master/app/src/main/java/com/grarak/kerneladiutor/fragments/statistics/OverallFragment.java)

## License

    Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
    
    Kernel Adiutor is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    Kernel Adiutor is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
