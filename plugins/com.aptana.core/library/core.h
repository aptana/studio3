
/**
 * core.h
 *
 * This file contains the global macro declarations for the
 * Core library.
 *
 */

#ifndef INC_core_H
#define INC_core_H

#include "jni.h"
#include "com_aptana_core_internal_platform_CoreNatives.h"

#define CORE_NATIVE(func) Java_com_aptana_core_internal_platform_CoreNatives_##func

#ifdef __cplusplus
extern "C" {
#endif

extern int IS_JNI_1_2;

/* 64 bit support */
#ifndef CORE_PTR_SIZE_64

#define GetCORE_PTRField GetIntField
#define SetCORE_PTRField SetIntField
#define NewCORE_PTRArray NewIntArray
#define CallStaticCORE_PTRMethodV CallStaticIntMethodV
#define CallCORE_PTRMethodV CallIntMethodV
#define CallStaticCORE_PTRMethod CallStaticIntMethod
#define CallCORE_PTRMethod CallIntMethod
#define GetCORE_PTRArrayElements GetIntArrayElements
#define ReleaseCORE_PTRArrayElements ReleaseIntArrayElements
#define CORE_PTRArray jintArray
#define CORE_PTR jint
#define CORE_PTR_SIGNATURE "I"

#else

#define GetCORE_PTRField GetLongField
#define SetCORE_PTRField SetLongField
#define NewCORE_PTRArray NewLongArray
#define CallStaticCORE_PTRMethodV CallStaticLongMethodV
#define CallCORE_PTRMethodV CallLongMethodV
#define CallStaticCORE_PTRMethod CallStaticLongMethod
#define CallCORE_PTRMethod CallLongMethod
#define GetCORE_PTRArrayElements GetLongArrayElements
#define ReleaseCORE_PTRArrayElements ReleaseLongArrayElements
#define CORE_PTRArray jlongArray
#define CORE_PTR jlong
#define CORE_PTR_SIGNATURE "J"

#endif

void throwOutOfMemory(JNIEnv *env);

#define CHECK_NULL_VOID(ptr) \
	if ((ptr) == NULL) { \
		throwOutOfMemory(env); \
		return; \
	}

#define CHECK_NULL(ptr) \
	if ((ptr) == NULL) { \
		throwOutOfMemory(env); \
		return 0; \
	}

#ifdef __cplusplus
}
#endif 

#endif /* ifndef INC_core_H */
