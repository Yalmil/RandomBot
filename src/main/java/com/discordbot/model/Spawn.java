package com.discordbot.model;

import java.time.Instant;

public class Spawn {
    private final String id;
    private final String characterName;
    private final String channelId;
    private final Instant expiresAt;
    private String messageId;
    
    public Spawn(String id, String characterName, String channelId, Instant expiresAt) {
        this.id = id;
        this.characterName = characterName;
        this.channelId = channelId;
        this.expiresAt = expiresAt;
    }
    
    public String getId() {
        return id;
    }
    
    public String getCharacterName() {
        return characterName;
    }
    
    public String getChannelId() {
        return channelId;
    }
    
    public Instant getExpiresAt() {
        return expiresAt;
    }
    
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}