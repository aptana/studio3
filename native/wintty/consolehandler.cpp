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
#include "consolehandler.h"
#include "utils.h"

static DWORD WINAPI MonitorThreadProc(LPVOID lpParameter);
static void ReadConsoleBuffer(void);
static void WriteConsole(void);
static void SetConsoleSize(SHORT width, SHORT height);

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
//static CONSOLE_CURSOR_INFO cciCursor;
static CHAR_INFO* lpPrevScreenBuffer = NULL;
static CHAR_INFO* lpNextScreenBuffer = NULL;
static DWORD dwBufferMemorySize = 0;
static DWORD dwScreenBufferSize = 0;

#define COLOR_MASK (COMMON_LVB_REVERSE_VIDEO|COMMON_LVB_UNDERSCORE|FOREGROUND_BLUE|FOREGROUND_GREEN|FOREGROUND_RED|FOREGROUND_INTENSITY|BACKGROUND_BLUE|BACKGROUND_GREEN|BACKGROUND_RED|BACKGROUND_INTENSITY)
#define DEFAULT_ATTRIBUTES (FOREGROUND_BLUE|FOREGROUND_GREEN|FOREGROUND_RED)

static WORD wCharAttributes = DEFAULT_ATTRIBUTES;

#define SCREEN_BUFFER_HEIGHT (10000)

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
		::SetPriorityClass(GetCurrentProcess(), REALTIME_PRIORITY_CLASS);
		::SetThreadPriority(hMonitorThread, THREAD_PRIORITY_TIME_CRITICAL);
		::SwitchToThread();
	}

	STARTUPINFO si;
	::ZeroMemory(&si, sizeof(si));
	si.cb = sizeof(si);
	::GetStartupInfo(&si);
	if( (si.dwXCountChars != 0) && (si.dwYCountChars != 0) ) {
		SetConsoleSize((SHORT)si.dwXCountChars, (SHORT)si.dwYCountChars);
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
	while ( ((dwWaitResult = ::WaitForMultipleObjects(sizeof(waitHandles)/sizeof(waitHandles[0]), waitHandles, FALSE, dwRefreshInterval)) != WAIT_OBJECT_0)
		&& (dwWaitResult != WAIT_OBJECT_0 + 1))
	{
		switch(dwWaitResult)
		{
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
	::CloseHandle(hConsoleIn);
	::CloseHandle(hConsoleOut);

	HWND hWnd = ::GetConsoleWindow();
	if( hWnd != NULL ) {
		::PostMessage(hWnd, WM_CLOSE, 0, 0) ;
	} else {
		TerminateProcessTree(::GetCurrentProcessId());
	}
	return 0;
}

static void FlushBuffer();
static void TestAndFlushBuffer();
static void OutputChar(CHAR ch);
static void OutputUnicodeChar(WCHAR ch);
static void OutputNumber(SHORT value);
static void OutputNumber64(DWORD value);
static void OutputString(CHAR* szStr);

static void OutputCRLF()
{
	OutputString("\r\n");
}

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
		if( (relX != 0) || (relY != 0) ) {
			MoveCursorAbs(x, y);
		}
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
		OutputString("\x1B[L");
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
	if( attrs == DEFAULT_ATTRIBUTES )
	{
		OutputString("\x1B[0m");
	} else
	{
		OutputChar('\x1B');
		OutputChar('[');
		BOOL bAddSep = FALSE;
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
		if(  (attrs & FOREGROUND_INTENSITY) != (prevAttrs & FOREGROUND_INTENSITY) ) {
			if( bAddSep ) {
				OutputChar(';');
				bAddSep = FALSE;
			}
			OutputString((attrs & FOREGROUND_INTENSITY) != 0 ? "1" : "22");
			bAddSep = TRUE;
		}
		if(  (attrs & BACKGROUND_INTENSITY) != (prevAttrs & BACKGROUND_INTENSITY) ) {
			if( bAddSep ) {
				OutputChar(';');
				bAddSep = FALSE;
			}
			OutputString((attrs & BACKGROUND_INTENSITY) != 0 ? "7" : "27");
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
		if( (attrs & COMMON_LVB_REVERSE_VIDEO) != (prevAttrs & COMMON_LVB_REVERSE_VIDEO)) {
			if( bAddSep ) {
				OutputChar(';');
				bAddSep = FALSE;
			}
			OutputString((attrs & COMMON_LVB_REVERSE_VIDEO) != 0 ? "7" : "27");
			bAddSep = TRUE;
		}
		OutputChar('m');
	}
}

static BOOL IsLineEmpty(CHAR_INFO *lpCharInfo, SHORT sLength);
static BOOL HasChanges(CHAR_INFO *lpPrevCharInfo, CHAR_INFO *lpNextCharInfo, SHORT sLength, SHORT sMinCount);

static void SnapshotBuffer(COORD& coordConsoleSize, COORD  coordBufferSize, SMALL_RECT& srWindow, SMALL_RECT& srBuffer, CHAR_INFO* lpBuffer)
{
	COORD coordStart = {0, 0};
	DWORD dwScreenBufferOffset = 0;
	SHORT i;
	for( i = 0; i < coordConsoleSize.Y / coordBufferSize.Y; ++i ) {
		::ReadConsoleOutput(hConsoleOut, lpBuffer+dwScreenBufferOffset, coordBufferSize, coordStart, &srBuffer);
		srBuffer.Top = srBuffer.Top + coordBufferSize.Y;
		srBuffer.Bottom = srBuffer.Bottom + coordBufferSize.Y;
		dwScreenBufferOffset += coordBufferSize.X * coordBufferSize.Y;
	}
	coordBufferSize.Y = coordConsoleSize.Y - i * coordBufferSize.Y;
	srBuffer.Bottom = srWindow.Bottom;
	if( coordBufferSize.Y != 0 ) { 
		::ReadConsoleOutput(hConsoleOut, lpBuffer+dwScreenBufferOffset, coordBufferSize, coordStart, &srBuffer);
	}
}

static bool ProcessSnapshotDiff(CHAR_INFO* lpPrevBuffer, CHAR_INFO* lpNextBuffer, COORD& coordConsoleSize)
{
	if( ::memcmp(lpPrevBuffer, lpNextBuffer, dwScreenBufferSize*sizeof(CHAR_INFO)) != 0 )
	{
		// partial changes
		CHAR_INFO* lpPrevCharInfo = lpPrevBuffer;
		CHAR_INFO* lpNextCharInfo = lpNextBuffer;
		for( SHORT y = 0; y < coordConsoleSize.Y; ++y)
		{
			BOOL bReplaceLine = FALSE;
			BOOL bErasedLine = FALSE;
			if( bReplaceLine = HasChanges(lpPrevCharInfo, lpNextCharInfo, coordConsoleSize.X, 10) )
			{
				MoveCursor(0, y);
				if( !IsLineEmpty(lpPrevCharInfo, coordConsoleSize.X) ) {
					EraseLine();
					bErasedLine = TRUE;
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
					if( bReplaceLine && !bErasedLine && !HasChanges(lpPrevCharInfo, lpNextCharInfo, coordConsoleSize.X - x, 0) ) {
						lpPrevCharInfo += coordConsoleSize.X - x;
						lpNextCharInfo += coordConsoleSize.X - x;
						break;
					} else if( bReplaceLine && bErasedLine && IsLineEmpty(lpNextCharInfo, coordConsoleSize.X - x) ) {
						lpPrevCharInfo += coordConsoleSize.X - x;
						lpNextCharInfo += coordConsoleSize.X - x;
						break;
					} else {
						MoveCursor(x, y);
					}
					if( lpNextCharInfo->Char.UnicodeChar == L'\0' ) {
						break;
					} else if( wCharAttributes != lpNextCharInfo->Attributes ) {
						ChangeAttributes(lpNextCharInfo->Attributes);
					}
					OutputUnicodeChar(lpNextCharInfo->Char.UnicodeChar);
					++csbiConsole.dwCursorPosition.X;
				}
			}
			if( bReplaceLine)
			{
				if( y !=  coordConsoleSize.Y -1 ) {
					OutputCRLF();
					csbiConsole.dwCursorPosition.X = 0;
					++csbiConsole.dwCursorPosition.Y;
				}
			}
			TestAndFlushBuffer();
		}
		return TRUE;
	}
	return FALSE;
}

static void ReadConsoleBuffer()
{
	CONSOLE_SCREEN_BUFFER_INFO csbiNextConsole;
	//CONSOLE_CURSOR_INFO cciNextCursor;
	COORD coordConsoleSize;
	COORD coordBufferSize;
	SMALL_RECT srBuffer;
	
	::ResetEvent(hConsoleOut);
	::GetConsoleScreenBufferInfo(hConsoleOut, &csbiNextConsole);
	coordConsoleSize.X = csbiNextConsole.dwSize.X; //csbiNextConsole.srWindow.Right - csbiNextConsole.srWindow.Left + 1;
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
			SHORT nRowsDiff = (csbiConsole.srWindow.Bottom - csbiConsole.srWindow.Top + 1) - coordConsoleSize.Y;
			if( (nRowsDiff > 0) && (csbiNextConsole.srWindow.Top != 0) && (csbiConsole.srWindow.Top == 0) ) {
				MoveCursorAbs(coordConsoleSize.X-1, coordConsoleSize.Y-1);
				for( SHORT i = 0; i < nRowsDiff; ++i) {
					OutputCRLF();
				}
			}
			EraseWindow();
			MoveCursorAbs(0, 0);
		}
		csbiConsole = csbiNextConsole;
		dwScreenBufferSize = dwNextScreenBufferSize;
		if( dwScreenBufferSize*sizeof(CHAR_INFO) > dwBufferMemorySize ) {
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
		SnapshotBuffer(coordConsoleSize, coordBufferSize, csbiConsole.srWindow, srBuffer, lpPrevScreenBuffer);

		CHAR_INFO* lpCharInfo = lpPrevScreenBuffer;
		BOOL bHasNonEmptyChar = FALSE;
		csbiConsole.dwCursorPosition.X = 0;
		csbiConsole.dwCursorPosition.Y = 0;
		for( SHORT y = 0; y < coordConsoleSize.Y; ++y)
		{
			if( IsLineEmpty(lpCharInfo, coordConsoleSize.X) )
			{
				if( bHasNonEmptyChar ) {
					if ( y != coordConsoleSize.Y-1) {
						OutputCRLF();
						csbiConsole.dwCursorPosition.X = 0;
						++csbiConsole.dwCursorPosition.Y;
					}
				}
				lpCharInfo += coordConsoleSize.X;
				continue;
			}
			for( SHORT x = 0; x < coordConsoleSize.X; ++x, ++lpCharInfo)
			{
				if( (lpCharInfo->Char.UnicodeChar == L' ') && (lpCharInfo->Attributes == wCharAttributes) && !bHasNonEmptyChar ) {
					continue;
				}
				if( !bHasNonEmptyChar ) {
					for( SHORT i = 0; i < y; ++i) {
						OutputCRLF();
						csbiConsole.dwCursorPosition.X = 0;
						++csbiConsole.dwCursorPosition.Y;
					}
					for( SHORT i = 0; i < x; ++i) {
						OutputChar(' ');
						++csbiConsole.dwCursorPosition.X;
					}
					bHasNonEmptyChar = TRUE;
				}
				if( lpCharInfo->Char.UnicodeChar == L'\0' ) {
					break;
				} else if( wCharAttributes != lpCharInfo->Attributes ) {
					ChangeAttributes(lpCharInfo->Attributes);
				} else if( (lpCharInfo->Char.UnicodeChar == L' ') && IsLineEmpty(lpCharInfo, coordConsoleSize.X - x) ) {
					lpCharInfo += coordConsoleSize.X - x;
					break;
				}
				OutputUnicodeChar(lpCharInfo->Char.UnicodeChar);
				++csbiConsole.dwCursorPosition.X;
			}
			if( bHasNonEmptyChar ) {
				if( IsLineEmpty(lpCharInfo, (coordConsoleSize.Y-y)*coordConsoleSize.X) ) {
					break;
				}
				if ( y != coordConsoleSize.Y-1) {
					OutputCRLF();
					csbiConsole.dwCursorPosition.X = 0;
					++csbiConsole.dwCursorPosition.Y; 
				}
			}
			TestAndFlushBuffer();
		}
	} else
	{
		while( csbiConsole.srWindow.Top < csbiNextConsole.srWindow.Top )
		{
			SMALL_RECT srPrevBuffer;
			srPrevBuffer.Top = csbiConsole.srWindow.Top;
			srPrevBuffer.Bottom = csbiConsole.srWindow.Top + coordBufferSize.Y - 1;
			srPrevBuffer.Left = csbiConsole.srWindow.Left;
			srPrevBuffer.Right = csbiConsole.srWindow.Right;
			SnapshotBuffer(coordConsoleSize, coordBufferSize, csbiConsole.srWindow, srPrevBuffer, lpNextScreenBuffer);
			ProcessSnapshotDiff(lpPrevScreenBuffer, lpNextScreenBuffer, coordConsoleSize);
			MoveCursorAbs(coordConsoleSize.X-1, coordConsoleSize.Y-1);
			SHORT nRows = csbiNextConsole.srWindow.Top - csbiConsole.srWindow.Top;
			if( nRows > coordConsoleSize.Y ) {
				nRows = coordConsoleSize.Y;
			}
			for( SHORT i = 0; i < nRows; ++i) {
				OutputCRLF();
			}
			csbiConsole.dwCursorPosition.X = 0;
			DWORD dwShift = nRows*coordConsoleSize.X;
			if( dwShift <= dwScreenBufferSize )
			{
				::MoveMemory(lpPrevScreenBuffer, lpNextScreenBuffer + dwShift, (dwScreenBufferSize-dwShift)*sizeof(CHAR_INFO));
				CHAR_INFO* lpCharInfo = lpPrevScreenBuffer + (dwScreenBufferSize-dwShift);
				for( DWORD i = 0; i < dwShift; ++i, ++lpCharInfo) {
					lpCharInfo->Char.UnicodeChar = L' ';
					lpCharInfo->Attributes = DEFAULT_ATTRIBUTES;
				}
			}
			csbiConsole.srWindow.Top += nRows;
			csbiConsole.srWindow.Bottom += nRows;
		}

		SnapshotBuffer(coordConsoleSize, coordBufferSize, csbiConsole.srWindow, srBuffer, lpNextScreenBuffer);
		if( ProcessSnapshotDiff(lpPrevScreenBuffer, lpNextScreenBuffer, coordConsoleSize) )
		{
			CHAR_INFO* tmpNext = lpPrevScreenBuffer;
			lpPrevScreenBuffer = lpNextScreenBuffer;
			lpNextScreenBuffer = tmpNext;
		}
	}
	::GetConsoleScreenBufferInfo(hConsoleOut, &csbiNextConsole);
	MoveCursor(csbiNextConsole.dwCursorPosition.X - csbiNextConsole.srWindow.Left, csbiNextConsole.dwCursorPosition.Y - csbiNextConsole.srWindow.Top);
	/*if( ::GetConsoleCursorInfo(hConsoleOut, &cciNextCursor) && (::memcmp(&cciCursor, &cciNextCursor, sizeof(CONSOLE_CURSOR_INFO)) != 0)) {
		cciCursor = cciNextCursor;
	}*/
	FlushBuffer();
}

static BOOL ProcessEscSequence(CHAR *chSequence, DWORD dwLength);
static BOOL IsSpecialConsoleApp();

#define ESC	'\x1B'
#define DLE	'\x10'

static void WriteConsole(void)
{
	CHAR chBuf[1024];
	DWORD dwRead = 0;
	::FlushFileBuffers(hParentHandles[0]);
	if( !::PeekNamedPipe(hParentHandles[0], NULL, 0L, NULL, &dwRead, NULL) || (dwRead == 0)) {
		return;
	}
	if( ::ReadFile(hParentHandles[0], chBuf, sizeof(chBuf), &dwRead, NULL) && (dwRead > 0) )
	{
		INPUT_RECORD ir[2];
		ir[0].EventType = KEY_EVENT;
		ir[0].Event.KeyEvent.wRepeatCount = 1;
		BOOL hasEscSequence = FALSE;
		CHAR chSeq[sizeof(chBuf)/sizeof(chBuf[0])];
		DWORD dwSeqIndex = 0;
		for( DWORD i = 0; i < dwRead+1; ++i )
		{
			CHAR ch;
			BOOL bIgnoreEsc = FALSE;
			if (i == dwRead)
			{
				if( hasEscSequence )
				{
					hasEscSequence = FALSE;
					bIgnoreEsc = TRUE;
					i -= dwSeqIndex;
					dwSeqIndex = 0;
				} else {
					break;
				}
			}
			ch = chBuf[i];
			if ( hasEscSequence ) {
				chSeq[dwSeqIndex++] = ch;
				if ( (ch != ESC) && (ch != DLE) && !isalpha(ch) ) {
					continue;
				}
				chSeq[dwSeqIndex] = '\0';
				hasEscSequence = FALSE;
				if( ProcessEscSequence(chSeq, dwSeqIndex) ) {
					dwSeqIndex = 0;
					continue;
				} else {
					i -= dwSeqIndex - 1;
					ch = chBuf[i];
					dwSeqIndex = 0;
				}
			} else if( ((ch == ESC) || (ch == DLE)) && !bIgnoreEsc ) {
				hasEscSequence = TRUE;
				chSeq[dwSeqIndex++] = ch;
				continue;
			}
			SHORT key = ::VkKeyScan(ch);
			SHORT state = HIBYTE(key);
			DWORD dwKeyState = 0;
			key = LOBYTE(key);
			if( (key == VK_CANCEL && !IsSpecialConsoleApp()) ) {
				::GenerateConsoleCtrlEvent(CTRL_C_EVENT, 0);
				continue;
			}
			if( (key == VK_BACK) )
			{
				ch = ::MapVirtualKey(key, 2/*MAPVK_VK_TO_CHAR*/);
			} else
			{
				if ((state & 1) == 1) {
					dwKeyState |= SHIFT_PRESSED;
				}
				if ((state & 2) == 2) {
					dwKeyState |= LEFT_CTRL_PRESSED;
				}
				if ((state & 4) == 4) {
					dwKeyState |= LEFT_ALT_PRESSED;
				}
			}
			ir[0].Event.KeyEvent.uChar.UnicodeChar = ch;
			ir[0].Event.KeyEvent.wVirtualKeyCode = key;
			ir[0].Event.KeyEvent.dwControlKeyState = dwKeyState;
			ir[0].Event.KeyEvent.wVirtualScanCode = ::MapVirtualKey(key, 0/*MAPVK_VK_TO_VSC*/);
			::CopyMemory(&ir[1], &ir[0], sizeof(INPUT_RECORD));
			ir[0].Event.KeyEvent.bKeyDown = TRUE;
			ir[1].Event.KeyEvent.bKeyDown = FALSE;
			DWORD dwWritten;
			::WriteConsoleInput(hConsoleIn, ir, 2, &dwWritten);
		}
	}
}

static void SendConsoleKey(WORD key)
{
#if TRUE
	WORD wScanCode = ::MapVirtualKey(key, 0/*MAPVK_VK_TO_VSC*/);
	::SendMessage(::GetConsoleWindow(), WM_KEYDOWN, key, MAKELPARAM(1, KF_EXTENDED | LOBYTE(wScanCode)));
	::SendMessage(::GetConsoleWindow(), WM_KEYUP, key, MAKELPARAM(1, KF_UP | KF_REPEAT | KF_EXTENDED | LOBYTE(wScanCode)));
#else
	DWORD dwWritten;
	INPUT_RECORD ir[2];
	ir[0].EventType = KEY_EVENT;
	ir[0].Event.KeyEvent.wRepeatCount = 1;
	ir[0].Event.KeyEvent.wVirtualKeyCode = key;
	ir[0].Event.KeyEvent.dwControlKeyState = 0;
	ir[0].Event.KeyEvent.uChar.UnicodeChar = ::MapVirtualKey(key, 2/*MAPVK_VK_TO_CHAR*/);
	ir[0].Event.KeyEvent.wVirtualScanCode = ::MapVirtualKey(key, 0/*MAPVK_VK_TO_VSC*/);
	::CopyMemory(&ir[1], &ir[0], sizeof(INPUT_RECORD));
	ir[0].Event.KeyEvent.bKeyDown = TRUE;
	ir[1].Event.KeyEvent.bKeyDown = FALSE;
	::WriteConsoleInput(hConsoleIn, ir, 2, &dwWritten);
#endif
}


static void SetConsoleSize(SHORT width, SHORT height)
{
	HANDLE hConsole = ::CreateFile(_T("CONOUT$"), GENERIC_WRITE | GENERIC_READ, FILE_SHARE_READ | FILE_SHARE_WRITE, NULL, OPEN_EXISTING, 0, 0);

	CONSOLE_SCREEN_BUFFER_INFO csbi;
	::GetConsoleScreenBufferInfo(hConsole, &csbi);
	
	COORD coordBufferSize;
	coordBufferSize.X = width;
	coordBufferSize.Y = height;

	SMALL_RECT srConsoleRect = csbi.srWindow;
	srConsoleRect.Right = srConsoleRect.Left + width - 1;
	BOOL bAdjustTop = TRUE;
	if( srConsoleRect.Bottom - srConsoleRect.Top + 1 < height ) { // height increase
		if( srConsoleRect.Bottom + 1 <= height ) { // if close to top, adjust top then adjust bottom
			srConsoleRect.Top = 0;
			bAdjustTop = FALSE;
		}
	} else { // height decrease
		if( csbi.dwCursorPosition.Y <= srConsoleRect.Top + height - 1 ) { // cursor is inside adjusted area, adjust bottom
			bAdjustTop = FALSE;
		} else { // cursor is outside, adjust bottom to cursor then adjust top
			srConsoleRect.Bottom = csbi.dwCursorPosition.Y;
		}
	}

	if( bAdjustTop ) {
		srConsoleRect.Top = srConsoleRect.Bottom - height + 1; // move top
	} else {
		srConsoleRect.Bottom = srConsoleRect.Top + height - 1; // move bottom
	}

	COORD finalCoordBufferSize = csbi.dwSize;
	SMALL_RECT finalConsoleRect = csbi.srWindow;
	// first, resize rows
	finalCoordBufferSize.Y = SCREEN_BUFFER_HEIGHT;
	finalConsoleRect.Top    = srConsoleRect.Top;
	finalConsoleRect.Bottom = srConsoleRect.Bottom;
	if( coordBufferSize.Y > csbi.dwSize.Y ) {
		// if new buffer size is > than old one, we need to resize the buffer first
		::SetConsoleScreenBufferSize(hConsole, finalCoordBufferSize);
		::SetConsoleWindowInfo(hConsole, TRUE, &finalConsoleRect);
	} else {
		::SetConsoleWindowInfo(hConsole, TRUE, &finalConsoleRect);
		::SetConsoleScreenBufferSize(hConsole, finalCoordBufferSize);
	}
	// then, resize columns
	finalCoordBufferSize.X  = coordBufferSize.X;
	finalConsoleRect.Left   = srConsoleRect.Left;
	finalConsoleRect.Right  = srConsoleRect.Right;
	if( coordBufferSize.X > csbi.dwSize.X ) {
		// if new buffer size is > than old one, we need to resize the buffer first
		::SetConsoleScreenBufferSize(hConsole, finalCoordBufferSize);
		::SetConsoleWindowInfo(hConsole, TRUE, &finalConsoleRect);
	} else {
		::SetConsoleWindowInfo(hConsole, TRUE, &finalConsoleRect);
		::SetConsoleScreenBufferSize(hConsole, finalCoordBufferSize);
	}
	::CloseHandle(hConsole);
}

static void SendProcessList();

static BOOL ProcessEscSequence(CHAR *chSequence, DWORD dwLength)
{
	int param[4];
	switch( chSequence[dwLength-1] ) {
		case 't':
			if( (sscanf_s(chSequence, "\x1B[%d;%d;%dt", &param[0], &param[1], &param[2]) == 3) && (param[0] == 8) ) {
				SetConsoleSize(param[2], param[1]);
				return TRUE;
			}
			break;
		case 'p':
			if( strcmp(chSequence, "\x10$p") == 0 ) {
				SendProcessList();
				return TRUE;
			}
			break;
		case 'A':
			SendConsoleKey(VK_UP);
			return TRUE;
		case 'B':
			SendConsoleKey(VK_DOWN);
			return TRUE;
		case 'C':
			SendConsoleKey(VK_RIGHT);
			return TRUE;
		case 'D':
			SendConsoleKey(VK_LEFT);
			return TRUE;
		case 'F':
			SendConsoleKey(VK_END);
			return TRUE;
		case 'H':
			SendConsoleKey(VK_HOME);
			return TRUE;
	}
	return FALSE;
}

static BOOL IsLineEmpty(CHAR_INFO *lpCharInfo, SHORT sLength)
{
	for( SHORT x = 0; x < sLength; ++x, ++lpCharInfo) {
		if( (lpCharInfo->Char.UnicodeChar == L'\0') || (lpCharInfo->Attributes == 0) ) {
			continue;
		}
		if( (lpCharInfo->Char.UnicodeChar != L' ') || (lpCharInfo->Attributes != DEFAULT_ATTRIBUTES) ) {
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

static void TestAndFlushBuffer()
{
	if( dwBufferFilled >= sizeof(chOutputBuffer)/2 ) {
		FlushBuffer();
	}
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

static void OutputNumber64(DWORD value)
{
	CHAR szBuf[128];
	_i64toa_s(value, szBuf, sizeof(szBuf)/sizeof(szBuf[0]), 10);
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

static void OutputUnicodeChar(WCHAR ch)
{
	if( dwBufferFilled + 1 > sizeof(chOutputBuffer) ){
		FlushBuffer();
	}
	WCHAR wcString[2] = { ch, L'\0' };
	CHAR cString[16];
	int size = ::WideCharToMultiByte(CP_UTF8, 0, wcString, -1, cString, sizeof(cString), NULL, NULL);
	if( size > 0 ) {
		OutputString(cString);
	}
}

static void SendProcessList()
{
	DWORD dwProcesses[64];
	DWORD dwCount = ::GetConsoleProcessList(dwProcesses, sizeof(dwProcesses)/sizeof(dwProcesses[0]));
	OutputString("\x10$");
	for (DWORD i = dwCount; i > 0; --i) {
		OutputNumber64(dwProcesses[i-1]);
		if( i > 1 ) {
			OutputChar(',');
		}
	}
	OutputChar('p');
	FlushBuffer();
}

static BOOL IsSpecialConsoleApp()
{
	BOOL bResult = FALSE;
	DWORD dwProcesses[64];
	DWORD dwCount = ::GetConsoleProcessList(dwProcesses, sizeof(dwProcesses)/sizeof(dwProcesses[0]));
	HANDLE hModuleSnap = CreateToolhelp32Snapshot(TH32CS_SNAPMODULE, dwProcesses[0]);
	if (hModuleSnap != INVALID_HANDLE_VALUE) {
		MODULEENTRY32 me32 = {0};
		me32.dwSize = sizeof(MODULEENTRY32);
		if (Module32First(hModuleSnap, &me32)) {
			bResult = wcsstr(me32.szExePath, _T("ssh.exe")) != NULL;
		}
		CloseHandle(hModuleSnap);
	}
	return bResult;
}