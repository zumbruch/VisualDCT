# Visual Database Configuration Tool (VDCT, VisualDCT)

This README is intended to inform the EPICS community of the development status of VDCT. It is meant to complement the information on the Cosylab page, not duplicate it.

## What is VDCT?

VDCT is the Visual Database Configuration Tool. It began as a project funded by the Swiss Light Source and developed by [Cosylab](http://www.cosylab.com/). Later development was funded by the APS, Diamond and the SNS. The code is maintained by Cosylab under a Free Software license (GPLv3). Source code and binaries are available from the Cosylab web site.

Any bugs should be reported [here](https://github.com/epics-extensions/VisualDCT/issues).

## Where to find it

VDCT appears on several pages on Cosylab's web-server

* [Downloads page](http://cosylab.com/resources/downloads_/). A link points to the latest available build of VDCT.
* [VDCT Builds page](http://visualdct.cosylab.com/builds/VisualDCT/). Current and previous builds, plus links to all documentation.
* [VisualDCT User's Manual](http://visualdct.cosylab.com/builds/VisualDCT/2.6.1274/doc/MAN-VisualDCT_Users_Manual.html).
* [VisualDCT Hierarchy Additions](http://visualdct.cosylab.com/builds/VisualDCT/2.6.1274/doc/SPE-VisualDCT_Hierarchy_Additions.html). Using templates.

Additional documentation on internal topics is also linked from the Builds page.

## Installation Tips

You don't need to build VDCT to install it. You can just get hold of the binary files and run it. You need a Java Runtime Environment to run it. These notes refer to version 2.5.1271 but there is at least one later builds available.

1. Go to the [Downloads page](http://cosylab.com/resources/downloads_/) at the CosyLab site
2. Click on the "Download latest" button in the VisualDCT section.
3. This should begin the download of the distribution zip file.
4. Save the zip file somewhere convenient.
5. Unzip the zip file, to somewhere convenient. On Windows systems a "vdct" directory in the "Program Files" directory seems a good place.
6. You should then see a "2.5.1271" directory. In that you'll see a file named "runScript".
7. Execute the file named "runScript" and VDCT should start. On windows systems you will have to rename "runScript" to "runSript.bat" first.

## Wish list - Possible long term requirements

An old list of these is on the [VDCT Bugs and Features Page](https://wiki-ext.aps.anl.gov/epics/index.php/VDCT_Bugs_and_Features_Page). This contains a list of known bugs, plus a list of things that people have come up with as "wouldn't it be nice if" (WIBNIf) ideas. They may or may not get implemented. In true EPICS tradition, if you really want something the best way to get it is to fund the development or contribute effort.

## VDCT mailing list

If you are interested in the development of VDCT you can subscribe to the VDCT [mailing list](http://lists.cosylab.com/).