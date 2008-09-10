set MOL=1STP.xml.gz
if NOT "%1" == "" set MOL=%1
java -D"java.library.path=../3rdParty/jnilibs" -jar ../RCSBViewers/RCSB-ProteinWorkshop.jar -standalone -structure_url molecules/%MOL% %2
