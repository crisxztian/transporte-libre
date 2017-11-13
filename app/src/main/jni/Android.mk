LOCAL_PATH := $(call my-dir)

	include $(CLEAR_VARS)

	LOCAL_SRC_FILES := com_example_crisi_deteccionrostro_DetectionBasedTracker

	LOCAL_LDLIBS += -llog
	LOCAL_MODULE := detection_based_tracker


	include $(BUILD_SHARED_LIBRARY)
