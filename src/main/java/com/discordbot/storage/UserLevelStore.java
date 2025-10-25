
package com.discordbot.storage;

import com.discordbot.model.UserLevel;
import java.util.Map;

public interface UserLevelStore {
    UserLevel getUserLevel(String userId);
    void updateUserLevel(UserLevel userLevel);
    Map<String, UserLevel> getAllUserLevels();
    void save();
}
