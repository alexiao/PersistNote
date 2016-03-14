package com.dv.persistnote.base;

public class NetworkState {

    public boolean isWifi;
    public boolean isConnected;


    public NetworkState(boolean isWifi, boolean isConnected){
        this.isWifi = isWifi;
        this.isConnected = isConnected;
    }
    
    @Override
    public String toString() {
        return "isWifi = "+isWifi + " isConnected : " + isConnected;
    }
    
}