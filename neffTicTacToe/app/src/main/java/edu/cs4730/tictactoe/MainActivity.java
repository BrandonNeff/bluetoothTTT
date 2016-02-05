/*Brandon Neff
 *Project 5 - Tic Tac Toe
 *COSC 4730
 */
package edu.cs4730.tictactoe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new TTT_Fragment()).commit();
        }
	}
}
