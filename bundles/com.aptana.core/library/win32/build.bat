@echo off

rem ********
rem MSVC 6.0
rem ********
call "C:\Program Files\Microsoft Visual Studio 8\VC\bin\vcvars32.bat"
rem call "C:\Program Files (x86)\Microsoft Visual Studio 8\VC\vcvarsall.bat" x64
rem call "C:\Program Files\Microsoft SDK\SetEnv.bat" /2000

set OUTPUT_DIR="..\..\..\com.aptana.core\os\win32\x86"

IF NOT "%JAVA_HOME%"=="" GOTO MAKE

set JAVA_HOME=C:\Program Files\Java\jdk1.6.0_06
set path=%JAVA_HOME%;%path%

:MAKE
nmake -f make_win32.mak clean
nmake -f make_win32.mak %1 %2 %3 %4
