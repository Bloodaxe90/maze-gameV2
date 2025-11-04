package io.github.eng1group9.toasts;

public class Toast {
    private String text;
    private long createdDate;

    public Toast(String text, long createdDate) {
        this.text = text;
        this.createdDate = createdDate;
    }

    public String getText() {
        return text;
    }

    public long getCreatedDate() {
        return createdDate;
    }
}
