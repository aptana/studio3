
#include "stdafx.h"
#include "utils.h"

HANDLE GetParentProcess(void)
{
	HANDLE hParentProcess = NULL;
	HANDLE hProcessSnap = ::CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
	if( hProcessSnap != INVALID_HANDLE_VALUE )
	{
		PROCESSENTRY32 pe32;
		pe32.dwSize = sizeof(PROCESSENTRY32);
		if( ::Process32First(hProcessSnap, &pe32) )
		{
			DWORD pid = GetCurrentProcessId();
			do {
				if( pid == pe32.th32ProcessID ) {
					hParentProcess = ::OpenProcess(SYNCHRONIZE | PROCESS_QUERY_INFORMATION | PROCESS_DUP_HANDLE, FALSE, pe32.th32ParentProcessID);
					break;
				}
			} while( ::Process32Next(hProcessSnap, &pe32) );
		}
		::CloseHandle(hProcessSnap);
	}
	return hParentProcess;
}

void TerminateProcessTree(DWORD dwProcessId)
{
	HANDLE hProcessSnap = ::CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
	if( hProcessSnap != INVALID_HANDLE_VALUE )
	{
		PROCESSENTRY32 pe32;
		pe32.dwSize = sizeof(PROCESSENTRY32);
		if( ::Process32First(hProcessSnap, &pe32) )
		{
			DWORD dwPid = dwProcessId != 0 ? dwProcessId : GetCurrentProcessId();
			do {
				if( dwProcessId == pe32.th32ParentProcessID ) {
					TerminateProcessTree(pe32.th32ProcessID);
				}
			} while( ::Process32Next(hProcessSnap, &pe32) );
		}
		::CloseHandle(hProcessSnap);
	}
	if( dwProcessId != GetCurrentProcessId() )
	{
		//::ExitProcess(EXIT_SUCCESS);
	} else
	{
		HANDLE hProcess = ::OpenProcess(PROCESS_TERMINATE, FALSE, dwProcessId);
		if( hProcess != NULL ) {
			::TerminateProcess(hProcess, EXIT_SUCCESS);
		}
	}
}
