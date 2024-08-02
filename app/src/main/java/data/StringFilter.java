package data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFilter {
    private static String buildRegex(String find) {
        String [] words = find.toLowerCase().split(" ");
        StringBuilder regex = new StringBuilder("\\b(");
        for (String word : words) {
            regex.append("\\w*").append(word).append("\\w*");
            if (!word.equals(words[words.length - 1])) {
                regex.append("|");
            }
        }
        regex.append(")\\b");
        return regex.toString();
    }

    static public Boolean matchByRegex(String target, String find) {
        String regex = buildRegex(find);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(target.toLowerCase());
        while (matcher.find()) {
            // if there is at least one instance, return True
            return true;
        }
        return false;
    }
}
