#include <jni.h>
#include <string>
#include "include/cnn_model.h"

extern "C" JNIEXPORT jstring

JNICALL
Java_com_example_nativenn_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C" JNIEXPORT void JNICALL
Java_com_example_nativenn_MainActivity_classifyImages(
        JNIEnv *env, jobject /* this */, jintArray inputTensor) {
    classify_images(env, inputTensor);
}