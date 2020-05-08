package com.sourav.apps;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            RecorderThread recorder = new RecorderThread(stream);
            BufferedReader breader = new BufferedReader(new InputStreamReader(System.in));
            SoundServer server = new SoundServer(8000);
            SoundClient client = new SoundClient("127.0.0.1", 8000);
            client.connect();
            boolean running = true;
            System.out.println(String.format("STARTED: %s, MUTED: %s", !recorder.isStopped(), recorder.isMuted()));
            while (running) {
                try {
                    System.out.print(String.format("%s> ", recorder.isMuted() ? "MUTED" : "UNMUTED"));
                    System.out.flush();

                    String input = breader.readLine();

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
                        case "play":
                            recorder.setMuted(true);
                            client.send(stream.toByteArray().clone());
                            //Playback.play(stream.toByteArray().clone());
                            stream = null;
                            stream = new ByteArrayOutputStream();
                            recorder.setStream(stream);
                            recorder.setMuted(false);
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
        } catch (Exception ex) {
            System.out.println("Ok closing app now");
        }
    }
}
