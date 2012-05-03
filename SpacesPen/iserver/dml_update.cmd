clear
echo Removing old DML file...
rm spaces.dml

echo Creating empty DML file...
cp spacesEmpty.dml spaces.dml

echo Importing XML iServer definition to the DML
./import.cmd spaces.xml spaces.dml

echo Replacing OM_ entries with o entries...
sed s/OM_/o/g spaces.dml > tmp.dml
cp tmp.dml spaces.dml
rm tmp.dml


echo All done!
