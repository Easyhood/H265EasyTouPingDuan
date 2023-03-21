package com.easyhood.h265easytoupingduan;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 功能：h264播放处理类
 * 详细描述：
 * 作者：guan_qi
 * 创建日期：2023-03-21
 */
public class H264Player implements SocketLive.SocketCallback{
    private static final String TAG = "H264Player";
    private MediaCodec mediaCodec;
    private int mWidth = 720;
    private int mHeight = 1280;

    /**
     * 构造方法
     * @param surface Surface
     */
    public H264Player(Surface surface) {
        try {
            int formatWidth = mWidth;
            int formatHeight = mHeight;
            if ((formatWidth & 1) == 1) {
                formatWidth--;
            }
            if ((formatHeight & 1) == 1) {
                formatHeight--;
            }
            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            final MediaFormat format = MediaFormat.
                    createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, formatWidth, formatHeight);
            format.setInteger(MediaFormat.KEY_BIT_RATE, formatWidth*formatHeight);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 20);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            mediaCodec.configure(format,
                    surface,
                    null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mediaCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收到消息处理
     * @param data byte[]
     */
    @Override
    public void callBack(byte[] data) {
        Log.d(TAG, "callBack: 解码器前长度 = " + data.length);
        int index = mediaCodec.dequeueInputBuffer(100000);
        if (index >= 0) {
            ByteBuffer inputBuffer = mediaCodec.getInputBuffer(index);
            inputBuffer.clear();
            inputBuffer.put(data, 0, data.length);
            mediaCodec.queueInputBuffer(index,
                    0, data.length, System.currentTimeMillis(), 0);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 100000);
            // 放一个完整帧渲染出来
            Log.d(TAG, "callBack: 解码器后长度 = " + bufferInfo.size);
            while (outputBufferIndex >= 0) {
                mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            }
        }
    }
}
