package com.example.myapplication;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    /** List of color names */
    List<String> backgroundColorNames;

    /** IDs for the color bars */
    List<Integer> colorbarIDs;

    /** Listener for EditText keyboards */
    private EditText.OnEditorActionListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the listener
        createListener();

        // Set the IDs of the color bars
        setColorbarIDs();

        // Picks the order in which the colors are displayed
        resetColorbars();
    }

    /**
     * Creates the listener for the EditText keyboards. Called when the user
     * clicks the "Done" button to perform the action
     */
    private void createListener() {
        listener = new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view,
                                          int actionId,
                                          KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Check if the guessed color was correct
                    boolean correct = checkGuess(view);

                    // Present a Toast notification informing them of success
                    // or failure
                    String msg = "";
                    if (correct) {
                        msg = getResources().getString(R.string.success_msg);
                    } else {
                        msg = getResources().getString(R.string.fail_msg);
                    }

                    Toast.makeText(getApplicationContext(),msg,
                            Toast.LENGTH_SHORT).show();

                    // Dismiss keyboard
                    InputMethodManager imm =(InputMethodManager)
                            getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);

                    // If correct answer is given then disable input,
                    // otherwise reset to the hint
                    if (correct) {
                        view.setFocusable(false);
                    } else {
                        view.setText("");
                        view.setHint(R.string.hint);
                    }

                    return true;
                }
                return false;
            }
        };
    }

    /**
     * Sets the IDs of the color bars for later reference and ease of access
     */
    private void setColorbarIDs() {
        colorbarIDs = new ArrayList<>();
        colorbarIDs.add(R.id.colorbar_1);
        colorbarIDs.add(R.id.colorbar_2);
        colorbarIDs.add(R.id.colorbar_3);
        colorbarIDs.add(R.id.colorbar_4);
        colorbarIDs.add(R.id.colorbar_5);
        colorbarIDs.add(R.id.colorbar_6);
    }

    /**
     * Performed on the press of the "Try Again" butotn
     * @param view
     */
    public void resetButton(View view) {
        resetColorbars();
    }

    /**
     * Sets the colorbars to six random colors
     */
    private void resetColorbars() {
        // Read the color_data array which contains the names of the
        // background and text colors
        List<String> colorData = Arrays.asList(
                getResources().getStringArray(R.array.color_data));

        // Shuffle the list to randomize the order in which the color bars
        // appear on the screen
        Collections.shuffle(colorData);

        // Re-initialize the list that holds the names of the colorbars
        final int N_BARS = colorbarIDs.size();
        backgroundColorNames = new ArrayList<String>(N_BARS);

        // Pull from the shuffled list and populate the view on screen
        for (int i = 0; i < N_BARS; i++) {
            // If for some reason we have more bars on the screen than
            // entries in our database I guess we just loop around
            int n = i % N_BARS;

            // Parse this line of the colorData list to get the background
            // color name and text color name
            String[] parts = colorData.get(i).split(",");
            String colorName = parts[0];
            String textColorName = parts[1];

            int backgroundColorID = getResources().getIdentifier(colorName,
                    "color",this.getPackageName());

            int textColorID = getResources().getIdentifier(textColorName,
                    "color",this.getPackageName());

            int backgroundColor =
                    ContextCompat.getColor(this, backgroundColorID);

            int textColor = ContextCompat.getColor(this, textColorID);

            // Set the colorbar properties and save the name
            backgroundColorNames.add(colorName);
            EditText colorbar = (EditText) findViewById(colorbarIDs.get(i));
            colorbar.setBackgroundColor(backgroundColor);
            colorbar.setText("");
            colorbar.setTextColor(textColor);
            colorbar.setHintTextColor(Color.DKGRAY);
            colorbar.setFocusable(true);
            colorbar.setFocusableInTouchMode(true);
            colorbar.setOnEditorActionListener(listener);
        }
    }

    /**
     * Called when the user wants to check their guess at the color name
     *
     * @param colorbar - the colorbar being checked
     * @return if the guess is correct or not
     */
    private boolean checkGuess(TextView colorbar) {
        // Get which bar was clicked
        int whichBar = colorbarIDs.indexOf(colorbar.getId());

        // Get the name of the background color
        String correctName = backgroundColorNames.get(whichBar);

        // Get the text entered
        String guess = colorbar.getText().toString();

        // Compare the text entered to the actual color
        return guess.equalsIgnoreCase(correctName);
    }

}
