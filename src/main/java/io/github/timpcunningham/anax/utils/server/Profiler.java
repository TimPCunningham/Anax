package io.github.timpcunningham.anax.utils.server;

import net.md_5.bungee.api.ChatColor;

import java.util.Date;

public class Profiler {
    private Date start;
    private Date end;
    private String tag;

    public Profiler(String tag) {
        this.tag = tag;
    }

    public void start() {
        start = new Date();
    }

    public void end() {
        end = new Date();
    }

    public String profile() {
        Long millis = end.getTime() - start.getTime();

        return ChatColor.GRAY + "Operation \"" + tag + "\" took " + millis + " milliseconds";
    }
}
