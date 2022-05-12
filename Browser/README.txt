Introduction
------------

This document describes:
  How to build with ANT
  How to set up a Virtual Image based development environment
  How to set up an Eclipse project
  How to set up a RAD (Rational Application Developer) project
  How to deploy to the Sun Application Server

Building with ANT
-----------------
1. Ensure ANT is on your path (if not, add it's lib directory; it's checked into the should-be-shared directory of the Browser build).
2. In a command window, cd to the Browser directory (the one containing build.xml).
3. Type 'ant'.

This will do the build, with both WARs and 'exploded' WARs being placed into the target directory.


Using the VMWare Image 
----------------------
The VMWare image has T24, Browser, Eclipse, Perforce, and an app server installed and configured.
This makes it easy to grab the image and get productive quickly.
To use the VMWare image, follow the readme in:
  \\ukfch-fs3\dev\ARC Internet Banking\Development\Dev Virtual Image


Setting up an Eclipse based Development Environment
---------------------------------------------------
Follow the readme in:
  Browser\config\project_files\eclipse


Setting up a RAD based Development Environment
---------------------------------------------------
Follow the readme in:
  Browser\config\project_files\rad
 

Deploying to a Sun Application Sevrer
-------------------------------------
There are ant tasks to automate this process.
Type the following in a command window in the main Browser directory for details:
  ant -f <filename> -p
<filename> should be replaced by the name of the build file for the appropriate configuration, e.g. build-browser.xml
