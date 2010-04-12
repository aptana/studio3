// wintty.cpp : Defines the entry point for the DLL application.
//

#include "stdafx.h"
#include "consolehandler.h"

#ifdef _MANAGED
#pragma managed(push, off)
#endif

BOOL APIENTRY DllMain( HMODULE hModule,
                       DWORD  ul_reason_for_call,
                       LPVOID lpReserved
					 )
{
	switch (ul_reason_for_call)
	{
	case DLL_PROCESS_ATTACH: {
			/*
			TCHAR szLoader[MAX_PATH];
			::GetModuleFileName(NULL, szLoader, MAX_PATH);
			if( _tcsicmp(_T("C:\\WINDOWS\\system32\\cmd.exe"), (const TCHAR *)szLoader) != 0 ) {
				// Do not load library for any processes except cmd.exe
				return FALSE;
			}
			*/
			DisableThreadLibraryCalls(hModule);
			return InitConsoleHandler();
		}
		break;

	case DLL_PROCESS_DETACH:
		DisposeConsoleHandler();
		break;

	default:
		break;
	}
    return TRUE;
}

#ifdef _MANAGED
#pragma managed(pop)
#endif

