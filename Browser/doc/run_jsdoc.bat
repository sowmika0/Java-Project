REM @ECHO OFF

set JSDOCHOME="..\..\should-be-shared\bin\jsdoc\1.10.2"
set JSDOCOUT="..\target\docs\jsdoc_out"

IF NOT EXIST %JSDOCOUT% MKDIR %JSDOCOUT%

REM Using --private below to see hidden attribs/methods (required for Fragment which has a private constructor)
REM 
perl %JSDOCHOME%\jsdoc.pl --private --logo temenos.jpg --project-name "Browser JavaScript" --page-footer "Copyright &copy; 2007 Temenos" --project-summary summary.html --directory %JSDOCOUT% "..\BrowserWeb\Web Content\scripts" "..\BrowserWeb\Web Content\scripts\ARC"

