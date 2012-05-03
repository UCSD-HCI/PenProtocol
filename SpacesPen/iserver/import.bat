@echo off

::SET M2=C:/Documents and Settings/%USERNAME%/.m2/repository
::SET M2=Z:/.m2/repository
SET M2=C:/Users/%USERNAME%/.m2/repository

echo Using Maven folder %M2%

SET ISERVER=%M2%/org/ximtec/iserver/iserver-core/1.0-SNAPSHOT/iserver-core-1.0-SNAPSHOT.jar;%M2%/org/ximtec/iserver/iserver-common/1.0-SNAPSHOT/iserver-common-1.0-SNAPSHOT.jar;%M2%/commons-io/commons-io/1.4/commons-io-1.4.jar;%M2%/jdom/jdom/1.0/jdom-1.0.jar;%M2%/xdatabase/xdatabase/2.0.50-SNAPSHOT/xdatabase-2.0.50-SNAPSHOT.jar;%M2%/commons-configuration/commons-configuration/1.3/commons-configuration-1.3.jar;%M2%/commons-lang/commons-lang/2.2/commons-lang-2.2.jar;%M2%/commons-collections/commons-collections/3.2/commons-collections-3.2.jar;%M2%/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar
SET IPAPER=%M2%/org/ximtec/ipaper/ipaper-core/1.0-SNAPSHOT/ipaper-core-1.0-SNAPSHOT.jar
SET SIGTEC=%M2%/org/sigtec/sigtec-cache/1.0-SNAPSHOT/sigtec-cache-1.0-SNAPSHOT.jar;%M2%/org/sigtec/sigtec-database/1.0-SNAPSHOT/sigtec-database-1.0-SNAPSHOT.jar;%M2%/org/sigtec/sigtec-graphix/1.0-SNAPSHOT/sigtec-graphix-1.0-SNAPSHOT.jar;%M2%/org/sigtec/sigtec-ink/1.0-SNAPSHOT/sigtec-ink-1.0-SNAPSHOT.jar;%M2%/org/sigtec/sigtec-input/1.0-SNAPSHOT/sigtec-input-1.0-SNAPSHOT.jar;%M2%/org/sigtec/sigtec-jdom/1.0-SNAPSHOT/sigtec-jdom-1.0-SNAPSHOT.jar;%M2%/org/sigtec/sigtec-multimedia/1.0-SNAPSHOT/sigtec-multimedia-1.0-SNAPSHOT.jar;%M2%/org/sigtec/sigtec-net/1.0-SNAPSHOT/sigtec-net-1.0-SNAPSHOT.jar;%M2%/org/sigtec/sigtec-om/1.0-SNAPSHOT/sigtec-om-1.0-SNAPSHOT.jar;%M2%/org/sigtec/sigtec-util/1.0-SNAPSHOT/sigtec-util-1.0-SNAPSHOT.jar;%M2%/org/sigtec/sigtec-win/1.0-SNAPSHOT/sigtec-win-1.0-SNAPSHOT.jar
SET XIMA=%M2%/org/ximtec/xima/xima/1.0-SNAPSHOT/xima-1.0-SNAPSHOT.jar

SET CLASSES=%ISERVER%;%IPAPER%;%SIGTEC%;%XIMA%;

@echo on

java -cp ".;%CLASSES%" -Xmx512M org.ximtec.iserver.authoring.Import papur "." %1 %2

pause;