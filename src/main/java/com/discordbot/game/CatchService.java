package com.discordbot.game;

import com.discordbot.storage.UserCollectionStore;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.text.Normalizer;

public class CatchService {
    private final UserCollectionStore collectionStore;
    private final LevenshteinDistance levenshtein;
    private final java.util.Map<String, java.util.List<String>> alternativeNames;
    
    public CatchService(UserCollectionStore collectionStore) {
        this.collectionStore = collectionStore;
        this.levenshtein = new LevenshteinDistance();
        this.alternativeNames = initializeAlternativeNames();
    }
    
    private java.util.Map<String, java.util.List<String>> initializeAlternativeNames() {
        java.util.Map<String, java.util.List<String>> alternatives = new java.util.HashMap<>();
        
        alternatives.put("AureComay", java.util.Arrays.asList("Aure", "Comay", "Aure tamal", "Aure comal"));
        alternatives.put("Abricraft", java.util.Arrays.asList("Abrilag", "Abri", "Abrucraft", "Abrulag"));
        alternatives.put("SaantyBl", java.util.Arrays.asList("Saanty", "Saantygod"));
        alternatives.put("Yalmil", java.util.Arrays.asList("Yalimil", "Yalmil programando"));
        alternatives.put("Pazmania", java.util.Arrays.asList("Paz", "guerra"));
        alternatives.put("Channel", java.util.Arrays.asList("Calculadora promedia", "Canal", "Canal amigo"));
        alternatives.put("Nickname", java.util.Arrays.asList("Nick"));
        alternatives.put("Nienie", java.util.Arrays.asList("Hermana de aure", "No se che"));
        
        return alternatives;
    }
    
    public CatchResult attemptCatch(String userId, String inputName, String correctName) {
        if (isNameMatch(inputName, correctName)) {
            boolean isNew = collectionStore.addToCollection(userId, correctName);
            return new CatchResult(true, isNew, correctName);
        }
        return new CatchResult(false, false, inputName);
    }
    
    private boolean isNameMatch(String input, String correct) {
        String normalizedInput = normalize(input);
        String normalizedCorrect = normalize(correct);
        
        // Check exact match with original name
        if (normalizedInput.equals(normalizedCorrect)) {
            return true;
        }
        
        // Check exact match with alternative names
        java.util.List<String> alternatives = alternativeNames.get(correct);
        if (alternatives != null) {
            for (String alternative : alternatives) {
                if (normalizedInput.equals(normalize(alternative))) {
                    return true;
                }
            }
        }
        
        // Check fuzzy match with original name
        int threshold = normalizedCorrect.length() <= 7 ? 2 : 3;
        int distance = levenshtein.apply(normalizedInput, normalizedCorrect);
        if (distance <= threshold) {
            return true;
        }
        
        // Check fuzzy match with alternative names
        if (alternatives != null) {
            for (String alternative : alternatives) {
                String normalizedAlternative = normalize(alternative);
                int altThreshold = normalizedAlternative.length() <= 7 ? 2 : 3;
                int altDistance = levenshtein.apply(normalizedInput, normalizedAlternative);
                if (altDistance <= altThreshold) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private String normalize(String text) {
        if (text == null) return "";
        
        // Remove diacritics, convert to lowercase, trim whitespace
        String normalized = Normalizer.normalize(text.trim().toLowerCase(), Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
    
    public static class CatchResult {
        private final boolean success;
        private final boolean isNewCatch;
        private final String name;
        
        public CatchResult(boolean success, boolean isNewCatch, String name) {
            this.success = success;
            this.isNewCatch = isNewCatch;
            this.name = name;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public boolean isNewCatch() {
            return isNewCatch;
        }
        
        public String getName() {
            return name;
        }
        
        public String getResponseMessage() {
            if (!success) {
                return name + " Hey you no se que mas decir porque no soy gringo pero, ese no es nombre correcto";
            }
            
            if (isNewCatch) {
                return "Has atrapado a " + name + "\n" + name + " ha sido añadido a tu colección";
            } else {
                return "Has atrapado a " + name + "\nYa lo tenías en tu colección";
            }
        }
    }
}