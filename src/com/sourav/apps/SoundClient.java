package com.sourav.apps;

import java.io.ByteArrayOutputStream;
import java.net.Socket;

public class SoundClient {
    private Socket clientSocket;
    private String hostIp;
    private int hostPort;

    public SoundClient(String hostIp, int hostPort) {
        this.hostIp = hostIp;
        this.hostPort = hostPort;
    }

    public void connect() throws Exception {
        this.clientSocket = new Socket(this.hostIp, this.hostPort);
    }

    public void send(byte[] audioData) throws Exception {
        this.clientSocket.getOutputStream().flush();
        this.clientSocket.getOutputStream().write(audioData);
    }

    public void close() throws Exception {
        this.clientSocket.close();
        this.clientSocket = null;
    }
}
