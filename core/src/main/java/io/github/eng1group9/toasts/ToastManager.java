package io.github.eng1group9.toasts;

import java.util.LinkedList;
import java.util.List;

public class ToastManager {
    private static List<Toast> toasts = new LinkedList<>();

    public static void addToast(String text) {
        Toast toast = new Toast(text, System.currentTimeMillis());
        toasts.add(toast);
    }

    public static void clearExpiredToasts() {
        Toast toastToRemove = null;

        for (Toast t : toasts) {
            if (System.currentTimeMillis() - t.getCreatedDate() > 3000) {
                toastToRemove = t;
            }
        }

        if (toastToRemove != null) {
            toasts.remove(toastToRemove);
        }
    }

    // Get list of currently displayed toasts
    public static List<String> getToasts() {
        List<String> toastTexts = new LinkedList<>();

        for (Toast t : toasts) {
            toastTexts.add(t.getText());
        }

        return toastTexts;
    }
}
