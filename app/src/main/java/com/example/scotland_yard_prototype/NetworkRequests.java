package com.example.scotland_yard_prototype;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// This file handles all network requests for Scotland Yard. See methods for description of functionality

public class NetworkRequests {

    // Current server URL to Nick's database.
    // I've made this a member variable to reduce the number of changes that
    // must be made if the URL changes in the future.
    // API endpoints are defined here for the same reason.
    private static final String baseServerURL = "http://www.trinity-developments.co.uk";
    private static final String mapsEndpoint = "maps"; // Maps endpoint
    private static final String gamesEndpoint = "games"; // Games endpoint
    private static final String startEndpoint = "start"; // Start endpoint
    private static final String playersEndpoint = "players"; // Players endpoint
    private static final String movesEndpoint = "moves"; // Moves endpoint

    /// !!! Generic request methods !!! ///

    // Generic method for handling GET requests.
    // See further down in file for specific GET request implementations

    public static String makeGetRequest(String urlString){

        HttpURLConnection connection = null;

        try {
            // Creating URL object
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Request headers
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // Get the HTTP response code (Debugging)
            int responseCode = connection.getResponseCode();
            Log.d("NetworkRequests", "Get Request Response Code: " + responseCode);
            //Log.d("NetworkRequests", "Response Data: " + getResponse(connection));

            return getResponse(connection);

        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();

        }finally {
            // Closing connection after use to free resources.
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    public static Bitmap downloadImage(String urlString){

        HttpURLConnection connection = null;
        Bitmap bitmap = null;

        try {
            // Creating URL object
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Request headers (optional for image)
            connection.setRequestProperty("Accept", "image/*"); // Specify image type (optional)

            // Get the HTTP response code (for debugging)
            int responseCode = connection.getResponseCode();
            Log.d("NetworkRequests", "Image Download Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Get the InputStream and decode it into a Bitmap
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } else {
                Log.e("NetworkRequests", "Failed to download image, Response Code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("NetworkRequests", "Error downloading image: " + e.getMessage());
        } finally {
            // Closing connection after use to free resources
            if (connection != null) {
                connection.disconnect();
            }
        }

        return bitmap;  // Return the downloaded Bitmap (null if failure) TODO: align more with other network functions
    }


    // Generic method for handling POST requests
    // See further down in file for specific POST request implementations
    public static String makePostRequest(String urlString, String jsonData){

        HttpURLConnection connection = null;

        try {
            // Creating URL object
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            // Request Headers
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // Writing JSON data to output stream
            try (OutputStream os = connection.getOutputStream()){
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input,0,input.length);

            }

            // Get the HTTP response code (Debugging)
            int responseCode = connection.getResponseCode();
            Log.d("NetworkRequests", "Response Code: " + responseCode);

            return getResponse(connection);

        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();

        }finally {
            // Closing connection after use to free resources.
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Generic method for handling PATCH requests
    // See further down in file for specific PATCH request implementations
    public static String makePatchRequest(String urlString){

        HttpURLConnection connection = null;

        try {
            // Creating URL object
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PATCH");

            // Request headers
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // Get the HTTP response code (Debugging)
            int responseCode = connection.getResponseCode();
            Log.d("NetworkRequests", "Patch Request Response Code: " + responseCode);
            //Log.d("NetworkRequests", "Response Data: " + getResponse(connection));

            return getResponse(connection);

        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();

        }finally {
            // Closing connection after use to free resources.
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    // Generic method for handling PATCH requests
    // See further down in file for specific PATCH request implementations

    public static String makeDeleteRequest(String urlString){
        HttpURLConnection connection = null;

        try {
            // Creating URL object
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            // Request headers
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // Get the HTTP response code (Debugging)
            int responseCode = connection.getResponseCode();
            Log.d("NetworkRequests", "Delete Request Response Code: " + responseCode);
            //Log.d("NetworkRequests", "Response Data: " + getResponse(connection));

            return getResponse(connection);

        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();

        }finally {
            // Closing connection after use to free resources.
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    // Helper method to handle reading response from Nick's server
    private static String getResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();

        // JSON Object to store response code
        JSONObject jsonResponse = new JSONObject();

        if (responseCode >= 200 && responseCode < 300){
            // Success (Codes between 200 & 299 inclusive)
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                String inputLine;
                while ((inputLine = in.readLine()) != null){
                    response.append(inputLine);
                }
            }

            try {
                // Append success status response
                jsonResponse.put("responseStatus", "Success");
                jsonResponse.put("responseCode", "Request succeeded with code: " + responseCode);
                jsonResponse.put("data", response.toString());
            } catch (Exception e){
                e.printStackTrace();
            }


        }else {
            // Handles failure codes (404, etc)
            //response.append("Request failed with code: ").append(responseCode);

            try {
                jsonResponse.put("responseStatus", "Failure");
                jsonResponse.put("responseCode", "Request failed with code: " + responseCode);
                jsonResponse.put("data", response.toString());
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        Log.d("NetworkRequests", "Post Request Response Data: " + response.toString());
        return jsonResponse.toString();
    }


    /// !!! Specific API request methods !!! ///

    // GET requests

    /**
     * Returns list of available maps
     * @return JSON consisting of "maps" list with individual maps within. Map keys: "mapId" : Id of map, "mapName": name of map, "mapThumb": endpoint to map thumbnail.
     */
    public static String getMaps(){

        // This method handles getting a list of available maps.

        // Forming of request URL
        // Example URL: http://www.trinity-developments.co.uk/maps
        String requestURL = baseServerURL + "/" + mapsEndpoint;

        // Makes the GET request to the endpoint using generic GET request method.
        return makeGetRequest(requestURL);
    }

    /**
     * Returns details of a specific map
     * @param mapId The Id of the desired map
     * @return JSON consisting of single map data. Map keys: "mapId": Id of map, "mapName": name of map, "mapImage": endpoint to map image, "mapThumb": endpoint to map thumbnail, "mapWidth": width in pixels of map, "mapHeight": height in pixels of map, "locations": (list of locations, consisting of the following keys - "location": location Id, "xPos": x coordinate of location, "yPos": y coordinate of location), "connections": list of connections consisting of the following keys - "locationA": Id of location A, "locationB" Id of location B, "ticket": type of ticket required)
     */
    public static String getMapSingle(int mapId){

        // This method handles getting the information of a specific map.

        // Forming of request URL
        // Example URL: http://www.trinity-developments.co.uk/maps/1
        String requestURL = baseServerURL + "/" + mapsEndpoint + "/" + mapId;

        // Makes the GET request to the endpoint using generic GET request method.
        return makeGetRequest(requestURL);
    }

    /**
     * Returns list of games with open lobbies
     * @return JSON consisting of a list of games with the following keys - "gameId": Id of the game, "gameName": name of game lobby, "mapId": Id of map used in lobby, "mapName": Name of map, "mapThumb": link to thumbnail of map, "status": Status of game, "players": List of players present in game with the following keys - "playerId": Id of the player, "playerName": Name of player.
     */
    public static String findLobbies(){

        // This method handles finding open lobbies.

        // Forming of request URL
        // Example URL: http://www.trinity-developments.co.uk/games
        String requestURL = baseServerURL + "/" + gamesEndpoint;

        // Makes the GET request to the endpoint using generic GET request method.
        return makeGetRequest(requestURL);
    }

    /**
     * Returns the status of a given game
     * @param gameId The Id of the game desired
     * @return JSON consisting of data from a game with the following keys - "gameId": Id of the game, "mapId": Id of the map used in the game, "state": Current state of the game, "winner": Winner of the game (if applicable), "round": round number, "length": length of the game in rounds, "players": list of players present in the game with the following keys - "playerId": Id of the player, "playerName": Name of player, "colour": Colour of the player, "location": Present location of the player.
     */
    public static String getGameState(int gameId){

        // This method handles getting game information for a single game

        // Forming of request URL
        // Example URL: http://www.trinity-developments.co.uk/games/13
        String requestURL = baseServerURL + "/" + gamesEndpoint + "/" + gameId;

        // Makes the GET request to the endpoint using generic GET request method.
        return makeGetRequest(requestURL);
    }

    /**
     * Returns the current status of a given player
     * @param playerId The Id of the specific player
     * @return JSON consisting of information regarding a specific player with the following keys - "playerId": Id of the player, "playerName": Name of the player, "role": Role of the player, "colour": Colour of the player, "location": Current location of the player, "yellow": Number of yellow tickets, "green": Number of green tickets, "red": Number of red tickets, "black": number of black tickets, "2x": Number of double move tickets.
     */
    public static String getPlayerStatus(int playerId){
        // This method handles getting the information of a specific player.

        // Forming of request URL
        // Example URL: http://www.trinity-developments.co.uk/games/13/players/201
        // WRONG String requestURL = baseServerURL + "/" + gamesEndpoint + "/" + gameId + "/" + playersEndpoint + "/" + playerId;

        // Forming of request URL
        // Example URL: http://www.trinity-developments.co.uk/players/201
        String requestURL = baseServerURL + "/" + playersEndpoint + "/" + playerId;

        // Makes the GET request to the endpoint using generic GET request method.
        return makeGetRequest(requestURL);
    }

    /**
     * Returns the move history of a given player
     * @param playerId The Id of the specific player
     * @return JSON consisting of move history of a specific player with the following keys - "playerId": Id of the player, "startLocation": Starting location of the player, "moves": List of moves made by the player with the following keys - "moveId": Id of the move, "round": Round in which the move was made, "ticket": Ticket used, "destination": Destination that has been moved to.
     */
    public static String getPlayerMoveHistory(int playerId){
        // This method handles getting a specific player's move history.

        // Forming of request URL
        // Example URL: http://www.trinity-developments.co.uk/players/201/moves
        String requestURL = baseServerURL +  "/" + playersEndpoint + "/" + playerId + "/" + movesEndpoint;

        // Makes the GET request to the endpoint using generic GET request method.
        return makeGetRequest(requestURL);
    }

    // POST requests

    /**
     * Creates a new game and opens the lobby.
     * @param name The name of the game to be created
     * @param mapId The desired map
     * @param gameLength either "short" or "long"
     * @return Successful call Returns the following as part of a JSON: "message" : "Game created", "gameId" : Id of created game, "name" : Name of created game, "mapId" : Id of map used in game, and "state" : "Open".
     */
    public static String createGame(String name, int mapId , String gameLength){

        // This method handles creating new game lobbies.
        // The JSON request body is formed within this method.

        // Creating JSON body:
        String jsonData = "{\n" +
                "\"name\": \"" + name + "\",\n" +
                "\"mapId\": " + mapId + ",\n" +
                "\"gameLength\": \"" + gameLength + "\"\n" +
                "}";

        // Forming of request URL
        // Example URL: http://www.trinity-developments.co.uk/maps
        String requestURL = baseServerURL + "/" + gamesEndpoint;

        // Makes the POST request to the endpoint using generic POST request method.
        return makePostRequest(requestURL, jsonData);
    }

    /**
     * Joins
     * @param playerName Desired player name
     * @param gameId The Id of the specific game to be joined
     * @return
     */
    public static String joinGame(String playerName, int gameId){

        // This method handles joining game lobbies.
        // The JSON request body is formed within this method.

        // Creating JSON body:
        String jsonData = "{\n" +
                "\"playerName\": \"" + playerName + "\"\n" +
                "}";

        // Forming of request URL
        // Example URL: http://www.trinity-developments.co.uk/1/players
        String requestURL = baseServerURL + "/" + gamesEndpoint + "/" + gameId + "/" + playersEndpoint;

        Log.d("NetworkRequests", "RequestURL: " + requestURL);

        // Makes the POST request to the endpoint using generic POST request method.
        return makePostRequest(requestURL, jsonData);

    }

    /**
     * Attempts to allow a player to make a move
     * @param playerId The Id of the specific player who is attempting to move
     * @param gameId The Id of the specific game in which a move is to be made
     * @param ticket The type of ticket that is attempting to be used
     * @param destination The location to which the player is attempting to move
     * @return
     */
    public static String playerMakeMove(int playerId,int gameId, String ticket, int destination){

        // This method handles allowing a single player to make a move.
        // The JSON request body is formed within this method.

        // Creating JSON body
        String jsonData = "{\n" +
                "\"gameID\": " + gameId + ",\n" +
                "\"ticket\": \"" + ticket.toLowerCase() + "\",\n" +  // Make sure ticket is lowercase
                "\"destination\": " + destination + "\n" +
                "}";

        // Forming of request URL
        // Example URL: http://www.trinity-developments.co.uk/players/201/moves
        String requestURL = baseServerURL + "/" + playersEndpoint + "/" + playerId + "/" + movesEndpoint;

        // Makes the POST request to the endpoint using generic GET request method.
        //Log.d("NetworkRequests", "JSONData: " + jsonData);
        //Log.d("NetworkRequests", "RequestURL: " + requestURL);
        return makePostRequest(requestURL, jsonData);
    }

    // PATCH requests

    /**
     * Closes the lobby and starts the game
     * @param gameId The Id of the game to be opened
     * @param playerId The Id of the player attempting to open the game
     * @return
     */
    public static String startGame(int gameId, int playerId){

        // This method handles starting games from within lobbies.
        // The JSON request body is formed within this method.

        // Forming of request URL
        // Example URL: http://www.trinity-developments.co.uk/games/0/start/201
        String requestURL = baseServerURL + "/" + gamesEndpoint + "/" + gameId + "/" + startEndpoint + "/" + playerId;

        // Makes the PATCH request to the endpoint using generic POST request method.
        return makePatchRequest(requestURL);
    }

    // DELETE requests

    /**
     * Removes a player from an ongoing game
     * @param playerId The Id of the player to be removed from the game
     * @return
     */
    public static String removeFromGame( int playerId){

        // This method handles kicking/removing players from ongoing games.
        // I'm uncertain if this works on lobbies.
        // The JSON request body is formed within this method.
        // Forming of request URL
        // Example URL: http://www.trinity-developments.co.uk/players/1
        String requestURL = baseServerURL + "/" + playersEndpoint + "/" + playerId;

        return makeDeleteRequest(requestURL);

    }

}


