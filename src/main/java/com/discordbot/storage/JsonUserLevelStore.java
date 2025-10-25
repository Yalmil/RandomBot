
package com.discordbot.storage;

import com.discordbot.model.UserLevel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsonUserLevelStore implements UserLevelStore {
    private final String filePath;
    private final Gson gson;
    private final Map<String, UserLevel> userLevels;
    
    public JsonUserLevelStore(String filePath) {
        this.filePath = filePath;
        this.gson = new Gson();
        this.userLevels = new ConcurrentHashMap<>();
        loadUserLevels();
    }
    
    @Override
    public UserLevel getUserLevel(String userId) {
        return userLevels.getOrDefault(userId, new UserLevel(userId));
    }
    
    @Override
    public void updateUserLevel(UserLevel userLevel) {
        userLevels.put(userLevel.getUserId(), userLevel);
        save();
    }
    
    @Override
    public Map<String, UserLevel> getAllUserLevels() {
        return new ConcurrentHashMap<>(userLevels);
    }
    
    @Override
    public void save() {
        try {
            // Ensure parent directory exists
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            try (Writer writer = new FileWriter(filePath)) {
                gson.toJson(userLevels, writer);
            }
        } catch (IOException e) {
            System.err.println("Failed to save user levels: " + e.getMessage());
        }
    }
    
    private void loadUserLevels() {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, UserLevel>>(){}.getType();
            Map<String, UserLevel> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                userLevels.putAll(loaded);
            }
        } catch (IOException e) {
            System.err.println("Failed to load user levels: " + e.getMessage());
        }
    }
}
