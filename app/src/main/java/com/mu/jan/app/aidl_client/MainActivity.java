package com.mu.jan.app.aidl_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MyAIDLInterfaceClient myAIDLInterfaceClient = null;
    private EditText num1,num2;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //This is client and have to connect to server (AIDL_1 project)
        //Connect to server, client use bind and unBind services

        num1 = (EditText)findViewById(R.id.num1);
        num2 = (EditText)findViewById(R.id.num2);
        btn = (Button)findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //connect to server to access its features
                    try {
                        int n1 = Integer.parseInt(num1.getText().toString().trim());
                        int n2 = Integer.parseInt(num2.getText().toString().trim());
                        int result = myAIDLInterfaceClient.getResultSum(n1,n2);
                        Toast.makeText(MainActivity.this,"Sum is "+result,Toast.LENGTH_SHORT);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(myAIDLInterfaceClient==null){
            //connect to server
            //bind to server

            Intent i = new Intent("ServerRemoteIntentAction");
            i.setClassName("com.mu.jan.aidl_1","com.mu.jan.app.aidl_1.AIDLService");
            //com.mu.jan.aidl_client intentFilter is created in androidManifest.xml in Server project (AIDL_1 project)
            boolean isBound = getApplicationContext().bindService(i,connection, Context.BIND_AUTO_CREATE);
            if(isBound == true){
               Toast.makeText(this,"bounded successfully",Toast.LENGTH_SHORT).show();
           }else {
                Toast.makeText(this,"failed to bound",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //when server connected
            myAIDLInterfaceClient = MyAIDLInterfaceClient.Stub.asInterface(iBinder);

            Toast.makeText(MainActivity.this,"connected to server",Toast.LENGTH_SHORT);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
           //when server disconnected
            myAIDLInterfaceClient = null;
            Toast.makeText(MainActivity.this,"disconnected to server",Toast.LENGTH_SHORT);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unbind service
        unbindService(connection);
        myAIDLInterfaceClient = null;
    }
}
