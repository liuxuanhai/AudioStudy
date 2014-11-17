LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := TestAudio
LOCAL_SRC_FILES := com_example_testopensl_URIActivity.cpp \
					com_example_testopensl_AudioTrackActivity.cpp \
					com_example_testopensl_AudioRecordActivity.cpp \
					com_example_openmax_OpenMaxActivity.cpp \
					com_example_openmax_OpenMaxURIActivity.cpp \

LOCAL_LDLIBS += -llog -lOpenSLES -landroid -lOpenMAXAL

include $(BUILD_SHARED_LIBRARY)