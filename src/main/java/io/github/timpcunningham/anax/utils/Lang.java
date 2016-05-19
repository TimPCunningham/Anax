package io.github.timpcunningham.anax.utils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public enum Lang {
    COMMAND_CREATE_SUCCESS,
    COMMAND_ERROR_SENDER_NOT_PLAYER,
    COMMAND_MAPS_HEADER,
    FLAG_DESC_ANIMALS,
    FLAG_DESC_EXPLOSIONS,
    FLAG_DESC_MONSTERS,
    FLAG_DESC_PHYSICS,
    FLAG_DESC_WEATHER,
    FLAG_HELP,
    FLAG_NOT_FOUND,
    FLAG_VALUE_CHANGED,
    PLUGIN_NAME,
    SERVER_NOLOADEDMAPS,
    SERVER_WORLD_LOADED,
    SERVER_WORLD_LOAD_FAILED,
    WORLD_CANT_ACCESS,
    WORLD_ALREADY_EXISTS,
    WORLD_ALREADY_LOADED,
    WORLD_LOADED,
    WORLD_MANAGE_DENY,
    WORLD_NOT_FOUND,
    WORLD_NOT_LOADED,
    WORLD_UNLOADED;


    static Map<String, ResourceBundle> bundle;
    public String get(String locale, Object... args) {
        ResourceBundle langFile = getLangFile(locale);

        String path = this.name().toLowerCase().replace('_', '.');
        String res = langFile.getString(path);

        res = MessageFormat.format(res, args);
        res = res.replace('`', '§');

        return res;
    }

    private ResourceBundle getLangFile(String playerLocale) {
        if(bundle == null) {
            bundle = new HashMap<>();
        }
        if(bundle.containsKey(playerLocale)) {
            return bundle.get(playerLocale);
        }

        try {
            ResourceBundle result = ResourceBundle.getBundle("lang." + playerLocale);
            bundle.put(playerLocale, result);
            return result;
        } catch (Exception e) {
            return ResourceBundle.getBundle("lang.en_US");
        }
    }
}
