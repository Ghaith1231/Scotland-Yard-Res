package com.example.scotland_yard_prototype;

public class PlayerClass {

    // Member variables
    private int playerId;
    private String playerName;
    private String gameName;
    private int gameId;
    private String role;
    private int startLocation;
    private int currentLocation;

    // Setter and Getter methods

    public int getPlayerId(){
        return playerId;
    }
    public void setPlayerId(int playerId){
        this.playerId = playerId;
    }

    public String getPlayerName(){
        return playerName;
    }
    public void setPlayerName(String playerName){
        this.playerName = playerName;
    }

    public String getGameName(){ return gameName;}
    public void setGameName(String gameName){ this.gameName = gameName;}

    public int getGameId(){ return gameId; }
    public void setGameId(int gameId){ this.gameId = gameId; }

    public String getRole(){ return role; }
    public void setRole(String role){ this.role = role; }

    public int getStartLocation(){ return startLocation; }
    public void setStartLocation(int startLocation){ this.startLocation = startLocation;}

    public int setCurrentLocation(){ return currentLocation; }
    public void getCurrentLocation(int currentLocation){ this.currentLocation = currentLocation; }
}
