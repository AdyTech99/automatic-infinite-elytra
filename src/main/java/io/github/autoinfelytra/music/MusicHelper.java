package io.github.autoinfelytra.music;

import io.github.autoinfelytra.AutomaticInfiniteElytraClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MusicHelper {
    public static SoundEvent registerSoundEvent(String name){
        Identifier id = new Identifier(AutomaticInfiniteElytraClient.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
}
