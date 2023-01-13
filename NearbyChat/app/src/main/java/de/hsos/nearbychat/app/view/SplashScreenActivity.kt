package de.hsos.nearbychat.app.view

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import de.hsos.nearbychat.R


class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        if(!checkPermissions()) {
            return
        }
        if(!checkCompatibility()) {
            return
        }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for(result in grantResults) {
            if(result != PackageManager.PERMISSION_GRANTED) {
                findViewById<TextView>(R.id.splash_screen_text).setText(R.string.permissions_missing)
                return
            }
        }
        if(!checkCompatibility()) {
            return
        }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun checkPermissions() : Boolean {
        val permissionNeeded = mutableListOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if(Build.VERSION.SDK_INT > 30) {
            permissionNeeded.add(Manifest.permission.BLUETOOTH_ADVERTISE)
            permissionNeeded.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissionNeeded.add(Manifest.permission.BLUETOOTH_SCAN)
            permissionNeeded.add(Manifest.permission.FOREGROUND_SERVICE)
        }

        val missingPermissions: MutableList<String> = mutableListOf()
        for (permission in permissionNeeded) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        if (missingPermissions.size > 0) {
            requestPermissions(missingPermissions.toTypedArray(), 0)
            return false
        }
        return true
    }

    private fun checkCompatibility(): Boolean {
        val bluetoothManager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        if(bluetoothManager.adapter.isLe2MPhySupported && bluetoothManager.adapter.isLeCodedPhySupported) {
            return true;
        }
        findViewById<TextView>(R.id.splash_screen_text).setText(R.string.not_compatible)
        return false
    }
}