package io.github.timpcunningham.anax.utils.chat;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public enum Lang {
    CHAT_CHANNEL_DUPLICATE,
    CHAT_CHANNEL_SET,
    CHAT_CHANNEL_SET_DENIED,
    CHAT_PERMISSION_DENIED,
    COMMAND_CREATE_SUCCESS,
    COMMAND_ERROR_SENDER_NOT_PLAYER,
    COMMAND_MAPS_HEADER,
    FLAG_DESC_ANIMALS,
    FLAG_DESC_EXPLOSIONS,
    FLAG_DESC_FIRE,
    FLAG_DESC_MONSTERS,
    FLAG_DESC_PHYSICS,
    FLAG_DESC_WEATHER,
    FLAG_DESC_WORLD,
    FLAG_HELP,
    FLAG_NOT_FOUND,
    FLAG_VALUE_CHANGED,
    FLAG_VALUE,
    FORMAT_ADMIN_ALERT,
    FORMAT_GLOBAL_ALERT,
    FORMAT_WORLD_ALERT,
    MESSAGE_CONSOLE,
    MESSAGE_DEFAULT,
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
        res = res.replace('`', '�');

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