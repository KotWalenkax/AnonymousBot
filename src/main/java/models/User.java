package models;

public class User {

    private final long chatId;
    private Status userStatus;

    public User(long chatId) {
        this.chatId = chatId;
        this.userStatus = Status.DEFAULT;
    }

    private User(long chatId, Status userStatus) {
        this.chatId = chatId;
        this.userStatus = userStatus;
    }

    public long getChatId() {
        return chatId;
    }

    public Status getUserStatus() {
        return userStatus;
    }

    public void setStatus(String userStatus) {
        this.userStatus = Status.valueOf(userStatus);
    }

}
