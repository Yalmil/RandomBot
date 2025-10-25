package com.discordbot.storage;

import com.discordbot.model.RandomBall;
import java.util.List;

public interface RandomBallStore {
    void addRandomBall(RandomBall ball);
    void updateRandomBall(RandomBall ball);
    RandomBall getRandomBallById(String ballId);
    List<RandomBall> getUserRandomBalls(String userId);
    List<RandomBall> getAllRandomBalls();
    void removeRandomBall(String ballId);
}