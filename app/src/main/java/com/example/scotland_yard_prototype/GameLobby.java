package com.example.scotland_yard_prototype;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Importing relevant modules for HTTP requests & String manipulation
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class GameLobby extends AppCompatActivity implements AsyncClass.GetGameStateTaskListener, AsyncClass.StartGameTaskListener {

    // PlayerData instance for accessing sharedPreferences
    PlayerData playerData = new PlayerData(this);

    // Handler/runnable instances for polling/running functions periodically
    private Handler handler = new Handler();
    private Runnable runnable;

    // Member variable. Currently saving gameId for testing.
    // Other game information is retrieved using this.
    private int gameId;
    private String gameName;


    // Display Elements
    private TextView gameNameDisplay;
    private TextView gameIdDisplay;
    private TextView gameStateDisplay;
    private TextView mapIdDisplay;
    private TextView playerTypeDisplay;
    private TextView numberOfConnectionsDisplay;

    private Button startGameButton;
    private Button returnToMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_lobby);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });

        gameNameDisplay = findViewById(R.id.gameNameDisplay);
        gameIdDisplay = findViewById(R.id.gameIdDisplay);
        gameStateDisplay = findViewById(R.id.gameStateDisplay);
        mapIdDisplay = findViewById(R.id.mapIdDisplay);
        playerTypeDisplay = findViewById(R.id.playerTypeDisplay);
        numberOfConnectionsDisplay = findViewById(R.id.numberOfConnectionsDisplay);
        startGameButton = findViewById(R.id.startGameButton);
        returnToMenuButton = findViewById(R.id.returnToMainMenuButton);

        // Get game name and ID from sharedPreferences
        gameId = playerData.getGameId();
        gameName = playerData.getGameName();

        startGameButton.setOnClickListener(v -> {
            // DEV OVERRIDE - remove later
            //pretend there are enough players
            // Handle button click
            attemptToLaunchGame();
        });

        returnToMenuButton.setOnClickListener(v -> {

            Intent intent = new Intent(GameLobby.this, MainActivity.class);
            startActivity(intent); // Starts the "Main Activity" activity
        });

        // Initial call of "updateLobby" to set up.
        findGameState();

        // Defining of tasks that will run every five seconds
        // In this case; polling the server for updates
        runnable = new Runnable() {
            @Override
            public void run() {
                // server call/ui update
                findGameState();

                // Repeat every five seconds (5000 ms)
                handler.postDelayed(this, 5000);
            }
        };

        handler.post(runnable);
    }

    private void findGameState(){
        // This is called every five seconds
        Log.d("GameLobby", "Lobby Updating.");
        new AsyncClass.GetGameStateTask(gameId, GameLobby.this).execute();
    }

    private void updateLobby(int mapId, String state, int numberOfConnections){

        Log.d("GameLobby", "Updating lobby display.");
        gameNameDisplay.setText(playerData.getGameName());
        gameIdDisplay.setText(String.valueOf(playerData.getGameId()));
        gameStateDisplay.setText(state);
        mapIdDisplay.setText(String.valueOf(mapId));
        playerTypeDisplay.setText(playerData.getRole());
        numberOfConnectionsDisplay.setText(String.valueOf(numberOfConnections));

    }

    private void attemptToLaunchGame(){

        new AsyncClass.StartGameTask(playerData.getPlayerId(), gameId, GameLobby.this).execute();

    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        // Remove any pending posts of the runnable when the activity is destroyed.
        // Hopefully this should prevent issues.
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove any pending posts of runnable to stop the task from repeating
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop the handler from running tasks when the activity is stopped
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onGetGameStateTaskCompleted(JSONObject jsonResponse){

        Log.d("GameLobby", "Lobby Updated.");
        try {
            // Process the JSON response.
            if (jsonResponse.has("responseStatus")){
                Log.d("GameLobby", "Response Code: " + jsonResponse.getString("responseCode"));

                String responseStatus = jsonResponse.getString("responseStatus");

                if (responseStatus.equals("Success")) {

                    // Retrieve "data" as a JSONObject
                    String dataString = jsonResponse.getString("data");
                    JSONObject dataObject = new JSONObject(dataString);
                    //JSONObject dataObject = jsonResponse.getJSONObject("data");

                    // Retrieving data from "data" object
                    int gameId = dataObject.getInt("gameId");
                    int mapId = dataObject.getInt("mapId");
                    String state = dataObject.getString("state");
                    JSONArray playersArray = dataObject.getJSONArray("players");

                    Log.d("GameLobby", "State: " + state);

                    if (state.equals("Open")){

                        // Update lobby
                        updateLobby(mapId,state,playersArray.length());

                    }else{
                        Intent intent = new Intent(GameLobby.this, GameInProgress.class);
                        startActivity(intent); // Starts the "Game In Progress" activity
                    }

                    //String name = dataObject.getString("name");
                    //int mapId = dataObject.getInt("mapId");

                } else if (responseStatus.equals("Failure")) {
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

    @Override
    public void onGetGameStateTaskError(String errorMessage){}

    @Override
    public void onStartGameTaskCompleted(JSONObject jsonResponse){

        Log.d("GameLobby", "Game attempting to start. ");
        //Log.d("GameLobby", "Response: " + jsonResponse);
        try {
            // Process the JSON response.
            if (jsonResponse.has("responseStatus")){
                Log.d("GameLobby", "Response Code: " + jsonResponse.getString("responseCode"));

                String responseStatus = jsonResponse.getString("responseStatus");

                if (responseStatus.equals("Success")) {

                    // Retrieve "data" as a JSONObject
                    String dataString = jsonResponse.getString("data");
                    JSONObject dataObject = new JSONObject(dataString);
                    //JSONObject dataObject = jsonResponse.getJSONObject("data");

                    // Retrieving data from "data" object
                    int gameId = dataObject.getInt("gameId");
                    //int mapId = dataObject.getInt("mapId");
                    String state = dataObject.getString("state");
                    //JSONArray playersArray = dataObject.getJSONArray("players");

                    Log.d("GameLobby", "State: " + state);

                    Intent intent = new Intent(GameLobby.this, GameInProgress.class);
                    startActivity(intent); // Starts the "Game In Progress" activity

                    //String name = dataObject.getString("name");
                    //int mapId = dataObject.getInt("mapId");

                } else if (responseStatus.equals("Failure")) {
                    // Handle failure case
                    // TODO: Join the in-progress game automatically if it is already running
                    Log.d("GameLobby", "Game unable to start");
                }else {
                    // Handle unexpected response
                }
            }

        } catch (JSONException e){
            e.printStackTrace();
            // TODO: Error handling.
        }

    }

    @Override
    public void onStartGameTaskError(String errorMessage){

    }












    // Method to trigger the AsyncTask for finding information for the current game using a GET request.
    // Todo: Move to dedicated class (For cleanliness and reusability)

    private void getGameState(int gameId){



        new GameLobby.GetGameStateTask(gameId, new GameLobby.GetGameStateTask.TaskListener(){
            @Override
            public void onTaskCompleted(JSONObject jsonResponse){
                // Handling of the response received from the GET request when it completes.
                // Due to this being Asynchronous I can't simply return the JSON when I call the method.

                try {

                    gameNameDisplay = findViewById(R.id.gameNameDisplay);
                    gameIdDisplay = findViewById(R.id.gameIdDisplay);
                    gameStateDisplay = findViewById(R.id.gameStateDisplay);
                    mapIdDisplay = findViewById(R.id.mapIdDisplay);
                    numberOfConnectionsDisplay = findViewById(R.id.numberOfConnectionsDisplay);

                    // Process the game response
                    int gameId = jsonResponse.getInt("gameId");
                    int mapId = jsonResponse.getInt("mapId");
                    String state = jsonResponse.getString("state");
                    //String name = jsonResponse.getString("name"); // This may not exist when a game is open.
                    JSONArray playersArray = jsonResponse.getJSONArray("players");

                    gameNameDisplay.setText("Ben's Test Lobby");
                    gameIdDisplay.setText(String.valueOf(gameId));
                    gameStateDisplay.setText(state);
                    mapIdDisplay.setText(String.valueOf(mapId));
                    numberOfConnectionsDisplay.setText(String.valueOf(playersArray.length()));




                } catch (Exception e){
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

    private static class GetGameStateTask extends AsyncTask<Void, Void, String> {


        // Member Variables
        private final int gameId;
        private final GameLobby.GetGameStateTask.TaskListener listener;

        // Constructor to pass data and listener to AsyncTask
        public GetGameStateTask(int gameId, GameLobby.GetGameStateTask.TaskListener listener){
            this.gameId = gameId;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids){ // This is deprecated but still works. Probably insecure.
            //  Calling the NetworkRequests class to perform the GET request
            return NetworkRequests.getGameState(gameId);
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
                listener.onTaskError("No response received or error occurred");

            }
        }

        // Interface to communicate with Activity (In this case Finding game information)
        public interface TaskListener {
            void onTaskCompleted(JSONObject jsonResponse);
            void onTaskError(String errorMessage);
        }
    }
}