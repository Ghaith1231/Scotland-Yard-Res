package com.example.scotland_yard_prototype;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Class to handle JSON parsing tasks to clean up other files

public class JSONParser {

    // Parse "Open game lobbies" JSON.
    public List<GameClass> parseGames(JSONObject jsonObject){
        List<GameClass> gamesList = new ArrayList<>();

        try {
            // Parsing JSON object
            //JSONObject jsonObject = new JSONObject(jsonResponse);

            // Get the array of Games from JSON response.
            JSONArray gamesArray = jsonObject.getJSONArray("games");

            // Loop through each game object in the gamesArray object
            for (int i = 0; i < gamesArray.length(); i++){
                JSONObject gameObject = gamesArray.getJSONObject(i);

                // Create a new GameClass object to store game information in.
                GameClass game = new GameClass();

                game.setGameId(gameObject.getInt("gameId"));
                game.setGameName(gameObject.getString("gameName"));
                game.setMapId(gameObject.getInt("mapId"));
                game.setMapName(gameObject.getString("mapName"));
                game.setMapThumb(gameObject.getString("mapThumb"));
                game.setStatus(gameObject.getString("status"));

                // Get the "players" array for this Game
                JSONArray playersArray = gameObject.getJSONArray("players");
                List<PlayerClass> playerList = new ArrayList<>();

                // Loop through each PlayerClass object in the "Players" array
                for (int j = 0; j < playersArray.length(); j++){
                    JSONObject playerObject = playersArray.getJSONObject(j);
                    PlayerClass player = new PlayerClass();
                    player.setPlayerId(playerObject.getInt("playerId"));
                    player.setPlayerName(playerObject.getString("playerName"));
                    playerList.add(player);
                }

                // Sets the playerList in the GameClass object
                game.setPlayers(playerList);

                // Add GameClass object to Games list
                gamesList.add(game);
            }
        }catch (JSONException e) { // Handle various errors. TODO: expand this if time allows.
            e.printStackTrace();
        }
        return gamesList;
    }
}
