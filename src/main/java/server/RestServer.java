package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class RestServer implements Runnable {
    private static ServerSocket _listener = null;

    public static void main(String[] args) {
        System.out.println("start server");

        try {
            _listener = new ServerSocket(10001, 5);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new RestServer()));

        try {
            while (true) {
                Socket s = _listener.accept();
                /*
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                System.out.println("srv: sending welcome message");
                writer.write("Welcome to myserver!");
                writer.newLine();
                writer.write("Please enter your commands...");
                writer.newLine();
                writer.flush();
                */

                BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String message;
                boolean head = true;
                Map<String, String> map = new HashMap<String, String>();

                do {
                    message = reader.readLine();
                    System.out.println("got: " + message);
                    if ("".equals(message)) {
                        if (map.containsKey("Content-Length")) {
                            int len = Integer.parseInt(map.get("Content-Length"));
                            char[] inbuf = new char[len]; // buffer where the reader saves the incoming message.
                            int readLength = reader.read(inbuf, 0, len);
                            String content = new String(inbuf);

                            // Parse the json if we got json data
                            if (map.containsKey("Content-Type")) {
                                if ("application/json".equals(map.get("Content-Type"))) {
                                    // we got a json
                                    JSONObject contentJson = new JSONObject(content);
                                    System.out.println("Content: " + contentJson.toString());
                                }
                            }
                        }
                    }
                    else if (head) {
                        String[] headSplit = message.split("\\s+");
                        String method = headSplit[0];
                        String url = headSplit[1];
                        map.put("METHOD", method);
                        map.put("URL", url);
                        head = false;
                    }
                    else {
                        int idx = message.indexOf(": ");
                        String key = message.substring(0, idx);
                        String value = message.substring(idx + 2);
                        map.put(key, value);
                        System.out.println("  putting " + key + " : " + value);
                    }

                } while (!"quit".equals(message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            _listener.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        _listener = null;
        System.out.println("close server");
    }

}
