package com.beta.xposed;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.beta.xposed.sp.SPreference;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_RESULT_CODE = 100;

    EditText editText;
    TextView mPkgName;
    Button mOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_RESULT_CODE);
        editText = findViewById(R.id.et_package_name);
        mPkgName = findViewById(R.id.tv_package_name);
        SharedPreferences prefs = SPreference.INSTANCE.pref(this);
        if (prefs != null) {
            mPkgName.setText(prefs.getString(Const.pref_key_pkgname, ""));
        }
        mOk = findViewById(R.id.bt_ok);
        mOk.setOnClickListener(v -> {
            mPkgName.setText(editText.getText());
            if (prefs != null) {
                prefs.edit().putString(Const.pref_key_pkgname, mPkgName.getText().toString()).apply();
            }
        });
        RadioGroup radioGroup = findViewById(R.id.webview_group);
        boolean debug = prefs.getBoolean(Const.pref_key_debug_wv, false);
        radioGroup.check(debug ? R.id.debug_wv_yes : R.id.debug_wv_no);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean debug;
                if (checkedId == R.id.debug_wv_yes) {
                    debug = true;
                } else {
                    debug = false;
                }
                prefs.edit().putBoolean(Const.pref_key_debug_wv, debug).apply();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PERMISSION_RESULT_CODE == requestCode) {
            int index = Arrays.asList(permissions).indexOf(Manifest.permission.READ_PHONE_STATE);
            Log.d("wbs", "onRequestPermissionsResult: index: " + grantResults[index]);
        }
    }
}
