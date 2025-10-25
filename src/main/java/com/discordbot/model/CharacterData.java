package com.discordbot.model;

public class CharacterData {
    private final String name;
    private final String imageUrl;
    
    public CharacterData(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
    
    public String getName() {
        return name;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
}