package com.discordbot.game;

import java.util.HashMap;
import java.util.Map;

public class TradeManager {
    private Map<String, TradeOffer> activeOffers;

    public TradeManager() {
        this.activeOffers = new HashMap<>();
    }

    // Crea una nueva oferta de intercambio
    public void createOffer(String offerId, String fromUserId, String toUserId, 
                           String fromBallId, String toBallId) {
        TradeOffer offer = new TradeOffer(offerId, fromUserId, toUserId, fromBallId, toBallId);
        activeOffers.put(offerId, offer);
    }

    // Obtiene una oferta activa
    public TradeOffer getOffer(String offerId) {
        return activeOffers.get(offerId);
    }

    // Cancela una oferta
    public void cancelOffer(String offerId) {
        activeOffers.remove(offerId);
    }

    // Verifica si un usuario tiene una oferta pendiente
    public boolean hasActiveTrade(String userId) {
        return activeOffers.values().stream()
                .anyMatch(offer -> offer.getFromUserId().equals(userId) || 
                                  offer.getToUserId().equals(userId));
    }

    // Obtiene todas las ofertas activas
    public java.util.Collection<TradeOffer> getAllOffers() {
        return activeOffers.values();
    }

    // Clase interna para representar una oferta de intercambio
    public static class TradeOffer {
        private String offerId;
        private String fromUserId;
        private String toUserId;
        private String fromBallId;
        private String toBallId;
        private boolean accepted;
        private long createdTimestamp;

        public TradeOffer(String offerId, String fromUserId, String toUserId, 
                         String fromBallId, String toBallId) {
            this.offerId = offerId;
            this.fromUserId = fromUserId;
            this.toUserId = toUserId;
            this.fromBallId = fromBallId;
            this.toBallId = toBallId;
            this.accepted = false;
            this.createdTimestamp = System.currentTimeMillis();
        }

        // Getters
        public String getOfferId() { return offerId; }
        public String getFromUserId() { return fromUserId; }
        public String getToUserId() { return toUserId; }
        public String getFromBallId() { return fromBallId; }
        public String getToBallId() { return toBallId; }
        public boolean isAccepted() { return accepted; }
        public long getCreatedTimestamp() { return createdTimestamp; }

        public void setAccepted(boolean accepted) { this.accepted = accepted; }
    }
}