package com.discordbot.storage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JsonUserCollectionStore implements UserCollectionStore {
    private final String filePath;
    private final Gson gson;
    private final Map<String, Map<String, Integer>> collections;
    
    public JsonUserCollectionStore(String filePath) {
        this.filePath = filePath;
        this.gson = new Gson();
        this.collections = new ConcurrentHashMap<>();
        loadCollections();
    }
    
    @Override
    public Set<String> getUserCollection(String userId) {
        return collections.getOrDefault(userId, new HashMap<>()).keySet();
    }
    
    @Override
    public Map<String, Integer> getUserCollectionWithCounts(String userId) {
        return new HashMap<>(collections.getOrDefault(userId, new HashMap<>()));
    }
    
    @Override
    public boolean addToCollection(String userId, String characterName) {
        Map<String, Integer> userCollection = collections.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
        boolean isNew = !userCollection.containsKey(characterName);
        userCollection.put(characterName, userCollection.getOrDefault(characterName, 0) + 1);
        save();
        return isNew;
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
                gson.toJson(collections, writer);
            }
        } catch (IOException e) {
            System.err.println("Failed to save collections: " + e.getMessage());
        }
    }
    
    private void loadCollections() {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        
        try (Reader reader = new FileReader(file)) {
            // Try to load as new format first (Map<String, Map<String, Integer>>)
            Type newType = new TypeToken<Map<String, Map<String, Integer>>>(){}.getType();
            try {
                Map<String, Map<String, Integer>> loaded = gson.fromJson(reader, newType);
                if (loaded != null) {
                    collections.putAll(loaded);
                    return;
                }
            } catch (Exception e) {
                // If new format fails, try old format
            }
            
            // Fallback to old format (Map<String, Set<String>>) and convert
            reader.close();
            try (Reader reader2 = new FileReader(file)) {
                Type oldType = new TypeToken<Map<String, Set<String>>>(){}.getType();
                Map<String, Set<String>> oldLoaded = gson.fromJson(reader2, oldType);
                if (oldLoaded != null) {
                    for (Map.Entry<String, Set<String>> entry : oldLoaded.entrySet()) {
                        Map<String, Integer> userCollection = new ConcurrentHashMap<>();
                        for (String character : entry.getValue()) {
                            userCollection.put(character, 1); // Convert old format to count of 1
                        }
                        collections.put(entry.getKey(), userCollection);
                    }
                    // Save in new format
                    save();
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load collections: " + e.getMessage());
        }
    }
}