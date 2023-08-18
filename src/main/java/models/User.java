package models;

public class User {

    private final long chatId;
    private Status userStatus;

    public User(long chatId) {
        this.chatId = chatId;
        this.userStatus = Status.DEFAULT;
    }

    public long getChatId() {
        return chatId;
    }

    public Status getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        switch (userStatus) {
            case "DEFAULT":
                this.userStatus = Status.DEFAULT;
                break;

            case "ANONYMOUS":
                this.userStatus = Status.ANONYMOUS;
                break;
        }
    }

}
