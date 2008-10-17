#! /bin/tcsh
if ( "$1" != "" ) then
  set MOL="$1"
endif
set CP="../3rdParty"
set JNIPATH="java.library.path=../3rdParty/jnilibs"
set PATHARGS="-cp $CP -D$JNIPATH"
