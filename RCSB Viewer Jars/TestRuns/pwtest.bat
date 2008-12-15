set CLASSPATH=
set MOL=1STP.xml.gz
call setargs.bat
java %PATHARGS% -jar ../RCSBViewers/RCSB-ProteinWorkshop.jar -standalone -structure_url molecules/%MOL%
