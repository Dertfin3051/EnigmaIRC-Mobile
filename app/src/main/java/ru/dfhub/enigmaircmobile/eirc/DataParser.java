package ru.dfhub.enigmaircmobile.eirc;


import org.json.JSONObject;
import ru.dfhub.enigmaircmobile.eirc.util.Encryption;
import ru.dfhub.enigmaircmobile.eirc.util.ResourcesReader;
/**
 * Class for working with data and processing it
 */
public class DataParser {

    /**
     * Types of incoming and outgoing messages
     */
    public enum MessageType {
        USER_MESSAGE("user_message"), // User text messages
        USER_SESSION("user_session"); // Messages about user join/leave

        private String fileName;

        MessageType(String fileName) {
            this.fileName = fileName;
        }

        private String getResourcesPath() {
            return String.format("message_templates/%s.json", this.fileName);
        }

        public String getTemplate() {
            return new ResourcesReader(this.getResourcesPath()).readString().replace("\n", "");
        }
    }

    /**
     * Parse the incoming message and take the necessary action to work with it
     * @param data Raw data from server
     */
    public static void handleInputData(String data) {
        JSONObject dataObj;
        try {
            dataObj = new JSONObject(data);
        } catch (Exception e) { return; } // Null message from server


        switch (dataObj.optString("type")) {
            case "user-message":
                handleUserMessage(dataObj.optJSONObject("content"));
                break;
            case "user-session":
                handleUserSession(dataObj.optJSONObject("content"));
                break;
            case "server-shutdown":
                // Main.handleServerShutdown();
                break;
        }
    }

    /**
     * Collect a user message into a data type accepted by the client
     * @param message Message
     */
    public static void handleOutputMessage(String message) {
        String template;
        try {
            template = MessageType.USER_MESSAGE.getTemplate();
        } catch (Exception e) {
            Gui.showNewMessage("There was an error sending the message (receiving template)", Gui.MessageType.SYSTEM_ERROR);
            e.printStackTrace();
            return;
        }

        String encryptedMessage;
        try {
            //encryptedMessage = Encryption.encrypt(message);
        } catch (Exception e) {
            Gui.showNewMessage("There was an error sending the message (encrypt process)", Gui.MessageType.SYSTEM_ERROR);
            e.printStackTrace();
            return;
        }

        /*
        Main.getServerConnection().sendToServer(template
            .replace("%user%", Config.getConfig().optString("username"))
            .replace("%message%", encryptedMessage)
        );
         */
    }

    /**
     * Process and send a message about your session (join/leave)
     * @param isJoin Is join
     */
    public static void handleOutputSession(boolean isJoin) {
        String status = isJoin ? "join" : "leave";

        String template;
        try {
            template = MessageType.USER_SESSION.getTemplate();
        } catch (Exception e) {
            Gui.showNewMessage("There was an error sending the session status (receiving template)", Gui.MessageType.SYSTEM_ERROR);
            e.printStackTrace();
            return;
        }

        /*
        Main.getServerConnection().sendToServer(template
                .replace("%user%", Config.getConfig().optString("username"))
                .replace("%status%", status)
        );

         */
    }

    /**
     * Processing an incoming user message
     * @param data Data's "content" object
     */
    private static void handleUserMessage(JSONObject data) {
        String sender = data.optString("user");
        String encryptedMessage = data.optString("message"); // In ftr, decrypt and handle decryption errors here

        String message = "";
        try {
            // message = Encryption.decrypt(encryptedMessage);
        } catch (Exception e) {
            Gui.showNewMessage("Failed to decrypt the incoming message! (wrong encryption key)", Gui.MessageType.SYSTEM_ERROR);
            return;
        }

        String formattedMessage = String.format("%s\n%s", sender, message); // In ftr, handle timestamps here

        if (sender.equals(Config.getConfig().optString("username"))) {
            Gui.showNewMessage(formattedMessage, Gui.MessageType.SELF_USER_MESSAGE);
        } else {
            Gui.showNewMessage(formattedMessage, Gui.MessageType.USER_MESSAGE);
        }
        Gui.scrollDown();
    }

    /**
     * Handle input user-session(join/leave) message and show it
     * @param data Data's "content" object
     */
    private static void handleUserSession(JSONObject data) {
        String user = data.optString("user");
        String status = data.optString("status").equals("join") ? "joined!" : "left.";

        String formattedMessage = String.format("%s %s", user, status);

        Gui.showNewMessage(formattedMessage, Gui.MessageType.USER_SESSION);
        Gui.scrollDown();
    }
}
