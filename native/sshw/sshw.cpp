/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

#include "stdafx.h"

static int create_ssh_process(_TCHAR* host, _TCHAR* cmd);
static int ask_password(_TCHAR* message);

static WCHAR szEnvVariableName[] = L"SSHW_PID";

int _tmain(int argc, _TCHAR* argv[])
{
	if (argc == 3) {
		return create_ssh_process(argv[1], argv[2]);
	} else if (argc == 2) {
		return ask_password(argv[1]);
	}
	return -1;
}

int create_ssh_process(_TCHAR* host, _TCHAR* cmd)
{
	STARTUPINFO si;
	PROCESS_INFORMATION pi;
	SECURITY_ATTRIBUTES sa;
	DWORD dwFlags = 0;
	HANDLE hStdInR = NULL, hStdInW = NULL;
	HANDLE hStdOutR = NULL, hStdOutW = NULL;
	HANDLE hStdErrR = NULL, hStdErrW = NULL;
	HANDLE hParentStdIn = ::GetStdHandle(STD_INPUT_HANDLE);
	HANDLE hParentStdOut = ::GetStdHandle(STD_OUTPUT_HANDLE);
	HANDLE hParentStdErr = ::GetStdHandle(STD_ERROR_HANDLE);
	TCHAR szPath[MAX_PATH];
	TCHAR szCmdline[1024];
	CHAR chBuf[1024];

	if (!::GetModuleFileName(NULL, szPath, MAX_PATH) ) {
		printf("Cannot get program path (%d)\n", GetLastError());
		return -1;
	}
	if( !::GetEnvironmentVariable(_T("SSH_CMD"), szCmdline, sizeof(szCmdline)/sizeof(*szCmdline)) ) {
		_tcscpy_s(szCmdline, sizeof(szCmdline)/sizeof(*szCmdline), _T("ssh"));
	}
	_tcscat_s(szCmdline, sizeof(szCmdline)/sizeof(*szCmdline), _T(" \""));
	_tcscat_s(szCmdline, sizeof(szCmdline)/sizeof(*szCmdline), host);
	_tcscat_s(szCmdline, sizeof(szCmdline)/sizeof(*szCmdline), _T("\" \""));
	_tcscat_s(szCmdline, sizeof(szCmdline)/sizeof(*szCmdline), cmd);
	_tcscat_s(szCmdline, sizeof(szCmdline)/sizeof(*szCmdline), _T("\""));
	
	sa.nLength = sizeof(SECURITY_ATTRIBUTES); 
	sa.bInheritHandle = TRUE; 
	sa.lpSecurityDescriptor = NULL;

	// STDOUT
	if ( !::CreatePipe(&hStdOutR, &hStdOutW, &sa, 0) ) {
		printf("Cannot create pipe (%d)\n", GetLastError());
		return -1;
	}
	if ( !::SetHandleInformation(hStdOutR, HANDLE_FLAG_INHERIT, 0) ) {
		printf("Set inheritable for pipe failed (%d)\n", GetLastError());
		return -1;
	}
	// STDERR
	if ( !::CreatePipe(&hStdErrR, &hStdErrW, &sa, 0) ) {
		printf("Cannot create pipe (%d)\n", GetLastError());
		return -1;
	}
	if ( !::SetHandleInformation(hStdErrR, HANDLE_FLAG_INHERIT, 0) ) {
		printf("Set inheritable for pipe failed (%d)\n", GetLastError());
		return -1;
	}
	// STDIN
	if ( !::CreatePipe(&hStdInR, &hStdInW, &sa, 0) ) {
		printf("Cannot create pipe (%d)\n", GetLastError());
		return -1;
	}
	if ( !::SetHandleInformation(hStdInW, HANDLE_FLAG_INHERIT, 0) ) {
		printf("Set inheritable for pipe failed (%d)\n", GetLastError());
		return -1;
	}

	::ZeroMemory(&si, sizeof(si));
	si.cb = sizeof(si);
	si.hStdError = hStdErrW;
	si.hStdOutput = hStdOutW;
	si.hStdInput = hStdInR;
	si.dwFlags |= STARTF_USESTDHANDLES;
	::ZeroMemory(&pi, sizeof(pi));

#ifdef UNICODE
    dwFlags = CREATE_UNICODE_ENVIRONMENT;
#endif

	::SetEnvironmentVariable(_T("SSH_ASKPASS"), szPath);
	::SetEnvironmentVariable(_T("DISPLAY"), _T(":9999"));

	WCHAR szValue[64];
	__time64_t ltime;
	_ui64tow_s(::GetCurrentProcessId(), szValue, sizeof(szValue)/sizeof(szValue[0]), 16);
	wcscat(szValue, L"/");
	_time64(&ltime);
	wcscat(szValue, _wctime64( &ltime ));
	::SetEnvironmentVariableW(szEnvVariableName, szValue);

	if( !::CreateProcess(
		NULL,		// No module name (use command line)
        szCmdline,	// Command line
        NULL,		// Process handle not inheritable
        NULL,		// Thread handle not inheritable
        TRUE,		// Set handle inheritance
        dwFlags | DETACHED_PROCESS,	// creation flags
        NULL,           // Use parent's environment block
        NULL,           // Use parent's starting directory 
        &si,            // Pointer to STARTUPINFO structure
        &pi )           // Pointer to PROCESS_INFORMATION structure
		) {
        printf( "CreateProcess failed (%d).\n", GetLastError() );
        return -1;
	}
	::CloseHandle(hStdOutW);
	::CloseHandle(hStdErrW);
	::CloseHandle(hStdInR);

	while( true ) {
		DWORD dwRead = 0, dwWritten = 0;
		DWORD dwExitCode = WaitForSingleObject(pi.hProcess, 100);
		::FlushFileBuffers(hParentStdIn);
		while ( ::PeekNamedPipe(hParentStdIn, NULL, 0L, NULL, &dwRead, NULL) && (dwRead != 0) ) {
			if( !::ReadFile(hParentStdIn, chBuf, sizeof(chBuf), &dwRead, NULL) ) {
				break;
			}
			if( !::WriteFile(hStdInW, chBuf, dwRead, &dwWritten, NULL) ) {
				break;
			}
		}
		if ( dwWritten != 0 ) {
			continue;
		}
		::FlushFileBuffers(hStdOutR);
		while ( ::PeekNamedPipe(hStdOutR, NULL, 0L, NULL, &dwRead, NULL) && (dwRead != 0) ) {
			if( !::ReadFile(hStdOutR, chBuf, sizeof(chBuf), &dwRead, NULL) ) {
				break;
			}
			if( !::WriteFile(hParentStdOut, chBuf, dwRead, &dwWritten, NULL) ) {
				break;
			}
		}
		if ( dwWritten != 0 ) {
			continue;
		}
		::FlushFileBuffers(hStdErrR);
		while ( ::PeekNamedPipe(hStdErrR, NULL, 0L, NULL, &dwRead, NULL) && (dwRead != 0) ) {
			if( !::ReadFile(hStdErrR, chBuf, sizeof(chBuf), &dwRead, NULL) ) {
				break;
			}
			if( !::WriteFile(hParentStdErr, chBuf, dwRead, &dwWritten, NULL) ) {
				break;
			}
		}
		if ( dwWritten != 0 ) {
			continue;
		}
		if( dwExitCode != WAIT_TIMEOUT ) {
			break;
		}
	}
	::CloseHandle(hStdOutR);
	::CloseHandle(hStdErrR);

	::WaitForSingleObject(pi.hProcess, INFINITE);

    // Close process and thread handles. 
	::CloseHandle(pi.hProcess);
	::CloseHandle(pi.hThread);
	return 0;
}

static BOOL CALLBACK DialogProc(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam);
static void SavePassword(LPCTSTR szKeyName);
static void ClearPassword(LPCTSTR szKeyName);
static BOOL LoadPassword(LPCTSTR szKeyName);

static _TCHAR szRegistrySubKey[] = _T("Software\\SSHW");
static _TCHAR szPassword[256];
static _TCHAR szKeyName[MAX_PATH+1];
static BOOL bRememberPassword = FALSE;

int ask_password(_TCHAR* message)
{
	if( _tcsstr(message, _T("(yes/no)"))) {
		if( ::MessageBox(NULL,
			message,
			_T("SSH"),
			MB_ICONEXCLAMATION | MB_YESNO) == IDYES ) {
			_tprintf(_T("yes"));
			return 0;
		}
	} else {
		int nDialogID = IDD_DIALOG;
		::ZeroMemory(szKeyName, sizeof(szKeyName));
		_TCHAR *lpszBegin = _tcschr(message, _T('\''));
		if( lpszBegin != NULL ) {
			++lpszBegin;
			_TCHAR *lpszEnd = _tcschr(lpszBegin, _T('\''));
			if( lpszEnd != NULL ) {
				_tcsncpy_s(szKeyName, sizeof(szKeyName)/sizeof(szKeyName[0]), lpszBegin, lpszEnd - lpszBegin);
			}
		}
		::ZeroMemory(szPassword, sizeof(szPassword));
		if( _tcslen(szKeyName) == 0 ) {
			_tcscpy_s(szKeyName, sizeof(szKeyName)/sizeof(szKeyName[0]), _T("default"));
			nDialogID = IDD_DIALOG_ASKPASS;
		}
		if( LoadPassword(szKeyName) ) {
			_tprintf(_T("%s"), szPassword);
			SavePassword(szKeyName);
			::SecureZeroMemory(szPassword, sizeof(szPassword));
			return 0;
		} else {
			ClearPassword(szKeyName);
		}
		if( ::DialogBoxParam(GetModuleHandle(NULL),
			MAKEINTRESOURCE(nDialogID),
			NULL,
			DialogProc,
			(LPARAM)message) ) {
				_tprintf(_T("%s"), szPassword);
				if( bRememberPassword && (_tcslen(szKeyName) != 0) ) {
					SavePassword(szKeyName);
				}
				::SecureZeroMemory(szPassword, sizeof(szPassword));
				return 0;
		}
	}
	return -1;
}

BOOL CALLBACK DialogProc(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam)
{
	switch(message) {
		case WM_INITDIALOG:
			::SetDlgItemText(hDlg, IDC_MESSAGE, (LPCTSTR)lParam);
			break;
		case WM_COMMAND:
			switch(LOWORD(wParam)) {
				case IDOK: {
						bRememberPassword = ::IsDlgButtonChecked(hDlg, IDC_REMEMBER) == BST_CHECKED;
						if( !::GetDlgItemText(hDlg, IDC_PASSWORD, szPassword, sizeof(szPassword)) ) {
							*szPassword = 0;
						}
						::EndDialog(hDlg, IDOK);
					}
					return TRUE;
				case IDCANCEL:
					::EndDialog(hDlg, IDCANCEL);
					return TRUE;
			}
			break;
	}
	return FALSE;
}

void SavePassword(LPCTSTR szKeyName)
{
	DATA_BLOB dbDataIn;
	DATA_BLOB dbDataOut;
	dbDataIn.pbData = (LPBYTE)szPassword;
	dbDataIn.cbData = (DWORD)(_tcslen(szPassword)+1)*sizeof(_TCHAR);
	WCHAR szDescription[64];
	::ZeroMemory(szDescription, sizeof(szDescription));
	::GetEnvironmentVariableW(szEnvVariableName, szDescription, sizeof(szDescription)/sizeof(szDescription[0]));
	if ( !::CryptProtectData(&dbDataIn, szDescription, NULL, NULL, NULL, CRYPTPROTECT_UI_FORBIDDEN, &dbDataOut) ) {
		return;
	}
	HKEY hKey;
	if( ::RegCreateKeyEx(HKEY_CURRENT_USER, szRegistrySubKey, 0, NULL, REG_OPTION_VOLATILE, KEY_ALL_ACCESS, NULL, &hKey, NULL) == ERROR_SUCCESS )
	{
		::RegSetValueEx(hKey, szKeyName, 0, REG_BINARY, dbDataOut.pbData, dbDataOut.cbData);
		::RegCloseKey(hKey);
	}
	::LocalFree(dbDataOut.pbData);
}

void ClearPassword(LPCTSTR szKeyName)
{
	HKEY hKey;
	if( ::RegOpenKeyEx(HKEY_CURRENT_USER, szRegistrySubKey, 0, KEY_READ | KEY_SET_VALUE, &hKey) != ERROR_SUCCESS ) {
		return;
	}
	::RegDeleteValue(hKey, szKeyName);
	::RegCloseKey(hKey);
}

BOOL LoadPassword(LPCTSTR szKeyName)
{
	HKEY hKey;
	if( ::RegOpenKeyEx(HKEY_CURRENT_USER, szRegistrySubKey, 0, KEY_READ, &hKey) != ERROR_SUCCESS ) {
		return FALSE;
	}
	BOOL bResult = FALSE;
	DWORD dwType;
	DWORD dwSize = 2048;
	LPBYTE lpData = (LPBYTE)::LocalAlloc(LPTR, dwSize);
	if( ::RegQueryValueEx(hKey, szKeyName, NULL, &dwType, lpData, &dwSize) == ERROR_SUCCESS )
	{
		DATA_BLOB dbDataIn;
		DATA_BLOB dbDataOut;
		dbDataIn.pbData = lpData;
		dbDataIn.cbData = dwSize;
		LPWSTR lpwstrDescription = NULL;
		if ( ::CryptUnprotectData(&dbDataIn, &lpwstrDescription, NULL, NULL, NULL, CRYPTPROTECT_UI_FORBIDDEN, &dbDataOut) ) {
			WCHAR szDescription[64];
			::ZeroMemory(szDescription, sizeof(szDescription));
			::GetEnvironmentVariableW(szEnvVariableName, szDescription, sizeof(szDescription)/sizeof(szDescription[0]));
			if( wcscmp(szDescription, lpwstrDescription) != 0 )
			{
				::CopyMemory(szPassword, dbDataOut.pbData, dbDataOut.cbData);
				::SecureZeroMemory(dbDataOut.pbData, dbDataOut.cbData);
				::LocalFree(dbDataOut.pbData);
				bResult = TRUE;
			}
		}
		::LocalFree(lpwstrDescription);
	}
	::RegCloseKey(hKey);
	::LocalFree(lpData);
	return bResult;
}

