package com.example.scotland_yard_prototype;

import java.util.List;
import java.util.ArrayList;

public class GameClass {

    // Member variables
    private int gameId;
    private String gameName;
    private int mapId;
    private String mapName;
    private String mapThumb;
    private String status;
    private List<PlayerClass> players;

    // Getter and Setter f̶u̶n̶c̶t̶i̶o̶n̶s̶ METHODS

    public int getGameId(){
        return gameId;
    }
    public void setGameId(int gameId){
        this.gameId = gameId;
    }

    public String getGameName(){
        return gameName;
    }
    public void setGameName(String gameName){
        this.gameName = gameName;
    }

    public int getMapId(){
        return mapId;
    }
    public void setMapId(int mapId){
        this.mapId = mapId;
    }

    public String getMapName(){
        return mapName;
    }
    public void setMapName(String mapName){
        this.mapName = mapName;
    }

    public String getMapThumb(){
        return mapThumb;
    }
    public void setMapThumb(String mapThumb){
        this.mapThumb = mapThumb;
    }

    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }

    public List<PlayerClass> getPlayers(){
        return players;
    }
    public void setPlayers(List<PlayerClass> players){
        this.players = players;
    }
}
