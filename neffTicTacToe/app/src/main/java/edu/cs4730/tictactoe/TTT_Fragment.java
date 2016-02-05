/*Brandon Neff
 *Project 5 - Tic Tac Toe
 *COSC 4730
 */
package edu.cs4730.tictactoe;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;


public class TTT_Fragment extends Fragment {
	TTTView tttv;
	//bluetooth device and code to turn the device on if needed.
	BluetoothAdapter mBluetoothAdapter =null;
	private static final int REQUEST_ENABLE_BT = 2;
	Button btn_client, btn_server;
	TextView logger;

	public TTT_Fragment() {
        setHasOptionsMenu(true);
	}


	//This code will check to see if there is a bluetooth device and
	//turn it on if is it turned off.
	public void startbt() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
			return;
		}
		//make sure bluetooth is enabled.
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			//bluetooth is on, so list paired devices from here.
			querypaired();
		}
	}

    /*
     * This method will query the bluetooth device and ask for a list of all
     * paired devices.  It will then display to the screen the name of the device and the address
     *   In client fragment we need this address to so we can connect to the bluetooth device that is acting as the server.
     */

    public void querypaired() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            final BluetoothDevice blueDev[] = new BluetoothDevice[pairedDevices.size()];
            String item;
            int i =0;
            for (BluetoothDevice devicel : pairedDevices) {
                blueDev[i] = devicel;
                item = blueDev[i].getName() + ": " + blueDev[i].getAddress();
                i++;
            }

        } else {
        }
    }
	//Inflates layout
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View myView = inflater.inflate(R.layout.ttt_fragment, container, false);
		tttv = (TTTView) myView.findViewById(R.id.tttv1);

        startbt();

		return myView;
	}

	//Inflates menu
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
	}

    //Displays menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                tttv.clearBoard();
                return true;
            case R.id.changeX:
                tttv.changeX();
                return true;
            case R.id.changeO:
                tttv.changeO();
                return true;
            case R.id.btConnect:
                querypaired();
                return true;
        }

		return false;
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            //bluetooth result code.
            if (resultCode == Activity.RESULT_OK) {
                //mkmsg("Bluetooth is on.");
                querypaired();
            } else {
                //mkmsg("Please turn the bluetooth on.");
            }
        }
    }
}
