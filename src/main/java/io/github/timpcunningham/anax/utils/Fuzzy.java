package io.github.timpcunningham.anax.utils;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Fuzzy {
    static final int MATCH_THRESHOLD = 10;


    public static List<String> findMatches(String part, List<String> potential) {
        List<String> results = new ArrayList<>();

        for(String item : potential) {
            if(item.toLowerCase().startsWith(part.toLowerCase())) {
                results.add(item);
            } else if(StringUtils.getLevenshteinDistance(part.toLowerCase(), item.toLowerCase()) <= MATCH_THRESHOLD) {
                results.add(item);
            }
        }

        return results;
    }

    public static String findBestMatch(String part, List<String> potential) {
        String result = "";
        int threshold = MATCH_THRESHOLD;
        int temp;

        List<String> matches = findMatches(part, potential);

        for(String match : matches) {
            temp = StringUtils.getLevenshteinDistance(part.toLowerCase(), match.toLowerCase());
            if(temp < threshold) {
                result = match;
                threshold = temp;
            }
        }

        return result;
    }


}
