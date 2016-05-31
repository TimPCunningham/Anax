package io.github.timpcunningham.anax.player;

import java.text.MessageFormat;

public enum Channel {
    ADMIN("`7[`6A`7] `b{0}`7: `f{1}","anax.chat.admin"),
    GLOBAL("`7<`b{0}`7>: `f{1}", "anax.chat.global"),
    WORLD("`7[`bWorld`7] `b{0}`7: `f{1}", "anax.chat.world");

    private String format;
    private String permission;
    Channel(String format, String permission) {
        this.format = format;
        this.permission = permission;
    }

    public String getFormat() {
        return this.format;
    }

    public String format(String sender, String message) {
        return MessageFormat.format(this.format, sender, message).replace('`','§');
    }

    public String getPermission() {
        return this.permission;
    }
}
