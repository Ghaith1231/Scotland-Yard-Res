package com.example.scotland_yard_prototype;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class SurrenderDecisionActivity extends AppCompatActivity
        implements AsyncClass.BroadcastGameOverTaskListener {

    private Button yesButton, noButton;

    private int playerId = -1;
    private String role = null;
    private int gameId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surrender_decision);

        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);
        noButton.setOnClickListener(v -> finish());



        Intent src = getIntent();
        if (src != null) {
            playerId = src.getIntExtra("playerId", -1);
            role = src.getStringExtra("playerRole");
            gameId = src.getIntExtra("gameId", -1);
        }



        yesButton.setOnClickListener(v -> {
            if (playerId == -1 || role == null || gameId == -1) {
                Toast.makeText(this, "Missing player/game info.", Toast.LENGTH_SHORT).show();
                return;
            }

            yesButton.setEnabled(false);

            new AsyncClass.BroadcastGameOverTask(
                    gameId,
                    "Detectives",
                    "SURRENDER",
                    playerId,
                    SurrenderDecisionActivity.this
            ).execute();
        });
    }

    @Override
    public void onBroadcastGameOverTaskCompleted(JSONObject jsonResponse) {
        Log.d("SurrenderDecision", "Broadcast success: " + (jsonResponse != null ? jsonResponse.toString() : "null"));
        Intent i = new Intent(this, WinnerScreen.class);
        i.putExtra("gameWinner", "DETECTIVES");
        startActivity(i);
        finish();
    }

    @Override
    public void onBroadcastGameOverTaskError(String errorMessage) {
        Log.e("SurrenderDecision", "Broadcast error: " + errorMessage);
        if (yesButton != null) yesButton.setEnabled(true);
        Toast.makeText(this, "Failed to surrender: " + (errorMessage == null ? "Unknown error" : errorMessage), Toast.LENGTH_SHORT).show();
    }

}