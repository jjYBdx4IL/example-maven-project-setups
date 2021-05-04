#include <com_github_jjYBdx4IL_maven_examples_jnajni_JNIAccessWithLibLoader.h>

/*
 * test functions for native access via JNI
 *
 */

/*
 * Class:     com_github_jjYBdx4IL_maven_examples_jnajni_JNIAccess
 * Method:    sum
 * Signature: ([I)I
 */
JNIEXPORT jint JNICALL Java_com_github_jjYBdx4IL_maven_examples_jnajni_JNIAccessWithLibLoader_sum
  (JNIEnv *env, jclass class, jintArray ptr)
{
    jint sum = 0;
    int i;

    jsize len = (*env)->GetArrayLength(env, ptr);
    jint *body = (*env)->GetIntArrayElements(env, ptr, 0);

    for (i=0; i < len; i++)
        sum += body[i];

    (*env)->ReleaseIntArrayElements(env, ptr, body, 0);
    return sum;
}

/*
 * Class:     com_github_jjYBdx4IL_maven_examples_jnajni_JNIAccess
 * Method:    inc
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_github_jjYBdx4IL_maven_examples_jnajni_JNIAccessWithLibLoader_inc
  (JNIEnv *env, jclass class, jint arg)
{
    return arg + 1;
}