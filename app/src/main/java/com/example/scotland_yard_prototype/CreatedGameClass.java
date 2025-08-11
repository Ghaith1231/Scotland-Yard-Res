package com.example.scotland_yard_prototype;

import java.io.Serializable; // Importing this to allow JSON data to be serialised to pass between activities.

// PROBABLY DEPRECATED - TODO: Should be replaced by GameClass

// Model class to represent JSON data for a newly created game.
public class CreatedGameClass implements Serializable{

    // Member variables
    private String message;
    private int gameId;
    private String name;
    private int mapId;
    private String state;

    // Constructor
    public CreatedGameClass(String message, int gameId, String name, int mapId, String state){
        this.message = message;
        this.gameId = gameId;
        this.name = name;
        this.mapId = mapId;
        this.state = state;
    }

    // Getters & Setters
    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message = message;
    }

    public int getGameId(){
        return gameId;
    }
    public void setGameId(int gameId){
        this.gameId = gameId;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public int getMapId(){
        return mapId;
    }
    public void setMapId(int mapId){
        this.mapId = mapId;
    }

    public String getState(){
        return state;
    }
    public void setState(String state){
        this.state = state;
    }
}
