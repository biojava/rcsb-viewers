#! /bin/tcsh
if ( "$1" != "" ) then
  set MOL="$1"
endif
set JNIPATH="java.library.path=../3rdParty/jnilibs"
set PATHARGS="-D$JNIPATH"
