# Welcome to AndroidRadio
An Android app and companion [Flask](http://flask.pocoo.org/)
application which can be used to turn a device like a
[C.H.I.P.](http://getchip.com/) or
[RaspberryPi](https://www.raspberrypi.org/)
attached to a speaker or stereo into a streaming Radio player. Browse
hundreds of streams from around the world on your Android device and
play what you wish on your stereo through an attached embedded Linux
device.

![screenshot](https://raw.githubusercontent.com/masselstine/AndroidRadio/master/screenshot.jpg "screenshot")

## Building
The project is built with Android Studio 3.0.1 and Android SDK 25
Revision 3 (Android 7.1.1 Nougat). APKs may be made available for
download once the project is a bit further along.

There is no build required for the Flask application that will run on
the embedded Linux device connected to your stereo or speaker.

## Server Prerequisites
Your embedded Linux device must have mDNS configured, your device
should be reachable using chip.local or similar (the C.H.I.P. ships
with mDNS configured). You must install 'mpv' and 'python3-flask'
> sudo apt install mpv python3-flask python3-requests

## Running the radio_host.py
Copy the radio_host.py file to your embedded device. Run the
radio_host.py in the following way:
> python3 radio_host.py

## NOTES
This is a personal project that is a work in progress. It is being
shared since I thought others might find it useful. I will review and
merge pull requests if they are sent.
