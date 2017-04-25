LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_LDLIBS :=-llog

LOCAL_MODULE    := libspeex
LOCAL_CFLAGS = -DFIXED_POINT -DUSE_KISS_FFT -DEXPORT="" -UHAVE_CONFIG_H
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include

LOCAL_SRC_FILES := speex_jni.cpp \
        ./libspeex/bits.c \
        ./libspeex/cb_search.c \
        ./libspeex/buffer.c \
        ./libspeex/exc_10_16_table.c \
        ./libspeex/exc_10_32_table.c \
        ./libspeex/exc_20_32_table.c \
        ./libspeex/exc_5_256_table.c \
        ./libspeex/exc_5_64_table.c \
        ./libspeex/exc_8_128_table.c \
        ./libspeex/fftwrap.c \
        ./libspeex/filterbank.c \
        ./libspeex/filters.c \
        ./libspeex/gain_table_lbr.c \
        ./libspeex/gain_table.c \
        ./libspeex/hexc_10_32_table.c \
        ./libspeex/hexc_table.c \
        ./libspeex/high_lsp_tables.c \
        ./libspeex/jitter.c \
        ./libspeex/kiss_fft.c \
        ./libspeex/kiss_fftr.c \
        ./libspeex/lpc.c \
        ./libspeex/lsp_tables_nb.c \
        ./libspeex/lsp.c \
        ./libspeex/ltp.c \
        ./libspeex/mdf.c \
        ./libspeex/modes_wb.c \
        ./libspeex/modes.c \
        ./libspeex/nb_celp.c \
        ./libspeex/preprocess.c \
        ./libspeex/quant_lsp.c \
        ./libspeex/sb_celp.c \
        ./libspeex/scal.c \
        ./libspeex/smallft.c \
        ./libspeex/speex_callbacks.c \
        ./libspeex/speex_header.c \
        ./libspeex/speex.c \
        ./libspeex/stereo.c \
        ./libspeex/vbr.c \
        ./libspeex/vorbis_psy.c \
        ./libspeex/vq.c \
        ./libspeex/window.c \

include $(BUILD_SHARED_LIBRARY)