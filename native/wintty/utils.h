#pragma once

extern HANDLE GetParentProcess(void);
extern void TerminateProcessTree(DWORD dwProcessId);
