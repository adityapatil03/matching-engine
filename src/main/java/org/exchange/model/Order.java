package org.exchange.model;

public interface Order {

    String getOrderId();

    Side getSide();

    int getPrice();

    int getVolume();

    long getTime();

    void setVolume(int volume);

    void setTime(long time);

}
