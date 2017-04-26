jMicro is a java free software **designed for children** to use computer microscope with Linux. With JMicro you can take photos, take planned photo and create slideshow video from them. You can also interact with the principal controls of Video 4 Linux and for some microscope (e.g QX3 and QX5) turn on/off the lamps.


# Interface

Here, some screenshots of the interface:
![First Start](/screens/screenshot0.png)

![Project Selection](/screens/screenshot1.png)

![Main window](/screens/screenshot2.png)

![Settings](/screens/screenshot3.png)

![Update available](/screens/screenshot4.png)


## Implemented functionality list

* Take pictures
* Take planned pictures and create slideshow video from them
* Built-in preview of taken pictures and created videos
* Manage projects
* Set brightness
* Turn ON/OFF QX3 lamps
* Auto updates


## Unsupported at the moment

* Pressing the hardware button on microscopes does not trigger a new picture
* Filters on images

# Known Issues
* If using directories name with spaces when updating another directory path using "%20" instead of the spaces will be created.

 
# Frequently Asked Questions

* **How can I access the Control Settings using the normal user?**

You can access the Control Settings using the ALT+C shortcut. 


* **When reaching the main window the program crashes with an error similar to 'Problematic frame: C  [libjpeg.so.8] jpeg_suppress_tables'. What can I do?**

We found that using the OpenJDK Java version this error might occour.
To solve install the OracleSDK java implementation.

# Third party libraries

**All third party libraries are embedded in the program so that the final user does not have to take care of dependences.**

Here the list of the embedded libraries:

* [vlcj](http://www.capricasoftware.co.uk/projects/vlcj/index.html) To play videos
* [v4l4j](https://code.google.com/p/v4l4j/) To access video 4 linux
* [common-compress](https://commons.apache.org/proper/commons-compress/) To extract .tar.bz2  files 
* [jCodec](http://jcodec.org/) To create timelaps videos from png images

# Contribute

## Report a problem or suggestion

Go to our [issue tracker](https://github.com/nicolacdnll/jmicro/issues) and check if your problem/suggestion is already reported. If not, create a new issue with a descriptive title and detail your suggestion or steps to reproduce the problem.

## Help us translating

If you would like to help us adding a new language, please contact us.


## Licensing

The source code is licensed under GPL v3. License is available [here](/LICENSE).
