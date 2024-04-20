//
// Created by vens8 on 17-04-2024.
//

#include "../include/cnn_model.h"
#include <android/log.h>
#include <vector>

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

void classify_images(JNIEnv* env, jintArray inputTensor) {
    LOGD("classify_images called");

    uint32_t currentOperandIndex = 0;

    // Load the CNN model using NNAPI
    ANeuralNetworksModel* model;
    int status = ANeuralNetworksModel_create(&model);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error creating ANeuralNetworksModel: %d", status);
        return;
    }
    LOGD("ANeuralNetworksModel created");


    // Add the input operand
    const uint32_t inputDimensions[] = {1, NUM_CHANNELS, IMAGE_SIZE, IMAGE_SIZE};
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
    uint32_t inputIndex = currentOperandIndex++;
    LOGD("input operand index: %d", inputIndex);
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
    uint32_t convWeightsIndex = currentOperandIndex++;
    LOGD("convWeights operand index: %d", convWeightsIndex);
    LOGD("Convolution weights operand added to the model");

    const uint32_t convBiasesDimensions[] = {CONV_FILTERS};
    ANeuralNetworksOperandType convBiasesType = {
            .type = ANEURALNETWORKS_TENSOR_FLOAT32,
            .dimensionCount = 1,
            .dimensions = convBiasesDimensions,
            .scale = 0.0f,
            .zeroPoint = 0
    };
    status = ANeuralNetworksModel_addOperand(model, &convBiasesType);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding convolution biases operand: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    uint32_t convBiasesIndex = currentOperandIndex++;
    LOGD("latest operand index: %d", convBiasesIndex);
    LOGD("Convolution biases operand added to the model");

    // Define padding, stride, and activation integers
    const int32_t padding_left = 0;
    const int32_t padding_right = 0;
    const int32_t padding_top = 0;
    const int32_t padding_bottom = 0;
    const int32_t stride_width = 1;
    const int32_t stride_height = 1;
    const int32_t activation = ANEURALNETWORKS_FUSED_NONE;

    ANeuralNetworksOperandType int32OperandType{
            .type = ANEURALNETWORKS_INT32,
            .dimensionCount = 0, // Scalars have no dimensions
            .dimensions = nullptr,
            .scale = 0.0f, // Not used for int32 type
            .zeroPoint = 0, // Not used for int32 type
    };


    // Add operands for the newly defined integers
    status = ANeuralNetworksModel_addOperand(model, &int32OperandType); // padding_left
    uint32_t paddingLeftIndex = currentOperandIndex++;
    status = ANeuralNetworksModel_addOperand(model, &int32OperandType); // padding_right
    uint32_t paddingRightIndex = currentOperandIndex++;
    status = ANeuralNetworksModel_addOperand(model, &int32OperandType); // padding_top
    uint32_t paddingTopIndex = currentOperandIndex++;
    status = ANeuralNetworksModel_addOperand(model, &int32OperandType); // padding_bottom
    uint32_t paddingBottomIndex = currentOperandIndex++;
    status = ANeuralNetworksModel_addOperand(model, &int32OperandType); // stride_width
    uint32_t strideWidthIndex = currentOperandIndex++;
    status = ANeuralNetworksModel_addOperand(model, &int32OperandType); // stride_height
    uint32_t strideHeightIndex = currentOperandIndex++;
    status = ANeuralNetworksModel_addOperand(model, &int32OperandType); // activation
    uint32_t activationIndex = currentOperandIndex++;

    // Set operand values for the added operands
    status = ANeuralNetworksModel_setOperandValue(model, paddingLeftIndex, &padding_left, sizeof(padding_left));
    status = ANeuralNetworksModel_setOperandValue(model, paddingRightIndex, &padding_right, sizeof(padding_right));
    status = ANeuralNetworksModel_setOperandValue(model, paddingTopIndex, &padding_top, sizeof(padding_top));
    status = ANeuralNetworksModel_setOperandValue(model, paddingBottomIndex, &padding_bottom, sizeof(padding_bottom));
    status = ANeuralNetworksModel_setOperandValue(model, strideWidthIndex, &stride_width, sizeof(stride_width));
    status = ANeuralNetworksModel_setOperandValue(model, strideHeightIndex, &stride_height, sizeof(stride_height));
    status = ANeuralNetworksModel_setOperandValue(model, activationIndex, &activation, sizeof(activation));


    const uint32_t convOutputDimensions[] = {1, IMAGE_SIZE, IMAGE_SIZE, CONV_FILTERS};
//    const uint32_t convOutputDimensions[] = {1, IMAGE_SIZE - CONV_KERNEL_SIZE + 1, IMAGE_SIZE - CONV_KERNEL_SIZE + 1, CONV_FILTERS};
    ANeuralNetworksOperandType convOutputType = {
            .type = ANEURALNETWORKS_TENSOR_FLOAT32,
            .dimensionCount = 4,
            .dimensions = convOutputDimensions,
            .scale = 0.0f,
            .zeroPoint = 0
    };
    status = ANeuralNetworksModel_addOperand(model, &convOutputType);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding convolution output type operand: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    uint32_t convOutputIndex = currentOperandIndex++;
    LOGD("latest operand index: %d", convOutputIndex);
    LOGD("Convolution output operand created");

    // Add the convolution operation
    std::vector<uint32_t> convInputIndexes = {inputIndex, convWeightsIndex, convBiasesIndex,
                                              paddingLeftIndex, paddingRightIndex,
                                              paddingTopIndex, paddingBottomIndex,
                                              strideWidthIndex, strideHeightIndex, activationIndex};
    uint32_t convOutputIndexes[] = {convOutputIndex};
    status = ANeuralNetworksModel_addOperation(model, ANEURALNETWORKS_CONV_2D, convInputIndexes.size(), convInputIndexes.data(), 1, convOutputIndexes);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding convolution operation: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Convolution operation added to the model");
//    currentOperandIndex++;

    // Add the ReLU layer operand
    ANeuralNetworksOperandType reluOutputType = convOutputType;
    status = ANeuralNetworksModel_addOperand(model, &reluOutputType);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding ReLU layer operand: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    uint32_t reluOutputIndex = currentOperandIndex++;
    LOGD("latest operand index: %d", reluOutputIndex);
    LOGD("ReLU layer operand added to the model");

    // Add the ReLU operation
    uint32_t reluInputIndexes[] = {convOutputIndexes[0]};
    uint32_t reluOutputIndexes[] = {reluOutputIndex};
    status = ANeuralNetworksModel_addOperation(model, ANEURALNETWORKS_RELU, 1, reluInputIndexes, 1, reluOutputIndexes);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding ReLU operation: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("ReLU operation added to the model");

    const int32_t poolFilterWidth = 2;
    const int32_t poolFilterHeight = 2;
    ANeuralNetworksModel_addOperand(model, &int32OperandType); // Filter width
    uint32_t poolFilterWidthIndex = currentOperandIndex++;
    ANeuralNetworksModel_setOperandValue(model, poolFilterWidthIndex, &poolFilterWidth, sizeof(poolFilterWidth));

    ANeuralNetworksModel_addOperand(model, &int32OperandType); // Filter height
    uint32_t poolFilterHeightIndex = currentOperandIndex++;
    ANeuralNetworksModel_setOperandValue(model, poolFilterHeightIndex, &poolFilterHeight, sizeof(poolFilterHeight));


    // Add the max pooling layer operand
    const uint32_t poolOutputDimensions[] = {1, IMAGE_SIZE / POOL_SIZE, IMAGE_SIZE / POOL_SIZE, CONV_FILTERS};
//    const uint32_t poolOutputDimensions[] = {1, (IMAGE_SIZE - CONV_KERNEL_SIZE + 1) / POOL_SIZE, (IMAGE_SIZE - CONV_KERNEL_SIZE + 1) / POOL_SIZE, CONV_FILTERS};
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
    uint32_t poolOutputIndex = currentOperandIndex++;
    LOGD("latest operand index: %d", poolOutputIndex);
    LOGD("Max pooling layer operand added to the model");

    // Add the max pooling operation
    std::vector<uint32_t> poolInputIndexes = {reluOutputIndex,  // Assuming this is the input tensor from previous layers
                                   paddingLeftIndex, paddingRightIndex, paddingTopIndex, paddingBottomIndex,  // Reused or newly defined
                                   strideWidthIndex, strideHeightIndex,  // Reused or newly defined
                                   poolFilterWidthIndex, poolFilterHeightIndex,  // Newly defined above
                                   activationIndex};
    uint32_t poolOutputIndexes[] = {poolOutputIndex};
    status = ANeuralNetworksModel_addOperation(model, ANEURALNETWORKS_MAX_POOL_2D, poolInputIndexes.size(), poolInputIndexes.data(), 1, poolOutputIndexes);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding max pooling operation: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Max pooling operation added to the model");

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
    uint32_t fcWeightsIndex = currentOperandIndex++;
    LOGD("latest operand index: %d", fcWeightsIndex);
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
    uint32_t fcBiasesIndex = currentOperandIndex++;
    LOGD("latest operand index: %d", fcBiasesIndex);
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
    uint32_t fcOutputIndex = currentOperandIndex++;
    LOGD("latest operand index: %d", fcOutputIndex);
    LOGD("Fully connected output operand added to the model");

    // Add the fully connected operation
    std::vector<uint32_t> fcInputIndexes = {poolOutputIndex, fcWeightsIndex, fcBiasesIndex, activationIndex};
    uint32_t fcOutputIndexes[] = {fcOutputIndex};
    status = ANeuralNetworksModel_addOperation(model, ANEURALNETWORKS_FULLY_CONNECTED, fcInputIndexes.size(), fcInputIndexes.data(), 1, fcOutputIndexes);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding fully connected operation: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Fully connected operation added to the model");

    ANeuralNetworksOperandType float32ScalarType = {
            .type = ANEURALNETWORKS_FLOAT32,
            .dimensionCount = 0, // Scalars have 0 dimensions
            .dimensions = nullptr,
            .scale = 0.0f, // Not used
            .zeroPoint = 0 // Not used
    };

    // Add the softmax beta operand (typically 1.0 for softmax)
    const float softmaxBeta = 1.0f;
    status = ANeuralNetworksModel_addOperand(model, &float32ScalarType);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding softmax beta operand: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    uint32_t softmaxBetaIndex = currentOperandIndex++;
    status = ANeuralNetworksModel_setOperandValue(model, softmaxBetaIndex, &softmaxBeta, sizeof(softmaxBeta));
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error setting softmax beta operand value: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }

    LOGD("Softmax beta operand added to the model");

    // Add the softmax layer operand
    const uint32_t softmaxOutputDimensions[] = {1, NUM_CLASSES};
    ANeuralNetworksOperandType softmaxOutputType = {
            .type = ANEURALNETWORKS_TENSOR_FLOAT32,
            .dimensionCount = 2,
            .dimensions = softmaxOutputDimensions,
            .scale = 0.0f,
            .zeroPoint = 0
    };
    uint32_t softmaxOutputIndex;

    status = ANeuralNetworksModel_addOperand(model, &softmaxOutputType);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding softmax layer operand: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    softmaxOutputIndex = currentOperandIndex++;
    LOGD("latest operand index: %d", softmaxOutputIndex);
    LOGD("Softmax layer operand added to the model");

    // Add the softmax operation
    std::vector<uint32_t> softmaxInputIndexes = {fcOutputIndex, softmaxBetaIndex};
    uint32_t softmaxOutputIndexes[] = {softmaxOutputIndex};
    status = ANeuralNetworksModel_addOperation(model, ANEURALNETWORKS_SOFTMAX, softmaxInputIndexes.size(), softmaxInputIndexes.data(), 1, softmaxOutputIndexes);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error adding softmax operation: %d", status);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Softmax operation added to the model");

    // Validate operand indices before identifyInputsAndOutputs call
    if (inputIndex >= currentOperandIndex || softmaxOutputIndex >= currentOperandIndex) {
        LOGE("Invalid operand indices");
        ANeuralNetworksModel_free(model);
        return;
    }

    // Identify the input and output tensors
    const uint32_t inputIndices[] = {inputIndex};
    const uint32_t outputIndices[] = {softmaxOutputIndex};
    status = ANeuralNetworksModel_identifyInputsAndOutputs(
            model, 1, inputIndices, 1, outputIndices);
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
    jint* inputTensorData = env->GetIntArrayElements(inputTensor, nullptr);
    // Convert the input tensor from int to float
    float inputTensorFloat[IMAGE_SIZE * IMAGE_SIZE * NUM_CHANNELS];
    for (int i = 0; i < IMAGE_SIZE * IMAGE_SIZE * NUM_CHANNELS; i++) {
        inputTensorFloat[i] = static_cast<float>(inputTensorData[i]);
    }

    status = ANeuralNetworksExecution_setInput(execution, inputIndex, nullptr, inputTensorData, sizeof(float) * IMAGE_SIZE * IMAGE_SIZE * NUM_CHANNELS);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error setting input tensor data: %d", status);
        env->ReleaseIntArrayElements(inputTensor, inputTensorData, 0);
        ANeuralNetworksExecution_free(execution);
        ANeuralNetworksCompilation_free(compilation);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Input tensor data set");
    env->ReleaseIntArrayElements(inputTensor, inputTensorData, 0);

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
    status = ANeuralNetworksExecution_getOutputOperandDimensions(execution, softmaxOutputIndex, outputDimensions);
    if (status != ANEURALNETWORKS_NO_ERROR) {
        LOGE("Error getting output tensor dimensions: %d", status);
        ANeuralNetworksExecution_free(execution);
        ANeuralNetworksCompilation_free(compilation);
        ANeuralNetworksModel_free(model);
        return;
    }
    LOGD("Output tensor dimensions retrieved");

    status = ANeuralNetworksExecution_getOutputOperandRank(execution, softmaxOutputIndex, &outputRank);
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
    status = ANeuralNetworksExecution_setOutput(execution, softmaxOutputIndex, nullptr, softmaxOutputData, outputSize);
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