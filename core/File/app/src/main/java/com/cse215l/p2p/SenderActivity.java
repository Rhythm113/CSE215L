package com.cse215l.p2p;

import android.Manifest;
import android.app.*;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.AppBarLayout;

import java.io.File;
import java.util.*;

public class SenderActivity extends AppCompatActivity {
	
	public final int REQ_CD_SELECTOR = 101;
	
	private Toolbar _toolbar;
	private AppBarLayout _app_bar;
	private CoordinatorLayout _coordinator;
	private String ip = "";
	private String path_final = "";
	private final String old = "";
	private int threads = 0;
	
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
	private LinearLayout linear13;
	private LinearLayout linear14;
	private TextView textview1;
	private TextView textview6;
	private TextView textview2;
	private Button button1;
	private EditText edittext1;
	private TextView textview3;
	private TextView path_text;
	private Button button2;
	private TextView textview4;
	private TextView thr;
	private SeekBar seekbar1;
	private Button snd;
	private TextView textview5;
	private ScrollView vscroll1;
    private static TextView cs_on;
	
	private final Intent selector = new Intent(Intent.ACTION_GET_CONTENT);
	private AlertDialog.Builder global_dl;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.sender);
		initialize(_savedInstanceState);
		
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
		|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
		} else {
			initializeLogic();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
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
		linear8 = findViewById(R.id.linear8);
		linear9 = findViewById(R.id.linear9);
		linear10 = findViewById(R.id.linear10);
		linear11 = findViewById(R.id.linear11);
		linear13 = findViewById(R.id.linear13);
		linear14 = findViewById(R.id.linear14);
		textview1 = findViewById(R.id.textview1);
		textview6 = findViewById(R.id.textview6);
		textview2 = findViewById(R.id.textview2);
		button1 = findViewById(R.id.button1);
		edittext1 = findViewById(R.id.edittext1);
		textview3 = findViewById(R.id.textview3);
		path_text = findViewById(R.id.path_text);
		button2 = findViewById(R.id.button2);
		textview4 = findViewById(R.id.textview4);
		thr = findViewById(R.id.thr);
		seekbar1 = findViewById(R.id.seekbar1);
		snd = findViewById(R.id.snd);
		textview5 = findViewById(R.id.textview5);
		vscroll1 = findViewById(R.id.vscroll1);
		cs_on = findViewById(R.id.cs_on);
		selector.setType("*/*");
		selector.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		global_dl = new AlertDialog.Builder(this);
		
		button1.setOnClickListener(_view -> {
            ip = Clip.getClipboardText(SenderActivity.this);
            edittext1.setText(ip);
        });
		
		button2.setOnClickListener(_view -> startActivityForResult(selector, REQ_CD_SELECTOR));
		
		seekbar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar _param1, int _param2, boolean _param3) {
				final int _progressValue = _param2;
				thr.setText(String.valueOf((long)(_progressValue)));
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar _param1) {
				
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar _param2) {
				
			}
		});
		
		snd.setOnClickListener(_view -> {
            threads = seekbar1.getProgress();
            //log(String.valueOf(threads));
            /*

    `			Sending logic & verification
            */

			showPairCodeDialog(threads);

			//validateAndSend(ip,path_final,threads,);


        });
	}
	
	private void initializeLogic() {
		FileUtil.makeDir(FileUtil.getExternalStorageDir().concat("/Download/P2P"));
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);
		
		switch (_requestCode) {
			case REQ_CD_SELECTOR:
			if (_resultCode == Activity.RESULT_OK) {
				ArrayList<String> _filePath = new ArrayList<>();
				if (_data != null) {
					if (_data.getClipData() != null) {
						for (int _index = 0; _index < _data.getClipData().getItemCount(); _index++) {
							ClipData.Item _item = _data.getClipData().getItemAt(_index);
							_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
						}
					}
					else {
						_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
					}
				}
				path_final = _filePath.get(0);
				_filePath.clear();
				path_text.setText(path_final);
			}
			else {
				path_text.setText("Cancelled !");
			}
			break;
			default:
			break;
		}
	}

	public static void log(String msg){
		cs_on.post(() -> {
			String old = (String) cs_on.getText();
			old += "\n";
			old += msg;
			cs_on.setText(old);
		});

	}

	private void validateAndSend(String ip, String filePath, int threads, String pairCode) {
		if (ip.trim().isEmpty() || filePath.trim().isEmpty() || pairCode.trim().isEmpty()) {
			log("Error: All fields are required!");
			_global_dialog("All fields are required!");
			return;
		}

		if (!Utils.isValidIP(ip)) {
			log("Error: Invalid IP address format!");
			_global_dialog("Invalid IP address format!");
			return;
		}

		File file = new File(filePath);
		if (!file.exists()) {
			log("Error: File does not exist!");
			_global_dialog("File does not exist!");
			return;
		}

		log("Starting file transfer...");

		runOnUiThread(() -> {
			try {
				FileClient client = new FileClient(ip, 5000);
				client.setCode(Integer.parseInt(pairCode));
				try {
					FileServ a = new FileServ(filePath,threads);
					a.split();
				}finally {
					String[] paths = client.getPaths(filePath, threads);
					client.sendFiles(paths, file.getName(), threads);
					log("File transfer initiated successfully.");
				}

			} catch (Exception ex) {
				log("Error initiating file transfer: " + ex.getMessage());
				_global_dialog("Failed to start transfer: " + ex.getMessage());
			}
		});
	}
	
	public void _global_dialog(final String _msg) {
		global_dl.setTitle("Notice");
		global_dl.setMessage(_msg);
		global_dl.setPositiveButton("Ok", (_dialog, _which) -> {

        });
		global_dl.create().show();
	}



	public void showPairCodeDialog(int th) {
		EditText input = new EditText(this);
		input.setHint("Enter 4-digit pair code");

		new AlertDialog.Builder(this)
				.setTitle("Enter Pair Code")
				.setMessage("Please enter a 4-digit pair code:")
				.setView(input)
				.setPositiveButton("OK", (dialog, which) -> {
                    String pairCode = input.getText().toString().trim();

                    if (pairCode.length() == 4 && pairCode.matches("\\d+")) {

                        Utils.showMessage(getApplicationContext(), "Pair Code: " + pairCode);
                        log("Pair code set to : " + pairCode);
						runOnUiThread(() -> {
							validateAndSend(String.valueOf(edittext1.getText()), path_final, th, pairCode);
						});
                    } else {
                        Utils.showMessage(getApplicationContext(), "Invalid pair code. Please enter a 4-digit number.");
                    }
                })
				.setNegativeButton("Cancel", null)
				.show();
	}




}
