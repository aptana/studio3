
# assumes JAVA_HOME is set in the environment from which nmake is run

APPVER=5.0
!include <..\make_common.mak>

pgm_ver_str="Core platform support library $(maj_ver).$(min_ver).$(rel_ver) for Windows"
timestamp_str=__DATE__\" \"__TIME__\" (PST)\"
copyright = "Copyright (c) 2005-2011 Appcelerator, Inc.  All rights reserved."

WS_PREFIX   = win32
CORE_VERSION = $(maj_ver)_$(min_ver)_$(rel_ver)

CORE_LIB    = core_$(CORE_VERSION).dll
CORE_OBJS   = core.obj win32.obj

CORE_LIBS    = kernel32.lib user32.lib advapi32.lib shell32.lib

CFLAGS = -c -W3 -G6 -GD -O1 -DCORE_VERSION=$(CORE_VERSION) -DCORE_BUILD_NUM=$(bld_num) -nologo -D_X86_=1 -D_WIN32 -D_WIN95 -D_WIN32_WINDOWS=0x0400 -D_MT -MT -DWIN32 -DXP_WIN -DXP_WIN32 -D_WIN32_DCOM -DUNICODE /I"$(JAVA_HOME)\include" /I"$(JAVA_HOME)\include\win32" /I. /I..
RCFLAGS = -DCORE_FILE_VERSION=\"$(maj_ver).$(min_ver).$(rel_ver)\" -DCORE_COMMA_VERSION=$(comma_ver) -DPRODUCT_COMMA_VERSION=$(comma_ver) -DPRODUCT_VERSION=\"$(maj_ver).$(min_ver).$(rel_ver).$(bld_num)\"
LFLAGS = /INCREMENTAL:NO /PDB:NONE /RELEASE /NOLOGO -entry:_DllMainCRTStartup@12 -dll /BASE:0x10000000 /comment:$(pgm_ver_str) /comment:$(copyright) /DLL
#X64 CFLAGS = -c -W3 -G6 -GD -O1 -DCORE_VERSION=$(CORE_VERSION) -DCORE_BUILD_NUM=$(bld_num) -nologo -D_WIN32 -D_WIN95 -D_WIN32_WINDOWS=0x0400 -D_MT -MT -DWIN32 -DXP_WIN -DXP_WIN32 -D_WIN32_DCOM -DUNICODE /I"$(JAVA_HOME)\include" /I"$(JAVA_HOME)\include\win32" /I. /I..
#x64 LFLAGS = /INCREMENTAL:NO /PDB:NONE /RELEASE /NOLOGO -entry:_DllMainCRTStartup -dll /BASE:0x10000000 /comment:$(pgm_ver_str) /comment:$(copyright) /DLL

XCFLAGS = $(CFLAGS) \


all: $(CORE_LIB)

core.obj: CoreNatives.h 
	cl $(XCFLAGS) ..\core.c

win32.obj: CoreNatives.h
	cl $(XCFLAGS) ..\win32\win32.c

$(CORE_LIB): $(CORE_OBJS) core.res
	echo $(LFLAGS) >templrf
	echo $(CORE_LIBS) >>templrf
	echo -machine:IX86 >>templrf
	echo -subsystem:windows >>templrf
	echo -out:$(CORE_LIB) >>templrf
	echo $(CORE_OBJS) >>templrf
	echo core.res >>templrf
	link @templrf
	del templrf

# -machine:X64

core.res:
	rc $(RCFLAGS) -DCORE_ORG_FILENAME=\"$(CORE_LIB)\" -r -fo core.res core.rc

CoreNatives.h:
	"$(JAVA_HOME)\bin\javah" -jni -classpath ../../bin com.aptana.core.internal.platform.CoreNatives	

clean:
    del *.obj *.res *.dll *.lib *.exp

install: all
	copy $(CORE_LIB) $(OUTPUT_DIR)
