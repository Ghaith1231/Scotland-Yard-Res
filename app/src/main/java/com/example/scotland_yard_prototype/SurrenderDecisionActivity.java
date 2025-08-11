package com.example.scotland_yard_prototype;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

public class SurrenderDecisionActivity extends AppCompatActivity
        implements AsyncClass.RemoveFromGameTaskListener {

    private Button yesButton, noButton;
    private int playerId;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surrender_decision);

        yesButton = findViewById(R.id.yesButton);
        noButton  = findViewById(R.id.noButton);

        // ✅ Java method calls (no named args)
        playerId = getIntent().getIntExtra("playerId", -1);
        role     = getIntent().getStringExtra("playerRole");

        yesButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                if ("Fugitive".equalsIgnoreCase(role)) {
                    yesButton.setEnabled(false); // prevent double-tap
                    // ✅ Run the remove here, listener is THIS activity
                    new AsyncClass.RemoveFromGameTask(playerId, SurrenderDecisionActivity.this).execute();
                } else {
                    Toast.makeText(SurrenderDecisionActivity.this,
                            "You cannot surrender as this role!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finish(); // just go back to the game
            }
        });
    }

    // ===== RemoveFromGameTaskListener =====
    @Override
    public void onRemoveFromGameTaskCompleted(JSONObject jsonResponse) {
        Log.d("SurrenderDecision", "Remove success: " + jsonResponse);
        // Mr X surrendered -> show LoserScreen for this client
        Intent i = new Intent(this, LoserScreen.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @Override
    public void onRemoveFromGameTaskError(String errorMessage) {
        yesButton.setEnabled(true);
        Log.e("SurrenderDecision", "Remove error: " + errorMessage);
        Toast.makeText(this, "Surrender failed: " + errorMessage, Toast.LENGTH_SHORT).show();
    }
}
