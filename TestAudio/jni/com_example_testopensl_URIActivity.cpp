/** log */
#define LOG_TAG "URIActivity"
#define DGB 1

#include <jni.h>
#include <assert.h>
#include <stdio.h>
#include <log.h>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include "com_example_testopensl_URIActivity.h"

// engine interfaces
static SLObjectItf engineObject = NULL;
static SLEngineItf engineEngine;

// output mix interfaces
static SLObjectItf outputMixObject = NULL;

// URI player interfaces
static SLObjectItf uriPlayerObject = NULL;
static SLPlayItf uriPlayerPlay;
static SLSeekItf uriPlayerSeek;
static SLMuteSoloItf uriPlayerMuteSolo;
static SLVolumeItf uriPlayerVolume;

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    createEngine
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_createEngine(JNIEnv *env, jclass clazz) {
	SLresult result;

	// create engine
	result = slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
	assert(SL_RESULT_SUCCESS == result);
	(void) result;

	// realize the engine
	result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
	assert(SL_RESULT_SUCCESS == result);
	(void) result;

	// get the engine interface, which is needed in order to create other objects
	result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);
	assert(SL_RESULT_SUCCESS == result);
	(void) result;

	// create output mix
	result = (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 0, 0, 0);
	assert(SL_RESULT_SUCCESS == result);
	(void) result;

	// realize the output mix
	result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
	assert(SL_RESULT_SUCCESS == result);
	(void) result;

}

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    createUriAudioPlayer
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_testopensl_URIActivity_createUriAudioPlayer(JNIEnv *env, jclass jobject, jstring uri) {

	SLresult result;
	const char* utf8Uri = env->GetStringUTFChars(uri, NULL);

	if (utf8Uri == NULL) {
		return false;
	}

	ALOGD("uri : %s", utf8Uri);

	SLDataLocator_URI loc_uri = { SL_DATALOCATOR_URI, (SLchar *) utf8Uri };
	SLDataFormat_MIME format_mime = { SL_DATAFORMAT_MIME, NULL,
	SL_CONTAINERTYPE_UNSPECIFIED };
	SLDataSource audioSrc = { &loc_uri, &format_mime };

	// configure audio sink
	SLDataLocator_OutputMix loc_outmix = { SL_DATALOCATOR_OUTPUTMIX, outputMixObject };
	SLDataSink audioSnk = { &loc_outmix, NULL };

	// create audio player
	const SLInterfaceID ids[3] = { SL_IID_SEEK, SL_IID_MUTESOLO, SL_IID_VOLUME };
	const SLboolean req[3] = { SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE };
	result = (*engineEngine)->CreateAudioPlayer(engineEngine, &uriPlayerObject, &audioSrc, &audioSnk, 3, ids, req);
	// note that an invalid URI is not detected here, but during prepare/prefetch on Android,
	// or possibly during Realize on other platforms
	assert(SL_RESULT_SUCCESS == result);
	(void) result;

	// realize the player
	result = (*uriPlayerObject)->Realize(uriPlayerObject, SL_BOOLEAN_FALSE);
	// this will always succeed on Android, but we check result for portability to other platforms
	if (SL_RESULT_SUCCESS != result) {
		(*uriPlayerObject)->Destroy(uriPlayerObject);
		uriPlayerObject = NULL;
		return JNI_FALSE;
	}

	// get the play interface
	result = (*uriPlayerObject)->GetInterface(uriPlayerObject, SL_IID_PLAY, &uriPlayerPlay);
	assert(SL_RESULT_SUCCESS == result);
	(void) result;

	// get the seek interface
	result = (*uriPlayerObject)->GetInterface(uriPlayerObject, SL_IID_SEEK, &uriPlayerSeek);

	assert(SL_RESULT_SUCCESS == result);
	(void) result;

	// get the mute/solo interface
	result = (*uriPlayerObject)->GetInterface(uriPlayerObject, SL_IID_MUTESOLO, &uriPlayerMuteSolo);
	assert(SL_RESULT_SUCCESS == result);
	(void) result;

	// get the volume interface
	result = (*uriPlayerObject)->GetInterface(uriPlayerObject, SL_IID_VOLUME, &uriPlayerVolume);
	assert(SL_RESULT_SUCCESS == result);
	(void) result;

	env->ReleaseStringUTFChars(uri, utf8Uri);
	return true;
}

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    setPlayingUriAudioPlayer
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_setPlayingUriAudioPlayer(JNIEnv *, jclass, jboolean isPlaying) {
	SLresult result;

	// make sure the URI audio player was created
	if (uriPlayerPlay != NULL) {
		// set the player's state
		result = (*uriPlayerPlay)->SetPlayState(uriPlayerPlay, isPlaying ? SL_PLAYSTATE_PLAYING : SL_PLAYSTATE_PAUSED);
		assert(SL_RESULT_SUCCESS == result);
	}
}

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    setChannelMuteUriAudioPlayer
 * Signature: (IZ)V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_setChannelMuteUriAudioPlayer(JNIEnv *, jclass, jint channel, jboolean mute) {
	SLresult result;
	if (uriPlayerMuteSolo != NULL) {
		result = (*uriPlayerMuteSolo)->SetChannelMute(uriPlayerMuteSolo, channel, mute);
		assert(SL_RESULT_SUCCESS == result);
	}
}

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    setChannelSoloUriAudioPlayer
 * Signature: (IZ)V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_setChannelSoloUriAudioPlayer(JNIEnv *, jclass, jint channel, jboolean solo) {
	SLresult result;
	if (uriPlayerMuteSolo != NULL) {
		result = (*uriPlayerMuteSolo)->SetChannelSolo(uriPlayerMuteSolo, channel, solo);
		assert(SL_RESULT_SUCCESS == result);
	}
}

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    getNumChannelsUriAudioPlayer
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_example_testopensl_URIActivity_getNumChannelsUriAudioPlayer(JNIEnv *, jclass) {
	SLresult result;
	if (uriPlayerMuteSolo != NULL) {
		SLuint8 chanelNumbes;
		result = (*uriPlayerMuteSolo)->GetNumChannels(uriPlayerMuteSolo, &chanelNumbes);
		assert(SL_RESULT_SUCCESS == result);
		if (result == SL_RESULT_SUCCESS) {
			return chanelNumbes;
		}
	}
	return 0;
}

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    setVolumeUriAudioPlayer
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_setVolumeUriAudioPlayer(JNIEnv *, jclass, jint index) {
	SLresult result;

	if (NULL != uriPlayerVolume) {
		SLmillibel value;
		result = (*uriPlayerVolume)->GetMaxVolumeLevel(uriPlayerVolume, &value);
		if (result == SL_RESULT_SUCCESS) {
			ALOGD("max %d set %d", value, index);
		}

		result = (*uriPlayerVolume)->SetVolumeLevel(uriPlayerVolume, index);
		assert(SL_RESULT_SUCCESS == result);
	}
}

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    setMuteUriAudioPlayer
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_setMuteUriAudioPlayer(JNIEnv *, jclass, jboolean mute) {
	SLresult result;
	if (NULL != uriPlayerVolume) {
		result = (*uriPlayerVolume)->SetMute(uriPlayerVolume, mute);
		assert(SL_RESULT_SUCCESS == result);
	}
}

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    enableStereoPositionUriAudioPlayer
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_enableStereoPositionUriAudioPlayer(JNIEnv *, jclass, jboolean) {

}

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    setStereoPositionUriAudioPlayer
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_setStereoPositionUriAudioPlayer(JNIEnv *, jclass, jint) {

}

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    shutdown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_shutdown(JNIEnv *, jclass) {

	// destroy URI audio player object, and invalidate all associated interfaces
	if (uriPlayerObject != NULL) {
		(*uriPlayerObject)->Destroy(uriPlayerObject);
		uriPlayerObject = NULL;
		uriPlayerPlay = NULL;
		uriPlayerSeek = NULL;
		uriPlayerMuteSolo = NULL;
		uriPlayerVolume = NULL;
	}

	// destroy output mix object, and invalidate all associated interfaces
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
}

