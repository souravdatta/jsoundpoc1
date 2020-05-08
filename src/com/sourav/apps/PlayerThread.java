package com.sourav.apps;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.Socket;

public class PlayerThread extends Thread {
    private Socket clientSocket;
    private volatile boolean running = true;
    private DataInputStream inputStream;

    public PlayerThread(Socket socket) throws Exception {
        this.clientSocket = socket;
        this.inputStream = new DataInputStream(new BufferedInputStream(this.clientSocket.getInputStream()));
        this.start();
    }

    public void run() {
        while (running) {
            try {
                byte[] audioData = this.inputStream.readNBytes(20000);
                Playback.play(audioData);
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        }
    }

    public void stopThread() {
        running = false;
    }

    public void close() throws Exception {
        this.clientSocket.close();
    }
}
