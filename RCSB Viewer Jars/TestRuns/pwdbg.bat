set CLASSPATH=
set MOL=1STP.xml.gz
call setargs.bat
call debugargs.bat
java %PATHARGS% %DEBUGARGS% -jar ../RCSBViewers/RCSB-ProteinWorkshop.jar -standalone -structure_url molecules/%MOL%
