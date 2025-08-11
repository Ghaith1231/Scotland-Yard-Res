
package com.example.scotland_yard_prototype;

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

public class WinnerScreen extends AppCompatActivity {

    private TextView winnerDisplay;
    private Button returnToMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_winner_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        winnerDisplay = findViewById(R.id.winnerDisplay);
        returnToMenuButton = findViewById(R.id.returnToMenuButton);

        returnToMenuButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WinnerScreen.this, MainActivity.class);
                startActivity(intent); // Starts the "Main Activity" activity

                // TODO: Use actual data

            }
        });

        // Retrieve intent that started this activity
        Intent intent = getIntent();

        // Get bundle of data
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            // Retrieve gameWinner value from bundle
            String gameWinner = bundle.getString("gameWinner");
            Log.d("WinnerScreen", "The winner is: " + gameWinner);

            String winnerDisplayText = "The winner of this game is the " + gameWinner + " team!";

            winnerDisplay.setText(winnerDisplayText);

        }
    }


}