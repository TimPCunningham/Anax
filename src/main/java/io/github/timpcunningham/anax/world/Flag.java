package io.github.timpcunningham.anax.world;

import io.github.timpcunningham.anax.utils.Lang;

public enum Flag {
    ANIMALS(Lang.FLAG_DESC_ANIMALS, false),
    EXPLOSIONS(Lang.FLAG_DESC_EXPLOSIONS, false),
    MONSTERS(Lang.FLAG_DESC_MONSTERS, false),
    PHYSICS(Lang.FLAG_DESC_PHYSICS, true),
    WEATHER(Lang.FLAG_DESC_WEATHER, false);

    private Lang desc;
    private boolean enabled;
    Flag(Lang desc, boolean enabled) {
        this.desc = desc;
        this.enabled = enabled;
    }

    Lang getDescription() {
        return this.desc;
    }

    boolean isEnabled() {
        return this.enabled;
    }

    boolean toggle() {
        this.enabled = !enabled;
        return isEnabled();
    }
}
