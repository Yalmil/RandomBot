
package com.discordbot.model;

public class UserLevel {
    private final String userId;
    private int experience;
    private int level;
    private int messagesCount;
    
    public UserLevel(String userId) {
        this.userId = userId;
        this.experience = 0;
        this.level = 1;
        this.messagesCount = 0;
    }
    
    public UserLevel(String userId, int experience, int level, int messagesCount) {
        this.userId = userId;
        this.experience = experience;
        this.level = level;
        this.messagesCount = messagesCount;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public int getExperience() {
        return experience;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getMessagesCount() {
        return messagesCount;
    }
    
    public void addExperience(int xp) {
        this.experience += xp;
        this.messagesCount++;
        updateLevel();
    }
    
    private void updateLevel() {
        int newLevel = calculateLevelFromXP(experience);
        this.level = newLevel;
    }
    
    public static int calculateLevelFromXP(int xp) {
        // Level formula: level = floor(sqrt(xp / 100)) + 1
        // This means: Level 1 = 0-99 XP, Level 2 = 100-399 XP, Level 3 = 400-899 XP, etc.
        return (int) Math.floor(Math.sqrt(xp / 100.0)) + 1;
    }
    
    public int getXPForNextLevel() {
        int nextLevel = level + 1;
        return (nextLevel - 1) * (nextLevel - 1) * 100;
    }
    
    public int getXPForCurrentLevel() {
        if (level <= 1) return 0;
        return (level - 1) * (level - 1) * 100;
    }
    
    public int getXPProgressInCurrentLevel() {
        return experience - getXPForCurrentLevel();
    }
    
    public int getXPNeededForCurrentLevel() {
        return getXPForNextLevel() - getXPForCurrentLevel();
    }
}
