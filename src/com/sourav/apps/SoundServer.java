package com.sourav.apps;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class SoundServer extends Thread {
    private List<PlayerThread> players;
    private int port;
    private volatile boolean running = true;
    private ServerSocket serverSocket;

    public SoundServer(int port) throws Exception {
        this.port = port;
        this.players = new ArrayList<>();
        this.serverSocket = new ServerSocket(port);
        System.out.println(String.format("[INFO] Server started at %s:%d", this.getHostIpAddress(),
                this.serverSocket.getLocalPort()));
        this.start();
    }

    private String getHostIpAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces
                        .nextElement();
                Enumeration<InetAddress> nias = ni.getInetAddresses();
                while(nias.hasMoreElements()) {
                    InetAddress ia = nias.nextElement();
                    if (!ia.isLinkLocalAddress()
                            && !ia.isLoopbackAddress()
                            && ia instanceof Inet4Address) {
                        return ia.getHostAddress();
                    }
                }
            }
        } catch (Exception ex1) {
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (Exception ex2) {
                System.out.println("Something failed while getting ip address");
                return "127.0.0.1";
            }
        }

        return "127.0.0.1";
    }

    public void run() {
        while (running) {
            try {
                Socket client = this.serverSocket.accept();
                PlayerThread player = new PlayerThread(client);
                this.players.add(player);
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        }
    }

    public void stopThread() throws Exception {
        this.running = false;

        for (PlayerThread player : this.players) {
            player.stopThread();
            player.close();
        }

        this.serverSocket.close();
    }
}
