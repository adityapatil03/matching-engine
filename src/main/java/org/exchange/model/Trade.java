package org.exchange.model;

public class Trade {

    public Trade(String aggressiveOrderId, String restingOrderId, int matchPrice, int volume) {
        this.aggressiveOrderId = aggressiveOrderId;
        this.restingOrderId = restingOrderId;
        this.matchPrice = matchPrice;
        this.volume = volume;

    }

    private String aggressiveOrderId;

    private String restingOrderId;

    private Integer matchPrice;

    private Integer volume;

    public String getAggressiveOrderId() {
        return aggressiveOrderId;
    }

    public void setAggressiveOrderId(String aggressiveOrderId) {
        this.aggressiveOrderId = aggressiveOrderId;
    }

    public String getRestingOrderId() {
        return restingOrderId;
    }

    public void setRestingOrderId(String restingOrderId) {
        this.restingOrderId = restingOrderId;
    }

    public Integer getMatchPrice() {
        return matchPrice;
    }

    public void setMatchPrice(Integer matchPrice) {
        this.matchPrice = matchPrice;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "trade " + this.getAggressiveOrderId() + "," + this.getRestingOrderId() + "," + this.getMatchPrice() + "," + this.getVolume();
    }
}
