package io.github.timpcunningham.anax.utils;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public enum Lang {
    PLUGIN_NAME;

    public String get(String locale, Object... args) {
        ResourceBundle langFile = getLangFile(locale);

        String path = this.name().toLowerCase().replace('_', '.');
        String res = langFile.getString(path);

        res = MessageFormat.format(res, args);
        return res;
    }

    private ResourceBundle getLangFile(String locale) {
        try {
            return ResourceBundle.getBundle(locale);
        } catch (Exception e) {
            return ResourceBundle.getBundle("en_US");
        }
    }
}
