package cse215l.p2p.filetrasnsfer;

public class Utils {

    public static int randomCode() {
        return 1000 + (int) (Math.random() * 9000);
    }

    public static boolean isValidIP(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        for (String part : parts) {
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

}
