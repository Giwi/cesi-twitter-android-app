package giwi.org.cesitwitter.model;

/**
 * Created by sca on 02/06/15.
 */
public class Message {

    /**
     * Instantiates a new Message.
     *
     * @param username the username
     * @param message  the message
     * @param date     the date
     * @param imageUrl the image url
     */
    public Message(String username, String message, String date, String imageUrl) {
        this.username = username;
        this.msg = message;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    private String imageUrl;

    /**
     * Gets image url.
     *
     * @return the image url
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets image url.
     *
     * @param imageUrl the image url
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets msg.
     *
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    String username;
    String msg;

    /**
     * Gets date.
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }

    String date;
}
