package com.sourav.apps;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RecorderThread extends Thread {
    private volatile boolean muted = true;
    private volatile boolean stopped = false;
    private ByteArrayOutputStream byteArrayOutputStream;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    byte[] tempBuffer;

    private AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;
        //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;
        //8,16
        int channels = 1;
        //1,2
        boolean signed = true;
        //true,false
        boolean bigEndian = false;
        //true,false
        return new AudioFormat(
                sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian);
    }

    public RecorderThread(ByteArrayOutputStream stream) {
        this.setStream(stream);
        try {
            tempBuffer = new byte[10000];
            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo =
                    new DataLine.Info(
                            TargetDataLine.class,
                            audioFormat);
            targetDataLine = (TargetDataLine)
                    AudioSystem.getLine(
                            dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            this.start();
        } catch (Exception ex) {
            System.out.println("Oops!");
        }
    }

    public void run() {
        if (isStopped()) return;

        while (!isStopped()) {
            if (!isMuted()) {
                try {
                    int cnt = targetDataLine.read(
                            tempBuffer,
                            0,
                            tempBuffer.length);
                    if (cnt > 0) {
                        System.out.flush();
                        byteArrayOutputStream.write(
                                tempBuffer, 0, cnt);
                    }
                    byteArrayOutputStream.close();
                } catch (IOException ioex) {
                    ioex.printStackTrace();
                }
            } else {
                // do not record sound here
                // just go to sleep for 10ms and
                // check again
                try {
                    Thread.sleep(10);
                } catch (InterruptedException iex) {
                    iex.printStackTrace();
                }
            }
        }
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void stopThread() {
        this.stopped = true;
    }

    public void setStream(ByteArrayOutputStream stream) {
        this.byteArrayOutputStream = null;
        this.byteArrayOutputStream = stream;
    }
}
