package io.github.hashibutogarasu.mla.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "mla")
public class ModConfig implements ConfigData {
    public static enum Mode{
        APRIL_FOOL,
        MOJANG_STUDIOS;

        Mode(){

        }
    }

    public Mode mode = Mode.MOJANG_STUDIOS;
}