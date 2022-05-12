Setting up an Eclipse Project
-----------------------------


Setting up Eclipse
------------------
A copy of Eclipse with all of the required plugins is avaliable at:
  \\Ukfch-fs3\DEV\ARC Internet Banking\Development\Eclipse\eclipse.zip

If you want to use an existing deployment of Eclipse instead, ensure you have the following plugins installed:
  WTP
  Checkstyle
  JSEclipse (optional)
  Perforce (optional)

Next, copy the files from this directory to the Browser directory.
This can be done manually or by entering the following in a command line in the Browser directory:
  ant eclipse-config

Now, the project needs to be set up:
  Run eclipse.
  Select 'Import' from the file menu.
  Select 'Existing projects into workspace' from the 'General' node
  Select the directory that contains the Browser project (as downloaded from Perforce, e.g. D:\work\ARC\Browser).
  You should see 'Browser' with a tick next to it in the Projects pane.
  Press 'finish'.
  
A variable must be set up in order for dependency JARs to be located:
  Select 'Properties' from the 'Project' menu.
  Select 'Java build path' in the tree.
  From the 'Library' tag, select 'Add Variable...'
  Select 'Configure Variables ...'
  Select 'New'
  Enter 'TEMENOS_SHARED_HOME' for the name
  Enter a location to the 'shared' directory.
    This is not yet really shared; use the 'should-be-shared' sub directory (as downloaded from Perforce, e.g. D:\work\ARC\should-be-shared).
  
  
Deploying to the Sun Application Server from Eclipse
----------------------------------------------------
We do this by mapping ANT tasks to the External tools toolbar in Eclipse.
This gives us one click - build / deploy / update for the application.
See Dave Burford for more details (I will expand this section on request).


Debugging in the Sun Application Server
---------------------------------------
This is simple to set up and use, see: http://ukhml-t24frame.europe.temenosgroup.com/mediawiki/index.php/Getting_Eclipse_debugger_to_connect_to_Glassfish