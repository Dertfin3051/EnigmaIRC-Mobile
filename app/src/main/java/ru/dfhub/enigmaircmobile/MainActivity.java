package ru.dfhub.enigmaircmobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.File;

import ru.dfhub.enigmaircmobile.eirc.Config;
import ru.dfhub.enigmaircmobile.eirc.util.Encryption;

public class MainActivity extends AppCompatActivity {

    public static File FILES_DIR;

    private EditText inputName, inputServerAddr, inputServerPort, inputSecurityKey;
    private ImageButton generateSecretKeyButton;
    private Button saveAndStartButton;

    private JSONObject config;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_settings_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FILES_DIR = getFilesDir();
        getActivityElements();
        config = Config.getConfig();

        fillInputsWithConfigData();

        saveAndStartButton.setOnClickListener(view -> {
            try {
                saveConfigWithInputData();
                Intent intent = new Intent(this, MessagingActivity.class);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                TextView tw = findViewById(R.id.textView2);
                tw.setText(e.getLocalizedMessage());
                e.printStackTrace();
            }
        });

        generateSecretKeyButton.setOnClickListener(view -> {
            String securityKey = Encryption.generateNewKey();
            inputSecurityKey.setText(securityKey);
        });
    }

    private void getActivityElements() {
        inputName = findViewById(R.id.input_name);
        inputServerAddr = findViewById(R.id.input_server_address);
        inputServerPort = findViewById(R.id.input_server_port);
        inputSecurityKey = findViewById(R.id.input_security_key);
        generateSecretKeyButton = findViewById(R.id.generate_security_key);
        saveAndStartButton = findViewById(R.id.save_and_start_button);
    }

    /**
     * Substitute the config values into the input fields (if they were previously specified)
     */
    private void fillInputsWithConfigData() {
        inputName.setText(config.optString("username", ""));
        inputServerAddr.setText(config.optString("server-address", ""));
        inputServerPort.setText(config.optString("server-port", ""));
        inputSecurityKey.setText(config.optString("security-key", ""));
    }

    private void saveConfigWithInputData() throws Exception {
        JSONObject newConfig = new JSONObject();
        newConfig.put("username", inputName.getText().toString());
        newConfig.put("server-address", inputServerAddr.getText().toString());
        newConfig.put("server-port", Integer.parseInt(inputServerPort.getText().toString()));
        newConfig.put("security-key", inputSecurityKey.getText().toString());
        Config.saveConfig(newConfig, getApplicationContext());
    }
}