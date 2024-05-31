package io.github.autoinfelytra.music;

import io.github.autoinfelytra.config.AutomaticElytraConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MusicPlayer {
    public static SoundEvent EMBARK;
    public static SoundEvent SWEEPING_CLOUDS_SOUND;
    public static SoundEvent SUNSHINE;
    public static SoundEvent HOMESICK;
    public static SoundEvent FEELING;

    private static boolean isPlayingMusic = false;
    private static boolean firstSoundPlaying = false;
    private static int musicNumber = 1;
    public static void playMusic(PlayerEntity player){
        if(MinecraftClient.getInstance().options.getSoundVolumeOption(SoundCategory.MASTER).getValue() > 0) {
            //PLAYING WAIT
            if (musicNumber == 1 && !isPlayingMusic) {
                if (AutomaticElytraConfig.HANDLER.instance().play_wait_disc) {
                    player.playSoundToPlayer(SoundEvents.MUSIC_DISC_WAIT, SoundCategory.MASTER, AutomaticElytraConfig.HANDLER.instance().volume, 1.0f);
                    isPlayingMusic = true;
                }
                player.sendMessage(Text.literal("Playing Wait"), true);
                incrementMusicNumberAndWrap();
            }
            //EMBARK
            if (musicNumber == 2 && !isPlayingMusic) {
                if (AutomaticElytraConfig.HANDLER.instance().play_embark) {
                    player.playSoundToPlayer(EMBARK, SoundCategory.MASTER, AutomaticElytraConfig.HANDLER.instance().volume, 1.0f);
                    isPlayingMusic = true;
                }
                player.sendMessage(Text.literal("Playing Embark On A New Journey"), true);
                incrementMusicNumberAndWrap();
            }
            //SWEEPING CLOUDS
            if (musicNumber == 3 && !isPlayingMusic) {
                if (AutomaticElytraConfig.HANDLER.instance().play_clouds) {
                    player.playSoundToPlayer(SWEEPING_CLOUDS_SOUND, SoundCategory.MASTER, AutomaticElytraConfig.HANDLER.instance().volume, 1.0f);
                    isPlayingMusic = true;
                }
                player.sendMessage(Text.literal("Playing Sweeping Through The Clouds"), true);
                incrementMusicNumberAndWrap();
            }
            //OTHERSIDE
            if (musicNumber == 4 && !isPlayingMusic) {
                if (AutomaticElytraConfig.HANDLER.instance().play_otherside_disc) {
                    player.playSoundToPlayer(SoundEvents.MUSIC_DISC_OTHERSIDE, SoundCategory.MASTER, AutomaticElytraConfig.HANDLER.instance().volume, 1.0f);
                    isPlayingMusic = true;
                }
                player.sendMessage(Text.literal("Playing Otherside"), true);
                incrementMusicNumberAndWrap();
            }
            //SUNSHINE
            if (musicNumber == 5 && !isPlayingMusic) {
                if (AutomaticElytraConfig.HANDLER.instance().play_sunshine) {
                    player.playSoundToPlayer(SUNSHINE, SoundCategory.MASTER, AutomaticElytraConfig.HANDLER.instance().volume, 1.0f);
                    isPlayingMusic = true;
                }
                player.sendMessage(Text.literal("Playing The First Ray Of Sunshine"), true);
                incrementMusicNumberAndWrap();
            }
            //PIGSTEP
            if (musicNumber == 6 && !isPlayingMusic) {
                if (AutomaticElytraConfig.HANDLER.instance().play_pigstep_disc) {
                    player.playSoundToPlayer(SoundEvents.MUSIC_DISC_PIGSTEP, SoundCategory.MASTER, AutomaticElytraConfig.HANDLER.instance().volume, 1.0f);
                    isPlayingMusic = true;
                }
                player.sendMessage(Text.literal("Playing Pigstep"), true);
                incrementMusicNumberAndWrap();
            }
            //
            if (musicNumber == 7 && !isPlayingMusic) {
                if (AutomaticElytraConfig.HANDLER.instance().play_mellohi_disc) {
                    player.playSoundToPlayer(SoundEvents.MUSIC_DISC_MELLOHI, SoundCategory.MASTER, AutomaticElytraConfig.HANDLER.instance().volume, 1.0f);
                    isPlayingMusic = true;
                }
                player.sendMessage(Text.literal("Playing Mellohi"), true);
                incrementMusicNumberAndWrap();
            }
            //FEELING
            if (musicNumber == 8 && !isPlayingMusic) {
                if (AutomaticElytraConfig.HANDLER.instance().play_feeling) {
                    player.playSoundToPlayer(FEELING, SoundCategory.MASTER, AutomaticElytraConfig.HANDLER.instance().volume, 1.0f);
                    isPlayingMusic = true;
                }
                player.sendMessage(Text.literal("Playing A Feeling Like Never Before"), true);
                incrementMusicNumberAndWrap();
            }
            //HOMESICK
            if (musicNumber == 9 && !isPlayingMusic) {
                if (AutomaticElytraConfig.HANDLER.instance().play_homesick) {
                    player.playSoundToPlayer(HOMESICK, SoundCategory.MASTER, AutomaticElytraConfig.HANDLER.instance().volume, 1.0f);
                    isPlayingMusic = true;
                }
                player.sendMessage(Text.literal("Playing Homesick"), true);
                incrementMusicNumberAndWrap();
            }

            isPlayingMusic = isSoundPlaying(EMBARK.getId(), MinecraftClient.getInstance().getSoundManager().soundSystem)
                    || isSoundPlaying(SWEEPING_CLOUDS_SOUND.getId(), MinecraftClient.getInstance().getSoundManager().soundSystem)
                    || isSoundPlaying(SUNSHINE.getId(), MinecraftClient.getInstance().getSoundManager().soundSystem)
                    || isSoundPlaying(SoundEvents.MUSIC_DISC_WAIT.getId(), MinecraftClient.getInstance().getSoundManager().soundSystem)
                    || isSoundPlaying(SoundEvents.MUSIC_DISC_OTHERSIDE.getId(), MinecraftClient.getInstance().getSoundManager().soundSystem)
                    || isSoundPlaying(SoundEvents.MUSIC_DISC_PIGSTEP.getId(), MinecraftClient.getInstance().getSoundManager().soundSystem)
                    || isSoundPlaying(SoundEvents.MUSIC_DISC_MELLOHI.getId(), MinecraftClient.getInstance().getSoundManager().soundSystem)
                    || isSoundPlaying(FEELING.getId(), MinecraftClient.getInstance().getSoundManager().soundSystem)
                    || isSoundPlaying(HOMESICK.getId(), MinecraftClient.getInstance().getSoundManager().soundSystem);
        }
        else {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Music cannot play; your master volume is 0%"), true);
        }
    }

    public static void stopAllMusic(){
        MinecraftClient.getInstance().getSoundManager().stopSounds(SoundEvents.MUSIC_DISC_WAIT.getId(), SoundCategory.MASTER);
        MinecraftClient.getInstance().getSoundManager().stopSounds(EMBARK.getId(), SoundCategory.MASTER);
        MinecraftClient.getInstance().getSoundManager().stopSounds(SWEEPING_CLOUDS_SOUND.getId(), SoundCategory.MASTER);
        MinecraftClient.getInstance().getSoundManager().stopSounds(SoundEvents.MUSIC_DISC_OTHERSIDE.getId(), SoundCategory.MASTER);
        MinecraftClient.getInstance().getSoundManager().stopSounds(SUNSHINE.getId(), SoundCategory.MASTER);
        MinecraftClient.getInstance().getSoundManager().stopSounds(SoundEvents.MUSIC_DISC_PIGSTEP.getId(), SoundCategory.MASTER);
        MinecraftClient.getInstance().getSoundManager().stopSounds(SoundEvents.MUSIC_DISC_MELLOHI.getId(), SoundCategory.MASTER);
        MinecraftClient.getInstance().getSoundManager().stopSounds(FEELING.getId(), SoundCategory.MASTER);
        MinecraftClient.getInstance().getSoundManager().stopSounds(HOMESICK.getId(), SoundCategory.MASTER);
        musicNumber = 1;

        isPlayingMusic = false;
    }

    public static boolean isPlayingMusic() {
        return isPlayingMusic;
    }

    public static boolean isSoundPlaying(Identifier id, SoundSystem soundSystem) {
        for (SoundInstance soundInstance : soundSystem.sources.keySet()) {
            if (soundInstance.getId().equals(id)) {
                return true; // Found a matching sound identifier
            }
        }
        return false; // No matching sound identifier found
    }

    private static void incrementMusicNumberAndWrap(){
        musicNumber = (musicNumber + 1) % 9;
        if(musicNumber == 0) musicNumber = 9;
    }

}
