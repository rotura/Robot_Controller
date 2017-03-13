cd %~dp0%
call _common.bat
set JBOSS_HOME=%~dp0\wildflyext
set JAVA_OPTS=%JAVA_OPTS% -Djboss.server.log.threshold=INFO
set JAVA_OPTS=%JAVA_OPTS% -Xms512m -Xmx1000m -XX:MaxPermSize=256m
rem del %JBOSS_HOME\standalone\deployments\*.*

cd wildflyext\bin
standalone --server-config=standalone-full.xml
