//
// Created by vens8 on 17-04-2024.
//

#include "../include/cnn_model.h"
#include <android/log.h>

#define LOG_TAG "NativeNNModel"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

const int IMAGE_SIZE = 28;
const int NUM_CHANNELS = 1;
const int NUM_CLASSES = 10;

const int CONV_FILTERS = 32;
const int CONV_KERNEL_SIZE = 5;
const int POOL_SIZE = 2;
const int FC_SIZE = 128;

int argmax(float *arr, const int size);

void classify_images(JNIEnv* env, jobject imageData) {
    LOGD("classify_images called");

    // Load the CNN model using NNAPI
    ANeuralNetworksModel* model;
    int status = ANeuralNetworksModel_create(&model);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error creating ANeuralNetworksModel: %d", status);
        return;
    }
    LOGD("ANeuralNetworksModel created");


    // Add the input operand
    const uint32_t inputDimensions[] = {1, IMAGE_SIZE, IMAGE_SIZE, NUM_CHANNELS};
    ANeuralNetworksOperandType inputType = {
            .type = ANEURALNETWORKS_TENSOR_FLOAT32,
            .dimensionCount = 4,
            .dimensions = inputDimensions,
            .scale = 0.0f,
            .zeroPoint = 0
    };
    status = ANeuralNetworksModel_addOperand(model, &inputType);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding input operand: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Input operand added to the model");

    // Add the convolution layer operands
    const uint32_t convWeightsDimensions[] = {CONV_FILTERS, NUM_CHANNELS, CONV_KERNEL_SIZE, CONV_KERNEL_SIZE};
    ANeuralNetworksOperandType convWeightsType = {
            .type = ANEURALNETWORKS_TENSOR_FLOAT32,
            .dimensionCount = 4,
            .dimensions = convWeightsDimensions,
            .scale = 0.0f,
            .zeroPoint = 0
    };
    status = ANeuralNetworksModel_addOperand(model, &convWeightsType);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding convolution weights operand: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Convolution weights operand added to the model");

    ANeuralNetworksOperandType convBiasesType = {
            .type = ANEURALNETWORKS_TENSOR_FLOAT32,
            .dimensionCount = 1,
            .dimensions = reinterpret_cast<const uint32_t *>(CONV_FILTERS),
            .scale = 0.0f,
            .zeroPoint = 0
    };
    status = ANeuralNetworksModel_addOperand(model, &convBiasesType);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding convolution biases operand: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Convolution biases operand added to the model");

    const uint32_t convOutputDimensions[] = {1, IMAGE_SIZE - CONV_KERNEL_SIZE + 1, IMAGE_SIZE - CONV_KERNEL_SIZE + 1, CONV_FILTERS};
    ANeuralNetworksOperandType convOutputType = {
            .type = ANEURALNETWORKS_TENSOR_FLOAT32,
            .dimensionCount = 4,
            .dimensions = convOutputDimensions,
            .scale = 0.0f,
            .zeroPoint = 0
    };
    LOGD("Convolution output operand created");

    // Add the ReLU layer operand
    ANeuralNetworksOperandType reluOutputType = convOutputType;
    status = ANeuralNetworksModel_addOperand(model, &reluOutputType);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding ReLU layer operand: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("ReLU layer operand added to the model");


    // Add the max pooling layer operand
    const uint32_t poolOutputDimensions[] = {1, (IMAGE_SIZE - CONV_KERNEL_SIZE + 1) / POOL_SIZE, (IMAGE_SIZE - CONV_KERNEL_SIZE + 1) / POOL_SIZE, CONV_FILTERS};
    ANeuralNetworksOperandType poolOutputType = {
            .type = ANEURALNETWORKS_TENSOR_FLOAT32,
            .dimensionCount = 4,
            .dimensions = poolOutputDimensions,
            .scale = 0.0f,
            .zeroPoint = 0
    };
    status = ANeuralNetworksModel_addOperand(model, &poolOutputType);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding max pooling layer operand: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Max pooling layer operand added to the model");


    // Add the fully connected layer operands
    const uint32_t fcWeightsDimensions[] = {FC_SIZE, (IMAGE_SIZE - CONV_KERNEL_SIZE + 1) / POOL_SIZE * (IMAGE_SIZE - CONV_KERNEL_SIZE + 1) / POOL_SIZE * CONV_FILTERS};
    ANeuralNetworksOperandType fcWeightsType = {
            .type = ANEURALNETWORKS_TENSOR_FLOAT32,
            .dimensionCount = 2,
            .dimensions = fcWeightsDimensions,
            .scale = 0.0f,
            .zeroPoint = 0
    };
    status = ANeuralNetworksModel_addOperand(model, &fcWeightsType);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding fully connected weights operand: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Fully connected weights operand added to the model");

    const uint32_t fcBiasesDimensions[] = {FC_SIZE};
    ANeuralNetworksOperandType fcBiasesType = {
            .type = ANEURALNETWORKS_TENSOR_FLOAT32,
            .dimensionCount = 1,
            .dimensions = fcBiasesDimensions,
            .scale = 0.0f,
            .zeroPoint = 0
    };
    status = ANeuralNetworksModel_addOperand(model, &fcBiasesType);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding fully connected biases operand: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Fully connected biases operand added to the model");

    const uint32_t fcOutputDimensions[] = {1, FC_SIZE};
    ANeuralNetworksOperandType fcOutputType = {
            .type = ANEURALNETWORKS_TENSOR_FLOAT32,
            .dimensionCount = 2,
            .dimensions = fcOutputDimensions,
            .scale = 0.0f,
            .zeroPoint = 0
    };
    status = ANeuralNetworksModel_addOperand(model, &fcOutputType);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding fully connected output operand: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Fully connected output operand added to the model");

    // Add the softmax layer operand
    const uint32_t softmaxOutputDimensions[] = {1, NUM_CLASSES};
    ANeuralNetworksOperandType softmaxOutputType = {
            .type = ANEURALNETWORKS_TENSOR_FLOAT32,
            .dimensionCount = 2,
            .dimensions = softmaxOutputDimensions,
            .scale = 0.0f,
            .zeroPoint = 0
    };
    status = ANeuralNetworksModel_addOperand(model, &softmaxOutputType);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding softmax layer operand: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Softmax layer operand added to the model");

    // Identify the input and output tensors
    uint32_t inputIndex, softmaxOutputIndex;
    status = ANeuralNetworksModel_identifyInputsAndOutputs(
            model, 1, &inputIndex, 1, &softmaxOutputIndex);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error identifying input and output tensors: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Input and output tensors identified");

    // Finish building the model
    status = ANeuralNetworksModel_finish(model);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error finishing the model: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Model finished");

    // Create the NNAPI compilation instance
    ANeuralNetworksCompilation* compilation;
    status = ANeuralNetworksCompilation_create(model, &compilation);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error creating NNAPI compilation instance: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGE("Error creating NNAPI compilation instance: %d", status);

    // Finish compiling the model
    status = ANeuralNetworksCompilation_finish(compilation);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error finishing the NNAPI compilation: %d", status);
        ANeuralNetworksCompilation_free(compilation);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("NNAPI compilation finished");

    // Create the NNAPI execution instance
    ANeuralNetworksExecution* execution;
    status = ANeuralNetworksExecution_create(compilation, &execution);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error creating NNAPI execution instance: %d", status);
        ANeuralNetworksCompilation_free(compilation);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("NNAPI execution instance created");

    // Set the input tensor data
    jint* inputTensorData = env->GetIntArrayElements(static_cast<jintArray>(imageData), nullptr);
    status = ANeuralNetworksExecution_setInput(execution, 0, nullptr, inputTensorData, sizeof(float) * IMAGE_SIZE * IMAGE_SIZE * NUM_CHANNELS);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error setting input tensor data: %d", status);
        env->ReleaseIntArrayElements(static_cast<jintArray>(imageData), inputTensorData, 0);
        ANeuralNetworksExecution_free(execution);
        ANeuralNetworksCompilation_free(compilation);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Input tensor data set");
    env->ReleaseIntArrayElements(static_cast<jintArray>(imageData), inputTensorData, 0);

    // Compute the output
    status = ANeuralNetworksExecution_compute(execution);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error computing the output: %d", status);
        ANeuralNetworksExecution_free(execution);
        ANeuralNetworksCompilation_free(compilation);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Output computed");

    // Retrieve the output tensor data
    uint32_t outputDimensions[4];
    uint32_t outputRank;
    status = ANeuralNetworksExecution_getOutputOperandDimensions(execution, 0, outputDimensions);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error getting output tensor dimensions: %d", status);
        ANeuralNetworksExecution_free(execution);
        ANeuralNetworksCompilation_free(compilation);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Output tensor dimensions retrieved");

    status = ANeuralNetworksExecution_getOutputOperandRank(execution, 0, &outputRank);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error getting output tensor rank: %d", status);
        ANeuralNetworksExecution_free(execution);
        ANeuralNetworksCompilation_free(compilation);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Output tensor rank retrieved");

    // Allocate memory for the output tensor
    size_t outputSize = sizeof(float);
    for (uint32_t i = 0; i < outputRank; i++) {
        outputSize *= outputDimensions[i];
    }
    float* softmaxOutputData = new float[outputSize / sizeof(float)];

    // Set the output tensor data
    status = ANeuralNetworksExecution_setOutput(execution, 0, nullptr, softmaxOutputData, outputSize);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error setting output tensor data: %d", status);
        delete[] softmaxOutputData;
        ANeuralNetworksExecution_free(execution);
        ANeuralNetworksCompilation_free(compilation);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Output tensor data set");

    // Compute the output
    status = ANeuralNetworksExecution_compute(execution);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error computing the output: %d", status);
        delete[] softmaxOutputData;
        ANeuralNetworksExecution_free(execution);
        ANeuralNetworksCompilation_free(compilation);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Output computed");

    // Process the classification result
    int predictedClass = argmax(softmaxOutputData, NUM_CLASSES);
    LOGD("Predicted class: %d", predictedClass);

    // Display the classification result
    jclass jcls = env->FindClass("com/example/nativenn/MainActivity");
    if (jcls == nullptr) {
        LOGE("Error finding MainActivity class");
        delete[] softmaxOutputData;
        ANeuralNetworksExecution_free(execution);
        ANeuralNetworksCompilation_free(compilation);
        ANeuralNetworksModel_free(model);
        return;
    }

    jmethodID mid = env->GetMethodID(jcls, "displayClassificationResult", "(I)V");
    if (mid == nullptr) {
        LOGE("Error finding displayClassificationResult method");
        env->DeleteLocalRef(jcls);
        delete[] softmaxOutputData;
        ANeuralNetworksExecution_free(execution);
        ANeuralNetworksCompilation_free(compilation);
        ANeuralNetworksModel_free(model);
        return;
    }

    // Get the MainActivity instance
    jobject mainActivityInstance = env->NewLocalRef(env->GetStaticObjectField(jcls, env->GetStaticFieldID(jcls, "instance", "Lcom/example/nativenn/MainActivity;")));

    // Call the displayClassificationResult method
    env->CallVoidMethod(mainActivityInstance, mid, predictedClass);
    LOGD("Calling displayClassificationResult method");

    // Clean up
    env->DeleteLocalRef(mainActivityInstance);
    env->DeleteLocalRef(jcls);
    delete[] softmaxOutputData;
    ANeuralNetworksExecution_free(execution);
    ANeuralNetworksCompilation_free(compilation);
    ANeuralNetworksModel_free(model);
}

int argmax(float *arr, const int size) {
    LOGD("argmax called");
    int maxIndex = 0;
    float maxValue = arr[0];
    for (int i = 1; i < size; i++) {
        if (arr[i] > maxValue) {
            maxIndex = i;
            maxValue = arr[i];
        }
    }
    LOGD("Predicted class index: %d", maxIndex);
    return maxIndex;
}