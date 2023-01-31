package org.exchange.model;

public class LimitOrder implements Order {

    private long time;

    private String orderId;

    private Side side;

    private int price;

    private int volume;

    public LimitOrder() {
        this.time = System.nanoTime();
    }

    public LimitOrder(String orderId, Side side, int price, int volume) {
        this.orderId = orderId;
        this.side = side;
        this.price = price;
        this.volume = volume;
        this.time = System.nanoTime();
    }

    @Override
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    @Override
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public void setVolume(int volume) {
        this.volume = volume;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public void setTime(long time) {
        this.time = time;
    }

}
