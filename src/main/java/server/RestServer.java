package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import logic.GameLogic;
import logic.User;
import org.json.HTTP;
import org.json.JSONObject;

public class RestServer implements Runnable {
    private static int port = 10001;
    private static ServerSocket _listener = null;
    private static GameLogic gameLogic = null;


    public static void main(String[] args) {
        gameLogic = new GameLogic();

        System.out.println("start server");

        /** ServerSocket(port, backlog)
         *  This function creates a server socket and binds it to the specified local port number, with the specified backlog.
         *  The backlog argument is the requested maximum number of pending connections on the socket.
         *  https://docs.oracle.com/javase/7/docs/api/java/net/ServerSocket.html
         *
         *  _listener is an instance of a Server Socket, where it "listens" to the Client-socket.
         * */
        try {
            _listener = new ServerSocket(port, 5);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        /** Runtime.getRuntime(): https://www.tutorialspoint.com/java/lang/runtime_getruntime.htm
         * addShutdownHook(): https://docs.oracle.com/javase/7/docs/api/java/lang/Runtime.html#addShutdownHook(java.lang.Thread)
         *      it takes a Thread as an argument, and that Thread is executed when the program exits.
         *      In this case the thread will execute a RestServer, once the program is ended.
         * */
        Runtime.getRuntime().addShutdownHook(new Thread(new RestServer()));

        try {
            while (true) {
                Socket s = _listener.accept(); //accept connexion.

                /** s.getInputStream() - it reads bytes from the socket s.
                 *  The information gotten in the stream is converted in a stream and stored in a buffer called reader.
                 * */
                BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String message;
                boolean head = true;
                // https://stleary.github.io/JSON-java/index.html
                String headerMsg = "";

                /**
                 * */

                do {
                    message = reader.readLine(); //get next line from socket (input).
                    //System.out.println("got: " + message);

                    if(!message.isEmpty()) {
                        headerMsg = headerMsg + message + "\n";
                    }

                    /*if ("".equals(message)) {
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
                    }*/


                } while (!message.isEmpty()); //loop ends when the received message is empty

                //System.out.println("Hedaer message is ");
                //System.out.printf(headerMsg);

                JSONObject jsonHeader = HTTP.toJSONObject(headerMsg);
                JSONObject jsonContent = null;
                String contentMsg = null;
                //System.out.println("Json Header object is ");
                //System.out.println(jsonHeader);

                if (jsonHeader.has("Content-Length")) { // check if there is a content in the message
                    int len = jsonHeader.getInt("Content-Length"); // get length of message-content
                    char[] inputBuffer = new char[len]; // create a buffer to save the content.
                    // https://www.tutorialspoint.com/java/io/inputstream_read.htm
                    // reads from the reader(reader is the buffer where the input stream from socket is stored).
                    int readLength = reader.read(inputBuffer, 0, len); //reads the number
                    contentMsg = new String(inputBuffer); // parse to a string

                    // Parse the json if we got json data
                    if (jsonHeader.has("Content-Type")) {
                        String contentType = jsonHeader.getString("Content-Type");
                        if ("application/json".equals(contentType)) { //check if what we get is a json.
                            jsonContent = new JSONObject(contentMsg);
                            //System.out.println("Content: " + jsonContent.toString());
                        }
                    }
                }

                //send request and get an answer.
                RequestAnswer requestAnswer = handleRequest(jsonHeader, jsonContent);

                //Write in the socket the answer
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                String answer = null;

                //Form of the Response according to:
                // https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages

                //get status:
                answer = jsonHeader.getString("HTTP-Version") + " ";
                answer = answer + Integer.toString(requestAnswer.status) + " " + requestAnswer.getStatusMsg() +"\n";

                //get content:
                if(requestAnswer.jsonAnswer != null) {
                    //System.out.println("Content-answer is ");
                    //System.out.println(requestAnswer.jsonAnswer);

                    // set content length ...
                    answer += "Content-Type: " + "application/json" + "\n";
                    String contentString = requestAnswer.jsonAnswer.toString();
                    answer += "Content-Length: " + contentString.length() + "\n";
                    answer += "\n";

                    // set content
                    answer += contentString;
                }
                else {
                    answer += "\n";
                }

                //write to the socket
                //System.out.println("Answer is ");
                //System.out.println(answer);
                writer.write(answer, 0, answer.length());

                writer.close();
                reader.close();

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

    static RequestAnswer handleRequest(JSONObject jsonHeader, JSONObject jsonContent) {
        //HashMap<String, String> test = new HashMap<String, String>();
        // test.put("Answer", "Success");
        //JSONObject jsonAnswer = new JSONObject(test);

        System.out.println("Header is ");
        System.out.println(jsonHeader);
        System.out.println("Content is ");
        System.out.println(jsonContent);

        RequestAnswer requestAnswer = null;

        try {
            if(jsonHeader.getString("Method").equals("POST")) {
                if(jsonHeader.getString("Request-URI").equals("/users")) { //create a User
                    User user = gameLogic.createUser(jsonContent.getString("Username"), jsonContent.getString("Password"));
                    if(user == null) {
                        throw new Exception("user could not be created");
                    }
                    else {
                        requestAnswer = new RequestAnswer(200, null);
                    }
                }
            }
            else if(jsonHeader.getString("Method").equals("GET")) {

            }
            else if(jsonHeader.getString("Method").equals("PUT")) {

            }
            else if(jsonHeader.getString("Method").equals("DELETE")) {

            }
            else {
                //throw exception.
            }
        }
        catch (Exception e) {
            HashMap<String, String> errorMessage = new HashMap<String, String>();
            errorMessage.put("ErrorMessage", e.getMessage());
            JSONObject jsonAnswer = new JSONObject(errorMessage);
            requestAnswer = new RequestAnswer(500, jsonAnswer);
            return requestAnswer;
        }


        return requestAnswer;
    }



}
