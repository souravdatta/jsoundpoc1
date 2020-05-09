package com.sourav.apps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        try {
            final Deque<AudioData> dataQueue = new ArrayDeque<>();
            RecorderThread recorder = new RecorderThread(dataQueue);
            BufferedReader breader = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter server port: ");
            String input = breader.readLine();
            int port = Integer.parseInt(input);

            System.out.print("Enter remote ip address: ");
            String remoteIp = breader.readLine();

            System.out.print("Enter remote port: ");
            input = breader.readLine();
            int remotePort = Integer.parseInt(input);

            SoundServer server = new SoundServer(port);
            final SoundClient client = new SoundClient(remoteIp, remotePort);
            boolean running = true;

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (recorder.isMuted())
                            return;

                        recorder.setMuted(true);
                        AudioData audioData = null;

                        synchronized (dataQueue) {
                            if (!dataQueue.isEmpty()) {
                                audioData = dataQueue.pop();
                                //System.out.println("|C> " + dataQueue.size());
                            }
                        }

                        if (audioData != null) {
                            client.send(audioData.getRawBytes());
                        }
                        recorder.setMuted(false);
                    } catch (Exception ex) {
                        System.exit(1);
                    }
                }
            };

            Timer timer = new Timer();

            while (running) {
                try {
                    System.out.print(String.format("%s> ", recorder.isMuted() ? "MUTED" : "UNMUTED"));
                    System.out.flush();

                    input = breader.readLine();

                    switch (input) {
                        case "mute":
                            recorder.setMuted(true);
                            System.out.println(String.format("STARTED: %s, MUTED: %s", !recorder.isStopped(), recorder.isMuted()));
                            break;
                        case "unmute":
                            recorder.setMuted(false);
                            System.out.println(String.format("STARTED: %s, MUTED: %s", !recorder.isStopped(), recorder.isMuted()));
                            break;
                        case "quit":
                            running = false;
                            break;
                        case "send":
                            client.connect();
                            timer.scheduleAtFixedRate(task, 0, 8);
                            break;
                        default:
                            System.out.println("WHOA!!");
                            break;
                    }
                } catch (IOException iox) {
                    System.exit(1);
                    recorder.stopThread();
                }
            }
            //Playback.play(stream);
            recorder.stopThread();
            server.stopThread();
            client.close();
            timer.cancel();
        } catch (Exception ex) {
            System.out.println("Ok closing app now");
            System.exit(1);
        }
    }
}
