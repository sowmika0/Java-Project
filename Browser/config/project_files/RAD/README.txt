Setting up a RAD Project
------------------------


Setting up RAD
------------------
The install files for an evaluation copy of RAD are in:
  \\Ukfch-fs3\DEV\Chimera\Downloads\ibm\rad


Copy the files from this directory to the Browser directory.
This can be done manually or by entering the following in a command line in the Browser directory:
  ant rad-config

Now, the projects (Browser and BrowserEAR) needs to be set up:
  Run RAD.
  Import Browser Project
    Select 'Import' from the file menu.
    Select 'Existing projects into workspace' from the 'General' node
    Select the directory that contains the Browser project (as downloaded from Perforce, e.g. D:\work\ARC\Browser).
    You should see 'Browser' with a tick next to it in the Projects pane.
    Press 'finish'.
  Import BrowserEAR Project
    As above, but for the BrowserEAR Project, a child directory of the Browser directory.

  
A variable must be set up in order for dependency JARs to be located:
  Select 'Properties' from the 'Project' menu.
  Select 'Java build path' in the tree.
  From the 'Library' tag, select 'Add Variable...'
  Select 'Configure Variables ...'
  Select 'New'
  Enter 'TEMENOS_SHARED_HOME' for the name
  Enter a location to the 'shared' directory.
    This is not yet really shared; use the 'should-be-shared' sub directory (as downloaded from Perforce, e.g. D:\work\ARC\should-be-shared).


NOTE: There may be a simpler way to set this up; e.g. without the WAR.
Please check in any simplifications that you make, and update this readme as appropriate.