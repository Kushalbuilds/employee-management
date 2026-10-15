@REM Maven Wrapper script for Windows
@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-21
set WRAPPER_JAR="%~dp0.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_PROPERTIES="%~dp0.mvn\wrapper\maven-wrapper.properties"
"%JAVA_HOME%\bin\java" -Dmaven.multiModuleProjectDirectory="%~dp0." -cp %WRAPPER_JAR% org.apache.maven.wrapper.MavenWrapperMain %*
