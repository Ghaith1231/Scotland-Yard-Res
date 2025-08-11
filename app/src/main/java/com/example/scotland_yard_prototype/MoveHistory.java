package com.example.scotland_yard_prototype;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MoveHistory extends AppCompatActivity implements AsyncClass.GetGameStateTaskListener, AsyncClass.GetPlayerMoveHistoryTaskListener{

    private List<PlayerEntry> playerEntries = new ArrayList<>();
    private LinearLayout moveHistoryLayout;

    private PlayerData playerData;

    private Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_move_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        returnButton = findViewById(R.id.returnToGameButton);

        returnButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoveHistory.this, GameInProgress.class);
                startActivity(intent); // Returns to the "Game In Progress" activity
            }
        });

        moveHistoryLayout = findViewById(R.id.moveHistoryLayout);

        playerData = new PlayerData(this);

        new AsyncClass.GetGameStateTask(playerData.getGameId(), MoveHistory.this).execute();

    }

    // Storage class for a player
    public class PlayerEntry{
        private int playerId;
        private int startLocation;
        private List<MoveEntry> moves;

        // Constructor, setters, getters.

//        public PlayerEntry(int playerId, int startLocation, List<MoveEntry> moves){
//            this.playerId = playerId;
//            this.startLocation = startLocation;
//            this.moves = moves;
//        }

        public int getPlayerId(){
            return playerId;
        }
        public void setPlayerId(int playerId){
            this.playerId = playerId;
        }

        public int getStartLocation(){
            return startLocation;
        }
        public void setStartLocation(int startLocation){
            this.startLocation = startLocation;
        }

        public List<MoveEntry> getMoves(){
            return moves;
        }
        public void setMoves(List<MoveEntry> moves){
            this.moves = moves;
        }
    }

    // Storage class for a move
    public class MoveEntry{
        private int moveId;
        private int round;
        private String ticket;
        private int destination;

        // Constructor, getters, setters,
        public MoveEntry( int moveId, int round, String ticket, int destination){
            this.moveId = moveId;
            this.round = round;
            this.ticket = ticket;
            this.destination = destination;
        }

        public int getMoveId(){
            return moveId;
        }
        public void setMoveId(int moveId){
            this.moveId = moveId;
        }

        public int getRound(){
            return round;
        }
        public void setRound(int round){
            this.round = round;
        }

        public String getTicket(){
            return ticket;
        }
        public void setTicket(String ticket){
            this.ticket = ticket;
        }

        public int getDestination(){
            return destination;
        }
        public void setDestination(int destination){
            this.destination = destination;
        }
    }

    public void populatePlayerMoveHistory(List<Integer> playerIds){

        //
        ExecutorService executor = Executors.newFixedThreadPool(playerIds.size());
        List<Future<JSONObject>> futures = new ArrayList<>();

        // Submit tasks to Executor
        for (int playerId : playerIds){
            Future<JSONObject> future = executor.submit(new Callable<JSONObject>() {
                @Override
                public JSONObject call() throws Exception {
                    // Execute AsyncTask and get raw JSON response
                    String jsonResponseString = new AsyncClass.GetPlayerMoveHistoryTask(playerId, MoveHistory.this).doInBackground();

                    // If the response is a String, convert it to JSONObject
                    try {
                        return new JSONObject(jsonResponseString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return null; // Handle or log the error appropriately
                    }
                }
            });
            futures.add(future);
        }
        // Wait for all tasks to complete and process the results
        for (Future<JSONObject> future : futures) {
            try {
                JSONObject jsonResponse = future.get(); // Blocks until task is completed

                if (jsonResponse != null) {
                    onGetPlayerMoveHistoryTaskCompleted(jsonResponse);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Once all tasks are done, display player moves
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayPlayerMoves();
            }
        });

        executor.shutdown();

//        // Loop through ID list and get player moves.
//        for (int playerId : playerIds){
//            new AsyncClass.GetPlayerMoveHistoryTask(playerId, MoveHistory.this).execute();
//        }
//        displayPlayerMoves();
    }

    public void displayPlayerMoves() {

        // Iterate through playerEntries list and display each player and their moves.

        for (PlayerEntry playerEntry : playerEntries) {

            // Log playerEntry data
            Log.d("MoveHistory", "Player Entry: " + playerEntry);

            // Create new LinearLayout for each player entry
            LinearLayout playerLayout = new LinearLayout(this);
            playerLayout.setOrientation(LinearLayout.VERTICAL);
            playerLayout.setPadding(16,16,16,16);
            playerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


            // Access data
            int playerId = playerEntry.getPlayerId();
            int startLocation = playerEntry.getStartLocation();
            List<MoveEntry> moves = playerEntry.getMoves();

            // TextView for player Id number
            TextView playerIdTextView = new TextView(this);
            String playerIdString = ("Player ID: " + playerId);
            playerIdTextView.setText(playerIdString);
            playerIdTextView.setTextSize(16);
            playerLayout.addView(playerIdTextView);

            // TextView for start location number
            TextView startLocationTextView = new TextView(this);
            String startLocationString = ("Start Location: " + startLocation);
            startLocationTextView.setText(startLocationString);
            startLocationTextView.setTextSize(16);
            playerLayout.addView(startLocationTextView);

            // TextView for displaying moves
            StringBuilder movesText = new StringBuilder();
            for (MoveEntry move : moves) {
                movesText.append("Move ID: ").append(move.getMoveId())
                        .append(" Round: ").append(move.getRound())
                        .append(" Ticket: ").append(move.getTicket())
                        .append(" Destination: ").append(move.getDestination())
                        .append("\n");
            }

            // TextView for Move History

            TextView moveHistoryTextView = new TextView(this);
            String moveHistoryString = ("Moves:\n" + movesText.toString());
            moveHistoryTextView.setText(moveHistoryString);
            moveHistoryTextView.setTextSize(14);
            playerLayout.addView(moveHistoryTextView);

            // Add the player's layout to the container (moveHistoryLayout)
            moveHistoryLayout.addView(playerLayout);
        }
    }



    @Override
    public void onGetGameStateTaskCompleted(JSONObject jsonResponse){

        try{

            // List to store playerId values
            List<Integer> playerIds = new ArrayList<>();

            if (jsonResponse.has("data")){
                // Get "data" component
                String dataString = jsonResponse.getString("data");

                // Parse "data" back into JSONObject
                JSONObject jsonData = new JSONObject(dataString);
                Log.d("MoveHistory", jsonData.toString());

                JSONArray playerArray = jsonData.getJSONArray("players");

                // Loop through "playerArray" and get the playerId of all players
                for (int i = 0; i < playerArray.length(); i++){

                    JSONObject player = playerArray.getJSONObject(i);
                    int playerId = player.getInt("playerId");
                    playerIds.add(playerId);
                }

                Log.d("MoveHistory", "Player IDs: " + playerIds);

                // Pass data to function for further processing
                populatePlayerMoveHistory(playerIds);

            }

        } catch(JSONException e){
            Log.d("MoveHistory", "OnGetGameState Failed");
            e.printStackTrace();
        }

    }

    @Override
    public void onGetGameStateTaskError(String errorMessage){

    }

    @Override
    public void onGetPlayerMoveHistoryTaskCompleted(JSONObject jsonResponse){

        try{

            if (jsonResponse.has("data")){
                // Get "data" component
                String dataString = jsonResponse.getString("data");

                // Parse "data" back into JSONObject
                JSONObject jsonData = new JSONObject(dataString);
                Log.d("MoveHistory", jsonData.toString());

                int playerId = jsonData.getInt("playerId");
                int startLocation = jsonData.getInt("startLocation");
                JSONArray playerMoves = jsonData.getJSONArray("moves");

                PlayerEntry playerEntry = new PlayerEntry();
                List<MoveEntry> tempMovesList = new ArrayList<>();

                if (playerMoves != null && playerMoves.length() > 0) {
                    for (int i = 0; i < playerMoves.length(); i++) {

                        JSONObject playerMove = playerMoves.getJSONObject(i);
                        int moveId = playerMove.getInt("moveId");
                        int round = playerMove.getInt("round");
                        String ticket = playerMove.getString("ticket");
                        int destination = playerMove.getInt("destination");

                        MoveEntry moveEntry = new MoveEntry(moveId, round, ticket, destination);

                        tempMovesList.add(moveEntry);

                    }
                }

                playerEntry.setPlayerId(playerId);
                playerEntry.setStartLocation(startLocation);
                playerEntry.setMoves(tempMovesList);

                playerEntries.add(playerEntry);

            }

        } catch(JSONException e){
            Log.d("MoveHistory", "onGetPlayerMoveHistory Failed");
            e.printStackTrace();
        }

    }

    @Override
    public void onGetPlayerMoveHistoryTaskError(String errorMessage){

    }

}