package com.sourav.apps;

public class AudioData {
    private byte[] rawBytes;

    public AudioData(byte[] data) {
        rawBytes = data;
    }

    public byte[] getRawBytes() {
        return rawBytes.clone();
    }

    public void setRawBytes(byte[] rawBytes) {
        this.rawBytes = rawBytes;
    }
}

