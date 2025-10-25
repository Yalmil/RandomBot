package com.discordbot.storage;

import com.discordbot.model.RandomBall;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class JsonRandomBallStore implements RandomBallStore {
    private String filePath;
    private Gson gson;
    private Map<String, RandomBall> balls;

    public JsonRandomBallStore(String filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.balls = new HashMap<>();
        loadFromFile();
    }

    private void loadFromFile() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                saveToFile();
                return;
            }

            String json = new String(Files.readAllBytes(Paths.get(filePath)));
            Type type = new TypeToken<Map<String, RandomBall>>(){}.getType();
            Map<String, RandomBall> loaded = gson.fromJson(json, type);
            
            if (loaded != null) {
                balls = loaded;
            }
        } catch (IOException e) {
            System.err.println("Error loading RandomBalls: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try {
            String json = gson.toJson(balls);
            Files.write(Paths.get(filePath), json.getBytes());
        } catch (IOException e) {
            System.err.println("Error saving RandomBalls: " + e.getMessage());
        }
    }

    @Override
    public void addRandomBall(RandomBall ball) {
        balls.put(ball.getId(), ball);
        saveToFile();
    }

    @Override
    public void updateRandomBall(RandomBall ball) {
        balls.put(ball.getId(), ball);
        saveToFile();
    }

    @Override
    public RandomBall getRandomBallById(String ballId) {
        return balls.get(ballId);
    }

    @Override
    public List<RandomBall> getUserRandomBalls(String userId) {
        return balls.values().stream()
                .filter(ball -> ball.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<RandomBall> getAllRandomBalls() {
        return new ArrayList<>(balls.values());
    }

    @Override
    public void removeRandomBall(String ballId) {
        balls.remove(ballId);
        saveToFile();
    }
}