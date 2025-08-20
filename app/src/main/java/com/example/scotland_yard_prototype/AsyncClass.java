package com.example.scotland_yard_prototype;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

// Class handling all Async tasks
// Requires passing in of relevant activity
public class AsyncClass {

    // Single generic listener used by all tasks. DEPRECATED
    public interface TaskListener {
        void onCreateGameTaskCompleted(JSONObject jsonResponse);

        void onCreateGameTaskError(String errorMessage);

        void onJoinGameTaskCompleted(JSONObject jsonResponse);

        void onJoinGameTaskError(String errorMessage);
    }

    // Get all maps information Task listener
    public interface GetMapsTaskListener {
        void onGetMapsTaskCompleted(JSONObject jsonResponse);

        void onGetMapsTaskError(String errorMessage);
    }






    // Get single map information Task Listener
    public interface GetMapSingleTaskListener {
        void onGetMapSingleTaskCompleted(JSONObject jsonResponse);

        void onGetMapSingleTaskError(String errorMessage);
    }

    // Get Game State Task Listener
    public interface GetGameStateTaskListener {
        void onGetGameStateTaskCompleted(JSONObject jsonResponse);

        void onGetGameStateTaskError(String errorMessage);
    }



    // Get Player Status Listener
    public interface GetPlayerStatusTaskListener {
        void onGetPlayerStatusTaskCompleted(JSONObject jsonResponse);

        void onGetPlayerStatusTaskError(String errorMessage);
    }

    // Get Player Move History Listener
    public interface GetPlayerMoveHistoryTaskListener {
        void onGetPlayerMoveHistoryTaskCompleted(JSONObject jsonResponse);

        void onGetPlayerMoveHistoryTaskError(String errorMessage);
    }

    // Find Lobbies Task Listener
    public interface FindLobbiesTaskListener {
        void onFindLobbiesTaskCompleted(JSONObject jsonResponse);

        void onFindLobbiesTaskError(String errorMessage);
    }

    // Get Image Task Listener
    public interface GetImageTaskListener {
        void onGetImageTaskCompleted(Bitmap result);

        void onGetImageTaskError();
    }

    // Create Game task listener
    public interface CreateGameTaskListener {
        void onCreateGameTaskCompleted(JSONObject jsonResponse);

        void onCreateGameTaskError(String errorMessage);
    }

    // Make Move Task Listener
    public interface PlayerMakeMoveTaskListener {
        void onPlayerMakeMoveTaskCompleted(JSONObject jsonResponse);

        void onPlayerMakeMoveTaskError(String errorMessage);
    }

    // Join Game task Listener
    public interface JoinGameTaskListener {
        void onJoinGameTaskCompleted(JSONObject jsonResponse);

        void onJoinGameTaskError(String errorMessage);
    }

    // Start Game task Listener
    public interface StartGameTaskListener {
        void onStartGameTaskCompleted(JSONObject jsonResponse);

        void onStartGameTaskError(String errorMessage);
    }

    // POST: Broadcast game over (surrender) to server
    public static class BroadcastGameOverTask extends android.os.AsyncTask<Void, Void, String> {

        private final int gameId;
        private final String winner;
        private final String reason;
        private final int surrenderedPlayerId;
        private final BroadcastGameOverTaskListener listener;

        public BroadcastGameOverTask(int gameId,
                                     String winner,
                                     String reason,
                                     int surrenderedPlayerId,
                                     BroadcastGameOverTaskListener listener) {
                                    this.gameId = gameId;
                                      this.winner = winner;
                                          this.reason = reason;
                                               this.surrenderedPlayerId = surrenderedPlayerId;
                                           this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) {
            // Calls your NetworkRequests method
            return NetworkRequests.broadcastGameOver(gameId, winner, reason, surrenderedPlayerId);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (result != null && !result.isEmpty()) {
                    org.json.JSONObject jsonResponse = new org.json.JSONObject(result);
                    if (listener != null) listener.onBroadcastGameOverTaskCompleted(jsonResponse);
                } else {
                    if (listener != null) listener.onBroadcastGameOverTaskError("Empty response.");
                }
            } catch (org.json.JSONException e) {
                e.printStackTrace();
                if (listener != null) listener.onBroadcastGameOverTaskError("JSON parse error.");
            }
        }
    }



    // Remove Player From Game Task Listener
    public interface RemoveFromGameTaskListener {
        void onRemoveFromGameTaskCompleted(JSONObject jsonResponse);

        void onRemoveFromGameTaskError(String errorMessage);
    }


    public interface BroadcastGameOverTaskListener {

        void onBroadcastGameOverTaskCompleted(org.json.JSONObject jsonResponse);

        void onBroadcastGameOverTaskError(String errorMessage);
    }





    // GET tasks

    // Inner class handling getting "all maps"
    public static class GetMapsTask extends AsyncTask<Void, Void, String> {

        // Member variables
        private final GetMapsTaskListener listener;

        public GetMapsTask(GetMapsTaskListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) { // This is deprecated but still works. Probably insecure.
            //  Calling the NetworkRequests class to perform the POST request
            return NetworkRequests.getMaps();
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
                    listener.onGetMapsTaskCompleted(jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onGetMapsTaskError("Error parsing JSON response.");
                }
            } else { // If the result is empty or something else untoward happens.
                listener.onGetMapsTaskError("No response recieved or error occurred");

            }
        }

    }

    // Inner class handling getting single map
    public static class GetMapSingleTask extends AsyncTask<Void, Void, String> {

        // Member variables
        private final int mapId;
        private final GetMapSingleTaskListener listener;

        public GetMapSingleTask(int mapId, GetMapSingleTaskListener listener) {
            this.mapId = mapId;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) { // This is deprecated but still works. Probably insecure.
            //  Calling the NetworkRequests class to perform the POST request
            return NetworkRequests.getMapSingle(mapId);
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
                    listener.onGetMapSingleTaskCompleted(jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onGetMapSingleTaskError("Error parsing JSON response.");
                }
            } else { // If the result is empty or something else untoward happens.
                listener.onGetMapSingleTaskError("No response recieved or error occurred");

            }
        }

    }

    // Inner class handling lobby finding tasks
    public static class FindLobbiesTask extends AsyncTask<Void, Void, String> {

        // Member Variables
        private final FindLobbiesTaskListener listener;

        // Constructor to pass data and listener to AsyncTask
        public FindLobbiesTask(FindLobbiesTaskListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) { // This is deprecated but still works. Probably insecure.
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
                    listener.onFindLobbiesTaskCompleted(jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onFindLobbiesTaskError("Error parsing JSON response.");
                }
            } else { // If the result is empty or something else untoward happens.
                listener.onFindLobbiesTaskError("No response received or error occurred");

            }
        }
    }

    public static class GetGameStateTask extends AsyncTask<Void, Void, String> {

        // Member variables
        private final int gameId;
        private final GetGameStateTaskListener listener;

        public GetGameStateTask(int gameId, GetGameStateTaskListener listener) {
            this.gameId = gameId;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) { // This is deprecated but still works. Probably insecure.
            //  Calling the NetworkRequests class to perform the POST request
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
                    listener.onGetGameStateTaskCompleted(jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onGetGameStateTaskError("Error parsing JSON response.");
                }
            } else { // If the result is empty or something else untoward happens.
                listener.onGetGameStateTaskError("No response received or error occurred");

            }
        }

    }







    public static class GetPlayerStatusTask extends AsyncTask<Void, Void, String> {

        // Member variables
        private final int playerId;
        //private final int gameId;
        private final GetPlayerStatusTaskListener listener;

        public GetPlayerStatusTask(int playerId, GetPlayerStatusTaskListener listener) {
            this.playerId = playerId;
            //this.gameId = gameId;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) { // This is deprecated but still works. Probably insecure.
            //  Calling the NetworkRequests class to perform the POST request

            // TODO: Update this dependent on whether "gameId" is necessary
            return NetworkRequests.getPlayerStatus(playerId);
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
                    listener.onGetPlayerStatusTaskCompleted(jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onGetPlayerStatusTaskError("Error parsing JSON response.");
                }
            } else { // If the result is empty or something else untoward happens.
                listener.onGetPlayerStatusTaskError("No response received or error occurred");

            }
        }

    }


    public static class GetPlayerMoveHistoryTask extends AsyncTask<Void, Void, String> {

        // Member variables
        private final int playerId;
        private final GetPlayerMoveHistoryTaskListener listener;

        public GetPlayerMoveHistoryTask(int playerId, GetPlayerMoveHistoryTaskListener listener) {
            this.playerId = playerId;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) { // This is deprecated but still works. Probably insecure.
            //  Calling the NetworkRequests class to perform the POST request

            // TODO: Update this dependent on whether "gameId" is necessary
            return NetworkRequests.getPlayerMoveHistory(playerId);
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
                    listener.onGetPlayerMoveHistoryTaskCompleted(jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onGetPlayerMoveHistoryTaskError("Error parsing JSON response.");
                }
            } else { // If the result is empty or something else untoward happens.
                listener.onGetPlayerMoveHistoryTaskError("No response received or error occurred");

            }
        }

    }

    public static class GetImageTask extends AsyncTask<Void, Void, Bitmap> {

        private final ImageView imageView;
        private final String urlString;
        private final GetImageTaskListener listener;

        public GetImageTask(ImageView imageView, String urlString, GetImageTaskListener listener) {
            this.imageView = imageView;
            this.urlString = urlString;
            Log.d("AsyncClass", "URLString: " + urlString);
            this.listener = listener;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            return NetworkRequests.downloadImage(urlString);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                // If the image download was successful, set the Bitmap to the ImageView
                imageView.setImageBitmap(bitmap);
                listener.onGetImageTaskCompleted(bitmap);  // Notify listener of the successful image download
            } else {
                // If the Bitmap is null, it means the download failed
                listener.onGetImageTaskError();
            }
        }

    }

    // POST tasks

    // Inner class handling game creation tasks
    public static class CreateGameTask extends AsyncTask<Void, Void, String> {

        // Member Variables
        private final String name;
        private final int mapId;
        private final String gameLength;
        private final CreateGameTaskListener listener; // TaskListener

        // Constructor to pass data and listener to AsyncTask
        public CreateGameTask(String name, int mapId, String gameLength, CreateGameTaskListener listener) {
            this.name = name;
            this.mapId = mapId;
            this.gameLength = gameLength;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) { // This is deprecated but still works. Probably insecure.
            //  Calling the NetworkRequests class to perform the POST request
            return NetworkRequests.createGame(name, mapId, gameLength);
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
                    listener.onCreateGameTaskCompleted(jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onCreateGameTaskError("Error parsing JSON response.");
                }
            } else { // If the result is empty or something else untoward happens.
                listener.onCreateGameTaskError("No response received or error occurred");

            }
        }
    }

    public static class PlayerMakeMoveTask extends AsyncTask<Void, Void, String> {

        // Member variables
        private final int playerId;
        private final int gameId;
        private final String ticket;
        private final int destination;
        private final PlayerMakeMoveTaskListener listener;

        public PlayerMakeMoveTask(int playerId, int gameId, String ticket, int destination, PlayerMakeMoveTaskListener listener) {
            this.playerId = playerId;
            this.gameId = gameId;
            this.ticket = ticket;
            this.destination = destination;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) { // This is deprecated but still works. Probably insecure.
            //  Calling the NetworkRequests class to perform the POST request
            return NetworkRequests.playerMakeMove(playerId, gameId, ticket, destination);
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
                    listener.onPlayerMakeMoveTaskCompleted(jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onPlayerMakeMoveTaskError("Error parsing JSON response.");
                }
            } else { // If the result is empty or something else untoward happens.
                listener.onPlayerMakeMoveTaskError("No response received or error occurred");

            }
        }

    }


    // PATCH tasks

    public static class JoinGameTask extends AsyncTask<Void, Void, String> {

        // Member Variables
        private final String playerName;
        private final int gameId;
        private final JoinGameTaskListener listener; // Tasklistener

        // Constructor to pass data & listener to AsyncTask
        public JoinGameTask(String playerName, int gameId, JoinGameTaskListener listener) {
            this.playerName = playerName;
            this.gameId = gameId;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) { // This is deprecated but still works. Probably insecure.
            //  Calling the NetworkRequests class to perform the POST request
            return NetworkRequests.joinGame(playerName, gameId);
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
                    listener.onJoinGameTaskCompleted(jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onJoinGameTaskError("Error parsing JSON response.");
                }
            } else { // If the result is empty or something else untoward happens.
                listener.onJoinGameTaskError("No response received or error occurred");

            }
        }

    }

    public static class StartGameTask extends AsyncTask<Void, Void, String> {

        // Member Variables
        private final int playerId;
        private final int gameId;
        private final StartGameTaskListener listener; // Tasklistener

        // Constructor to pass data & listener to AsyncTask
        public StartGameTask(int playerId, int gameId, StartGameTaskListener listener) {
            this.playerId = playerId;
            this.gameId = gameId;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) { // This is deprecated but still works. Probably insecure.
            //  Calling the NetworkRequests class to perform the POST request
            return NetworkRequests.startGame(gameId, playerId);
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
                    listener.onStartGameTaskCompleted(jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onStartGameTaskError("Error parsing JSON response.");
                }
            } else { // If the result is empty or something else untoward happens.
                listener.onStartGameTaskError("No response received or error occurred");
            }
        }
    }




    // DELETE tasks

    public static class RemoveFromGameTask extends AsyncTask<Void, Void, String> {

        // Member Variables
        private final int playerId;
        private final RemoveFromGameTaskListener listener; // Tasklistener

        // Constructor to pass data & listener to AsyncTask
        public RemoveFromGameTask(int playerId, RemoveFromGameTaskListener listener) {
            this.playerId = playerId;
            this.listener = listener;
        }



        @Override
        protected String doInBackground(Void... voids) { // This is deprecated but still works. Probably insecure.
            //  Calling the NetworkRequests class to perform the POST request
            // TODO: Determine if "gameId" is necessary.
            return NetworkRequests.removeFromGame(playerId);
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
                    listener.onRemoveFromGameTaskCompleted(jsonResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onRemoveFromGameTaskError("Error parsing JSON response.");
                }
            } else { // If the result is empty or something else untoward happens.
                listener.onRemoveFromGameTaskError("No response received or error occurred");

            }
        }


    }
    }