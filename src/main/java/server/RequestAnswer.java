package server;

import org.json.JSONObject;

public class RequestAnswer {
    int status;

    JSONObject jsonAnswer;

    RequestAnswer(int status, JSONObject jsonAnswer) {
        this.status = status;

        this.jsonAnswer = jsonAnswer;
    }

    String getStatusMsg() {
        String statusMsg;
        if(status == 200) {
            statusMsg = "OK";
        }
        else {
            statusMsg = "Unknown";
        }

        return statusMsg;
    }
}
