package com.example.scotland_yard_prototype;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JoinGame extends AppCompatActivity implements AsyncClass.FindLobbiesTaskListener, AsyncClass.JoinGameTaskListener {

    // Instance of "PlayerData" class. This is necessary to provide it "Context"
    PlayerData playerData = new PlayerData(this);

    // Member Variables

    private EditText playerNameText;

    private Button returnToMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        returnToMenu = findViewById(R.id.returnToMainMenuJoinGame);

        playerNameText = findViewById(R.id.joinGamePlayerName);

        returnToMenu.setOnClickListener(v -> {

            Intent intent = new Intent(JoinGame.this, MainActivity.class);
            startActivity(intent); // Starts the "Main Activity" activity
        });

        //findLobbies();
        // Find open lobbies
        new AsyncClass.FindLobbiesTask(JoinGame.this).execute();

    }

    @Override
    public void onFindLobbiesTaskCompleted(JSONObject jsonResponse){

        try {

            if (jsonResponse.has("data")) {
                // Get "data" component
                String dataString = jsonResponse.getString("data");

                // Parse "data" back into JSONObject
                JSONObject jsonData = new JSONObject(dataString);
                Log.d("JoinGame", jsonData.toString());


                // use JSONParser to parse the list of games.
                JSONParser parser = new JSONParser();
                List<GameClass> games = parser.parseGames(jsonData);

                // Get the LinearLayout where joinable lobbies buttons will be added
                LinearLayout joinableLobbiesButtons = findViewById(R.id.joinableLobbies);

                // Clears previous buttons (If present)
                joinableLobbiesButtons.removeAllViews();

                // Loop through each game
                for (GameClass game : games) {
                    // Creating a button for each game lobby present
                    Button lobbyButton = new Button(JoinGame.this);
                    lobbyButton.setText("Game: " + game.getGameName() + " Map: " + game.getMapName() + " " + game.getPlayers().size() + "/6 players");
                    lobbyButton.setTag(game); // Saves the GameClass object in the button's "Tag" for later use
                    lobbyButton.setOnClickListener(v -> {
                        // Handle button click

                        // Ensure that a name has been chosen for the player.
                        if (!TextUtils.isEmpty(playerNameText.getText())) {


                            GameClass selectedGame = (GameClass) v.getTag();
                            Log.d("Game", "Game clicked: " + selectedGame.getGameName());
                            // TODO: Handle game selection, joining lobby.

                            //playerData.setPlayerName(playerNameText.getText().toString());
                            playerData.setGameName(game.getGameName());
                            playerData.setMapId(game.getMapId());

                            new AsyncClass.JoinGameTask(playerNameText.getText().toString(), game.getGameId(), JoinGame.this).execute();

                            // Assign playerData Data

                            //playerData.setPlayerId();
                            //playerData.setPlayerName();
                            //playerData.setGameName(game.getGameName());
                            //playerData.setGameId(game.getGameId());
                            //playerData.setMapId(game.getMapId());
                            //playerData.setRole("detective");
                            //playerData.setStartLocation();
                            //playerData.setCurrentLocation(playerData.getStartLocation());
                        }



                        //Intent intent = new Intent(JoinGame.this, GameLobby.class);

                        // Creation of bundle to pass the selected game's details.
                        // TODO: cause a real connection to fire to the game lobby, rather than a facsimile
                        //Bundle bundle = new Bundle();

                        // Game information for bundle
                        // Requirements: Game Name, Game ID, Game State, Map ID, Player Type
                        // Actually, it may be better to simply use gameID

                        //bundle.putInt("gameId", selectedGame.getGameId());

                        //intent.putExtras(bundle);
                        //startActivity(intent); // Starts the "Game Lobby" activity

                    });
                    // Add the game button to the scrollable layout
                    joinableLobbiesButtons.addView(lobbyButton);
                }
            }

            // TODO: save this data somewhere, and/or use it to create a lobby.

        } catch (Exception e){
            e.printStackTrace();
            // TODO: Error handling.
        }

    }

    @Override
    public void onFindLobbiesTaskError(String errorMessage){

    }

    @Override
    public void onJoinGameTaskCompleted(JSONObject jsonResponse){

        try {

            // TODO: Handle failed response codes

            if (jsonResponse.has("data")) {
                // Get "data" component
                String dataString = jsonResponse.getString("data");

                // Parse "data" back into JSONObject
                JSONObject jsonData = new JSONObject(dataString);
                Log.d("JoinGame", jsonData.toString());

                playerData.setPlayerId(jsonData.getInt("playerId"));
                playerData.setPlayerName(jsonData.getString("playerName"));
                //playerData.setGameName(game.getGameName());
                playerData.setGameId(jsonData.getInt("gameId"));
                //playerData.setMapId(game.getMapId());
                playerData.setRole(jsonData.getString("role"));
                playerData.setStartLocation(jsonData.getInt("startLocation"));
                playerData.setCurrentLocation(jsonData.getInt("startLocation"));

                // TODO: Switch to GameLobby activity

                Intent intent = new Intent(JoinGame.this, GameLobby.class);
                startActivity(intent); // Starts the "Game Lobby" activity
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onJoinGameTaskError(String errorMessage){

    }














    // Method to trigger the AsyncTask for finding open lobbies using a GET request.
    // Todo: Move to dedicated class (For cleanliness and reusability)

    private void findLobbies(){

        new FindLobbiesTask(new FindLobbiesTask.TaskListener(){
            @Override
            public void onTaskCompleted(JSONObject jsonResponse){
                // Handling of the response received from the GET request when it completes.
                // Due to this being Asynchronous I can't simply return the JSON when I call the method.

                try {

                    // use JSONParser to parse the list of games.
                    JSONParser parser = new JSONParser();
                    List<GameClass> games = parser.parseGames(jsonResponse);

                    // Get the LinearLayout where joinable lobbies buttons will be added
                    LinearLayout joinableLobbiesButtons = findViewById(R.id.joinableLobbies);

                    // Clears previous buttons (If present)
                    joinableLobbiesButtons.removeAllViews();

                    // Loop through each game
                    for (GameClass game : games) {
                        // Creating a button for each game lobby present
                        Button lobbyButton = new Button(JoinGame.this);
                        lobbyButton.setText("Game: " + game.getGameName());
                        lobbyButton.setTag(game); // Saves the GameClass object in the button's "Tag" for later use
                        lobbyButton.setOnClickListener(v -> {
                            // Handle button click
                            GameClass selectedGame = (GameClass) v.getTag();
                            Log.d("Game", "Game clicked: " + selectedGame.getGameName());
                            // TODO: Handle game selection, joining lobby.




                            Intent intent = new Intent(JoinGame.this, GameLobby.class);

                            // Creation of bundle to pass the selected game's details.
                            // TODO: cause a real connection to fire to the game lobby, rather than a facsimile
                            Bundle bundle = new Bundle();

                            // Game information for bundle
                            // Requirements: Game Name, Game ID, Game State, Map ID, Player Type
                            // Actually, it may be better to simply use gameID

                            bundle.putInt("gameId", selectedGame.getGameId());

                            intent.putExtras(bundle);
                            startActivity(intent); // Starts the "Game Lobby" activity

                        });
                        // Add the game button to the scrollable layout
                        joinableLobbiesButtons.addView(lobbyButton);
                    }

                    // TODO: save this data somewhere, and/or use it to create a lobby.

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

    private static class FindLobbiesTask extends AsyncTask<Void, Void, String> {


        // Member Variables
        private final FindLobbiesTask.TaskListener listener;

        // Constructor to pass data and listener to AsyncTask
        public FindLobbiesTask(FindLobbiesTask.TaskListener listener){
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids){ // This is deprecated but still works. Probably insecure.
            //  Calling the NetworkRequests class to perform the GET request
            return NetworkRequests.findLobbies();
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

        // Interface to communicate with Activity (In this case FindLobbies)
        public interface TaskListener {
            void onTaskCompleted(JSONObject jsonResponse);
            void onTaskError(String errorMessage);
        }
    }
}