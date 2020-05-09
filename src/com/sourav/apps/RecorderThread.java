package com.sourav.apps;

import javax.sound.sampled.*;
import java.util.Deque;

public class RecorderThread extends Thread {
    private volatile boolean muted = true;
    private volatile boolean stopped = false;
    private Deque<AudioData> dataQueue;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;

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

    public RecorderThread(Deque<AudioData> dataQueue) {
        this.setDataQueue(dataQueue);
        try {
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
                byte[] tempBuffer = new byte[6000];
                int cnt = targetDataLine.read(
                        tempBuffer,
                        0,
                        tempBuffer.length);
                if (cnt > 0) {
                    System.out.flush();
                    synchronized (this.dataQueue) {
                        //System.out.println("|P> " + this.dataQueue.size());
                        this.dataQueue.push(new AudioData(tempBuffer.clone()));
                    }
                }
            } else {
                // do not record sound here
                // just go to sleep for 10ms and
                // check again
                try {
                    Thread.sleep(10);
                } catch (InterruptedException iex) {
                    //iex.printStackTrace();
                }
            }
        }
    }

    public synchronized boolean isMuted() {
        return muted;
    }

    public synchronized void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void stopThread() {
        this.stopped = true;
    }

    public Deque<AudioData> getDataQueue() {
        return dataQueue;
    }

    public void setDataQueue(Deque<AudioData> dataQueue) {
        this.dataQueue = dataQueue;
    }
}
