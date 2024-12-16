package com.cse215l.p2p;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

	private static final int REQUEST_MANAGE_STORAGE = 2296;
	private static final int REQUEST_NETWORK_PERMISSION = 101;
	
	private HashMap<String, Object> nones = new HashMap<>();
	
	private LinearLayout linear1;
	private LinearLayout linear2;
	private LinearLayout linear3;
	private LinearLayout linear4;
	private LinearLayout linear5;
	private LinearLayout linear6;
	private LinearLayout linear7;
	private LinearLayout linear8;
	private LinearLayout linear9;
	private LinearLayout linear10;
	private LinearLayout linear11;
	private LinearLayout linear12;
	private LinearLayout linear13;
	private LinearLayout linear14;
	private TextView textview1;
	private Button button1;
	private Button button2;
	private TextView textview2;
	
	private AlertDialog.Builder welcome;
	private final Intent send = new Intent();
	private final Intent rcv = new Intent();
	private AlertDialog.Builder warn;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		checkAndRequestPermissions();
		
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
		|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
		} else {
			initializeLogic();
		}

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectNetwork()
				.penaltyLog()
				.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedClosableObjects()
				.penaltyLog()
				.build());

	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}


	public void checkAndRequestPermissions() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			// Check if "All Files Access" permission is granted
			if (!Environment.isExternalStorageManager()) {
				try {
					Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
					intent.setData(Uri.parse("package:" + getPackageName()));
					startActivityForResult(intent, REQUEST_MANAGE_STORAGE);
				} catch (Exception e) {
					Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
					startActivityForResult(intent, REQUEST_MANAGE_STORAGE);
				}
			} else {
				Utils.showMessage(this, "All Files Access Permission Already Granted");
			}
		} else {
			// Request traditional storage permissions for Android < 11
			ActivityCompat.requestPermissions(
					this,
					new String[]{
							Manifest.permission.READ_EXTERNAL_STORAGE,
							Manifest.permission.WRITE_EXTERNAL_STORAGE
					},
					REQUEST_NETWORK_PERMISSION
			);
		}
	}



	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = findViewById(R.id.linear1);
		linear2 = findViewById(R.id.linear2);
		linear3 = findViewById(R.id.linear3);
		linear4 = findViewById(R.id.linear4);
		linear5 = findViewById(R.id.linear5);
		linear6 = findViewById(R.id.linear6);
		linear7 = findViewById(R.id.linear7);
		linear8 = findViewById(R.id.linear8);
		linear9 = findViewById(R.id.linear9);
		linear10 = findViewById(R.id.linear10);
		linear11 = findViewById(R.id.linear11);
		linear12 = findViewById(R.id.linear12);
		linear13 = findViewById(R.id.linear13);
		linear14 = findViewById(R.id.linear14);
		textview1 = findViewById(R.id.textview1);
		button1 = findViewById(R.id.button1);
		button2 = findViewById(R.id.button2);
		textview2 = findViewById(R.id.textview2);
		welcome = new AlertDialog.Builder(this);
		warn = new AlertDialog.Builder(this);
		
		button1.setOnClickListener(_view -> {
            send.setClass(getApplicationContext(), SenderActivity.class);
            startActivity(send);
        });
		
		button2.setOnClickListener(_view -> {
            rcv.setClass(getApplicationContext(), RcvActivity.class);
            startActivity(rcv);
        });
	}
	
	private void initializeLogic() {
		FileUtil.makeDir(FileUtil.getPackageDataDir(getApplicationContext()));
		welcome.setTitle("Notice");
		welcome.setMessage("This is just a test version. Might contain a lot of bugs !");
		welcome.setPositiveButton("Understood", (_dialog, _which) -> Utils.showMessage(getApplicationContext(), "Enjoy"));
		welcome.create().show();
		nones = new Gson().fromJson("{}", new TypeToken<HashMap<String, Object>>(){}.getType());
	}
	
	@Override
	public void onBackPressed() {
		warn.setTitle("Note");
		warn.setMessage("Do you really want to quit?");
		warn.setPositiveButton("Yes", (_dialog, _which) -> finish());
		warn.setNegativeButton("No", (_dialog, _which) -> Utils.showMessage(getApplicationContext(), "Ok"));
		warn.create().show();
	}
}
