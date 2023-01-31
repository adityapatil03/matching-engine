package org.exchange.model;

public enum Side {

    Buy("B"), Sell("S");

    public final String label;

    Side(String label) {
        this.label = label;
    }
}
