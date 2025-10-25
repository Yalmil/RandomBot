package com.discordbot.storage;

import java.util.Map;
import java.util.Set;

public interface UserCollectionStore {
    Set<String> getUserCollection(String userId);
    Map<String, Integer> getUserCollectionWithCounts(String userId);
    boolean addToCollection(String userId, String characterName);
    void save();
}