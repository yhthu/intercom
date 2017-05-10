#include <jni.h>

#include <string.h>
#include <unistd.h>

#include <speex/speex.h>
#include <speex/speex_preprocess.h>

#include<Android/log.h>

static int codec_open = 0;

static int dec_frame_size;
static int enc_frame_size;

static SpeexBits ebits, dbits;
void *enc_state;
void *dec_state;

static SpeexPreprocessState *preprocess_state;

static JavaVM *gJavaVM;

#define TAG "Intercom JNI" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG ,__VA_ARGS__) // 定义LOGD类型

extern "C"
JNIEXPORT jint JNICALL Java_com_jd_wly_intercom_audio_Speex_open(JNIEnv *env, jobject obj, jint compression) {
    int tmp;

    if (codec_open++ != 0)
        return (jint)0;
    // 初始化SpeexBits数据结构
    speex_bits_init(&ebits);
    speex_bits_init(&dbits);
    // 设置编码为窄带编码
    enc_state = speex_encoder_init(&speex_nb_mode);
    dec_state = speex_decoder_init(&speex_nb_mode);
    // 压缩比例
    tmp = compression;
    speex_encoder_ctl(enc_state, SPEEX_SET_QUALITY, &tmp);
    // 设置编码的比特率，即语音质量。由参数tmp控制
    speex_encoder_ctl(enc_state, SPEEX_GET_FRAME_SIZE, &enc_frame_size);
    speex_decoder_ctl(dec_state, SPEEX_GET_FRAME_SIZE, &dec_frame_size);

    // frame_size = enc_frame_size,
    preprocess_state = speex_preprocess_state_init(160, 16000);//创建预处理对象

    int vad = 1;
    int vadProbStart = 80;
    int vadProbContinue = 65;
    // 静音检测
    speex_preprocess_ctl(preprocess_state, SPEEX_PREPROCESS_SET_VAD, &vad);
    speex_preprocess_ctl(preprocess_state, SPEEX_PREPROCESS_SET_PROB_START , &vadProbStart);
    speex_preprocess_ctl(preprocess_state, SPEEX_PREPROCESS_SET_PROB_CONTINUE, &vadProbContinue);

    return (jint)0;
}

extern "C"
JNIEXPORT jint JNICALL Java_com_jd_wly_intercom_audio_Speex_encode
    (JNIEnv *env, jobject obj, jshortArray lin, jbyteArray encoded, jint size) {

    jshort buffer[enc_frame_size];
    jbyte output_buffer[enc_frame_size];
    int nSamples = size / enc_frame_size;
    int i, tot_bytes, curr_bytes = 0;

    if (!codec_open)
        return 0;

    for (i = 0; i < nSamples; i++) {
        // 从Java中拷贝数据到C中，每次拷贝enc_frame_size = 160个short
        env->GetShortArrayRegion(lin, i*enc_frame_size, enc_frame_size, buffer);
        // 降噪、增益、静音检测等处理
        speex_preprocess_run(preprocess_state, buffer);
        // 编码数据到ebits中
        speex_bits_reset(&ebits);
        speex_encode_int(enc_state, buffer, &ebits);
        // 将编码数据写入output_buffer，每次最多写入enc_frame_size = 160个，实际写入curr_bytes个char
        curr_bytes = speex_bits_write(&ebits, (char *)output_buffer, enc_frame_size);
        // 将C层的char类型数据写入Java层的字节数组中，开始写入index为tot_bytes，本次写入curr_bytes
        env->SetByteArrayRegion(encoded, tot_bytes, curr_bytes, output_buffer);
        // 更新总数
        tot_bytes += curr_bytes;
    }

    return (jint)tot_bytes;
}

extern "C"
JNIEXPORT jint JNICALL Java_com_jd_wly_intercom_audio_Speex_decode
    (JNIEnv *env, jobject obj, jbyteArray encoded, jshortArray lin, jint size) {

    jbyte buffer[dec_frame_size];
    jshort output_buffer[dec_frame_size];
    int length = env->GetArrayLength(encoded);
    int nSamples = length / size;
    int i = 0;

    if (!codec_open)
        return 0;

    for(i = 0; i < nSamples; i++) {
        // 从Java中拷贝数据到C中，size = 28个字节
        env->GetByteArrayRegion(encoded, i * size, size, buffer);
        // 编码数据到dbits中
        speex_bits_read_from(&dbits, (char *)buffer, size);
        // 解码到output_buffer，28个字节到160个short
        speex_decode_int(dec_state, &dbits, output_buffer);
        // 将C层的short类型数据写入Java层的short数组中
        speex_preprocess_run(preprocess_state, output_buffer);
        env->SetShortArrayRegion(lin, i * dec_frame_size, dec_frame_size, output_buffer);
    }

    return (jint)nSamples;
}

extern "C"
JNIEXPORT jint JNICALL Java_com_jd_wly_intercom_audio_Speex_getFrameSize
    (JNIEnv *env, jobject obj) {

    if (!codec_open)
        return 0;
    return (jint)enc_frame_size;

}

extern "C"
JNIEXPORT void JNICALL Java_com_jd_wly_intercom_audio_Speex_close
    (JNIEnv *env, jobject obj) {

    if (--codec_open != 0)
        return;
    speex_preprocess_state_destroy(preprocess_state);
    speex_bits_destroy(&ebits);
    speex_bits_destroy(&dbits);
    speex_decoder_destroy(dec_state);
    speex_encoder_destroy(enc_state);
}