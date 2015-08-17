KernelAdiutor
=============
[![Crowdin](https://d322cqt584bo4o.cloudfront.net/kernel-adiutor/localized.png)](https://crowdin.com/project/kernel-adiutor)

<img alt="Screenshot"
   src="https://raw.githubusercontent.com/Grarak/KernelAdiutor/master/screenshots/screenshot.png" />

* [Join Google+ Community](https://plus.google.com/communities/108445529270785762340) (Beta Testing)

<a href="https://play.google.com/store/apps/details?id=com.grarak.kerneladiutor">
  <img alt="Google Play"
       src="http://developer.android.com/images/brand/en_generic_rgb_wo_60.png" />
</a> <a href="https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=JSCNTZC4H73JG">
  <img alt="PayPal" width="200" height="70"
       src="https://support.ankama.com/hc/fr/article_attachments/200892097/Paypal.jpg" />
</a>

Kernel Downloader
----------------

This is a feature which allows the user to download different custom kernels for their device. To add more kernel the developers have to add support by their own. I wrote up a [documentation](https://github.com/Grarak/KernelAdiutor/wiki/Adding-Download-Support) which explains how to do this.

Report a bug or request a feature
----------------
You can report or request a features by opening an issue [Open new issue][1]

#### How to report a bug
* A detailed description of the bug
* Logcat
* Make sure there are no similar bug reports already

#### How to request a feature
* A detailed description of the features
* All kind of information
* Paths to sys interface
* What's the content if the sys file
* How to apply a new value
* Make sure there are no similar feature requests already

Download & Build
----------------
Clone the project and come in:

``` bash
$ git clone git://github.com/Grarak/KernelAdiutor.git
$ cd KernelAdiutor
$ ./gradlew build
```

Credits
----------------

I used following libraries:

* Google: [Appcompat v7](https://developer.android.com/tools/support-library/features.html#v7-appcompat)
* Google: [Cardview v7](https://developer.android.com/tools/support-library/features.html#v7-cardview)
* Google: [Recyclerview v7](https://developer.android.com/tools/support-library/features.html#v7-recyclerview)
* Jerzy Chałupski: [FloatingActionButton](https://github.com/futuresimple/android-floating-action-button)
* Roman Nurik: [Dashclock](https://github.com/romannurik/dashclock)
* Jake Wharton: [NineOldAndroids](https://github.com/JakeWharton/NineOldAndroids)
* Square Inc.: [Picasso](https://github.com/square/picasso)
* Karim Frenn: [MaterialTabs](https://github.com/pizza/MaterialTabs)

Also codes from different people:

#### Andrei F.

* [RootUtils](https://github.com/Grarak/KernelAdiutor/blob/master/library/src/main/java/com/kerneladiutor/library/root/RootUtils.java)

#### apbaxel

* [Constants](https://github.com/Grarak/KernelAdiutor/blob/master/app/src/main/java/com/grarak/kerneladiutor/utils/Constants.java)

_(Many sys interface paths has been taken from his [UKM-Project](https://github.com/apbaxel/UKM))_

#### Brandon Valosek

* [CpuSpyApp](https://github.com/Grarak/KernelAdiutor/blob/master/app/src/main/java/com/bvalosek/cpuspy/CpuSpyApp.java)
* [CpuStateMonitor](https://github.com/Grarak/KernelAdiutor/blob/master/app/src/main/java/com/bvalosek/cpuspy/CpuStateMonitor.java)
* [FrequencyTableFragment](https://github.com/Grarak/KernelAdiutor/blob/master/app/src/main/java/com/grarak/kerneladiutor/fragments/information/FrequencyTableFragment.java)

#### Google

* [ScrimInsetsFrameLayout](https://github.com/Grarak/KernelAdiutor/blob/master/app/src/main/java/com/grarak/kerneladiutor/elements/ScrimInsetsFrameLayout.java)

License
----------------

    Copyright (C) 2015 Willi Ye

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
