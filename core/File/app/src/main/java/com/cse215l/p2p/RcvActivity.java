package com.cse215l.p2p;

import android.content.*;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

public class RcvActivity extends AppCompatActivity {
	
	private Toolbar _toolbar;
	private AppBarLayout _app_bar;
	private CoordinatorLayout _coordinator;
	
	private LinearLayout linear1;
	private LinearLayout linear2;
	private LinearLayout linear3;
	private LinearLayout linear4;
	private LinearLayout linear5;
	private LinearLayout linear6;
	private LinearLayout linear7;
	private LinearLayout linear9;
	private LinearLayout linear10;
	private TextView textview1;
	private TextView textview2;
	private TextView textview3;
	private TextView textview4;
	private TextView lan_ip;
	private Button button1;
	private TextView textview5;
	private TextView pair_code;
	private Button start;
	private TextView textview9;
	private ScrollView vscroll2;
	private TextView cs_text;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.rcv);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		_app_bar = findViewById(R.id._app_bar);
		_coordinator = findViewById(R.id._coordinator);
		_toolbar = findViewById(R.id._toolbar);
		setSupportActionBar(_toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _v) {
				onBackPressed();
			}
		});
		linear1 = findViewById(R.id.linear1);
		linear2 = findViewById(R.id.linear2);
		linear3 = findViewById(R.id.linear3);
		linear4 = findViewById(R.id.linear4);
		linear5 = findViewById(R.id.linear5);
		linear6 = findViewById(R.id.linear6);
		linear7 = findViewById(R.id.linear7);
		linear9 = findViewById(R.id.linear9);
		linear10 = findViewById(R.id.linear10);
		textview1 = findViewById(R.id.textview1);
		textview2 = findViewById(R.id.textview2);
		textview3 = findViewById(R.id.textview3);
		textview4 = findViewById(R.id.textview4);
		lan_ip = findViewById(R.id.lan_ip);
		button1 = findViewById(R.id.button1);
		textview5 = findViewById(R.id.textview5);
		pair_code = findViewById(R.id.pair_code);
		start = findViewById(R.id.start);
		textview9 = findViewById(R.id.textview9);
		vscroll2 = findViewById(R.id.vscroll2);
		cs_text = findViewById(R.id.cs_text);
		
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
                getApplicationContext();
                ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", lan_ip.getText().toString()));
				Utils.showMessage(getApplicationContext(), "IP Copied!");
			}
		});
		
		start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				
			}
		});
	}
	
	private void initializeLogic() {
		/*


// import java.net.InetAddress;

//InetAddress localIP = InetAddress.getLocalHost();

try{
           
lan_ip.setText(java.net.InetAddress.getLocalHost().getHostAddress());

} catch(java.net.UnknownHostException e){
  lan_ip.setText("Failed to get IP ! ");
}*/
		Context context = getApplicationContext();
		android.net.wifi.WifiManager wm = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
		int ipAddress = wm.getConnectionInfo().getIpAddress();
		String ip = String.format(
		    "%d.%d.%d.%d",
		    (ipAddress & 0xff),
		    (ipAddress >> 8 & 0xff),
		    (ipAddress >> 16 & 0xff),
		    (ipAddress >> 24 & 0xff)
		);
		
		lan_ip.setText(ip);
		
	}
	
}
