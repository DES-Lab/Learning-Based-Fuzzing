package fuzzing;

import java.security.SecureRandom;
import java.util.*;

public class RandomString {
    private List<String> nonAcceptedChars = Arrays.asList("\u0000", "\n");

    public String getFuzzedTopic(int length) {
        this.buf = new char[length];
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        String res = new String(buf);
        int depthLevel = random.nextInt(res.length() / 3 + 1);
        for (int i = 0; i < depthLevel; i++)
            res = insertSlashAt(res);
        return res;
    }

    public String getSystemTopic(int lenght) {
        return random.nextBoolean() ? "$SYS/" + getFuzzedTopic(lenght) : "$" + getFuzzedTopic(lenght);
    }

    public String insertInvalidCharInTopic(String topic){
        String fault = nonAcceptedChars.get(random.nextInt(nonAcceptedChars.size()));
        return topic.substring(0, topic.length()/2) + fault + topic.substring(topic.length()/2);
    }

    private static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String lower = upper.toLowerCase(Locale.ROOT);

    private static final String digits = "0123456789";

    private static final String special = "!@%^&*()_~`±§<>,}]{[\"\\";

    private static final String alphanum = upper + lower + digits + special;

    private final Random random;

    private final char[] symbols;

    private char[] buf;


    private RandomString(Random random, String symbols) {
        if (symbols.length() < 2) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
    }

    /**
     * Create an alphanumeric strings from a secure generator.
     */
    public RandomString() {
        this(new SecureRandom(), alphanum);
    }

    private String insertSlashAt(String str){
        int r = random.nextInt(str.length() - 1);
        str = str.substring(0, r) + "/" + str.substring(r);
        return str;
    }
}
