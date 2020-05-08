package com.sourav.apps;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Playback {
    private static AudioFormat getAudioFormat() {
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

    public static void play(ByteArrayOutputStream stream) {
        play(stream.toByteArray());
    }

    public static void play(byte[] audioData) {
        try {
            byte[] tempBuffer = new byte[10000];
            int cnt;

            ByteArrayInputStream inputStream = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = new AudioInputStream(
                    inputStream,
                    getAudioFormat(),
                    audioData.length
            );
            DataLine.Info dataLineInfo =
                    new DataLine.Info(
                            SourceDataLine.class,
                            getAudioFormat());
            SourceDataLine sourceDataLine = (SourceDataLine)
                    AudioSystem.getLine(
                            dataLineInfo);
            sourceDataLine.open(getAudioFormat());
            sourceDataLine.start();
            while ((cnt = audioInputStream.
                    read(tempBuffer, 0,
                            tempBuffer.length)) != -1) {
                if (cnt > 0) {
                    sourceDataLine.write(
                            tempBuffer, 0, cnt);
                }
            }
            sourceDataLine.drain();
            sourceDataLine.close();
        } catch (Exception ex) {
            System.out.println("Oops!");
        }
    }


}
