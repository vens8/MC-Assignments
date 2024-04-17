//
// Created by vens8 on 17-04-2024.
//

#pragma once

#include <jni.h>
#include <android/NeuralNetworks.h>
#include <android/NeuralNetworksTypes.h>

void classify_images(JNIEnv* env, jobject imageData);