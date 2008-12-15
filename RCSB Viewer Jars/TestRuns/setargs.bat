if NOT "%1%" == "" (
  set MOL="%1%"
)
set JNIPATH="java.library.path=../3rdParty/jnilibs"
set PATHARGS=-D%JNIPATH%
