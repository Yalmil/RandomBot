package com.discordbot.model;

public class RandomBall {
    private String id;
    private String characterName;
    private String ownerId;
    private int level;
    private int experience;
    private long caughtTimestamp;

    public RandomBall(String id, String characterName, String ownerId) {
        this.id = id;
        this.characterName = characterName;
        this.ownerId = ownerId;
        this.level = 1;
        this.experience = 0;
        this.caughtTimestamp = System.currentTimeMillis();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public long getCaughtTimestamp() {
        return caughtTimestamp;
    }

    public void setCaughtTimestamp(long caughtTimestamp) {
        this.caughtTimestamp = caughtTimestamp;
    }

    // Calcula XP necesaria para subir de nivel
    public int getXPNeededForNextLevel() {
        return level * 100;
    }

    // Añade experiencia y verifica si sube de nivel
    public boolean addExperience(int xp) {
        this.experience += xp;
        int xpNeeded = getXPNeededForNextLevel();
        
        if (this.experience >= xpNeeded) {
            this.experience -= xpNeeded;
            this.level++;
            return true; // Subió de nivel
        }
        return false;
    }

    // Verifica si alcanzó un nivel especial
    public boolean isSpecialLevel() {
        return level == 25 || level == 50 || level == 75 || level == 100;
    }

    // Obtiene el emoji especial correspondiente al nivel
    public String getSpecialEmoji() {
        switch (level) {
            case 25:
                return "🌟"; // Puedes cambiar esto
            case 50:
                return "💎";
            case 75:
                return "👑";
            case 100:
                return "🏆";
            default:
                return "";
        }
    }
}