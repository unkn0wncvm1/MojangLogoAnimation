package io.github.hashibutogarasu.mla;

import io.github.hashibutogarasu.mla.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

public class MojangLogoAnimationClient implements ClientModInitializer {
    public static ModConfig config;

    @Override
    public void onInitializeClient() {
        try {
            System.out.println("Registering ModConfig...");
            AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
            System.out.println("Retrieving ModConfig...");
            config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            System.out.println("ModConfig retrieved successfully: " + config.mode);
        } catch (Exception e) {
            System.err.println("Failed to initialize MojangLogoAnimationClient");
            e.printStackTrace();
        }
    }
}
