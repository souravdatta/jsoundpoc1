package com.sourav.apps;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    public static void main(String[] args) {
        try {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            RecorderThread recorder = new RecorderThread(stream);
            BufferedReader breader = new BufferedReader(new InputStreamReader(System.in));
            SoundServer server = new SoundServer(8000);
            final SoundClient client = new SoundClient("<>", 8001);
            boolean running = true;
            System.out.println(String.format("STARTED: %s, MUTED: %s", !recorder.isStopped(), recorder.isMuted()));

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        recorder.setMuted(true);
                        client.send(stream.toByteArray().clone());
                        //Playback.play(stream.toByteArray().clone());
                        stream.reset();
                        recorder.setStream(stream);
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
                        case "send":
                            client.connect();
                            timer.scheduleAtFixedRate(task, 0, 8);
                            break;
                        case "stop":
                            client.close();
                            timer.cancel();
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
