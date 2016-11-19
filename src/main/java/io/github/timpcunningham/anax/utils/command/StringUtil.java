package io.github.timpcunningham.anax.utils.command;

import com.google.common.base.Strings;
import com.sk89q.minecraft.util.commands.ChatColor;
import io.github.timpcunningham.anax.utils.server.Debug;
import org.bukkit.util.ChatPaginator;

import java.util.List;

public class StringUtil {

    public static String center(String message, char filler, ChatColor fillerColor) {
        int padSize = ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH - ChatColor.stripColor(message).length();
        padSize = (padSize / 2) - 2;

        String padding = fillerColor.toString() + ChatColor.STRIKETHROUGH + Strings.repeat(String.valueOf(filler), padSize);

        return padding + ChatColor.RESET + " " + message + " " + padding;
    }

    public static String toDisplay(List<String> input) {
        String result = "";

        for(int index = 0; index < input.size(); index++) {
            result += input.get(index) + ", ";
        }
        result = result.substring(0, result.length() - 2);

        if(input.size() > 1) {
            result = replaceLast(result, ", ", ", and ");
        }

        return result;
    }

    public static String replaceLast(String input, String oldSeq, String newSeq) {
        int index = input.lastIndexOf(oldSeq);

        if(index < 0) {
            return  input;
        } else {
            return input.substring(0, index) + newSeq + input.substring(index + oldSeq.length());
        }
    }
}
