package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.regex.Pattern;

import logic.GameLogic;
import logic.User;
import org.json.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.naming.ldap.HasControls;

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
                // https://stleary.github.io/JSON-java/index.html
                String headerMsg = "";
                /**
                 * */

                // GET REQUEST FROM CLIENT
                do {
                    message = reader.readLine(); //get next line from socket (input).
                    //System.out.println("got: " + message);
                    if(!message.isEmpty()) {
                        headerMsg = headerMsg + message + "\n";
                    }

                } while (!message.isEmpty()); //loop ends when the received message is empty

                JSONObject jsonHeader = HTTP.toJSONObject(headerMsg); //get JSON Object
                //https://www.tutorialspoint.com/org_json/org_json_http.htm
                JSONObject jsonContent = null;
                String contentMsg = null;

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
                            System.out.println("Content message is" + contentMsg);
                            if(contentMsg.isEmpty()) {
                                jsonContent = null;
                            }
                            else if(contentMsg.charAt(0) =='['){ //If the content is an array:
                                System.out.println("es un array");
                                JSONArray jsonArray = new JSONArray(contentMsg);
                                jsonContent = new JSONObject();
                                jsonContent.put("cards", jsonArray);

                            }
                            else {
                                jsonContent = new JSONObject(contentMsg);
                            }

                        }
                    }
                }

                //SEND REQUEST AND GET ANSWER FROM DB - hndleRequest is defined down.
                RequestAnswer requestAnswer = handleRequest(jsonHeader, jsonContent);

                //ANSWER BACK TO THE CLIENT: Write in the socket the answer
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
        JSONObject jsonAnswer = null;

        try {
            if(jsonHeader.getString("Method").equals("POST")) {
                if(jsonHeader.getString("Request-URI").equals("/users")) { //CREATE A USER
                    User user = gameLogic.createUser(jsonContent.getString("Username"), jsonContent.getString("Password"));
                    if(user == null) {
                        throw new Exception("user could not be created");
                    }
                    else {
                        jsonAnswer = new JSONObject();
                        jsonAnswer.put("username", user.getUsername());
                        jsonAnswer.put("result", "OK");
                        jsonAnswer.put("message", "User successfully created");
                        requestAnswer = new RequestAnswer(200, jsonAnswer);
                    }
                }
                else if(jsonHeader.getString("Request-URI").equals("/sessions")) { //LOGIN A USER
                    User user = gameLogic.loginUser(jsonContent.getString("Username"), jsonContent.getString("Password"));
                    if(user == null) {
                        throw new Exception("user could not log-in");
                    }
                    else {
                        jsonAnswer = new JSONObject();
                        jsonAnswer.put("username", user.getUsername());
                        jsonAnswer.put("result", "OK");
                        jsonAnswer.put("message", "User successfully logged-in!");
                        requestAnswer = new RequestAnswer(200, jsonAnswer);
                    }
                }
                else if(jsonHeader.getString("Request-URI").equals("/packages")) { //CREATE PACKAGE
                    String token = jsonHeader.getString("Authorization");
                    JSONArray cards = jsonContent.getJSONArray("cards");
                    if(gameLogic.addPackageToDB(cards, token)) {
                        jsonAnswer = new JSONObject();
                        jsonAnswer.put("result", "OK");
                        jsonAnswer.put("message", "Package successfully added");
                        requestAnswer = new RequestAnswer(200, jsonAnswer);
                    }
                    else {
                        throw new Exception("Package could not be added");
                    }

                }
                else if(jsonHeader.getString("Request-URI").equals("/transactions/packages")) {
                    String token = jsonHeader.getString("Authorization");
                    Object cardsInJson = gameLogic.acquirePackage(token);
                    if(cardsInJson != null){
                        jsonAnswer = new JSONObject();
                        jsonAnswer.put("result", "OK");
                        jsonAnswer.put("message", "package has been acquired");
                        jsonAnswer.put("cards", cardsInJson);
                        requestAnswer = new RequestAnswer(200, jsonAnswer);
                    }
                    else {
                        throw new Exception("Package could not be added");
                    }

                }
                else if(jsonHeader.getString("Request-URI").equals("/battles")) {
                    String token = jsonHeader.getString("Authorization");
                    JSONObject battleLog = gameLogic.startBattle(token);
                    if(battleLog != null) {
                        jsonAnswer = new JSONObject();
                        jsonAnswer.put("result", "OK");
                        jsonAnswer.put("battle Result",battleLog);
                        requestAnswer = new RequestAnswer(200, jsonAnswer);

                    }
                }
            }
            else if(jsonHeader.getString("Method").equals("GET")) {
                if(jsonHeader.getString("Request-URI").equals("/cards")){
                    String token = jsonHeader.getString("Authorization");
                    Object cardsInJson = gameLogic.getUserCards(token);
                    if(cardsInJson != null){
                        jsonAnswer = new JSONObject();
                        jsonAnswer.put("result", "OK");
                        jsonAnswer.put("message", "User cards successfully accessed");
                        jsonAnswer.put("cards", cardsInJson);
                        requestAnswer = new RequestAnswer(200, jsonAnswer);
                    }
                    else {
                        throw new Exception("User has no cards or they could not be found");
                    }
                }
                else if(jsonHeader.getString("Request-URI").equals("/deck")){
                    String token = jsonHeader.getString("Authorization");
                    Object cardsInJson = gameLogic.showUserDeck(token);
                    if(cardsInJson != null){
                        jsonAnswer = new JSONObject();
                        jsonAnswer.put("result", "OK");
                        jsonAnswer.put("message", "Deck cards successfully accessed");
                        jsonAnswer.put("cards", cardsInJson);
                        requestAnswer = new RequestAnswer(200, jsonAnswer);
                    }
                    else {
                        throw new Exception("Deck is not configured or empty");
                    }
                }
                else if(jsonHeader.getString("Request-URI").equals("/deck?format=plain")) {
                    String token = jsonHeader.getString("Authorization");
                    Object cardsInJson = gameLogic.showUserDeck(token);
                    if(cardsInJson != null){
                        jsonAnswer = new JSONObject();
                        jsonAnswer.put("result", "OK");
                        jsonAnswer.put("message", "Deck cards successfully accessed");
                        jsonAnswer.put("cards", cardsInJson);
                        requestAnswer = new RequestAnswer(200, jsonAnswer);
                    }
                    else {
                        throw new Exception("Deck is not configured or empty");
                    }

                }
                else if(jsonHeader.getString("Request-URI").contains("/users/")) {
                    //get the last part of Request-URI with user
                    String username = jsonHeader.getString("Request-URI").substring(jsonHeader.getString("Request-URI").lastIndexOf("/") + 1);
                    String token = jsonHeader.getString("Authorization");
                    JSONObject userData = gameLogic.getUserData(username, token);
                    if(userData != null) {
                        jsonAnswer = new JSONObject();
                        jsonAnswer.put("result", "OK");
                        jsonAnswer.put("User Data", userData);
                        requestAnswer = new RequestAnswer(200, jsonAnswer);
                    }
                    else {
                        throw new Exception("Token and user do not match");
                    }


                }
                else if(jsonHeader.getString("Request-URI").equals("/stats")) {
                    String token = jsonHeader.getString("Authorization");
                    int elo = gameLogic.getEloScore(token);
                    jsonAnswer = new JSONObject();
                    jsonAnswer.put("result", "OK");
                    jsonAnswer.put("ELO-Score", elo);
                    requestAnswer = new RequestAnswer(200, jsonAnswer);

                }
                else if(jsonHeader.getString("Request-URI").equals("/score")){
                    String token = jsonHeader.getString("Authorization");
                    HashMap<String, Integer> scores = gameLogic.getScoreboard();
                    if(!scores.isEmpty()) {
                        jsonAnswer = new JSONObject();
                        jsonAnswer.put("result", "OK");
                        jsonAnswer.put("scoreboard", scores);
                        requestAnswer = new RequestAnswer(200, jsonAnswer);
                    }
                    else {
                        throw new Exception("An error has occurred while retrieving scoreboard");
                    }

                }
            }
            else if(jsonHeader.getString("Method").equals("PUT")) {
                if(jsonHeader.getString("Request-URI").equals("/deck")) {
                    String token = jsonHeader.getString("Authorization");
                    JSONArray cards = jsonContent.getJSONArray("cards");
                    if(gameLogic.configureDeck(cards, token)) {
                        jsonAnswer = new JSONObject();
                        jsonAnswer.put("result", "OK");
                        jsonAnswer.put("message", "Deck cards successfully added");
                        requestAnswer = new RequestAnswer(200, jsonAnswer);
                    }
                    else {
                        throw new Exception("Deck could not be configured");
                    }
                }
                else if(jsonHeader.getString("Request-URI").contains("/user")) {
                    //get the last part of Request-URI with user
                    String username = jsonHeader.getString("Request-URI").substring(jsonHeader.getString("Request-URI").lastIndexOf("/") + 1);
                    String token = jsonHeader.getString("Authorization");
                    if(gameLogic.updateUser(username, token, jsonContent)) {
                        jsonAnswer = new JSONObject();
                        jsonAnswer.put("result", "OK");
                        jsonAnswer.put("message", "User-data successfully updated");
                        requestAnswer = new RequestAnswer(200, jsonAnswer);
                    }
                    else {
                        throw new Exception("User-data could not be updated");
                    }

                }
            }
            else if(jsonHeader.getString("Method").equals("DELETE")) {

            }
            else {
                //throw exception.
                throw new Exception("No correct option selected");
            }
        }
        catch (Exception e) {
            jsonAnswer = new JSONObject();
            jsonAnswer.put("result", "ERR");
            jsonAnswer.put("message", e.getMessage());
            requestAnswer = new RequestAnswer(500, jsonAnswer);
            return requestAnswer;
        }


        return requestAnswer;
    }



}
