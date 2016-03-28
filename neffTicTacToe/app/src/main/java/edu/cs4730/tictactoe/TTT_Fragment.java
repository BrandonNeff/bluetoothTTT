/*Brandon Neff
 *Project 3 - Bluetooth Tic Tac Toe
 *COSC 4735 or 4010
 */
package edu.cs4730.tictactoe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Set;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class TTT_Fragment extends Fragment{
    //bluetooth device and code to turn the device on if needed.
    public boolean xo, clientConnect = false, serverConnect = false;
    static volatile boolean[] gottenMove = new boolean[9];
    static volatile boolean whoseTurn = false, gameOver = false;
    static volatile String placement;
    static volatile boolean imserver = false;
    static volatile String serverChar, clientChar;
    int counter = 0;
    BluetoothAdapter mBluetoothAdapter =null;
    private static final int REQUEST_ENABLE_BT = 2;
    Button btn_client, btn_server, btn_device;
    BluetoothDevice device;
    TTTView tttv;
    int clientLoc = 0, serverLoc = 0;
    static volatile int nowinner = 0;

    public TTT_Fragment() {
        setHasOptionsMenu(true);
    }

    //Inflates layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.ttt_fragment, container, false);
        tttv = (TTTView) myView.findViewById(R.id.tttv1);
        tttv.setClickable(false);
        btn_device = (Button) myView.findViewById(R.id.which_device);
        btn_device.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                querypaired();
                btn_server.setEnabled(false);
                btn_client.setEnabled(true);
                btn_device.setEnabled(true);
            }
        });
        btn_client = (Button) myView.findViewById(R.id.btnClient);
        btn_client.setEnabled(false);
        btn_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startClient();
                //btn_client.setBackgroundColor(0x123456);
                btn_server.setEnabled(false);
                btn_client.setEnabled(false);
                btn_device.setEnabled(false);
            }
        });
        btn_server = (Button)myView.findViewById(R.id.btnServer);
        btn_server.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startServer();
                //btn_server.setBackgroundColor(0x123456);
                btn_server.setEnabled(false);
                btn_client.setEnabled(false);
                btn_device.setEnabled(false);
            }
        });
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
            case R.id.resetBT:
                startbt();
                return true;
        }

        return false;
    }

    //This code will check to see if there is a bluetooth device and
    //turn it on if is it turned off.
    public void startbt() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            btn_server.setEnabled(false);
            btn_client.setEnabled(false);
            btn_device.setEnabled(false);
            return;
        }
        //make sure bluetooth is enabled.
        if (!mBluetoothAdapter.isEnabled()) {
            mkmsg("There is bluetooth, but turned off");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            mkmsg("The bluetooth is ready to use.");
            //bluetooth is on, so list paired devices from here.
            btn_server.setEnabled(true);
            btn_client.setEnabled(false);
            btn_device.setEnabled(true);
        }
    }

    public Handler handler =  new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (serverChar != null && clientChar != null) {
                if (imserver) {
                    if (clientLoc > 0 && clientLoc < 10) {
                        Log.i("Handling:", clientChar + " placed at: " + clientLoc);
                        tttv.btTTT(clientLoc, clientChar);
                        tttv.winner();
                        tttv.invalidate();
                    }
                    clientLoc = 0;
                } else {
                    if ((clientLoc > 0 && clientLoc < 10)) {
                        Log.i("Handling:", serverChar + " placed at: " + clientLoc);
                        tttv.btTTT(clientLoc, serverChar);
                        tttv.winner();
                        tttv.invalidate();
                    }
                    clientLoc = 0;
                }
                whoseTurn = !whoseTurn;
            }else{
                Log.i("WHYYYYYYYYY", " please noooooooooooo");
            }
            return true;
        }
    });

    public Handler myHandler =  new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (serverChar != null && clientChar != null) {
                if (imserver) {
                    tttv.btTTT(serverLoc, serverChar);
                    tttv.winner();
                    tttv.invalidate();
                } else {
                    tttv.btTTT(serverLoc, clientChar);
                    tttv.winner();
                    tttv.invalidate();
                }
            }
            return true;
        }
    });


    public void setWin(int setter){
        nowinner = setter;
    }

    public String getWin(){
        //tttv.winner();
        if (nowinner == 1)
            return "winner";
        else if (nowinner == 2)
            return "winner";
        else if (nowinner == 3)
            return "tie";
        else
            return "nowinner";
    }

    public void mkmsg(String str) {
        Log.i("Make message", str);
        if (str.equals("received a message:\n1\n")){
            clientLoc = 1;
        } else if (str.equals("received a message:\n2\n")){
            clientLoc = 2;
        } else if (str.equals("received a message:\n3\n")){
            clientLoc = 3;
        } else if (str.equals("received a message:\n4\n")){
            clientLoc = 4;
        } else if (str.equals("received a message:\n5\n")){
            clientLoc = 5;
        } else if(str.equals("received a message:\n6\n")){
            clientLoc = 6;
        } else if(str.equals("received a message:\n7\n")){
            clientLoc = 7;
        } else if(str.equals("received a message:\n8\n")){
            clientLoc = 8;
        } else if(str.equals("received a message:\n9\n")) {
            clientLoc = 9;
        }
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("msg", str);
        msg.setData(b);
        handler.sendMessage(msg);
    }

    public void myMove (String str){
        Log.i("Make message", str);
        if (str.equals("1")){
            serverLoc = 1;
        } else if (str.equals("2")){
            serverLoc = 2;
        } else if (str.equals("3")){
            serverLoc = 3;
        } else if (str.equals("4")){
            serverLoc = 4;
        } else if (str.equals("5")){
            serverLoc = 5;
        } else if(str.equals("6")){
            serverLoc = 6;
        } else if(str.equals("7")){
            serverLoc = 7;
        } else if(str.equals("8")){
            serverLoc = 8;
        } else if(str.equals("9")) {
            serverLoc = 9;
        }
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("msg", str);
        msg.setData(b);
        myHandler.sendMessage(msg);
    }

    public void setPlacement (int y, int x){
        if (x == 0 && y == 0)
            placement = "1";
        else if (x == 0 && y == 1)
            placement = "2";
        else if (x == 0 && y == 2)
            placement = "3";
        else if (x == 1 && y == 0)
            placement = "4";
        else if (x == 1 && y == 1)
            placement = "5";
        else if (x == 1 && y == 2)
            placement = "6";
        else if (x == 2 && y == 0)
            placement = "7";
        else if (x == 2 && y == 1)
            placement = "8";
        else if (x == 2 && y == 2)
            placement = "9";
        Log.i("Placement", placement);
        gottenMove[counter] = true;
        Log.i("set move", Boolean.toString(gottenMove[counter]) + " " + counter);
        counter++;
    }

    public String getPlacement (){
        Log.i("Return placement", placement);
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("msg", "place");
        msg.setData(b);
        myHandler.sendMessage(msg);
        //gottenMove = false;
        return placement;
    }

    public static Boolean getMove (int c){
        return gottenMove[c];
    }

    public void querypaired() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            final BluetoothDevice blueDev[] = new BluetoothDevice[pairedDevices.size()];
            String[] items = new String[blueDev.length];
            int i =0;
            for (BluetoothDevice devicel : pairedDevices) {
                blueDev[i] = devicel;
                items[i] = blueDev[i].getName() + ": " + blueDev[i].getAddress();
                i++;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Choose Bluetooth:");
            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    dialog.dismiss();
                    if (item >= 0 && item <blueDev.length) {
                        device = blueDev[item];
                        btn_device.setText("device: "+blueDev[item].getName());
                        btn_client.setEnabled(true);
                        btn_server.setEnabled(false);
                    }

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void startClient() {
        if (device != null) {
            new Thread(new ConnectThread(device)).start();
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    public class ConnectThread extends Thread {
        private BluetoothSocket socket;
        private int count = 0;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MainActivity.MY_UUID);
            } catch (IOException e) {
                mkmsg("Client connection failed: "+e.getMessage().toString()+"\n");
            }
            socket = tmp;
        }

        public void run() {
            mkmsg("Client running\n");
            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                socket.connect();
            } catch (IOException e) {
                mkmsg("Connect failed\n");
                try {
                    socket.close();
                    socket = null;
                } catch (IOException e2) {
                    mkmsg("unable to close() socket during connection failure: "+e2.getMessage().toString()+"\n");
                    socket = null;
                }
                // Start the service over to restart listening mode
            }
            // If a connection was accepted
            if (socket != null) {
                mkmsg("Connection made\n");
                mkmsg("Remote device address: "+socket.getRemoteDevice().getAddress().toString()+"\n");
                //Note this is copied from the TCPdemo code.
                try {
                    String place;
                    boolean move = false;
                    PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String str;
                    if (clientConnect == false) {
                        mkmsg("Attempting to receive a message ...\n");
                        str = in.readLine();
                        mkmsg("received a message:\n" + str + "\n");
                        if (str.equals("Player X")) {
                            clientChar = "O";
                            serverChar = "X";
                            mkmsg("Server goes first\n");
                            mkmsg("Attempting to send message ...\n");
                            out.println("agree");
                            out.flush();
                            mkmsg("They go first...\n");
                            clientConnect = true;
                        } else if (str.equals("Player O")) {
                            clientChar = "X";
                            serverChar = "O";
                            mkmsg("Attempting to send message ...\n");
                            out.println("agree");
                            out.flush();
                            mkmsg("I go first\n");
                            while (move == false) {
                                //waiting to select move
                                move = getMove(count);
                            }
                            count++;
                            place = getPlacement();
                            mkmsg("Attempting to send move: \n" + place);
                            myMove(place);
                            out.println(place);
                            out.flush();
                            mkmsg("First move sent...\n");
                            clientConnect = true;
                        } else {
                            socket.close();
                        }
                    }
                    while (!gameOver) {
                        BufferedReader in1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        //String str1;
                        //PrintWriter out1 = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);
                        boolean tempmove = false;
                        str = in.readLine();
                        if (str.equals("agree")) {
                            mkmsg("Server agrees:\n");
                            if (getWin().equals("nowinner")) {
                                mkmsg("NOWINNER\n");
                                out.println("nowinner");
                                out.flush();
                                in1.readLine();
                            }else if (getWin().equals("winner")) {
                                mkmsg("WINNER\n");
                                out.println("winner");
                                out.flush();
                                in1.readLine();
                            }else if (getWin().equals("tie")) {
                                mkmsg("CAT\n");
                                out.println("tie");
                                out.flush();
                                in1.readLine();
                            }
                            str = in.readLine();
                            mkmsg("received a message:\n" + str + "\n");
                            mkmsg("Attempting to send agreement ...\n");
                            out.println("agree");
                            out.flush();
                            in1.readLine();
                            out.println("agree");
                            out.flush();
                        } else {
                            mkmsg("received a message:\n" + str + "\n");
                            mkmsg("Attempting to send agreement ...\n");
                            out.println("agree");
                            out.flush();
                            in1.readLine();
                            out.println("agree");
                            out.flush();
                        }
                        while (tempmove == false) {
                            //waiting to select move
                            tempmove = getMove(count);
                        }
                        place = getPlacement();
                        Log.i("C2Place: ", place);
                        mkmsg("Attempting to send move: \n" + place);
                        myMove(place);
                        out.println(place);
                        out.flush();
                        mkmsg("Move sent...\n");
                        count++;
                    }
                } catch(Exception e) {
                    mkmsg("Error happened sending/receiving\n");
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
                mkmsg("Unable to close socket"+e.getMessage()+"\n");
            }
            mkmsg("Client ending \n");
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {}
        }
    }

    public void startServer() {
        serverXorO();
        imserver = !imserver;
        new Thread(new AcceptThread()).start();
    }

    public void serverXorO(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Would you like to be X or O?");
        builder.setPositiveButton("X", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                dialog.dismiss();
                Log.i("Server choice: ", "X");
                xo = true;
                serverChar = "X";
                clientChar = "O";
            }
        });
        builder.setNegativeButton("O", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                dialog.dismiss();
                Log.i("Server choice: ", "O");
                xo = false;
                serverChar = "O";
                clientChar ="X";
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    public class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;
        private int count = 0;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            // Create a new listening server socket
            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(MainActivity.NAME, MainActivity.MY_UUID);
            } catch (IOException e) {
                mkmsg("Failed to start server\n");
            }
            mmServerSocket = tmp;
        }
        public void run() {
            String place;
            boolean move = false;
            mkmsg("waiting on accept");
            BluetoothSocket socket = null;
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                mkmsg("Failed to accept\n");
            }

            // If a connection was accepted
            if (socket != null) {
                mkmsg("Connection made\n");
                mkmsg("Remote xdevice address: "+socket.getRemoteDevice().getAddress().toString()+"\n");
                //Note this is copied from the TCPdemo code.
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String str;
                    PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);
                    if (xo) {
                        whoseTurn = true;
                        serverChar = "X";
                        clientChar = "O";
                        mkmsg("Attempting to be Player X...\n");
                        out.println("Player X");
                        out.flush();
                    } else {
                        whoseTurn = false;
                        serverChar = "O";
                        clientChar = "X";
                        mkmsg("Attempting to be Player O...\n");
                        out.println("Player O");
                        out.flush();
                    }
                    mkmsg("Message sent...\n");
                    mkmsg("Attempting to receive a message ...\n");
                    str = in.readLine();
                    mkmsg("received a message:\n" + str + "\n");
                    if (str.equals("agree") && serverChar.equals("X")) {
                        while (move == false) {
                            //waiting to select move
                            move = getMove(count);
                        }
                        count++;
                        place = getPlacement();
                        mkmsg("Attempting to send move: \n" + place);
                        myMove(place);
                        out.println(place);
                        out.flush();
                        mkmsg("First move sent...\n");
                    }
                    while (!gameOver) {
                        BufferedReader in1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        //String str1;
                        //PrintWriter out1 = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);
                        boolean tempmove = false;
                        mkmsg("Attempting to receive another message ...\n");
                        str = in.readLine();
                        if (str.equals("agree")) {
                            mkmsg("Client agrees...\n");
                            if (getWin().equals("nowinner")) {
                                out.println("nowinner");
                                out.flush();
                                in1.readLine();
                            }else if (getWin().equals("winner")) {
                                out.println("winner");
                                out.flush();
                                in1.readLine();
                            }else if (getWin().equals("tie")) {
                                out.println("tie");
                                out.flush();
                                in1.readLine();
                            }
                            str = in.readLine();
                            mkmsg("received a message:\n" + str + "\n");
                            mkmsg("Attempting to send agreement ...\n");
                            out.println("agree");
                            out.flush();
                            in1.readLine();
                            out.println("agree");
                            out.flush();
                        } else {
                            mkmsg("received a message:\n" + str + "\n");
                            mkmsg("Attempting to send agreement ...\n");
                            out.println("agree");
                            out.flush();
                            in1.readLine();
                            out.println("agree");
                            out.flush();
                        }
                        //flipClickable(false);
                        while (tempmove == false) {
                            //waiting to select move
                            tempmove = getMove(count);
                        }
                        place = getPlacement();
                        mkmsg("Attempting to send move: \n" + place);
                        myMove(place);
                        out.println(place);
                        out.flush();
                        mkmsg("Move sent...\n");
                        count++;
                    }
                } catch(Exception e) {
                    mkmsg("Error happened sending/receiving\n");
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
                mkmsg("Unable to close socket"+e.getMessage()+"\n");
            }
            mkmsg("Server ending \n");
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {}
        }
    }
}
