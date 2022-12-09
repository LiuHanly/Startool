#include <jni.h>
#include <string>
#include "stdlib.h"
#include "stdio.h"
#include "string.h"
#include <unistd.h>
#include "android/log.h"


#define MEMOP_LOG_TAG "libjnimemop"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, MEMOP_LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, MEMOP_LOG_TAG, __VA_ARGS__)


#define MAX_SIZE 2000
static int *p[MAX_SIZE];
static int count = 0;


extern "C"
JNIEXPORT void JNICALL
Java_com_glorystar_systemtest_utlis_tool_MemOpUtils_free(JNIEnv *env, jclass clazz) {
    for (int i = 0; i < MAX_SIZE; i++) {
        if (NULL != p[i]) {
            free(p[i]);
            p[i] = NULL;
        }
    }
    count = 0;
    LOGI("memfree, free memory");
    return;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_glorystar_systemtest_utlis_tool_MemOpUtils_malloc(JNIEnv *env, jclass clazz, jint size) {
    if (count < MAX_SIZE) {
        p[count] = (int *) malloc(256 * 1024 * size * sizeof(int));
        if (p[count]) {
            memset(p[count], 2, 256 * 1024 * size * sizeof(int));
            count++;
            LOGI("memfill, malloc memory size is %d", size);
            return size;
        } else {
            LOGI("memfill, malloc memory error");
        }
    } else {
        LOGE("memfill, count is MAX_SIZE");
    }
    return 0;

}




extern "C"
JNIEXPORT void JNICALL
Java_com_glorystar_systemtest_utlis_tool_MemOpUtils_bluetooth(JNIEnv *env, jclass clazz) {


}