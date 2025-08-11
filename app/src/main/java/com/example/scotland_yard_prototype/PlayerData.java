package com.example.scotland_yard_prototype;

// This class saves and retrieves a player's data using SharedPreferences

import android.content.SharedPreferences;
import android.content.Context;

public class PlayerData {

    private Context context;

    // Constructor to pass context in from activities
    public PlayerData(Context context){
        this.context = context;
    }

    // Constructor function, in case I am able to pass/set all data at once
    public void setPlayerData(int playerId, String playerName, String gameName, int gameId, String role, int startLocation, int currentLocation){}

    public void setPlayerId(int playerId){
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("playerId", playerId);
        editor.apply();

    }
    public int getPlayerId(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("playerId", -1);
    }

    public void setPlayerName(String playerName){
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("playerName", playerName);
        editor.apply();
    }
    public String getPlayerName(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        return sharedPreferences.getString("playerName", "");
    }

    public void setGameName(String gameName){
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("gameName", gameName);
        editor.apply();
    }
    public String getGameName(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        return sharedPreferences.getString("gameName", "");
    }

    public void setGameId(int gameId){
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("gameId", gameId);
        editor.apply();
    }
    public int getGameId(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("gameId", -1);
    }

    public void setMapId(int mapId){
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("mapId", mapId);
        editor.apply();
    }
    public int getMapId(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("mapId", -1);
    }

    public void setRole(String role){
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("role", role);
        editor.apply();
    }
    public String getRole(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        return sharedPreferences.getString("role", "");
    }

    public void setStartLocation(int startLocation){
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("startLocation", startLocation);
        editor.apply();
    }
    public int getStartLocation(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("startLocation", -1);
    }

    public void setCurrentLocation(int currentLocation){
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("currentLocation", currentLocation);
        editor.apply();
    }
    public int getCurrentLocation(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("currentLocation", -1);
    }

}
