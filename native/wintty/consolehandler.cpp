
#include "stdafx.h"
#include "consolehandler.h"
#include "utils.h"

static DWORD WINAPI MonitorThreadProc(LPVOID lpParameter);
static void ReadConsoleBuffer(void);
static void WriteConsole(void);

static HANDLE hParentProcess = NULL;
static HANDLE hMonitorThread = NULL;
static HANDLE hMonitorThreadExitEvent = NULL;
static HANDLE hParentHandles[3] = { NULL, NULL, NULL };
static HANDLE hConsoleIn = NULL;
static HANDLE hConsoleOut = NULL;
static DWORD dwInputPollInterval = 10;
static DWORD dwRefreshInterval = 500;
static DWORD dwNotificationTimeout = 10;

static DWORD dwBufferFilled = 0L;
static CHAR chInputBuffer[1024];
static CHAR chOutputBuffer[1024];

static HANDLE hHeap = NULL;
static CONSOLE_SCREEN_BUFFER_INFO csbiConsole;
static CHAR_INFO* lpPrevScreenBuffer = NULL;
static CHAR_INFO* lpNextScreenBuffer = NULL;
static DWORD dwBufferMemorySize = 0;
static DWORD dwScreenBufferSize = 0;

#define COLOR_MASK (COMMON_LVB_REVERSE_VIDEO|COMMON_LVB_UNDERSCORE|FOREGROUND_BLUE|FOREGROUND_GREEN|FOREGROUND_RED|FOREGROUND_INTENSITY|BACKGROUND_BLUE|BACKGROUND_GREEN|BACKGROUND_RED|BACKGROUND_INTENSITY)
#define DEFAULT_ATTRIBUTES (FOREGROUND_BLUE|FOREGROUND_GREEN|FOREGROUND_RED)


static WORD wCharAttributes = DEFAULT_ATTRIBUTES;


BOOL InitConsoleHandler(void)
{
	hParentProcess = GetParentProcess();
	if( hParentProcess == NULL ) {
		return FALSE;
	}

	TCHAR szName[64];
	_ui64tot_s(::GetCurrentProcessId(), szName, sizeof(szName)/sizeof(szName[0]), 16);
	_tcscat_s(szName, sizeof(szName)/sizeof(szName[0]), _T("-WINTTY"));
	DWORD dwSize = 3*sizeof(HANDLE);
	HANDLE hMapFile = ::OpenFileMapping(FILE_MAP_ALL_ACCESS, FALSE, szName);
	if( hMapFile == NULL ) {
		return FALSE;
	}
	BOOL bResult = TRUE;
	LPHANDLE lpData = (LPHANDLE)::MapViewOfFile(hMapFile, FILE_MAP_READ, 0, 0, dwSize);
	if( lpData != NULL )
	{
		for( int i = 0; i < 3; ++i) {
			bResult = bResult && ::DuplicateHandle(hParentProcess, lpData[i],
							::GetCurrentProcess(), &hParentHandles[i], NULL, FALSE,
							DUPLICATE_SAME_ACCESS | DUPLICATE_CLOSE_SOURCE);
		}
		::UnmapViewOfFile(lpData);
	} else {
		bResult = FALSE;

	}
	::CloseHandle(hMapFile);
	if( !bResult ) {
		::ExitProcess(EXIT_SUCCESS);
		return FALSE;
	}

	hHeap = ::HeapCreate(HEAP_GENERATE_EXCEPTIONS, 0, 0);

	hMonitorThreadExitEvent = ::CreateEvent(NULL, FALSE, FALSE, NULL);
	hMonitorThread = ::CreateThread(NULL, 0, MonitorThreadProc, 0, 0, NULL);
	if ( hMonitorThread != NULL ) {
		::SetThreadPriority(hMonitorThread, THREAD_PRIORITY_HIGHEST);
		::SwitchToThread();
	}
	return TRUE;
}

void DisposeConsoleHandler(void)
{
	if( hMonitorThreadExitEvent == NULL ) {
		return;
	}
	::SetEvent(hMonitorThreadExitEvent);
	::WaitForSingleObject(hMonitorThread, 10000);
	::CloseHandle(hMonitorThread);
	::CloseHandle(hMonitorThreadExitEvent);
	::HeapDestroy(hHeap);
}

static DWORD WINAPI MonitorThreadProc(LPVOID lpParameter)
{
	HANDLE hTimer = ::CreateWaitableTimer(NULL, FALSE, NULL);

	hConsoleIn = ::CreateFile(_T("CONIN$"), GENERIC_WRITE | GENERIC_READ, FILE_SHARE_READ | FILE_SHARE_WRITE, NULL, OPEN_EXISTING, 0, 0);
	hConsoleOut = ::CreateFile(_T("CONOUT$"), GENERIC_WRITE | GENERIC_READ, FILE_SHARE_READ | FILE_SHARE_WRITE, NULL, OPEN_EXISTING, 0, 0);

	::SetConsoleCP(65001); // UTF-8
	::SetConsoleOutputCP(65001); // UTF-8

	HANDLE  waitHandles[] = {
		hMonitorThreadExitEvent,
		hParentProcess,
		hTimer,
		hConsoleOut
	};
	LARGE_INTEGER liDueTime;
	::ZeroMemory(&liDueTime, sizeof(liDueTime));
	__int64 qwDueTime = -500000;
	liDueTime.LowPart  = (DWORD) ( qwDueTime & 0xFFFFFFFF );
	liDueTime.HighPart = (LONG)  ( qwDueTime >> 32 );
	SetWaitableTimer(hTimer, &liDueTime, dwInputPollInterval, NULL, NULL, FALSE);
	DWORD dwWaitResult = 0;
	while ( (dwWaitResult = ::WaitForMultipleObjects(sizeof(waitHandles)/sizeof(waitHandles[0]), waitHandles, FALSE, dwRefreshInterval)) != WAIT_OBJECT_0 )
	{
		switch(dwWaitResult)
		{
		case WAIT_OBJECT_0 + 1: {
				TerminateProcessTree(::GetCurrentProcessId());
				HWND hWnd = ::GetConsoleWindow();
				if( hWnd != NULL ) {
					::PostMessage(hWnd, WM_CLOSE, 0, 0) ;
				}
				return 0;
			}
		case WAIT_OBJECT_0 + 2:
			WriteConsole();
		case WAIT_OBJECT_0 + 3:
			::Sleep(dwNotificationTimeout);
		case WAIT_TIMEOUT:
			ReadConsoleBuffer();
			break;
		}
	}
	::CancelWaitableTimer(hTimer);
	::CloseHandle(hTimer);
	return 0;
}

static void FlushBuffer();
static void OutputChar(CHAR ch);
static void OutputNumber(SHORT value);
static void OutputString(CHAR* szStr);

static void MoveCursorAbs(SHORT x, SHORT y)
{
	OutputChar('\x1B');
	OutputChar('[');
	if( (x != 0) || (y != 0) ) {
		OutputNumber(y+1);
		OutputChar(';');
		OutputNumber(x+1);
	}
	OutputChar('H');
}

static void MoveCursorRel(SHORT x, SHORT y)
{
	if( x != 0 ) {
		OutputChar('\x1B');
		OutputChar('[');
		if ( abs(x) != 1 ) {
			OutputNumber(abs(x));
		}
		OutputChar(x > 0 ? 'C' : 'D');
	}
	if( y != 0 ) {
		OutputChar('\x1B');
		OutputChar('[');
		if ( abs(y) != 1 ) {
			OutputNumber(abs(y));
		}
		OutputChar(y > 0 ? 'B' : 'A');
	}
}

static void MoveCursor(SHORT x, SHORT y)
{
	SHORT relX = x - csbiConsole.dwCursorPosition.X;
	SHORT relY = y - csbiConsole.dwCursorPosition.Y;
	if( (x == 0) && (y == 0) ) {
		MoveCursorAbs(x, y);
	} else if( (relX == 0) || (relY == 0) ) {
		MoveCursorRel(relX, relY);
	} else {
		MoveCursorAbs(x, y);
	}
	csbiConsole.dwCursorPosition.X = x;
	csbiConsole.dwCursorPosition.Y = y;
}

static void EraseLine()
{
	OutputString("\x1B[2K");
}

static void NextLine(SHORT count)
{
	for( SHORT i = 0; i < count; ++i) {
		OutputString("\x1B[E");
	}
}

static void PrevLine()
{
	OutputString("\x1B[F");
}

static void InsertBlank(SHORT count)
{
	OutputChar('\x1B');
	OutputChar('[');
	if ( count != 1 ) {
		OutputNumber(count);
	}
	OutputChar('@');
}

static void EraseWindow()
{
	OutputString("\x1B[2J");
}

static void ScrollWindow(BOOL bUp)
{
	if( bUp ) {
		OutputString("\x1B[D");
	} else {
		OutputString("\x1B[M");
	}
}

static void ChangeAttributes(WORD attrs)
{
	WORD prevAttrs = wCharAttributes & COLOR_MASK;
	wCharAttributes = attrs;
	attrs &= COLOR_MASK;
	if( attrs == prevAttrs ) {
		return;
	}
	if( attrs == 0 )
	{
		OutputString("\x1B[0m");
	} else if(attrs & COMMON_LVB_REVERSE_VIDEO)
	{
		OutputString("\x1B[7m");
	} else
	{
		OutputChar('\x1B');
		OutputChar('[');
		BOOL bAddSep = FALSE;
		if(  (attrs & FOREGROUND_INTENSITY) != (prevAttrs & FOREGROUND_INTENSITY) ) {
			OutputString((attrs & FOREGROUND_INTENSITY) != 0 ? "1" : "22");
			bAddSep = TRUE;
		}
		if( (attrs & COMMON_LVB_UNDERSCORE) != (prevAttrs & COMMON_LVB_UNDERSCORE) ) {
			if( bAddSep ) {
				OutputChar(';');
				bAddSep = FALSE;
			}
			OutputString((attrs & COMMON_LVB_UNDERSCORE) != 0 ? "4" : "24");
			bAddSep = TRUE;
		}
		WORD fg = attrs & (FOREGROUND_BLUE|FOREGROUND_GREEN|FOREGROUND_RED);
		if( fg != (prevAttrs & (FOREGROUND_BLUE|FOREGROUND_GREEN|FOREGROUND_RED)) )
		{
			if( bAddSep ) {
				OutputChar(';');
				bAddSep = FALSE;
			}
			OutputChar('3');
			switch( fg ) {
				case 0:
					OutputChar('0');
					break;
				case FOREGROUND_BLUE:
					OutputChar('4');
					break;
				case FOREGROUND_GREEN:
					OutputChar('2');
					break;
				case FOREGROUND_RED:
					OutputChar('1');
					break;
				case FOREGROUND_BLUE | FOREGROUND_GREEN:
					OutputChar('6');
					break;
				case FOREGROUND_BLUE | FOREGROUND_RED:
					OutputChar('5');
					break;
				case FOREGROUND_GREEN | FOREGROUND_RED:
					OutputChar('3');
					break;
				case FOREGROUND_BLUE | FOREGROUND_GREEN | FOREGROUND_RED:
					OutputChar('9');
					break;
			}
			bAddSep = TRUE;
		}
		WORD bg = attrs & (BACKGROUND_BLUE|BACKGROUND_GREEN|BACKGROUND_RED);
		if( bg != (prevAttrs & (BACKGROUND_BLUE|BACKGROUND_GREEN|BACKGROUND_RED)) )
		{
			if( bAddSep ) {
				OutputChar(';');
				bAddSep = FALSE;
			}
			OutputChar('4');
			switch( bg ) {
				case 0:
					OutputChar('9');
					break;
				case BACKGROUND_BLUE:
					OutputChar('4');
					break;
				case BACKGROUND_GREEN:
					OutputChar('2');
					break;
				case BACKGROUND_RED:
					OutputChar('1');
					break;
				case BACKGROUND_BLUE | BACKGROUND_GREEN:
					OutputChar('6');
					break;
				case BACKGROUND_BLUE | BACKGROUND_RED:
					OutputChar('5');
					break;
				case BACKGROUND_GREEN | BACKGROUND_RED:
					OutputChar('3');
					break;
				case BACKGROUND_BLUE | BACKGROUND_GREEN | BACKGROUND_RED:
					OutputChar('7');
					break;
			}
			bAddSep = TRUE;
		}
		OutputChar('m');
	}
}

static BOOL IsLineEmpty(CHAR_INFO *lpCharInfo, SHORT sLength);
static BOOL HasChanges(CHAR_INFO *lpPrevCharInfo, CHAR_INFO *lpNextCharInfo, SHORT sLength, SHORT sMinCount);

static void ReadConsoleBuffer()
{
	CONSOLE_SCREEN_BUFFER_INFO csbiNextConsole;
	COORD coordConsoleSize;
	COORD coordStart = {0, 0};
	COORD coordBufferSize;
	SMALL_RECT srBuffer;

	::ResetEvent(hConsoleOut);
	::GetConsoleScreenBufferInfo(hConsoleOut, &csbiNextConsole);
	coordConsoleSize.X = csbiNextConsole.srWindow.Right - csbiNextConsole.srWindow.Left + 1;
	coordConsoleSize.Y = csbiNextConsole.srWindow.Bottom - csbiNextConsole.srWindow.Top + 1;

	// make cursor positions relative to console window
	csbiNextConsole.dwCursorPosition.X -= csbiNextConsole.srWindow.Left;
	csbiNextConsole.dwCursorPosition.Y -= csbiNextConsole.srWindow.Top;
	
	coordBufferSize.X = coordConsoleSize.X;
	coordBufferSize.Y = min(coordConsoleSize.Y, 6144 / coordBufferSize.X); // limit to ~6k per read operation

	srBuffer.Top = csbiNextConsole.srWindow.Top;
	srBuffer.Bottom = csbiNextConsole.srWindow.Top + coordBufferSize.Y - 1;
	srBuffer.Left = csbiNextConsole.srWindow.Left;
	srBuffer.Right = csbiNextConsole.srWindow.Right;

	DWORD dwNextScreenBufferSize = coordConsoleSize.X * coordConsoleSize.Y;
	if( (lpPrevScreenBuffer == NULL) || (dwScreenBufferSize != dwNextScreenBufferSize))
	{
		// refresh screen
		if( lpPrevScreenBuffer != NULL ) {
			EraseWindow();
		}
		csbiConsole = csbiNextConsole;
		dwScreenBufferSize = dwNextScreenBufferSize;
		if( dwScreenBufferSize*sizeof(CHAR_INFO) > dwBufferMemorySize ) {
			if( ::HeapValidate(hHeap, 0, NULL) == 0 ) {
			}
			if( lpPrevScreenBuffer != NULL ) {
				::HeapFree(hHeap, 0, lpPrevScreenBuffer);
			}
			if( lpNextScreenBuffer ) {
				::HeapFree(hHeap, 0, lpNextScreenBuffer);
			}
			dwBufferMemorySize = dwScreenBufferSize*sizeof(CHAR_INFO);
			lpPrevScreenBuffer = (CHAR_INFO*) ::HeapAlloc(hHeap, HEAP_GENERATE_EXCEPTIONS | HEAP_ZERO_MEMORY, dwBufferMemorySize);
			lpNextScreenBuffer = (CHAR_INFO*) ::HeapAlloc(hHeap, HEAP_GENERATE_EXCEPTIONS | HEAP_ZERO_MEMORY, dwBufferMemorySize);
		}
		DWORD dwScreenBufferOffset = 0;
		SHORT i;
		for( i = 0; i < coordConsoleSize.Y / coordBufferSize.Y; ++i ) {
			::ReadConsoleOutput(hConsoleOut, lpPrevScreenBuffer+dwScreenBufferOffset, coordBufferSize, coordStart, &srBuffer);
			srBuffer.Top = srBuffer.Top + coordBufferSize.Y;
			srBuffer.Bottom = srBuffer.Bottom + coordBufferSize.Y;
			coordStart.Y += coordBufferSize.Y;
			dwScreenBufferOffset += coordBufferSize.X * coordBufferSize.Y;
		}
		coordBufferSize.Y = coordConsoleSize.Y - i * coordBufferSize.Y;
		srBuffer.Bottom = csbiConsole.srWindow.Bottom;
		if( coordBufferSize.Y != 0 ) { 
			::ReadConsoleOutput(hConsoleOut, lpPrevScreenBuffer+dwScreenBufferOffset, coordBufferSize, coordStart, &srBuffer);
		}

		CHAR_INFO* lpCharInfo = lpPrevScreenBuffer;
		BOOL bHasNonEmptyChar = FALSE;
		csbiConsole.dwCursorPosition.X = 0;
		csbiConsole.dwCursorPosition.Y = 0;
		for( SHORT y = 0; y < coordConsoleSize.Y; ++y)
		{
			if( IsLineEmpty(lpCharInfo, coordConsoleSize.X) )
			{
				if( bHasNonEmptyChar ) {
					OutputString("\r\n");
				}
				lpCharInfo += coordConsoleSize.X;
				continue;
			}
			for( SHORT x = 0; x < coordConsoleSize.X; ++x, ++lpCharInfo)
			{
				if( (lpCharInfo->Char.AsciiChar == ' ') && (lpCharInfo->Attributes == wCharAttributes) && !bHasNonEmptyChar ) {
					continue;
				}
				if( !bHasNonEmptyChar ) {
					for( SHORT i = 0; i < y; ++i) {
						OutputString("\r\n");
					}
					for( SHORT i = 0; i < x; ++i) {
						OutputChar(' ');
					}
					bHasNonEmptyChar = TRUE;
				}
				if( wCharAttributes != lpCharInfo->Attributes ) {
					ChangeAttributes(lpCharInfo->Attributes);
				} else if( (lpCharInfo->Char.AsciiChar == ' ') && IsLineEmpty(lpCharInfo, coordConsoleSize.X - x) ) {
					lpCharInfo += coordConsoleSize.X - x;
					break;
				}
				OutputChar(lpCharInfo->Char.AsciiChar);
				csbiConsole.dwCursorPosition.X = x + 1;
				csbiConsole.dwCursorPosition.Y = y;
			}
			if( bHasNonEmptyChar ) {
				if( IsLineEmpty(lpCharInfo, (coordConsoleSize.Y-y)*coordConsoleSize.X) ) {
					break;
				}
				OutputString("\r\n");
			}
		}
	} else
	{
		DWORD dwScreenBufferOffset = 0;
		SHORT i;
		for( i = 0; i < coordConsoleSize.Y / coordBufferSize.Y; ++i ) {
			::ReadConsoleOutput(hConsoleOut, lpNextScreenBuffer+dwScreenBufferOffset, coordBufferSize, coordStart, &srBuffer);
			srBuffer.Top = srBuffer.Top + coordBufferSize.Y;
			srBuffer.Bottom = srBuffer.Bottom + coordBufferSize.Y;
			coordStart.Y += coordBufferSize.Y;
			dwScreenBufferOffset += coordBufferSize.X * coordBufferSize.Y;
		}
		coordBufferSize.Y = coordConsoleSize.Y - i * coordBufferSize.Y;
		srBuffer.Bottom = csbiConsole.srWindow.Bottom;
		if( coordBufferSize.Y != 0 ) { 
			::ReadConsoleOutput(hConsoleOut, lpNextScreenBuffer+dwScreenBufferOffset, coordBufferSize, coordStart, &srBuffer);
		}

		if( ::memcmp(lpPrevScreenBuffer, lpNextScreenBuffer, dwScreenBufferSize*sizeof(CHAR_INFO)) != 0 )
		{
			// partial changes
			CHAR_INFO* lpPrevCharInfo = lpPrevScreenBuffer;
			CHAR_INFO* lpNextCharInfo = lpNextScreenBuffer;
			for( SHORT y = 0; y < coordConsoleSize.Y; ++y)
			{
				BOOL bReplaceLine = FALSE;
				if( bReplaceLine = HasChanges(lpPrevCharInfo, lpNextCharInfo, coordConsoleSize.X, 4) )
				{
					MoveCursor(0, y);
					if( !IsLineEmpty(lpPrevCharInfo, coordConsoleSize.X) ) {
						EraseLine();
					}
				} else {
					if(!HasChanges(lpPrevCharInfo, lpNextCharInfo, coordConsoleSize.X, 0) && IsLineEmpty(lpNextCharInfo, coordConsoleSize.X) ) {
						lpPrevCharInfo += coordConsoleSize.X;
						lpNextCharInfo += coordConsoleSize.X;
						continue;
					}
				}
				for( SHORT x = 0; x < coordConsoleSize.X; ++x, ++lpPrevCharInfo, ++lpNextCharInfo)
				{
					if( bReplaceLine || (::memcmp(lpPrevCharInfo, lpNextCharInfo, sizeof(CHAR_INFO)) != 0) )
					{
						if( wCharAttributes != lpNextCharInfo->Attributes ) {
							MoveCursor(x, y);
							ChangeAttributes(lpNextCharInfo->Attributes);
						} else if( bReplaceLine && (lpNextCharInfo->Char.AsciiChar == ' ') && !HasChanges(lpPrevCharInfo, lpNextCharInfo, coordConsoleSize.X - x, 0) ) {
							lpPrevCharInfo += coordConsoleSize.X - x;
							lpNextCharInfo += coordConsoleSize.X - x;
							csbiConsole.dwCursorPosition.X += coordConsoleSize.X - x;
							break;
						} else {
							MoveCursor(x, y);
						}
						OutputChar(lpNextCharInfo->Char.AsciiChar);
						++csbiConsole.dwCursorPosition.X;
					}
				}
				if( bReplaceLine)
				{
					if( y !=  coordConsoleSize.Y -1 ) {
						OutputString("\r\n");
					}
					csbiConsole.dwCursorPosition.X = 0;
					++csbiConsole.dwCursorPosition.Y;
				}
			}
			CHAR_INFO* tmpNext = lpPrevScreenBuffer;
			lpPrevScreenBuffer = lpNextScreenBuffer;
			lpNextScreenBuffer = tmpNext;
		}
	}
	MoveCursor(csbiNextConsole.dwCursorPosition.X, csbiNextConsole.dwCursorPosition.Y);
	FlushBuffer();
}

static BOOL ProcessEscSequence(CHAR *chSequence, DWORD dwLength);

static void WriteConsole(void)
{
	CHAR chBuf[1024];
	DWORD dwRead = 0;
	DWORD dwWritten;
	::FlushFileBuffers(hParentHandles[0]);
	if( !::PeekNamedPipe(hParentHandles[0], NULL, 0L, NULL, &dwRead, NULL) || (dwRead == 0)) {
		return;
	}
	if( ::ReadFile(hParentHandles[0], chBuf, sizeof(chBuf), &dwRead, NULL) && (dwRead > 0) )
	{
		INPUT_RECORD ir;
		ir.EventType = KEY_EVENT;
		ir.Event.KeyEvent.bKeyDown = TRUE;
		ir.Event.KeyEvent.wRepeatCount = 1;
		BOOL hasEscSequence = FALSE;
		CHAR chSeq[sizeof(chBuf)/sizeof(chBuf[0])];
		DWORD dwSeqIndex = 0;
		for( DWORD i = 0; i < dwRead; ++i )
		{
			CHAR ch = chBuf[i];
			if ( hasEscSequence ) {
				chSeq[dwSeqIndex++] = ch;
				if ( (ch != '\x1B') && !isalpha(ch) ) {
					continue;
				}
				chSeq[dwSeqIndex] = '\0';
				hasEscSequence = FALSE;
				if( ProcessEscSequence(chSeq, dwSeqIndex) ) {
					continue;
				} else {
					i -= dwSeqIndex;
					ch = chBuf[i];
				}
			} else if( ch == '\x1B' ) {
				hasEscSequence = TRUE;
				continue;
			}
			ir.Event.KeyEvent.uChar.UnicodeChar = ch;
			SHORT key = ::VkKeyScan(ch);
			SHORT state = HIBYTE(key);
			key = LOBYTE(key);
			ir.Event.KeyEvent.wVirtualKeyCode = key;
			ir.Event.KeyEvent.dwControlKeyState = 0;
			if( (key == VK_BACK) )
			{
				ir.Event.KeyEvent.uChar.UnicodeChar = ::MapVirtualKey(ir.Event.KeyEvent.wVirtualKeyCode, 2/*MAPVK_VK_TO_CHAR*/);
			} else
			{
				if ((state & 1) == 1) {
					ir.Event.KeyEvent.dwControlKeyState = SHIFT_PRESSED;
				}
				if ((state & 2) == 2) {
					ir.Event.KeyEvent.dwControlKeyState = LEFT_CTRL_PRESSED;
				}
				if ((state & 4) == 4) {
					ir.Event.KeyEvent.dwControlKeyState = LEFT_ALT_PRESSED;
				}
			}
			ir.Event.KeyEvent.wVirtualScanCode = ::MapVirtualKey(ir.Event.KeyEvent.wVirtualKeyCode, 0/*MAPVK_VK_TO_VSC*/);
			::WriteConsoleInput(hConsoleIn, &ir, 1, &dwWritten);
		}
	}
}

static BOOL ProcessEscSequence(CHAR *chSequence, DWORD dwLength)
{
	int param[4];
	if ( (chSequence[dwLength-1] == 't') && sscanf_s(chSequence, "[%d;%d;%dt", &param[0], &param[1], &param[2]) == 3 )
	{
		if ( param[0] == 8 )
		{
			CONSOLE_SCREEN_BUFFER_INFO csbi;
			::GetConsoleScreenBufferInfo(hConsoleOut, &csbi);
			
			COORD coordBufferSize;
			coordBufferSize.X = param[2];
			coordBufferSize.Y = param[1];

			SMALL_RECT srConsoleRect = csbi.srWindow;
			srConsoleRect.Right = srConsoleRect.Left + coordBufferSize.X - 1;
			srConsoleRect.Bottom = srConsoleRect.Top + coordBufferSize.Y - 1;

			COORD finalCoordBufferSize = csbi.dwSize;
			SMALL_RECT finalConsoleRect = csbi.srWindow;
			// first, resize rows
			//finalCoordBufferSize.Y  = coordBufferSize.Y;
			finalConsoleRect.Top    = srConsoleRect.Top;
			finalConsoleRect.Bottom = srConsoleRect.Bottom;
			if( coordBufferSize.Y > csbi.dwSize.Y ) {
				// if new buffer size is > than old one, we need to resize the buffer first
				::SetConsoleScreenBufferSize(hConsoleOut, finalCoordBufferSize);
				::SetConsoleWindowInfo(hConsoleOut, TRUE, &finalConsoleRect);
			} else {
				::SetConsoleWindowInfo(hConsoleOut, TRUE, &finalConsoleRect);
				::SetConsoleScreenBufferSize(hConsoleOut, finalCoordBufferSize);
			}
			// then, resize columns
			finalCoordBufferSize.X  = coordBufferSize.X;
			finalConsoleRect.Left   = srConsoleRect.Left;
			finalConsoleRect.Right  = srConsoleRect.Right;
			if( coordBufferSize.X > csbi.dwSize.X ) {
				// if new buffer size is > than old one, we need to resize the buffer first
				::SetConsoleScreenBufferSize(hConsoleOut, finalCoordBufferSize);
				::SetConsoleWindowInfo(hConsoleOut, TRUE, &finalConsoleRect);
			} else {
				::SetConsoleWindowInfo(hConsoleOut, TRUE, &finalConsoleRect);
				::SetConsoleScreenBufferSize(hConsoleOut, finalCoordBufferSize);
			}
			return TRUE;
		}
	}
	return FALSE;
}

static BOOL IsLineEmpty(CHAR_INFO *lpCharInfo, SHORT sLength)
{
	for( SHORT x = 0; x < sLength; ++x, ++lpCharInfo) {
		if( (lpCharInfo->Char.AsciiChar != ' ') || (lpCharInfo->Attributes != DEFAULT_ATTRIBUTES) ) {
			return FALSE;
		}
	}
	return TRUE;
}

static BOOL HasChanges(CHAR_INFO *lpPrevCharInfo, CHAR_INFO *lpNextCharInfo, SHORT sLength, SHORT sMinCount)
{
	SHORT sCount = 0;
	for( SHORT x = 0; x < sLength; ++x, ++lpPrevCharInfo, ++lpNextCharInfo) {
		if( ::memcmp(lpPrevCharInfo, lpNextCharInfo, sizeof(CHAR_INFO)) != 0 ) {
			if( ++sCount >= sMinCount ) {
				return TRUE;
			}
		}
	}
	return FALSE;
}

static void FlushBuffer()
{
	DWORD dwWritten = 0L;
	if( dwBufferFilled == 0 ) {
		return;
	}
	::WriteFile(hParentHandles[1], chOutputBuffer, dwBufferFilled, &dwWritten, NULL);
	::FlushFileBuffers(hParentHandles[1]);
	dwBufferFilled = 0L;
}

static void OutputChar(CHAR ch)
{
	if( dwBufferFilled + 1 > sizeof(chOutputBuffer) ){
		FlushBuffer();
	}
	chOutputBuffer[dwBufferFilled++] = ch;
}

static void OutputNumber(SHORT value)
{
	CHAR szBuf[64];
	_itoa_s(value, szBuf, sizeof(szBuf)/sizeof(szBuf[0]), 10);
	OutputString(szBuf);
}

static void OutputString(CHAR* szStr)
{
	if( dwBufferFilled + strlen(szStr) > sizeof(chOutputBuffer) ){
		FlushBuffer();
	}
	while( *szStr != 0 ) {
		chOutputBuffer[dwBufferFilled++] = *szStr++;
	}
}

