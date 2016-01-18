LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := wavelet

LOCAL_SRC_FILES := \
wavelet/wavelet_utils_Wavelet.cpp\
wavelet/Wavelet.cpp \
wavelet/Smooth.cpp \

include $(BUILD_SHARED_LIBRARY)
