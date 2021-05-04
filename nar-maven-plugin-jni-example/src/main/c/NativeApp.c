#include <stdio.h>
#include "com_github_jjYBdx4IL_maven_examples_nar_NativeApp.h"

JNIEXPORT jstring JNICALL Java_com_github_jjYBdx4IL_maven_examples_nar_NativeApp_sayHello( JNIEnv *env, jobject obj ) {
	jstring value;           /* the return value */

	char buf[40];            /* working buffer (really only need 20 ) */

	sprintf ( buf, "%s", "Hello NAR World!" );

	value = (*env)->NewStringUTF( env, buf );

	return value;
}

