#!/bin/bash

COLOR_RED="\033[0;31m"
COLOR_GREEN="\033[1;32m"
COLOR_NEUTRAL="\033[0m"
PROJECT_ROOT=$PWD
APK_SIGN_KEY="../app/sp.jks"

# Checking the existence of apk signing key
if [ -e $APK_SIGN_KEY ]; then
	echo -e $COLOR_GREEN"\n Copying apk signing key to the download folder..."$COLOR_NEUTRAL
	cp $APK_SIGN_KEY spsigningkey.jks

	echo -e $COLOR_GREEN"\n Re-signing apk..."$COLOR_NEUTRAL
	jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore spsigningkey.jks com.smartpack.kernelmanager.apk sp_key1

	# Remove the signing key file once the process is done
	rm -r spsigningkey.jks

	echo -e $COLOR_GREEN"\n Everything done..."$COLOR_NEUTRAL
else
	echo -e $COLOR_RED"\n Signing key not found..."$COLOR_RED
fi
