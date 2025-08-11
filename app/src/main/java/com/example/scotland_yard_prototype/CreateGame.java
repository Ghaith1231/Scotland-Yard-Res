package com.example.scotland_yard_prototype;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGame extends AppCompatActivity implements AsyncClass.CreateGameTaskListener, AsyncClass.JoinGameTaskListener, AsyncClass.GetMapsTaskListener {

    // Instance of "PlayerData" class. This is necessary to provide it "Context"
    PlayerData playerData = new PlayerData(this);

    private EditText playerNameText;
    private EditText gameNameText;
    private EditText mapChoiceText;
    private Spinner mapChoiceDropdown;
    private Spinner gameLengthDropdown;
    private EditText gameLengthText;
    private TextView errorText;

    private Map<String, Integer> mapChoices = new HashMap<>();
    private Map<String, String> gameLengthChoices = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_game);

        Button returnButton = findViewById(R.id.returnToMain);

        returnButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateGame.this, MainActivity.class);
                startActivity(intent); // Starts the "Main Activity" activity
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        playerNameText = findViewById(R.id.playerName);
        gameNameText = findViewById(R.id.gameName);
        //mapChoiceText = findViewById(R.id.mapChoice);
        mapChoiceDropdown = findViewById(R.id.mapNumberDropdown);
        gameLengthDropdown = findViewById(R.id.gameLengthDropdown);
        //gameLengthText = findViewById(R.id.gameLength);
        errorText = findViewById(R.id.errorText);

        new AsyncClass.GetMapsTask(CreateGame.this).execute();

        setupGameLengthDropdown();

        Button createNewGameButton = findViewById(R.id.createNewGame);

        createNewGameButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                // Checks if player name field or game name field is empty (Resulting in failed game creation)
                if (!playerNameText.getText().toString().isEmpty()){
                    //errorText.setText("Player name cannot be empty.");
                    if (!gameNameText.getText().toString().isEmpty()){ //TODO: create game based on game name entered
                        errorText.setText("Game Name Accepted.");

                        String playerName = playerNameText.getText().toString();
                        String gameName = gameNameText.getText().toString();
                        String mapName = (String) mapChoiceDropdown.getSelectedItem();
                        int mapNumber = mapChoices.get(mapName);
                        String gameLengthChoice = (String) gameLengthDropdown.getSelectedItem();
                        String gameLengthValue = gameLengthChoices.get(gameLengthChoice);

                        new AsyncClass.CreateGameTask(gameName, mapNumber, gameLengthValue, CreateGame.this).execute();
                    }
                    else {
                        errorText.setText("Game Name cannot be blank.");
                    }

                }
                else {
                    errorText.setText("Player name cannot be empty.");
                }

                //Intent intent = new Intent(MainActivity.this, CreateGame.class);
                //startActivity(intent); // Starts the "Create Game" activity
            }
        });
    }

    public void setupGameLengthDropdown(){

        // populate hasmap with values for "Short Game" and "Long Game"
        gameLengthChoices.put("Short Game - 13 rounds", "short");
        gameLengthChoices.put("Long Game - 24 rounds", "long");

        // Populate the Dropdown with the keys
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gameLengthChoices.keySet().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameLengthDropdown.setAdapter(adapter);
    }

    // Handling completion of CreateGameTask
    @Override
    public void onCreateGameTaskCompleted(JSONObject jsonResponse){

        // TODO: Join created game lobby

        try {
            // Process the JSON response.
            if (jsonResponse.has("responseStatus")){
                Log.d("CreateGame", "Response Code: " + jsonResponse.getString("responseCode"));

                String responseStatus = jsonResponse.getString("responseStatus");

                if (true) {

                    String dataString = jsonResponse.getString("data");

                    // Retrieve "data" as a JSONObject
                    JSONObject dataObject = new JSONObject(dataString);

                    // Retrieving data from "data" object
                    String message = dataObject.getString("message");
                    int gameId = dataObject.getInt("gameId");
                    String name = dataObject.getString("name");
                    int mapId = dataObject.getInt("mapId");
                    String state = dataObject.getString("state");

                    // Set game name in sharedPreferences
                    playerData.setGameName(name);

                    playerData.setMapId(mapId);

                    // Attempt to join the lobby as a new player
                    new AsyncClass.JoinGameTask(playerNameText.getText().toString(), gameId, CreateGame.this).execute();

                } else if (responseStatus.equals("failure")) {
                    // Handle failure case
                }else {
                    // Handle unexpected response
                }


            }

            //String message = jsonResponse.getString("message");
            //int gameId = jsonResponse.getInt("gameId");
            //String name = jsonResponse.getString("name");
            //int mapId = jsonResponse.getInt("mapId");
            //String state = jsonResponse.getString("state");

            // TODO: save this data somewhere, and/or use it to create a lobby.

        } catch (JSONException e){
            e.printStackTrace();
            // TODO: Error handling.
        }

        //Intent intent = new Intent(CreateGame.this, GameLobby.class);
        //intent.putExtra("game_data", testGame);

        //startActivity(intent);

    }

    // Handling failure of CreateGameTask
    @Override
    public void onCreateGameTaskError(String errorMessage){

    }



    // Handling success of JoinGameTask
    @Override
    public void onJoinGameTaskCompleted(JSONObject jsonResponse){

        try {
            // Process the JSON response.
            if (jsonResponse.has("responseStatus")){
                Log.d("CreateGame", "Response Code: " + jsonResponse.getString("responseCode"));

                String responseStatus = jsonResponse.getString("responseStatus");

                if (true) {

                    String dataString = jsonResponse.getString("data");

                    // Retrieve "data" as a JSONObject
                    JSONObject dataObject = new JSONObject(dataString);

                    // Retrieve "data" as a JSONObject
                    //JSONObject dataObject = jsonResponse.getJSONObject("data");

                    // Retrieving data from "data" object
                    String message = dataObject.getString("message");
                    int gameId = dataObject.getInt("gameId");
                    //int mapId = dataObject.getInt("mapId");
                    int playerId = dataObject.getInt("playerId");
                    String playerName = dataObject.getString("playerName");
                    String role = dataObject.getString("role");
                    int startLocation = dataObject.getInt("startLocation");

                    // Set sharedPreferences key/value pairs for aspects of player data
                    playerData.setPlayerId(playerId);
                    playerData.setPlayerName(playerName);
                    //playerData.setGameName();
                    playerData.setGameId(gameId);
                    //playerData.setMapId(mapId);
                    playerData.setRole(role);
                    playerData.setStartLocation(startLocation);
                    playerData.setCurrentLocation(startLocation);

                    // Switch to lobby
                    Intent intent = new Intent(CreateGame.this, GameLobby.class);
                    startActivity(intent); // Starts the "Lobby" activity


                } else if (responseStatus.equals("failure")) {
                    // Handle failure case
                }else {
                    // Handle unexpected response
                }
            }

        } catch (JSONException e){
            e.printStackTrace();
            // TODO: Error handling.
        }


    }

    // Handling failure of JoinGameTask
    @Override
    public void onJoinGameTaskError(String errorMessage){

    }

    // Handling population of "mapChoice" dropdown
    @Override
    public void onGetMapsTaskCompleted(JSONObject jsonResponse){

        try {

            if (jsonResponse.has("data")) {
                // Get "data" component
                String dataString = jsonResponse.getString("data");

                // Parse "data" back into JSONObject
                //JSONObject jsonData = new JSONObject(dataString);
                JSONArray mapsArray = new JSONArray(dataString);
                //Log.d("JoinGame", jsonData.toString());
                Log.d("JoinGame", mapsArray.toString());


                // Loop through available maps
                if (true) { // TODO: Remove if statement
                    // Cast as array
                    //JSONArray mapsArray = jsonData.getJSONArray("maps");

                    // Loop through maps array
                    for (int i = 0; i< mapsArray.length(); i++) {
                        JSONObject mapObject = mapsArray.getJSONObject(i);

                        // Extract map name and map ID from map object
                        // TODO: Expand this to display mapThumb
                        String mapName = mapObject.getString("mapName");
                        int mapId = mapObject.getInt("mapId");

                        Log.d("Map", "Map Name: " + mapName + ", Map ID: " + mapId);

                        // Put mapName and mapId into hashmap for later usage
                        mapChoices.put(mapName, mapId);
                    }

                    // Use ArrayAdapter to populate map choice dropdown
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mapChoices.keySet().toArray(new String[0]));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mapChoiceDropdown.setAdapter(adapter);

                }

            }

            // TODO: save this data somewhere, and/or use it to create a lobby.

        } catch (Exception e){
            e.printStackTrace();
            // TODO: Error handling.
        }

    }

    @Override
    public void onGetMapsTaskError(String errorMessage){

    }








    // !!! DEPRECATED BELOW, WILL DELETE !!! //
    // Method to trigger the AsyncTask for creating a game through a POST request.
    // Todo: Move to dedicated class (For cleanliness and reusability)

    private void createGame(String name, int mapId, String gameLength){

        new CreateGameTask(name, mapId, gameLength, new CreateGameTask.TaskListener(){
            @Override
            public void onTaskCompleted(JSONObject jsonResponse){
                // Handling of the response received from the POST request when it completes.
                // Due to this being Asynchronous I can't simply return the JSON when I call the method.

                try {
                    // Process the JSON response.
                    String message = jsonResponse.getString("message");
                    int gameId = jsonResponse.getInt("gameId");
                    String name = jsonResponse.getString("name");
                    int mapId = jsonResponse.getInt("mapId");
                    String state = jsonResponse.getString("state");

                    // TODO: save this data somewhere, and/or use it to create a lobby.

                } catch (JSONException e){
                    e.printStackTrace();
                    // TODO: Error handling.
                }
            }

            @Override
            public void onTaskError(String errorMessage){
                // Todo: Handle errors somehow. (If I get time)
                // Log.e("CreateGame", "Error: " + errorMessage); maybe?
            }
        }).execute();
    }

    // Inner AsyncTask class for creating a game
    // Todo: Move to dedicated class (For cleanliness and reusability)

    private static class CreateGameTask extends AsyncTask<Void, Void, String> {


        // Member Variables
        private final String name;
        private final int mapId;
        private final String gameLength;
        private final TaskListener listener;

        // Constructor to pass data and listener to AsyncTask
        public CreateGameTask(String name, int mapId, String gameLength, TaskListener listener){
            this.name = name;
            this.mapId = mapId;
            this.gameLength = gameLength;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids){ // This is deprecated but still works. Probably insecure.
            //  Calling the NetworkRequests class to perform the POST request
            return NetworkRequests.createGame(name,mapId,gameLength);
        }

        @Override
        protected void onPostExecute(String result) { // Also deprecated.
            super.onPostExecute(result);

            // If result is not empty, parse as JSON
            if (result != null && !result.isEmpty()) {
                try {
                    // Parse result into JSONObject
                    JSONObject jsonResponse = new JSONObject(result);

                    // Pass the response to the listener
                    listener.onTaskCompleted(jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onTaskError("Error parsing JSON response.");
                }
            } else { // If the result is empty or something else untoward happens.
                listener.onTaskError("No response recieved or error occurred");

        }
    }

    // Interface to communicate with Activity (In this case CreateGame)
        public interface TaskListener {
        void onTaskCompleted(JSONObject jsonResponse);
        void onTaskError(String errorMessage);
        }
    }


    // Method to trigger the AsyncTask for joining a game through a POST request (After creating a game resolves).
    // Todo: Move to dedicated class (For cleanliness and reusability)

    private void joinGame(String playerName, int gameId){

        new JoinGameTask(playerName, gameId, new JoinGameTask.TaskListener(){
            @Override
            public void onTaskCompleted(JSONObject jsonResponse){
                // Handling of the response received from the POST request when it completes.
                // Due to this being Asynchronous I can't simply return the JSON when I call the method.

                try {
                    // Process the JSON response.
                    String message = jsonResponse.getString("message");
                    int gameId = jsonResponse.getInt("gameId");
                    int playerId = jsonResponse.getInt("playerId");
                    String playerName = jsonResponse.getString("playerName");
                    String role = jsonResponse.getString("role");
                    String colour = jsonResponse.getString("colour");
                    int startLocation = jsonResponse.getInt("startLocation");


                    // Saves PlayerId to sharedPreferences for later use.
                    playerData.setPlayerId(playerId);

                    // TODO: save this data somewhere, and/or use it to create a lobby.

                } catch (JSONException e){
                    e.printStackTrace();
                    // TODO: Error handling.
                }
            }

            @Override
            public void onTaskError(String errorMessage){
                // Todo: Handle errors somehow. (If I get time)
                // Log.e("CreateGame", "Error: " + errorMessage); maybe?
            }
        }).execute();


    }

    // Inner AsyncTask class for joining a game
    // Todo: Move to dedicated class (For cleanliness and reusability)

    private static class JoinGameTask extends AsyncTask<Void, Void, String> {

        // Member Variables
        //private final String message;
        private final int gameId;
        private final String playerName;
        private final TaskListener listener;
        //private final String role;
        //private final String colour;
        //private final int startLocation;

        // Constructor to pass data and listener to AsyncTask
        public JoinGameTask(String playerName, int gameId , TaskListener listener){
            this.gameId = gameId;
            this.playerName = playerName;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids){ // This is deprecated but still works. Probably insecure.
            //  Calling the NetworkRequests class to perform the POST request
            return NetworkRequests.joinGame(playerName,gameId);
        }

        @Override
        protected void onPostExecute(String result) { // Also deprecated.
            super.onPostExecute(result);

            // If result is not empty, parse as JSON
            if (result != null && !result.isEmpty()) {
                try {
                    // Parse result into JSONObject
                    JSONObject jsonResponse = new JSONObject(result);

                    // Pass the response to the listener
                    listener.onTaskCompleted(jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onTaskError("Error parsing JSON response.");
                }
            } else { // If the result is empty or something else untoward happens.
                listener.onTaskError("No response recieved or error occurred");

            }
        }

        // Interface to communicate with Activity (In this case CreateGame)
        public interface TaskListener {
            void onTaskCompleted(JSONObject jsonResponse);
            void onTaskError(String errorMessage);
        }
    }



}