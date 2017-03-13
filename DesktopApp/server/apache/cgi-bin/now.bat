@echo off
set JAVA_HOME=%CD%\jdk
set PATH=%JAVA_HOME%\bin;%PATH%
set CLASSPATH=%JAVA_HOME%\lib\tools.jar;.;%CLASSPATH%
call javaw Now %*



