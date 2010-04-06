// sshw.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"

static int create_ssh_process(_TCHAR* host, _TCHAR* cmd);
static int ask_password(_TCHAR* message);

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
	HANDLE hParentStdIn = GetStdHandle(STD_INPUT_HANDLE);
	HANDLE hParentStdOut = GetStdHandle(STD_OUTPUT_HANDLE);
	HANDLE hParentStdErr = GetStdHandle(STD_ERROR_HANDLE);
	TCHAR szPath[MAX_PATH];
	TCHAR szCmdline[1024];
	CHAR chBuf[1024];

	if (!GetModuleFileName(NULL, szPath, MAX_PATH) ) {
		printf("Cannot get program path (%d)\n", GetLastError());
		return -1;
	}
	if( !GetEnvironmentVariable(_T("SSH_CMD"), szCmdline, sizeof(szCmdline)/sizeof(*szCmdline)) ) {
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
	if ( !CreatePipe(&hStdOutR, &hStdOutW, &sa, 0) ) {
		printf("Cannot create pipe (%d)\n", GetLastError());
		return -1;
	}
	if ( !SetHandleInformation(hStdOutR, HANDLE_FLAG_INHERIT, 0) ) {
		printf("Set inheritable for pipe failed (%d)\n", GetLastError());
		return -1;
	}
	// STDERR
	if ( !CreatePipe(&hStdErrR, &hStdErrW, &sa, 0) ) {
		printf("Cannot create pipe (%d)\n", GetLastError());
		return -1;
	}
	if ( !SetHandleInformation(hStdErrR, HANDLE_FLAG_INHERIT, 0) ) {
		printf("Set inheritable for pipe failed (%d)\n", GetLastError());
		return -1;
	}
	// STDIN
	if ( !CreatePipe(&hStdInR, &hStdInW, &sa, 0) ) {
		printf("Cannot create pipe (%d)\n", GetLastError());
		return -1;
	}
	if ( !SetHandleInformation(hStdInW, HANDLE_FLAG_INHERIT, 0) ) {
		printf("Set inheritable for pipe failed (%d)\n", GetLastError());
		return -1;
	}

	ZeroMemory(&si, sizeof(si));
	si.cb = sizeof(si);
	si.hStdError = hStdErrW;
	si.hStdOutput = hStdOutW;
	si.hStdInput = hStdInR;
	si.dwFlags |= STARTF_USESTDHANDLES;
    ZeroMemory(&pi, sizeof(pi));

#ifdef UNICODE
    dwFlags = CREATE_UNICODE_ENVIRONMENT;
#endif

	SetEnvironmentVariable(_T("SSH_ASKPASS"), szPath);
	SetEnvironmentVariable(_T("DISPLAY"), _T(":9999"));

	if( !CreateProcess(
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
	CloseHandle(hStdOutW);
	CloseHandle(hStdErrW);
	CloseHandle(hStdInR);

	while( true ) {
		DWORD dwRead = 0, dwWritten = 0;
		DWORD dwExitCode = WaitForSingleObject(pi.hProcess, 100);
		FlushFileBuffers(hParentStdIn);
		while ( PeekNamedPipe(hParentStdIn, NULL, 0L, NULL, &dwRead, NULL) && (dwRead != 0) ) {
			if( !ReadFile(hParentStdIn, chBuf, sizeof(chBuf), &dwRead, NULL) ) {
				break;
			}
			if( !WriteFile(hStdInW, chBuf, dwRead, &dwWritten, NULL) ) {
				break;
			}
		}
		if ( dwWritten != 0 ) {
			continue;
		}
		FlushFileBuffers(hStdOutR);
		while ( PeekNamedPipe(hStdOutR, NULL, 0L, NULL, &dwRead, NULL) && (dwRead != 0) ) {
			if( !ReadFile(hStdOutR, chBuf, sizeof(chBuf), &dwRead, NULL) ) {
				break;
			}
			if( !WriteFile(hParentStdOut, chBuf, dwRead, &dwWritten, NULL) ) {
				break;
			}
		}
		if ( dwWritten != 0 ) {
			continue;
		}
		FlushFileBuffers(hStdErrR);
		while ( PeekNamedPipe(hStdErrR, NULL, 0L, NULL, &dwRead, NULL) && (dwRead != 0) ) {
			if( !ReadFile(hStdErrR, chBuf, sizeof(chBuf), &dwRead, NULL) ) {
				break;
			}
			if( !WriteFile(hParentStdErr, chBuf, dwRead, &dwWritten, NULL) ) {
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
	CloseHandle(hStdOutR);
	CloseHandle(hStdErrR);

	WaitForSingleObject(pi.hProcess, INFINITE);

    // Close process and thread handles. 
    CloseHandle(pi.hProcess);
    CloseHandle(pi.hThread);
	return 0;
}

static BOOL CALLBACK DialogProc(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam);
_TCHAR szPassword[80];

int ask_password(_TCHAR* message)
{
	if( _tcsstr(message, _T("(yes/no)"))) {
		if( MessageBox(NULL,
			message,
			_T("SSH"),
			MB_ICONEXCLAMATION | MB_YESNO) == IDYES ) {
			_tprintf(_T("yes"));
			return 0;
		}
	} else {
		if( DialogBoxParam(GetModuleHandle(NULL),
			MAKEINTRESOURCE(IDD_DIALOG),
			NULL,
			DialogProc,
			(LPARAM)message) ) {
				_tprintf(szPassword);
				return 0;
		}
	}
	return -1;
}

BOOL CALLBACK DialogProc(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam)
{
	switch(message) {
		case WM_INITDIALOG:
			SetDlgItemText(hDlg, IDC_MESSAGE, (LPCTSTR)lParam);
			break;
		case WM_COMMAND:
			switch(LOWORD(wParam)) {
				case IDOK: {
						if( !GetDlgItemText(hDlg, IDC_PASSWORD, szPassword, sizeof(szPassword)) ) {
							*szPassword = 0;
						}
						EndDialog(hDlg, IDOK);
					}
					return TRUE;
				case IDCANCEL:
					EndDialog(hDlg, IDCANCEL);
					return TRUE;
			}
			break;
	}
	return FALSE;
}

