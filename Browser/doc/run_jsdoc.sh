#!/bin/sh
JSDOCHOME="../../should-be-shared/bin/jsdoc/1.10.2"
JSDOCOUT="../target/docs/jsdoc_out"

[ ! -d $JSDOCOUT ] && mkdir -p $JSDOCOUT

# Using --private below to see hidden attribs/methods (required for Fragment which has a private constructor)
perl $JSDOCHOME/jsdoc.pl --private --logo temenos.jpg --project-name "Browser JavaScript" --page-footer "Copyright &copy; 2007 Temenos" --project-summary summary.html --directory $JSDOCOUT "../BrowserWeb/Web Content/scripts" "../BrowserWeb/Web Content/scripts/ARC"
