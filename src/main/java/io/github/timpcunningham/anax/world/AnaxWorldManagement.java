package io.github.timpcunningham.anax.world;

import java.util.HashMap;
import java.util.Map;

public class AnaxWorldManagement {
    private static AnaxWorldManagement self;
    private Map<String, AnaxWorld> loadedWorlds;

    private AnaxWorldManagement() {
        loadedWorlds = new HashMap<>();
    }

    public static AnaxWorldManagement getInstance() {
        if(self == null) {
            self = new AnaxWorldManagement();
        }
        return self;
    }

    public AnaxWorld getWorld(String worldName) {
        return loadedWorlds.get(worldName);
    }

    public boolean isLoadedWorld(String worldName) {
        return loadedWorlds.containsKey(worldName);
    }
}
