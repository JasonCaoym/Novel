#include <jni.h>
#include <string>
#include <iostream>
#include <iomanip>
#include <sstream>
#include<android/log.h>

#define TAG "Donny-jni"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__)

jstring newString(JNIEnv *env, jbyteArray data) {
    jclass clsString = env->FindClass("java/lang/String");
    jmethodID medNew = env->GetMethodID(clsString, "<init>", "([B)V");
    jstring result = (jstring) env->NewObject(clsString, medNew, data);
    return result;
}

jboolean equals(JNIEnv *env, jstring strL, jstring strR) {
    jclass clsString = env->FindClass("java/lang/String");
    jmethodID medEquals = env->GetMethodID(clsString, "equals", "(Ljava/lang/Object;)Z");
    jboolean result = env->CallBooleanMethod(strL, medEquals, strR);
    return result;
}

jbyteArray toByteArray(JNIEnv *env, jstring data) {
    jclass clsString = env->FindClass("java/lang/String");
    jmethodID medGetBytes = env->GetMethodID(clsString, "getBytes", "()[B");
    jbyteArray result = (jbyteArray) env->CallObjectMethod(data, medGetBytes);
    return result;
}

jbyteArray encodeBase64(JNIEnv *env, jbyteArray data) {
    //base64编码
    jclass clsBase64 = env->FindClass("android/util/Base64");
    jmethodID medDecode = env->GetStaticMethodID(clsBase64, "encode", "([BI)[B");
    jbyteArray result = (jbyteArray) env->CallStaticObjectMethod(clsBase64, medDecode, data, 2);
    return result;
}

jbyteArray toMD5(JNIEnv *env, jbyteArray data) {
    //字符串MD5计算
    jclass clsMessageDigest = env->FindClass("java/security/MessageDigest");
    jmethodID medGetInstance = env->GetStaticMethodID(clsMessageDigest, "getInstance",
                                                      "(Ljava/lang/String;)Ljava/security/MessageDigest;");
    jobject objMessageDigest = env->CallStaticObjectMethod(clsMessageDigest, medGetInstance,
                                                           env->NewStringUTF("MD5"));
    jmethodID medUpdate = env->GetMethodID(clsMessageDigest, "update", "([B)V");
    env->CallVoidMethod(objMessageDigest, medUpdate, data);
    jmethodID medDigest = env->GetMethodID(clsMessageDigest, "digest", "()[B");
    jbyteArray result = (jbyteArray) env->CallObjectMethod(objMessageDigest, medDigest);
    return result;
}

jint getSdkInt(JNIEnv *env) {
    //获取API版本
    jclass clsVersion = env->FindClass("android/os/Build$VERSION");
    jfieldID fidSdkInt = env->GetStaticFieldID(clsVersion, "SDK_INT", "I");
    jint objSdkInt = env->GetStaticIntField(clsVersion, fidSdkInt);
    return objSdkInt;
}

jbyteArray getSignature(JNIEnv *env) {
    //获取Application
    jclass clsActivityThread = env->FindClass("android/app/ActivityThread");
    jmethodID medCurrentApplication = env->GetStaticMethodID(clsActivityThread,
                                                             "currentApplication",
                                                             "()Landroid/app/Application;");
    jobject objApplication = env->CallStaticObjectMethod(clsActivityThread, medCurrentApplication);
    //获取包名
    jclass clsContextWrapper = env->FindClass("android/content/ContextWrapper");
    jmethodID medGetPackageManager = env->GetMethodID(clsContextWrapper, "getPackageManager",
                                                      "()Landroid/content/pm/PackageManager;");
    jobject objPackageManager = env->CallObjectMethod(objApplication, medGetPackageManager);
    jmethodID medGetPackageName = env->GetMethodID(clsContextWrapper, "getPackageName",
                                                   "()Ljava/lang/String;");
    jstring objPackageName = (jstring) env->CallObjectMethod(objApplication, medGetPackageName);
    //获取签名
    jclass clsPackageManager = env->FindClass("android/content/pm/PackageManager");
    jmethodID medGetPackageInfo = env->GetMethodID(clsPackageManager, "getPackageInfo",
                                                   "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    jint objSdkInt = getSdkInt(env);
    jint flag;
    if (objSdkInt < 28) {
        flag = 0x00000040;
    } else {
        flag = 0x08000000;
    }
    jobject objPackageInfo = env->CallObjectMethod(objPackageManager, medGetPackageInfo,
                                                   objPackageName, flag);
    jclass clsPackageInfo = env->FindClass("android/content/pm/PackageInfo");
    jobjectArray arraySignatures;
    if (objSdkInt < 28) {
        jfieldID fidSignature = env->GetFieldID(clsPackageInfo, "signatures",
                                                "[Landroid/content/pm/Signature;");
        arraySignatures = (jobjectArray) env->GetObjectField(objPackageInfo, fidSignature);
    } else {
        jfieldID fidSigningInfo = env->GetFieldID(clsPackageInfo, "signingInfo", "Landroid/content/pm/SigningInfo;");
        jobject objSigningInfo = env->GetObjectField(objPackageInfo, fidSigningInfo);
        jclass clsSigningInfo = env->FindClass("android/content/pm/SigningInfo");
        jmethodID medGetApkContentsSigners = env->GetMethodID(clsSigningInfo, "getApkContentsSigners",
                                                              "()[Landroid/content/pm/Signature;");
        arraySignatures = (jobjectArray) env->CallObjectMethod(objSigningInfo, medGetApkContentsSigners);
    }
    jobject objSignatures = env->GetObjectArrayElement(arraySignatures, 0);
    jclass clsSignature = env->FindClass("android/content/pm/Signature");
    jmethodID medToByteArray = env->GetMethodID(clsSignature, "toByteArray", "()[B");
    jbyteArray arraySign = (jbyteArray) env->CallObjectMethod(objSignatures, medToByteArray);
    return arraySign;
}

static bool hasSign = false;
const char *SIGN_BASE64 = "aq3VCWkiNJCE0YgshO1FAg==";

bool compareSign(JNIEnv *env, jbyteArray data) {
    jstring sign = newString(env, data);
    jstring signc = env->NewStringUTF(SIGN_BASE64);
    return equals(env, sign, signc);
}

bool checkSign(JNIEnv *env) {
    if (!hasSign) {
        LOGD("check sign");
        jbyteArray sign = getSignature(env);
        jbyteArray md5 = toMD5(env, sign);
        jbyteArray base64 = encodeBase64(env, md5);
        hasSign = compareSign(env, base64);
    }
    LOGD("check result: %d", hasSign);
    return hasSign;
}

jstring createKey(JNIEnv *env, jsize size) {
    jclass clsInteger = env->FindClass("java/lang/Integer");
    jmethodID medToHexString = env->GetStaticMethodID(clsInteger, "toHexString",
                                                      "(I)Ljava/lang/String;");
    jstring result = (jstring) env->CallStaticObjectMethod(clsInteger, medToHexString, size);
    return result;
}

jbyteArray toXOR(JNIEnv *env, jbyteArray dataArray) {
    //字符串异或混淆
    jsize dataLen = env->GetArrayLength(dataArray);
    jbyte *dataBytes = env->GetByteArrayElements(dataArray, JNI_FALSE);

    jstring key = createKey(env, dataLen);
    jbyteArray keyArray = toByteArray(env, key);
    jsize keyLen = env->GetArrayLength(keyArray);
    jbyte *keyBytes = env->GetByteArrayElements(keyArray, JNI_FALSE);

    jbyte keyByte;
    jint index;
    for (jint i = 0; i < dataLen; i++) {
        index = i % keyLen;
        keyByte = keyBytes[index];
        *dataBytes = *dataBytes ^ keyByte;
        dataBytes++;
    }
    dataBytes = dataBytes - dataLen;
    jbyteArray result = env->NewByteArray(dataLen);
    env->SetByteArrayRegion(result, 0, dataLen, dataBytes);
    env->ReleaseByteArrayElements(keyArray, keyBytes, JNI_ABORT);
    env->ReleaseByteArrayElements(dataArray, dataBytes, JNI_ABORT);
    return result;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_duoyue_lib_base_crypto_NES_encode(JNIEnv *env, jclass type, jbyteArray data) {
    if (checkSign(env)) {
        return toXOR(env, data);
    }
    return NULL;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_duoyue_lib_base_crypto_NES_decode(JNIEnv *env, jclass type, jbyteArray data) {
    if (checkSign(env)) {
        return toXOR(env, data);
    }
    return NULL;
}