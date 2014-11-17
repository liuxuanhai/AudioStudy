/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_example_testopensl_URIActivity */

#ifndef _Included_com_example_testopensl_URIActivity
#define _Included_com_example_testopensl_URIActivity
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    createEngine
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_createEngine
  (JNIEnv *, jclass);

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    createBufferQueueAudioPlayer
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_createBufferQueueAudioPlayer
  (JNIEnv *, jclass);

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    createAssetAudioPlayer
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_testopensl_URIActivity_createAssetAudioPlayer
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    setPlayingAssetAudioPlayer
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_setPlayingAssetAudioPlayer
  (JNIEnv *, jclass, jboolean);

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    createUriAudioPlayer
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_example_testopensl_URIActivity_createUriAudioPlayer
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    setPlayingUriAudioPlayer
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_setPlayingUriAudioPlayer
  (JNIEnv *, jclass, jboolean);

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    setLoopingUriAudioPlayer
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_setLoopingUriAudioPlayer
  (JNIEnv *, jclass, jboolean);

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    setChannelMuteUriAudioPlayer
 * Signature: (IZ)V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_setChannelMuteUriAudioPlayer
  (JNIEnv *, jclass, jint, jboolean);

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    setChannelSoloUriAudioPlayer
 * Signature: (IZ)V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_setChannelSoloUriAudioPlayer
  (JNIEnv *, jclass, jint channel, jboolean solo);

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    getNumChannelsUriAudioPlayer
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_example_testopensl_URIActivity_getNumChannelsUriAudioPlayer
  (JNIEnv *, jclass);

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    setVolumeUriAudioPlayer
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_setVolumeUriAudioPlayer
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    setMuteUriAudioPlayer
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_setMuteUriAudioPlayer
  (JNIEnv *, jclass, jboolean);

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    enableStereoPositionUriAudioPlayer
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_enableStereoPositionUriAudioPlayer
  (JNIEnv *, jclass, jboolean);

/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    setStereoPositionUriAudioPlayer
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_setStereoPositionUriAudioPlayer
  (JNIEnv *, jclass, jint);


/*
 * Class:     com_example_testopensl_URIActivity
 * Method:    shutdown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_example_testopensl_URIActivity_shutdown
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif