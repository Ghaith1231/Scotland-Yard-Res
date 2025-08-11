
package com.example.scotland_yard_prototype;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoserScreen extends AppCompatActivity {

    private Button returnToMenuButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loser_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView loserDisplay = findViewById(R.id.loserDisplay);
        returnToMenuButton = findViewById(R.id.returnToMenuButton);

        returnToMenuButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoserScreen.this, MainActivity.class);
                startActivity(intent); // Starts the "Main Activity" activity

            }
        });

        // Retrieve intent that started this activity
        Intent intent = getIntent();

        // Get bundle of data
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            String gameWinner = bundle.getString("gameWinner");
            String playerRole = bundle.getString("playerRole");

            String loserDisplayText;

            if (playerRole.equals("Fugitive")) {
                loserDisplayText = "You've been defeated! The Detectives win!";
            } else {
                loserDisplayText = "You've been defeated! Fugitive has escaped!";
            }

            loserDisplay.setText(loserDisplayText);

        }
    }


}