
#include "core.h"
#include <windows.h>
#include <tlhelp32.h>
#include <shlobj.h>
#include <stdlib.h>
#include <string.h>


#ifndef NO_GetProcessList
/*
 * Class:     com_aptana_core_internal_platform_CoreNatives
 * Method:    GetProcessList
 * Signature: ()[Ljava/lang/Object;
 */
JNIEXPORT jobjectArray JNICALL CORE_NATIVE(GetProcessList)
	(JNIEnv *env, jclass that )
{
	HANDLE hProcessSnap = NULL;
	int i, size = 0;
	int array_length = 16;
	jobject* array = malloc(sizeof(jobject*)*array_length);
	jobjectArray result;
	jclass objectClazz = (*env)->FindClass(env, "java/lang/Object");
	jclass integerClazz = (*env)->FindClass(env, "java/lang/Integer");
	jmethodID integerConstructor;
	if ((objectClazz == NULL) || (integerClazz == NULL)) {
		return NULL; /* exception thrown */
	}
	integerConstructor = (*env)->GetMethodID(env, integerClazz, "<init>", "(I)V");
	if (integerConstructor == NULL) {
		return NULL; /* exception thrown */
	}
	
	// Take a snapshot of all processes in the system
	hProcessSnap = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
	if (hProcessSnap != INVALID_HANDLE_VALUE) {
		PROCESSENTRY32 pe32 = {0};
		pe32.dwSize = sizeof(PROCESSENTRY32);
		if (Process32First(hProcessSnap, &pe32)) {
			do {
				HANDLE hModuleSnap = CreateToolhelp32Snapshot(TH32CS_SNAPMODULE, pe32.th32ProcessID);
				if (hModuleSnap != INVALID_HANDLE_VALUE) {
					MODULEENTRY32 me32 = {0};
					me32.dwSize = sizeof(MODULEENTRY32);
					if (Module32First(hModuleSnap, &me32)) {
						do {
							//if (me32.th32ModuleID == pe32.th32ModuleID) {
								jobject pid;
								jobject ppid;
								jstring string = (*env)->NewString(env, me32.szExePath, wcslen(me32.szExePath));
								if (string == NULL) {
									return NULL; /* exception thrown */
								}
								pid = (*env)->NewObject(env, integerClazz, integerConstructor, (jint)pe32.th32ProcessID);
								if (pid == NULL) {
									return NULL; /* exception thrown */
								}
								ppid = (*env)->NewObject(env, integerClazz, integerConstructor, (jint)pe32.th32ParentProcessID);
								if (ppid == NULL) {
									return NULL; /* exception thrown */
								}
								if ( size+2 >= array_length ) {
									array_length *= 2;
									array = realloc(array,sizeof(jobject*)*array_length);
								}
								array[size++] = string;								
								array[size++] = pid;								
								array[size++] = ppid;								
								break;
							//}
						} while(Module32Next(hModuleSnap, &me32));
					}				
					CloseHandle(hModuleSnap);
				}
			} while(Process32Next(hProcessSnap, &pe32));
		}
		CloseHandle(hProcessSnap);
	}
	
	result = (*env)->NewObjectArray(env, size, objectClazz, NULL);
	if (result == NULL) {
		return NULL; /* exception thrown */
	}
	
	for( i = 0; i < size; ++i ) {
		jobject value = array[i];
		(*env)->SetObjectArrayElement(env, result, i, value);
		(*env)->DeleteLocalRef(env, value);
	}
	return result;
}
#endif

#ifndef NO_GetCurrentProcessId
/*
 * Class:     com_aptana_core_internal_platform_CoreNatives
 * Method:    GetCurrentProcessId
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_aptana_ide_internal_core_CoreNatives_GetCurrentProcessId
  (JNIEnv *env, jclass that)
{
	return GetCurrentProcessId();
}
#endif

#ifndef NO_KillProcess
/*
 * Class:     com_aptana_core_internal_platform_CoreNatives
 * Method:    KillProcess
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_aptana_ide_internal_core_CoreNatives_KillProcess
  (JNIEnv *env, jclass that, jint pid)
{
	HANDLE hProcess = OpenProcess(PROCESS_TERMINATE, FALSE, (DWORD)pid);
	if (hProcess != NULL) {
		TerminateProcess(hProcess, 0);
	}
}
#endif

#ifndef NO_GetSpecialFolderPath
/*
 * Class:     com_aptana_core_internal_platform_CoreNatives
 * Method:    GetSpecialFolderPath
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL CORE_NATIVE(GetSpecialFolderPath)
	(JNIEnv *env, jclass that, jint csidl )
{
	jstring str = NULL;
	TCHAR szPath[MAX_PATH];
	
	if(SUCCEEDED(SHGetFolderPath(NULL, csidl, NULL, SHGFP_TYPE_CURRENT, szPath) ) )
	{
		str = (*env)->NewString(env, szPath, wcslen(szPath));
	}
	
	return str;

}
#endif

#ifndef NO_SHObjectProperties
/*
 * Class:     com_aptana_core_internal_platform_CoreNatives
 * Method:    SHObjectProperties
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jboolean JNICALL CORE_NATIVE(SHObjectProperties)
	(JNIEnv *env, jclass that, CORE_PTR hwnd, jint type, jstring object, jstring page )
{
	jboolean result = JNI_FALSE;
	const TCHAR *szPage = NULL;
	const TCHAR *szObject = (*env)->GetStringChars(env, object, NULL);
	if ( szObject == NULL ){
		return JNI_FALSE; /* exception thrown */
	}
	if ( page != NULL ) {
		szPage = (*env)->GetStringChars(env, page, NULL);
		if ( szPage == NULL ){
			return JNI_FALSE; /* exception thrown */
		}
	}
	result = SHObjectProperties((HWND)hwnd, (DWORD)type, szObject, szPage);
	if ( (page != NULL) && (szPage != NULL) ) {
		(*env)->ReleaseStringChars(env, page, szPage);
	}
	(*env)->ReleaseStringChars(env, object, szObject);
	
	return result;

}
#endif

#ifndef NO_ExpandEnvironmentStrings
/*
 * Class:     com_aptana_core_internal_platform_CoreNatives
 * Method:    ExpandEnvironmentStrings
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL CORE_NATIVE(ExpandEnvironmentStrings)
	(JNIEnv *env, jclass that, jstring path )
{
	jstring result = path;
	TCHAR szPath[2*MAX_PATH];
	const TCHAR *szEnvPath = (*env)->GetStringChars(env, path, NULL);
	if ( szEnvPath == NULL ){
		return NULL; /* exception thrown */
	}
	if( ExpandEnvironmentStrings(szEnvPath, szPath, sizeof(szPath)) != 0 )
	{
		result = (*env)->NewString(env, szPath, wcslen(szPath));
	}
	(*env)->ReleaseStringChars(env, path, szEnvPath);
	
	return result;

}
#endif


#ifndef NO_SetFileAttributes
/*
 * Class:     com_aptana_core_internal_platform_CoreNatives
 * Method:    SetFileAttributes
 * Signature: (Ljava/lang/String;II)Z
 */
JNIEXPORT jboolean JNICALL CORE_NATIVE(SetFileAttributes)
	(JNIEnv *env, jclass that, jstring path, jint attrsSet, jint attrsClear )
{
	jboolean result = JNI_FALSE;
	DWORD dwAttrs = 0;
	const TCHAR *szPath = (*env)->GetStringChars(env, path, NULL);
	if ( szPath == NULL ){
		return JNI_FALSE; /* exception thrown */
	}
	dwAttrs = GetFileAttributes(szPath);
	if( dwAttrs != INVALID_FILE_ATTRIBUTES ) {
		dwAttrs |= attrsSet;
		dwAttrs &= ~attrsClear;		
		result = SetFileAttributes(szPath, dwAttrs) != 0 ? JNI_TRUE : JNI_FALSE;
	}
	(*env)->ReleaseStringChars(env, path, szPath);
	
	return result;

}
#endif

#ifndef NO_RegOpenKey
/*
 * Class:     com_aptana_core_internal_platform_CoreNatives
 * Method:    RegOpenKey
 * Signature: (JLjava/lang/String;I[J)Z
 */
JNIEXPORT jboolean JNICALL CORE_NATIVE(RegOpenKey)
  (JNIEnv *env, jclass that, jlong hKeyParent, jstring keyName, jint accessMask, jlongArray hKeyResult)
{
	jboolean result = JNI_FALSE;
	jlong *lphKeyResult;
	HKEY hKey = NULL;
	LONG lRes;
	
	const TCHAR *szKeyName = (*env)->GetStringChars(env, keyName, NULL);
	if ( szKeyName == NULL ){
		return JNI_FALSE; /* exception thrown */
	}
	lphKeyResult = (*env)->GetLongArrayElements(env, hKeyResult, NULL);
	if ( lphKeyResult == NULL ){
		return JNI_FALSE; /* exception thrown */
	}
	
	lRes = RegOpenKeyEx((HKEY)hKeyParent, szKeyName, 0, (REGSAM)accessMask, &hKey);
	if (lRes == ERROR_SUCCESS)
	{
		result = JNI_TRUE;
		*lphKeyResult = (jlong)hKey;
	}
	
	(*env)->ReleaseLongArrayElements(env, hKeyResult, lphKeyResult, 0);
	(*env)->ReleaseStringChars(env, keyName, szKeyName);
	
	return result;
}
#endif

#ifndef NO_RegCreateKey
/*
 * Class:     com_aptana_core_internal_platform_CoreNatives
 * Method:    RegCreateKey
 * Signature: (JLjava/lang/String;I[J)Z
 */
JNIEXPORT jboolean JNICALL CORE_NATIVE(RegCreateKey)
  (JNIEnv *env, jclass that, jlong hKeyParent, jstring keyName, jint accessMask, jlongArray hKeyResult)
{
	jboolean result = JNI_FALSE;
	jlong *lphKeyResult;
	HKEY hKey = NULL;
	LONG lRes;
	
	const TCHAR *szKeyName = (*env)->GetStringChars(env, keyName, NULL);
	if ( szKeyName == NULL ){
		return JNI_FALSE; /* exception thrown */
	}
	lphKeyResult = (*env)->GetLongArrayElements(env, hKeyResult, NULL);
	if ( lphKeyResult == NULL ){
		return JNI_FALSE; /* exception thrown */
	}
	
	lRes = RegCreateKeyEx((HKEY)hKeyParent, szKeyName, 0, NULL, REG_OPTION_NON_VOLATILE, (REGSAM)accessMask, NULL, &hKey, NULL);
	if (lRes == ERROR_SUCCESS)
	{
		result = JNI_TRUE;
		*lphKeyResult = (jlong)hKey;
	}
	
	(*env)->ReleaseLongArrayElements(env, hKeyResult, lphKeyResult, 0);
	(*env)->ReleaseStringChars(env, keyName, szKeyName);
	
	return result;
}
#endif

#ifndef NO_RegCloseKey
/*
 * Class:     com_aptana_core_internal_platform_CoreNatives
 * Method:    RegCloseKey
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL CORE_NATIVE(RegCloseKey)
  (JNIEnv *env, jclass that, jlong hKey)
{
	jboolean result = JNI_FALSE;
	LONG lRes = RegCloseKey((HKEY)hKey);
	result = (lRes == ERROR_SUCCESS);
	
	return result;
}
#endif

#ifndef NO_RegQueryValue
/*
 * Class:     com_aptana_core_internal_platform_CoreNatives
 * Method:    RegQueryValue
 * Signature: (JLjava/lang/String;[Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL CORE_NATIVE(RegQueryValue)
  (JNIEnv *env, jclass that, jlong hKey, jstring valueName, jobjectArray valueResult)
{
	jboolean result = JNI_FALSE;
	LONG lRes;
	DWORD dwType = 0;
	ULONG nBytes = 0L;
	
	const TCHAR *szValueName = NULL;
	if ( valueName != NULL )
	{
		szValueName = (*env)->GetStringChars(env, valueName, NULL);
		if ( szValueName == NULL ) {
			return JNI_FALSE; /* exception thrown */
		}
	}
		
	lRes = RegQueryValueEx((HKEY)hKey, szValueName, NULL, &dwType, NULL, &nBytes);
	if ( (lRes == ERROR_SUCCESS) && ( (dwType == REG_SZ) || (dwType == REG_EXPAND_SZ) ) )
	{
		TCHAR *lpszValue = (TCHAR*)malloc(nBytes);
		lRes = RegQueryValueEx((HKEY)hKey, szValueName, NULL, &dwType, (LPBYTE)lpszValue, &nBytes);
		if ( (lRes == ERROR_SUCCESS) && (lpszValue != NULL) && (nBytes>=sizeof(TCHAR)))
		{
			jstring value = (*env)->NewString(env, lpszValue, (nBytes/sizeof(TCHAR))-1 );
			if ( value == NULL ) {
				return JNI_FALSE; /* exception thrown */
			}
			(*env)->SetObjectArrayElement(env, valueResult, 0, value);
			result = JNI_TRUE;
		}	
		free(lpszValue);
	}
	
	if ( (valueName != NULL) && (szValueName != NULL) )
	{
		(*env)->ReleaseStringChars(env, valueName, szValueName);
	}
	
	return result;
}
#endif


#ifndef NO_RegSetValue
/*
 * Class:     com_aptana_core_internal_platform_CoreNatives
 * Method:    RegSetValue
 * Signature: (JLjava/lang/String;Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL CORE_NATIVE(RegSetValue)
  (JNIEnv *env, jclass that, jlong hKey, jstring valueName, jstring value)
{
	jboolean result;
	LONG lRes;
	
	const TCHAR *szValueName = NULL;
	const TCHAR *szValue = NULL;
	DWORD dwLength = 0;
	if ( valueName != NULL )
	{
		szValueName = (*env)->GetStringChars(env, valueName, NULL);
		if ( szValueName == NULL ) {
			return JNI_FALSE; /* exception thrown */
		}
	}
	if ( value != NULL )
	{
		dwLength = (*env)->GetStringLength(env, value);
		szValue = (*env)->GetStringChars(env, value, NULL);
		if ( szValue == NULL ) {
			return JNI_FALSE; /* exception thrown */
		}
	}
	
	lRes = RegSetValueEx((HKEY)hKey, szValueName, NULL, REG_SZ, (LPBYTE)szValue, dwLength*sizeof(TCHAR));
	result = (lRes == ERROR_SUCCESS) ? JNI_TRUE : JNI_FALSE;
	
	if ( (value != NULL) && (szValue != NULL) )
	{
		(*env)->ReleaseStringChars(env, value, szValue);
	}
	if ( (valueName != NULL) && (szValueName != NULL) )
	{
		(*env)->ReleaseStringChars(env, valueName, szValueName);
	}
	return result;
}
#endif


#ifndef NO_IsUserAnAdmin
/*
 * Class:     com_aptana_core_internal_platform_CoreNatives
 * Method:    IsUserAnAdmin
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL CORE_NATIVE(IsUserAnAdmin)
  (JNIEnv *env, jclass that)
{
	jboolean result = JNI_FALSE;
	SID_IDENTIFIER_AUTHORITY authority = SECURITY_NT_AUTHORITY;
	PSID adminGroup;
	if ( AllocateAndInitializeSid(
		&authority, 2,
		SECURITY_BUILTIN_DOMAIN_RID,
		DOMAIN_ALIAS_RID_ADMINS,
		0, 0, 0, 0, 0, 0,
		&adminGroup) )
	{
		BOOL member = FALSE;
		if ( CheckTokenMembership( NULL, adminGroup, &member) && (member == TRUE))
		{
			result = JNI_TRUE;
		}
		FreeSid(adminGroup);
	}
	
	return result;
}
#endif

#ifndef NO_ShellExecuteEx
/*
 * Class:     com_aptana_core_internal_platform_CoreNatives
 * Method:    ShellExecuteEx
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)Z
 */
JNIEXPORT jboolean JNICALL CORE_NATIVE(ShellExecuteEx)
	(JNIEnv *env, jclass that, jstring file, jstring params, jstring verb, jstring dir, jint nShow)
{
	SHELLEXECUTEINFO shExecInfo;
	
	jboolean result = JNI_FALSE;
	const TCHAR *szParams = NULL;
	const TCHAR *szVerb = NULL;
	const TCHAR *szDir = NULL;
	const TCHAR *szFile = (*env)->GetStringChars(env, file, NULL);
	if ( szFile == NULL ){
		return JNI_FALSE; /* exception thrown */
	}
	if ( params != NULL ) {
		szParams = (*env)->GetStringChars(env, params, NULL);
		if ( szParams == NULL ){
			return JNI_FALSE; /* exception thrown */
		}
	}
	if ( verb != NULL ) {
		szVerb = (*env)->GetStringChars(env, verb, NULL);
		if ( szVerb == NULL ){
			return JNI_FALSE; /* exception thrown */
		}
	}
	if ( dir != NULL ) {
		szDir = (*env)->GetStringChars(env, dir, NULL);
		if ( szDir == NULL ){
			return JNI_FALSE; /* exception thrown */
		}
	}
	
	shExecInfo.cbSize = sizeof(SHELLEXECUTEINFO);
	shExecInfo.fMask = NULL;
	shExecInfo.hwnd = NULL;
	shExecInfo.lpVerb = szVerb;
	shExecInfo.lpFile = szFile;
	shExecInfo.lpParameters = szParams;
	shExecInfo.lpDirectory = szDir;
	shExecInfo.nShow = nShow;
	shExecInfo.hInstApp = NULL;
	
	result = ShellExecuteEx(&shExecInfo);
	
	if ( (dir != NULL) && (szDir != NULL) ) {
		(*env)->ReleaseStringChars(env, dir, szDir);
	}
	if ( (verb != NULL) && (szVerb != NULL) ) {
		(*env)->ReleaseStringChars(env, verb, szVerb);
	}
	if ( (params != NULL) && (szParams != NULL) ) {
		(*env)->ReleaseStringChars(env, params, szParams);
	}
	(*env)->ReleaseStringChars(env, file, szFile);
	
	return result;

}
#endif
  