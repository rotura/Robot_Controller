
@echo off

REM Vamos a definir la unidad S: asociada a la carpeta raiz de nuestro entorno-sdi
REM Es importante respetar la unidad "S", ya que será la que se use SIEMPRE internamente en todas las configuraciones de nuestras herramientas.

REM Definimos variables de entorno necesarias para lanzar Eclipse
set APACHE_HOME=%~dp0\apache
set JBOSS_HOME=%~dp0\wildflyext
set JAVA_HOME=%~dp0\jdk
set PATH=%JAVA_HOME%\bin;%PATH%
set CLASSPATH=%JAVA_HOME%\lib\tools.jar;.;%CLASSPATH%
set WORKSPACE=%~dp0\work
