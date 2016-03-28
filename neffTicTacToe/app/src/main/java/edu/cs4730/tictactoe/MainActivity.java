/*Brandon Neff
 *Project 5 - Tic Tac Toe
 *COSC 4730
 */
package edu.cs4730.tictactoe;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

	public static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	public static final String NAME = "tictactoe";

	FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.frag_container, new TTT_Fragment()).commit();
	}
}
