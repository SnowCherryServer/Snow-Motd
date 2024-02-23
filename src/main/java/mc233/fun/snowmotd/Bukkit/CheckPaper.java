package mc233.fun.snowmotd;

import java.util.logging.Logger;

public class CheckPaper {
    private static final Logger logger = Logger.getLogger("Snow-Motd");

    public CheckPaper() {
    }

    public static boolean isPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        } catch (ClassNotFoundException var1) {
            return false;
        }
    }
}
