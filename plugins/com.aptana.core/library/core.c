 
#include "core.h"

int IS_JNI_1_2 = 0;

#ifdef JNI_VERSION_1_2
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
	IS_JNI_1_2 = 1;
	return JNI_VERSION_1_2;
}
#endif


void throwOutOfMemory(JNIEnv *env) {
	jclass clazz = (*env)->FindClass(env, "java/lang/OutOfMemoryError");
	if (clazz != NULL) {
		(*env)->ThrowNew(env, clazz, "");
	}
}

