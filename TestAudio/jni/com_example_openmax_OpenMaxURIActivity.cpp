/** log */
#define LOG_TAG "OpenMaxURIActivity"
#define DGB 1

#include <jni.h>
#include <assert.h>
#include <pthread.h>
#include <stdio.h>
#include <string.h>

// for native media
#include <OMXAL/OpenMAXAL.h>
#include <OMXAL/OpenMAXAL_Android.h>

//  for native window JNI
#include <android/native_window_jni.h>

#include "log.h"
#include "com_example_openmax_OpenMaxURIActivity.h"

// engine interfaces
static XAObjectItf engineObject = NULL;
static XAEngineItf engineEngine = NULL;

// output mix interfaces
static XAObjectItf outputMixObject = NULL;

// streaming media player interfaces
static XAObjectItf playerObj = NULL;
static XAPlayItf playerPlayItf = NULL;
static XAStreamInformationItf playerStreamInfoItf = NULL;
static XAVolumeItf playerVolItf = NULL;
static XASeekItf playerSeekItf = NULL;

// video sink for the player
static ANativeWindow* theNativeWindow;

/*
 * Class:     com_example_openmax_OpenMaxURIActivity
 * Method:    createEngine
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_example_openmax_OpenMaxURIActivity_createEngine(JNIEnv *, jclass) {
	XAresult res;

	// create engine
	res = xaCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
	assert(XA_RESULT_SUCCESS == res);

	// realize the engine
	res = (*engineObject)->Realize(engineObject, XA_BOOLEAN_FALSE);
	assert(XA_RESULT_SUCCESS == res);

	// get the engine interface, which is needed in order to create other objects
	res = (*engineObject)->GetInterface(engineObject, XA_IID_ENGINE, &engineEngine);
	assert(XA_RESULT_SUCCESS == res);

	// create output mix
	res = (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 0, NULL, NULL);
	assert(XA_RESULT_SUCCESS == res);

	// realize the output mix
	res = (*outputMixObject)->Realize(outputMixObject, XA_BOOLEAN_FALSE);
	assert(XA_RESULT_SUCCESS == res);
}

/*
 * Class:     com_example_openmax_OpenMaxURIActivity
 * Method:    createStreamingMediaPlayer
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_openmax_OpenMaxURIActivity_createStreamingMediaPlayer(JNIEnv *env, jclass, jstring filename) {

	XAresult res;

	// convert Java string to UTF-8
	const char *utf8 = env->GetStringUTFChars(filename, NULL);
	assert(NULL != utf8);

	XADataLocator_URI loc_uri = { XA_DATALOCATOR_URI, (XAchar *) utf8 };
	XADataFormat_MIME format_mime = { XA_DATAFORMAT_MIME, NULL, XA_CONTAINERTYPE_UNSPECIFIED };
	XADataSource dataSrc = { &loc_uri, &format_mime };

	// configure audio sink
	XADataLocator_OutputMix loc_outmix = { XA_DATALOCATOR_OUTPUTMIX, outputMixObject };
	XADataSink audioSnk = { &loc_outmix, NULL };

	// configure image video sink
	XADataLocator_NativeDisplay loc_nd = {
	XA_DATALOCATOR_NATIVEDISPLAY,        // locatorType
			// the video sink must be an ANativeWindow created from a Surface or SurfaceTexture
			(void*) theNativeWindow,              // hWindow
			// must be NULL
			NULL                                 // hDisplay
			};
	XADataSink imageVideoSink = { &loc_nd, NULL };

	// declare interfaces to use
	XAboolean required[3] = { XA_BOOLEAN_TRUE, XA_BOOLEAN_TRUE, XA_BOOLEAN_TRUE };
	XAInterfaceID iidArray[3] = { XA_IID_PLAY, XA_IID_STREAMINFORMATION, XA_IID_SEEK };

	(*engineEngine)->CreateMediaPlayer(engineEngine, &playerObj, &dataSrc,
	NULL, &audioSnk, &imageVideoSink, NULL, NULL, 3 /*XAuint32 numInterfaces*/, iidArray /*const XAInterfaceID *pInterfaceIds*/,
			required /*const XAboolean *pInterfaceRequired*/);

	assert(XA_RESULT_SUCCESS == res);

	// release the Java string and UTF-8
	env->ReleaseStringUTFChars(filename, utf8);

	// realize the player
	res = (*playerObj)->Realize(playerObj, XA_BOOLEAN_FALSE);
	assert(XA_RESULT_SUCCESS == res);

	// get the play interface
	res = (*playerObj)->GetInterface(playerObj, XA_IID_PLAY, &playerPlayItf);
	assert(XA_RESULT_SUCCESS == res);


	// get the stream information interface (for video size)
	res = (*playerObj)->GetInterface(playerObj, XA_IID_STREAMINFORMATION, &playerStreamInfoItf);
	assert(XA_RESULT_SUCCESS == res);

	// get the volume interface
	res = (*playerObj)->GetInterface(playerObj, XA_IID_VOLUME, &playerVolItf);
	assert(XA_RESULT_SUCCESS == res);

	// get the seek interface
	res = (*playerObj)->GetInterface(playerObj, XA_IID_SEEK, &playerSeekItf);
	assert(XA_RESULT_SUCCESS == res);

	// prepare the player
	res = (*playerPlayItf)->SetPlayState(playerPlayItf, XA_PLAYSTATE_PAUSED);
	assert(XA_RESULT_SUCCESS == res);

	// set the volume
	res = (*playerVolItf)->SetVolumeLevel(playerVolItf, 0);
	assert(XA_RESULT_SUCCESS == res);

	// start the playback
	res = (*playerPlayItf)->SetPlayState(playerPlayItf, XA_PLAYSTATE_PLAYING);
	assert(XA_RESULT_SUCCESS == res);

	return JNI_TRUE;
}

/*
 * Class:     com_example_openmax_OpenMaxURIActivity
 * Method:    setPlayingStreamingMediaPlayer
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_example_openmax_OpenMaxURIActivity_setPlayingStreamingMediaPlayer(JNIEnv *, jclass, jboolean state) {
	if (playerPlayItf != NULL) {
		XAresult res;
		res = (*playerPlayItf)->SetPlayState(playerPlayItf, state ? XA_PLAYSTATE_PLAYING : XA_PLAYSTATE_PAUSED);
		assert(XA_RESULT_SUCCESS == res);
	}
}

/*
 * Class:     com_example_openmax_OpenMaxURIActivity
 * Method:    setSurface
 * Signature: (Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_com_example_openmax_OpenMaxURIActivity_setSurface(JNIEnv *env, jclass, jobject surface) {
	// obtain a native window from a Java surface
	theNativeWindow = ANativeWindow_fromSurface(env, surface);
}

/*
 * Class:     com_example_openmax_OpenMaxURIActivity
 * Method:    rewindStreamingMediaPlayer
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_example_openmax_OpenMaxURIActivity_rewindStreamingMediaPlayer(JNIEnv *, jclass) {
	if (playerSeekItf != NULL) {
		XAresult res;
		res = (*playerSeekItf)->SetPosition(playerSeekItf, 0, XA_SEEKMODE_FAST);
		assert(XA_RESULT_SUCCESS == res);
	}
}

/*
 * Class:     com_example_openmax_OpenMaxURIActivity
 * Method:    shutdown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_example_openmax_OpenMaxURIActivity_shutdown(JNIEnv *, jclass) {
	// destroy media player object, and invalidate all associated interfaces
	if (playerObj != NULL) {
		(*playerObj)->Destroy(playerObj);
		playerObj = NULL;
		playerPlayItf = NULL;
		playerStreamInfoItf = NULL;
		playerVolItf = NULL;
		playerSeekItf = NULL;
	}

	//destory the outputMix Object ,and invalidate all associated interfaces
	if (outputMixObject != NULL) {
		(*outputMixObject)->Destroy(outputMixObject);
		outputMixObject = NULL;
	}

	// destroy engine object, and invalidate all associated interfaces
	if (engineObject != NULL) {
		(*engineObject)->Destroy(engineObject);
		engineObject = NULL;
		engineEngine = NULL;
	}

	// make sure we don't leak native windows
	if (theNativeWindow != NULL) {
		ANativeWindow_release(theNativeWindow);
		theNativeWindow = NULL;
	}
}

