# Important notice for Android 10+ users

In order to meet the new regulations of Google, the following changes are made to SmartPack-Kernel Manager, and are in effect from v16.5 onwards.

* The internal storage folder of SmartPack-Kernel Manager in newer Android (10 and above) versions are now moved from '<b>/sdcard/SP</b>' to  '<b>/sdcard/Android/data/application_id/files</b>'. This means the app will lose access to the already existing files, such as the partition images, custom controls, scripts etc unless the users moved the files from the old path to the new one. Upon first launching the new version (<b>v16.5</b>) of the application, users will be prompted with a migration message. <b>Please Note</b>: This change only affect the existing Android 10+ users. If you're a new user or using SmartPack-Kernel Manager on an older Android (on or below Android 9) version, please ignore this.

* SmartPack-Kernel Manager will now onwards uses an in-built file picker for various purposes (such as Flashing, importing custom controls, scripts etc), and do not support using external file manager applications anymore.