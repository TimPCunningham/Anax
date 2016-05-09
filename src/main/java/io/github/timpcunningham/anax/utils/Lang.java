package io.github.timpcunningham.anax.utils;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public enum Lang {
    COMMAND_ERROR_SENDER_NOT_PLAYER,
    FLAG_DESC_ANIMALS,
    FLAG_DESC_EXPLOSIONS,
    FLAG_DESC_MONSTERS,
    FLAG_DESC_PHYSICS,
    FLAG_DESC_WEATHER,
    FLAG_NOT_FOUND,
    FLAG_VALUE_CHANGED,
    PLUGIN_NAME,
    WORLD_MANAGE_DENY,
    WORLD_NOT_FOUND,
    WORLD_NOT_LOADED;

    public String get(String locale, Object... args) {
        ResourceBundle langFile = getLangFile(locale);

        String path = this.name().toLowerCase().replace('_', '.');
        String res = langFile.getString(path);

        res = MessageFormat.format(res, args);
        res = res.replace('`', '§');

        return res;
    }

    private ResourceBundle getLangFile(String locale) {
        try {
            return ResourceBundle.getBundle("lang/" + locale);
        } catch (Exception e) {
            return ResourceBundle.getBundle("lang/en_US");
        }
    }
}
