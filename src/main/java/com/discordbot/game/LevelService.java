
package com.discordbot.game;

import com.discordbot.model.UserLevel;
import com.discordbot.storage.UserLevelStore;

public class LevelService {
    private final UserLevelStore levelStore;
    private static final int BASE_XP_PER_MESSAGE = 15;
    private static final int BONUS_XP_RANGE = 10; // Random bonus 0-10 XP
    
    public LevelService(UserLevelStore levelStore) {
        this.levelStore = levelStore;
    }
    
    public LevelUpResult processMessage(String userId) {
        UserLevel userLevel = levelStore.getUserLevel(userId);
        int oldLevel = userLevel.getLevel();
        
        // Calculate XP gain (15 + random 0-10)
        int xpGain = BASE_XP_PER_MESSAGE + (int) (Math.random() * (BONUS_XP_RANGE + 1));
        userLevel.addExperience(xpGain);
        
        // Save updated level
        levelStore.updateUserLevel(userLevel);
        
        int newLevel = userLevel.getLevel();
        boolean leveledUp = newLevel > oldLevel;
        
        return new LevelUpResult(userLevel, xpGain, leveledUp, oldLevel, newLevel);
    }
    
    public UserLevel getUserLevel(String userId) {
        return levelStore.getUserLevel(userId);
    }
    
    public static class LevelUpResult {
        private final UserLevel userLevel;
        private final int xpGained;
        private final boolean leveledUp;
        private final int oldLevel;
        private final int newLevel;
        
        public LevelUpResult(UserLevel userLevel, int xpGained, boolean leveledUp, int oldLevel, int newLevel) {
            this.userLevel = userLevel;
            this.xpGained = xpGained;
            this.leveledUp = leveledUp;
            this.oldLevel = oldLevel;
            this.newLevel = newLevel;
        }
        
        public UserLevel getUserLevel() {
            return userLevel;
        }
        
        public int getXpGained() {
            return xpGained;
        }
        
        public boolean isLeveledUp() {
            return leveledUp;
        }
        
        public int getOldLevel() {
            return oldLevel;
        }
        
        public int getNewLevel() {
            return newLevel;
        }
        
        public String getLevelUpMessage(String username) {
            if (leveledUp) {
                return "🎉 **¡" + username + "** ha subido al nivel **" + newLevel + "**! 🎉";
            }
            return null;
        }
    }
}
