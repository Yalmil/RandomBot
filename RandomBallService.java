package com.discordbot.game;

import com.discordbot.model.RandomBall;
import com.discordbot.storage.RandomBallStore;
import java.util.List;
import java.util.Random;

public class RandomBallService {
    private RandomBallStore store;
    private Random random;

    public RandomBallService(RandomBallStore store) {
        this.store = store;
        this.random = new Random();
    }

    // Añade experiencia a una RandomBall aleatoria del usuario
    public String addRandomExperience(String userId) {
        List<RandomBall> userBalls = store.getUserRandomBalls(userId);
        
        if (userBalls.isEmpty()) {
            return null; // Usuario no tiene RandomBalls
        }

        // Selecciona una RandomBall aleatoria
        RandomBall ball = userBalls.get(random.nextInt(userBalls.size()));
        
        // Añade 10-25 XP aleatoriamente
        int xpGained = 10 + random.nextInt(16);
        boolean leveledUp = ball.addExperience(xpGained);
        
        // Guarda el cambio
        store.updateRandomBall(ball);

        if (leveledUp) {
            String message = "🎉 ¡Tu **" + ball.getCharacterName() + "** subió al nivel **" + ball.getLevel() + "**!";
            
            // Verifica si alcanzó un nivel especial
            if (ball.isSpecialLevel()) {
                message += "\n" + ball.getSpecialEmoji() + " **¡NIVEL ESPECIAL ALCANZADO!** Has desbloqueado un emoji especial.";
            }
            
            return message;
        }

        return null; // No subió de nivel
    }

    // Obtiene las RandomBalls de un usuario
    public List<RandomBall> getUserRandomBalls(String userId) {
        return store.getUserRandomBalls(userId);
    }

    // Obtiene una RandomBall específica por ID
    public RandomBall getRandomBallById(String ballId) {
        return store.getRandomBallById(ballId);
    }

    // Transfiere una RandomBall de un usuario a otro
    public boolean transferRandomBall(String ballId, String fromUserId, String toUserId) {
        RandomBall ball = store.getRandomBallById(ballId);
        
        if (ball == null || !ball.getOwnerId().equals(fromUserId)) {
            return false; // La bola no existe o no pertenece al usuario
        }

        ball.setOwnerId(toUserId);
        store.updateRandomBall(ball);
        return true;
    }

    // Añade una nueva RandomBall al usuario
    public void addRandomBall(String userId, String characterName) {
        String id = java.util.UUID.randomUUID().toString();
        RandomBall ball = new RandomBall(id, characterName, userId);
        store.addRandomBall(ball);
    }
}