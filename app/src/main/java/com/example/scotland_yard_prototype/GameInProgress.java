package com.example.scotland_yard_prototype;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GameInProgress extends AppCompatActivity implements AsyncClass.GetMapSingleTaskListener, AsyncClass.GetImageTaskListener, AsyncClass.PlayerMakeMoveTaskListener, AsyncClass.GetGameStateTaskListener, AsyncClass.GetPlayerStatusTaskListener, AsyncClass.RemoveFromGameTaskListener{



    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean alreadyExited = false;


    private final Runnable pollGameState = new Runnable() {
        @Override public void run() {
            if (alreadyExited) return;
            if (gameId == -1) {                  // no game yet? try again shortly
                handler.postDelayed(this, 2000);
                return;
            }
            new AsyncClass.GetGameStateTask(gameId, GameInProgress.this).execute();
            handler.postDelayed(this, 2000);     // simple heartbeat
        }
    };

    // Handler/runnable instances for polling/running functions periodically

    private Runnable runnable;

    private FrameLayout mapDisplayFrame; //= findViewById(R.id.mapFrameLayout);
    private JSONArray locationData;
    private JSONArray connectionData;

    private int maxPlayers = 6;
    private Button[] playerIcons = new Button[maxPlayers];

    private Button x2TicketButton;
    private Button moveHistoryButton;

    private Button surrenderButton;

    private Button leaveGameButton;

    private Button kickPlayerButton;
    private Spinner kickPlayerDropdown;

    private List<Integer> playerIds;

    private PlayerData playerData = new PlayerData(this);

    private boolean mapDrawn = false; // Boolean to determine whether game map/players has already been drawn

    private int lastDestinationClicked; // Last destination clicked

    private boolean movedThisRound = false;

    // Member Variables

    private TextView playerNameDisplay;
    private TextView gameNameDisplay;
    private TextView gameStateDisplay;
    private TextView gameRoundDisplay;
    private TextView yellowTicketDisplay;
    private TextView greenTicketDisplay;
    private TextView redTicketDisplay;
    private TextView blackTicketDisplay;
    private TextView x2TicketDisplay;
    private int gameId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_in_progress);
        gameId = getIntent().getIntExtra("gameId", -1);
        handler.post(pollGameState);
        Log.d("GameInProgress", "Started polling gameId=" + gameId);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {



            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;



        });





        playerIds = new ArrayList<>();

        surrenderButton = findViewById(R.id.surrenderButton);
        if (!"Fugitive".equalsIgnoreCase(playerData.getRole())) {
            surrenderButton.setVisibility(View.GONE);
        }
        surrenderButton.bringToFront();
        leaveGameButton = findViewById(R.id.leaveGameButton);
        mapDisplayFrame = findViewById(R.id.mapFrameLayout);
        playerNameDisplay = findViewById(R.id.playerNameDisplay);
        gameNameDisplay = findViewById(R.id.gameNameProgDisplay);
        gameStateDisplay = findViewById(R.id.gameStateProgDisplay);
        gameRoundDisplay = findViewById(R.id.gameRoundProgLabel2);
        yellowTicketDisplay = findViewById(R.id.yellowTicketVal);
        greenTicketDisplay = findViewById(R.id.greenTicketVal);
        redTicketDisplay = findViewById(R.id.redTicketVal);
        blackTicketDisplay = findViewById(R.id.blackTicketVal);
        x2TicketDisplay = findViewById(R.id.x2TicketVal);

        x2TicketButton = findViewById(R.id.x2TicketButton);
        x2TicketButton.setVisibility(View.GONE); // x2 button only visible/usable if player is Fugitive.

        leaveGameButton = findViewById(R.id.leaveGameButton);
        moveHistoryButton = findViewById(R.id.viewMoveHistoryButton);

        kickPlayerButton = findViewById(R.id.kickPlayerButton);
        kickPlayerDropdown = findViewById(R.id.kickPlayerDropdown);

        x2TicketButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                // Attempt to make move
                //onLocationClicked(locationNum);

                // Attempt to use x2 ticket
                new AsyncClass.PlayerMakeMoveTask(playerData.getPlayerId(), playerData.getGameId(),"x2", 1, GameInProgress.this).execute();

                // TODO: Use actual data

            }
        });

        leaveGameButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameInProgress.this, MainActivity.class);
                startActivity(intent); // Starts the "Main Activity" activity

                // TODO: Use actual data

            }
        });

        surrenderButton.setOnClickListener(v -> {
            String role = playerData.getRole();       // "Fugitive"
            int pid     = playerData.getPlayerId();
            int gid     = playerData.getGameId();
            if (gid ==-1) {
                Toast.makeText(this,"no game id", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("Fugitive".equalsIgnoreCase(role)) {
                if (gid == -1) {
                    Toast.makeText(GameInProgress.this, "Missing gameId.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent(GameInProgress.this, SurrenderDecisionActivity.class);
                i.putExtra("playerRole", role);
                i.putExtra("playerId", pid);
                i.putExtra("gameId", gid);
                startActivity(i);
            } else {
                Toast.makeText(GameInProgress.this, "Only the Fugitive can surrender!", Toast.LENGTH_SHORT).show();
            }
        });


        kickPlayerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Get the selected player ID
                int selectedPlayerId = kickPlayerDropdown.getSelectedItemPosition();
                if (selectedPlayerId != -1) {
                    // Perform kick player action
                    Log.d("GameInProgress", "PlayerID being kicked: " + playerIds.get(selectedPlayerId));
                    new AsyncClass.RemoveFromGameTask(playerIds.get(selectedPlayerId), GameInProgress.this).execute();
                }
                // Attempt to make move
                //onLocationClicked(locationNum);

                // Attempt to use x2 ticket
                //new AsyncClass.PlayerMakeMoveTask(playerData.getPlayerId(), playerData.getGameId(),"x2", 1, GameInProgress.this).execute();

                // TODO: KICK PLAYER

            }
        });

        moveHistoryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                // Switch to lobby
                Intent intent = new Intent(GameInProgress.this, MoveHistory.class);
                startActivity(intent); // Starts the "MoveHistory" activity

                // TODO: Use actual data

            }
        });

        // Create player Icons
        createPlayerIcons();

        // Get and draw Map/locations (Only done once)
        new AsyncClass.GetMapSingleTask(playerData.getMapId(), GameInProgress.this).execute();

        // Get GameState, drawing player icons, ticket values, etc. Will be repeated during polling.
        //new AsyncClass.GetGameStateTask(playerData.getGameId(),GameInProgress.this).execute();

        // Defining of tasks that will run every five seconds
        // In this case; polling the server for updates

        // Make x2 ticket button visible if player is "fugitive"
        if (playerData.getRole().equals("Fugitive")){
            x2TicketButton.setVisibility(View.VISIBLE);
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                // server call/ui update
                //findGameState();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateDisplay();
                    }
                });
                //updateDisplay();

                // Repeat every five seconds (5000 ms)
                handler.postDelayed(this, 5000);
            }
        };

        handler.post(runnable);

        Log.d("GameInProgress", "OnCreate Completed");

    }

    private void drawMap(String mapURL){

        ImageView tempMapImage = findViewById(R.id.tempMapImage);
        new AsyncClass.GetImageTask(tempMapImage,mapURL, GameInProgress.this).execute();

    }

    private void drawLocationButtons(){
        // Iterates through button data and draws buttons on map.
        // Further: Creates functionality for each button based on stored data.

        try {

            // Frame containing both map image and buttons
            mapDisplayFrame = findViewById(R.id.mapFrameLayout);

            int buttonDiameter = 75; // Diameter of buttons
            float fontSize = 8f; // Desired size of button text (sp)

            // Create and place each button
            for (int i = 0; i < locationData.length(); i++) {

                JSONObject singleLocationData = locationData.getJSONObject(i);

                int locationNum = singleLocationData.getInt("location");
                int xPos = singleLocationData.getInt("xPos");
                int yPos = singleLocationData.getInt("yPos");

                Button locationButton = new Button(this);
                locationButton.setText(String.valueOf(locationNum));

                locationButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(buttonDiameter, buttonDiameter);

                // Set button to be circular
                GradientDrawable buttonShape = new GradientDrawable();
                buttonShape.setShape(GradientDrawable.OVAL);
                buttonShape.setColor(getResources().getColor(android.R.color.holo_blue_light));
                locationButton.setBackground(buttonShape);

                // Set location of button
                params.leftMargin = xPos - (buttonDiameter / 2);
                params.topMargin = yPos - (buttonDiameter / 2);

                // Set button parameters
                locationButton.setLayoutParams(params);

                // Set low alpha for transparency but still clickable
                locationButton.setAlpha(0.1f); // 10% opacity

                // Add button to frame
                mapDisplayFrame.addView(locationButton);

                locationButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        // Attempt to make move
                        onLocationClicked(locationNum);

                        // TODO: Use actual data

                    }
                });
            }
        } catch (JSONException e){ // Catch errors
            e.printStackTrace();
        }
    }

    private void createPlayerIcons(){

        int[] buttonColours = {
                android.R.color.white,        // Player 0 (Fugitive)
                android.R.color.holo_red_light,  // Player 1 (Detective)
                android.R.color.holo_green_light,  // Player 2 (Detective)
                android.R.color.holo_blue_light,  // Player 3 (Detective)
                android.R.color.holo_orange_light,  // Player 4 (Detective)
                android.R.color.black          // Player 5 (Detective)
        };

        int buttonDiameter = 75; // Diameter of buttons
        float fontSize = 8f; // Desired size of button text (sp)

        // Loop to create each player button
        for (int i = 0; i < buttonColours.length; i++){

            Button playerButton = new Button(this);

            // Prevent interaction with these buttons, they are just meant to represent the players
            playerButton.setClickable(false); // Player button is unclickable
            playerButton.setFocusable(false); // Player button is unfocusable

            // Setting text size for player button (May be irrelevant)
            playerButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

            // Create GradientDrawable for button's shape
            GradientDrawable buttonShape = new GradientDrawable();
            buttonShape.setShape(GradientDrawable.OVAL);
            buttonShape.setColor(getResources().getColor(buttonColours[i])); // Set colour based on the index of this loop
            playerButton.setBackground(buttonShape);

            // Set button visibility to "GONE" initially, to hide the icons
            playerButton.setVisibility(View.GONE);

            // Store the button in the "playerIcons" array
            playerIcons[i] = playerButton;

            // Create layout parameters and set initial position (I don't think this matters)
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(buttonDiameter, buttonDiameter);
            params.leftMargin = 100;
            params.topMargin = 100;
            playerButton.setLayoutParams(params);

            // Add button to map display frame

            mapDisplayFrame.addView(playerButton);

        }

        // DEPRECATED

//        // Create buttons to represent players
//        Button player0 = new Button(this);
//        Button player1 = new Button(this);
//        Button player2 = new Button(this);
//        Button player3 = new Button(this);
//        Button player4 = new Button(this);
//        Button player5 = new Button(this);
//
//        int buttonDiameter = 75; // Diameter of buttons
//        float fontSize = 8f; // Desired size of button text (sp)
//
//        player0.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
//        player1.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
//        player2.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
//        player3.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
//        player4.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
//        player5.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
//
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(buttonDiameter, buttonDiameter);
//
//        GradientDrawable buttonShape0 = new GradientDrawable();
//        buttonShape0.setShape(GradientDrawable.OVAL);
//        buttonShape0.setColor(getResources().getColor(android.R.color.white));
//        player0.setBackground(buttonShape0);
//
//        GradientDrawable buttonShape1 = new GradientDrawable();
//        buttonShape1.setShape(GradientDrawable.OVAL);
//        buttonShape1.setColor(getResources().getColor(android.R.color.holo_red_light));
//        player1.setBackground(buttonShape1);
//
//        GradientDrawable buttonShape2 = new GradientDrawable();
//        buttonShape2.setShape(GradientDrawable.OVAL);
//        buttonShape2.setColor(getResources().getColor(android.R.color.holo_green_light));
//        player2.setBackground(buttonShape2);
//
//        GradientDrawable buttonShape3 = new GradientDrawable();
//        buttonShape3.setShape(GradientDrawable.OVAL);
//        buttonShape3.setColor(getResources().getColor(android.R.color.holo_blue_light));
//        player3.setBackground(buttonShape3);
//
//        GradientDrawable buttonShape4 = new GradientDrawable();
//        buttonShape4.setShape(GradientDrawable.OVAL);
//        buttonShape4.setColor(getResources().getColor(android.R.color.holo_orange_light));
//        player4.setBackground(buttonShape4);
//
//        GradientDrawable buttonShape5 = new GradientDrawable();
//        buttonShape5.setShape(GradientDrawable.OVAL);
//        buttonShape5.setColor(getResources().getColor(android.R.color.black));
//        player5.setBackground(buttonShape5);
//
//        player0.setVisibility(View.GONE);
//        player1.setVisibility(View.GONE);
//        player2.setVisibility(View.GONE);
//        player3.setVisibility(View.GONE);
//        player4.setVisibility(View.GONE);
//        player5.setVisibility(View.GONE);
//
//        playerIcons[0] = player0;
//        playerIcons[1] = player1;
//        playerIcons[2] = player2;
//        playerIcons[3] = player3;
//        playerIcons[4] = player4;
//        playerIcons[5] = player5;
//
//        mapDisplayFrame.addView(player0);
//        mapDisplayFrame.addView(player1);
//        mapDisplayFrame.addView(player2);
//        mapDisplayFrame.addView(player3);
//        mapDisplayFrame.addView(player4);
//        mapDisplayFrame.addView(player5);


        // clear, red, green, blue, yellow, black

        // android.R.color.white
        // android.R.color.holo_red_light
        // android.R.color.holo_green_light
        // android.R.color.holo_blue_light
        // android.R.color.holo_orange_light
        // android.R.color.black


    }

    private void drawPlayers(JSONArray playersData){

        if (playersData != null && playersData.length() > 0){

            for (int i = 0; i < maxPlayers; i++){

                // Hide button
                playerIcons[i].setVisibility(View.GONE);

                // Get player information
                JSONObject player = playersData.optJSONObject(i);

                // Button Diameter TODO: should be inherited from "drawLocations"
                int buttonDiameter = 75;

                // Ensure player is not null, and draw player location if this is the case
                if (player != null){

                    try{
                        String playerLocation = player.getString("location");

                        // If the controlling player is the fugitive,
                        // Draw their player icon regardless of whether it should otherwise be hidden
                        if ("fugitive".equals(playerData.getRole())){ // TODO: Currently never fires, was causing a crash

                            //FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(buttonDiameter,buttonDiameter);

                            // Gets the current location of the player
                            JSONObject currentLocation = locationData.getJSONObject((Integer.parseInt(playerLocation) - 1));

                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(buttonDiameter,buttonDiameter);

                            // X and Y coordinates of location at which player is located
                            int xPos = currentLocation.getInt("xPos");
                            int yPos = currentLocation.getInt("yPos");

                            params.leftMargin = xPos - (buttonDiameter / 2);
                            params.topMargin = yPos - (buttonDiameter / 2);

                            playerIcons[i].setLayoutParams(params);

                            // Show button
                            playerIcons[i].setVisibility(View.VISIBLE);


                        } else{

                            // Check if player is Fugitive and that their location should be hidden.
                            if ("Hidden".equals(playerLocation)){
                                // Do Nothing
                            }else{

                                // Place and show button

                                JSONObject currentLocation = locationData.getJSONObject((Integer.parseInt(playerLocation) - 1));

                                int xPos = currentLocation.getInt("xPos");
                                int yPos = currentLocation.getInt("yPos");

                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(buttonDiameter,buttonDiameter);

                                params.leftMargin = xPos - (buttonDiameter / 2);
                                params.topMargin = yPos - (buttonDiameter / 2);

                                playerIcons[i].setLayoutParams(params);

                                playerIcons[i].setVisibility(View.VISIBLE);

                            }

                        }



                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                }else {


                    // Hide button
                    playerIcons[i].setVisibility(View.GONE);
                }

            }

        }

    }

    private void onLocationClicked(int destination){

        // Check to see if player has already moved this round (if detective)
        if (!playerData.getRole().equals("Fugitive") && movedThisRound){
            Log.d("GameInProgress", "You have already moved this round.");

        }else {

            Log.d("GameInProgress", "Location " + destination + " clicked");

            // Attempt to make move
            int playerCurrentLocation = playerData.getCurrentLocation();
            String requiredTicket = getTicketForLocations(playerCurrentLocation, destination);
            Log.d("GameInProgress", "Starting Location: " + playerData.getCurrentLocation());
            Log.d("GameInProgress", "Destination Location: " + destination);
            Log.d("GameInProgress", "Required Ticket: " + requiredTicket);
            new AsyncClass.PlayerMakeMoveTask(playerData.getPlayerId(), playerData.getGameId(), requiredTicket, destination, GameInProgress.this).execute();
        }
    }

    private void updatePlayerData(JSONObject jsonResponse){

    }



    private String getTicketForLocations(int location1, int location2){
        // Helper function to find the required ticket for travel between two locations

        // Loop through each element in "connectionData" and determine whether there is a connection
        // between the two provided locations
        for (int i = 0; i < connectionData.length(); i++){

            try {
                JSONObject connection = connectionData.getJSONObject(i);

                int locationA = connection.getInt("locationA");
                int locationB = connection.getInt("locationB");
                String ticket = connection.getString("ticket");

                // Check if the pair of locations matches in either order (Somewhat redundant)
                if ((locationA == location1 && locationB == location2) || (locationA == location2 && locationB == location1)){
                    return ticket;
                }

            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return "NoConnection"; // Return null if no matching pair is found (If this happens I've made a mistake)
    }

    private void updateDisplay(){

        new AsyncClass.GetGameStateTask(playerData.getGameId(),GameInProgress.this).execute();

    }

    private void populateKickDialog(JSONArray players){
        //playerIds = new ArrayList<>();

        playerIds.clear();

        try {


            for (int i = 0; i < players.length(); i++){
                JSONObject player = players.getJSONObject(i);
                int playerId = player.getInt("playerId");
                playerIds.add(playerId);
            }

        }catch (JSONException e){
            e.printStackTrace();
            return; // If error parsing JSON, exit method
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(GameInProgress.this, android.R.layout.simple_spinner_item, playerIds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kickPlayerDropdown.setAdapter(adapter);

    }









    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pollGameState != null) handler.removeCallbacks(pollGameState);
        if (runnable != null)      handler.removeCallbacks(runnable);
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
    public void onGetMapSingleTaskCompleted(JSONObject jsonResponse){

        Log.d("GameInProgress", jsonResponse.toString());

        try{

            if (jsonResponse.has("data")){
                // Get "data" component
                String dataString = jsonResponse.getString("data");

                // Parse "data" back into JSONObject
                JSONObject jsonData = new JSONObject(dataString);
                Log.d("GameInProgress", jsonData.toString());

                locationData = jsonData.getJSONArray("locations");
                connectionData = jsonData.getJSONArray("connections");

                drawMap(jsonData.getString("mapImage"));
                drawLocationButtons();

                // Get gamestate and update other aspects of game
                Log.d("GameInProgress", "Game ID: " + playerData.getGameId());
                new AsyncClass.GetGameStateTask(playerData.getGameId(),GameInProgress.this).execute();
            }

        } catch(JSONException e){
            Log.d("GameInProgress", "OnGetMapSingleTask Failed");
            e.printStackTrace();
        }

    }

    @Override
    public void onGetMapSingleTaskError(String errorMessage){

    }

    @Override
    public void onGetImageTaskCompleted(Bitmap bitmap){

        ImageView tempMapImage = findViewById(R.id.tempMapImage);

        //ImageView newMapImage = new ImageView(this);
        //newMapImage.setScaleType(ImageView.ScaleType.CENTER);

        int bitWidth = bitmap.getWidth();
        int bitHeight = bitmap.getHeight();

        Log.d("Bitmap Width",String.valueOf(bitWidth));
        Log.d("Bitmap Height",String.valueOf(bitHeight));

        //newMapImage.setLayoutParams(new FrameLayout.LayoutParams(bitWidth, bitHeight));


        //int scaleFactor = 3;
        int desiredWidth = 1783;
        int desiredHeight = 1056;

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, true);


        //tempMapImage.setLayoutParams(new FrameLayout.LayoutParams(desiredWidth, desiredHeight));
        tempMapImage.setImageBitmap(bitmap);

        //newMapImage.setImageBitmap(bitmap);

        mapDisplayFrame = findViewById(R.id.mapFrameLayout);
        // Placing of button on image based on coordinates (Test)
        //Button testButton20 = new Button(this);
        //Button testButton18 = new Button(this);
        //testButton20.setText("20");
        //testButton18.setText("18");

        // Button fontsize (sp)
        //float fontSize = 8f;
        //testButton20.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        //testButton18.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);



        // Set button size
        //int buttonDiameter = 75;
        //testButton20.setWidth(buttonDiameter);
        //testButton20.setHeight(buttonDiameter);
        //testButton18.setWidth(buttonDiameter);
       // testButton18.setHeight(buttonDiameter);

        //testButton20.setLayoutParams(new FrameLayout.LayoutParams(buttonDiameter,buttonDiameter));
        //testButton18.setLayoutParams(new FrameLayout.LayoutParams(buttonDiameter,buttonDiameter));

        //FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(buttonDiameter, buttonDiameter);
        //FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(buttonDiameter, buttonDiameter);

        // Cause button to be circular
        //GradientDrawable buttonShape = new GradientDrawable();
        //buttonShape.setShape(GradientDrawable.OVAL);
        //buttonShape.setColor(getResources().getColor(android.R.color.holo_blue_light));
        //testButton18.setBackground(buttonShape);
        //testButton20.setBackground(buttonShape);

        // Button size/position
//        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT
//        );
//        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT
//        );

        //params1.leftMargin = 513 - (buttonDiameter / 2);
        //params1.topMargin = 517 - (buttonDiameter / 2);

        //params2.leftMargin = 475 - (buttonDiameter / 2);
        //params2.topMargin = 305 - (buttonDiameter / 2);

        ///testButton20.setLayoutParams(params1);
        //testButton18.setLayoutParams(params2);

        // Button added to FrameLayout
        //mapDisplayFrame.addView(newMapImage);
        //mapDisplayFrame.addView(testButton20);
        //mapDisplayFrame.addView(testButton18);

    }

    @Override
    public void onGetImageTaskError(){

    }

    @Override
    public void onPlayerMakeMoveTaskCompleted(JSONObject jsonResponse){

        Log.d("GameInProgress", jsonResponse.toString());

        try{

            if (jsonResponse.has("data")){
                // Get "data" component
                String dataString = jsonResponse.getString("data");

                // Parse "data" back into JSONObject
                JSONObject jsonData = new JSONObject(dataString);
                Log.d("GameInProgress", jsonData.toString());

                playerData.setCurrentLocation(jsonData.getInt("location"));

                movedThisRound = true;

            }

        } catch(JSONException e){
            Log.d("GameInProgress", "OnPlayerMakeMoveTask Failed");
            e.printStackTrace();
        }

    }

    @Override
    public void onPlayerMakeMoveTaskError(String errorMessage){

    }

    @Override
    public void onGetGameStateTaskCompleted(JSONObject jsonResponse) {
        try {
            if (jsonResponse == null) return;
            if (jsonResponse.has("data")) {
                JSONObject jsonData = new JSONObject(jsonResponse.getString("data"));

                // Test plz work
                Log.d("GameInProgress", "Poll snapshot: " + jsonData.toString());

                String winner = jsonData.optString("winner", "None");
                if (!"None".equalsIgnoreCase(winner)) {
                    Log.d("GameInProgress", "Winner detected via poll: " + winner);
                    routeToWinnerScreen(winner);
                    return;
                }

            }
        } catch (Exception e) {
            Log.e("GameInProgress", "onGetGameStateTaskCompleted parse error: " + e.getMessage());
        }
    }





    @Override
    public void onGetGameStateTaskError(String errorMessage){

    }

    @Override
    public void onGetPlayerStatusTaskCompleted(JSONObject jsonResponse){

        try{
            if (jsonResponse.has("data")){
                // Get "data" component
                String dataString = jsonResponse.getString("data");

                // Parse "data" back into JSONObject
                JSONObject jsonData = new JSONObject(dataString);
                Log.d("GameInProgress", jsonData.toString());

                // Display player information
                //new AsyncClass.GetPlayerStatusTask(playerData.getPlayerId(),GameInProgress.this).execute();

                // Use optInt() to avoid JSONException if the key does not exist ( I think this is already handled though)
                yellowTicketDisplay.setText(String.valueOf(jsonData.optInt("yellow", 0)));  // Default to 0 if the key is not found
                greenTicketDisplay.setText(String.valueOf(jsonData.optInt("green", 0)));    // Default to 0 if the key is not found
                redTicketDisplay.setText(String.valueOf(jsonData.optInt("red", 0)));        // Default to 0 if the key is not found
                blackTicketDisplay.setText(String.valueOf(jsonData.optInt("black", 0)));    // Default to 0 if the key is not found
                x2TicketDisplay.setText(String.valueOf(jsonData.optInt("2x", 0)));          // Default to 0 if the key is not found

                //JSONArray playersData = jsonData.getJSONArray("players");
                //drawPlayers(playersData);
            }

        } catch(JSONException e){
            Log.d("GameInProgress", "OnGetMapSingleTask Failed");
            e.printStackTrace();
        }
    }

    @Override
    public void onGetPlayerStatusTaskError(String errorMessage){

    }

    @Override
    public void onRemoveFromGameTaskCompleted(org.json.JSONObject jsonResponse) {
        Log.d("GameInProgress", "Surrender success: " + jsonResponse);
        Intent i = new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }



    @Override
    public void onRemoveFromGameTaskError(String errorMessage) {
        Log.e("GameInProgress", "Surrender failed: " + errorMessage);
        Toast.makeText(this, "Surrender failed: " + errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void routeToWinnerScreen(String winner) {
        try {
            if (handler != null && pollGameState != null) handler.removeCallbacks(pollGameState);
        } catch (Exception ignored) {}
        Intent i = new Intent(this, WinnerScreen.class);
        i.putExtra("gameWinner", winner);
        startActivity(i);
        finish();
    }

}

