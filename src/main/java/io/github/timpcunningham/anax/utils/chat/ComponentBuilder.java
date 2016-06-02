package io.github.timpcunningham.anax.utils.chat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ComponentBuilder {
    private TextComponent component;

    public ComponentBuilder() {
        component = new TextComponent();
    }

    public ComponentBuilder color(ChatColor color) {
        component.setColor(color);
        return this;
    }

    public ComponentBuilder text(String locale, Lang message, Object... args) {
        component.setText(message.get(locale, args));
        return this;
    }

    public  ComponentBuilder text(String text) {
        component.setText(text);
        return this;
    }

    public ComponentBuilder bold() {
        component.setBold(true);
        return this;
    }

    public ComponentBuilder underline() {
        component.setUnderlined(true);
        return this;
    }

    public ComponentBuilder strikethrough() {
        component.setStrikethrough(true);
        return this;
    }

    public ComponentBuilder obfuscate() {
        component.setObfuscated(true);
        return this;
    }

    public ComponentBuilder italicise() {
        component.setItalic(true);
        return this;
    }

    public ComponentBuilder add(BaseComponent baseComponent) {
        component.addExtra(baseComponent);
        return this;
    }

    public ComponentBuilder click(ClickEvent.Action action, String content) {
        ClickEvent event = new ClickEvent(action, content);

        component.setClickEvent(event);
        return this;
    }

    public ComponentBuilder hover(BaseComponent... content) {
        HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, content);

        component.setHoverEvent(event);
        return this;
    }

    public TextComponent build() {
        return component;
    }
}
