package com.discordbot.game;

import com.discordbot.model.Spawn;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpawnManager {
    private final Map<String, Spawn> activeSpawns;
    
    // Character images mapping
    private final Map<String, String> characterImages = Map.of(
        "AureComay", "https://cdn.discordapp.com/avatars/1124589523003785246/1d4e3f885b485679dcbf302db5d94236.png?size=1024",
        "SaantyBl", "https://cdn.discordapp.com/avatars/1367355249622257756/321057a0f9c5e039e7d8fdae072dade4.png?size=1024",
        "Nickname", "https://cdn.discordapp.com/avatars/725457574618726451/77d16b9d3992cdac62d809f1783e50ed.png?size=1024",
        "Abricraft", "https://cdn.discordapp.com/avatars/1226360384517046303/51c5a9adfa4fb382273f34c8ac685ae9.png?size=1024",
        "Nienie", "https://cdn.discordapp.com/avatars/1135321386416603418/41cb6a447172650aaec6609e11635505.png?size=1024",
        "Pazmania", "https://cdn.discordapp.com/avatars/1145162477156454410/13654a18569b538a867fe589cf8e0a53.png?size=1024",
        "Channel", "https://cdn.discordapp.com/attachments/1403395343143534613/1403408269619236965/channel_de_temu.png?ex=68c98a4d&is=68c838cd&hm=acebdc89a9b1c3c73339d16246d19a9045c2362093fb705a2af0d5f7683499f4&"
    );
    
    public SpawnManager() {
        this.activeSpawns = new ConcurrentHashMap<>();
    }
    
    public SpawnData createSpawn(String characterName, String channelId) {
        // Clean up expired spawns
        cleanupExpiredSpawns();
        
        String spawnId = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES);
        
        Spawn spawn = new Spawn(spawnId, characterName, channelId, expiresAt);
        activeSpawns.put(spawnId, spawn);
        
        // Create embed with button
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Un random salvaje ha aparecido!")
                .setDescription("Pulsa el botón para atraparlo")
                .setColor(0xFF5733);
                
        // Add image if available
        String imageUrl = characterImages.get(characterName);
        if (imageUrl != null) {
            embed.setImage(imageUrl);
        }
        
        Button catchButton = Button.primary("catch:" + spawnId, "Atrapar");
        ActionRow actionRow = ActionRow.of(catchButton);
        
        System.out.println("Created spawn: " + characterName + " with ID: " + spawnId);
        
        return new SpawnData(spawn, embed.build(), actionRow);
    }
    
    public Spawn getSpawn(String spawnId) {
        Spawn spawn = activeSpawns.get(spawnId);
        if (spawn != null && spawn.isExpired()) {
            activeSpawns.remove(spawnId);
            return null;
        }
        return spawn;
    }
    
    public void removeSpawn(String spawnId) {
        activeSpawns.remove(spawnId);
    }
    
    public Spawn tryConsumeSpawn(String spawnId) {
        // Atomically remove and return the spawn - only one thread can succeed
        Spawn spawn = activeSpawns.remove(spawnId);
        if (spawn != null && spawn.isExpired()) {
            // If spawn was expired, return null since it's no longer valid
            return null;
        }
        return spawn;
    }
    
    public void restoreSpawn(Spawn spawn) {
        // Restore spawn if catch failed (only if not expired)
        if (spawn != null && !spawn.isExpired()) {
            activeSpawns.put(spawn.getId(), spawn);
        }
    }
    
    private void cleanupExpiredSpawns() {
        activeSpawns.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    public static class SpawnData {
        private final Spawn spawn;
        private final MessageEmbed embed;
        private final ActionRow actionRow;
        
        public SpawnData(Spawn spawn, MessageEmbed embed, ActionRow actionRow) {
            this.spawn = spawn;
            this.embed = embed;
            this.actionRow = actionRow;
        }
        
        public Spawn getSpawn() {
            return spawn;
        }
        
        public MessageEmbed getEmbed() {
            return embed;
        }
        
        public ActionRow getActionRow() {
            return actionRow;
        }
    }
}