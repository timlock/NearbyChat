package de.hsos.nearbychat.app.view

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.viewmodel.NearbyChatObserver
import de.hsos.nearbychat.app.viewmodel.NearbyChatServiceCon
import de.hsos.nearbychat.common.domain.Profile
import de.hsos.nearbychat.service.controller.NearbyChatService


class SplashScreenActivity : AppCompatActivity() {
    private val TAG: String = SplashScreenActivity::class.java.simpleName

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var locationManager: LocationManager

    private var alreadyTriedPermissionRequest = false

    private var btIntentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            proceedCheck()
        }

    private var gpsIntentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            proceedCheck()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        if (intent?.action == NearbyChatService.ACTION_SHUTDOWN) {
            this.shutdownService(intent)
            finishAffinity()
            return
        }
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        supportActionBar?.hide()

        proceedCheck()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy() called")
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        alreadyTriedPermissionRequest = true

        proceedCheck()
    }

    private fun proceedCheck() {
        if (!checkPermissions()) {
            requestMissingPermissions()
            return
        }
        if (!checkBtCompatibility()) {
            return
        }
        if (!checkBtEnabled()) {
            requestBtEnable()
            return
        }
        if (!checkBtLeCompatibility()) {
            findViewById<TextView>(R.id.splash_screen_text).setText(R.string.not_compatible)
            return
        }
        if (!checkGpsEnabled()) {
            requestGpsEnable()
            return
        }
        startApp()
    }

    private fun checkPermissions(): Boolean {
        return getMissingPermissions().isEmpty()
    }

    private fun requestMissingPermissions() {
        Log.d(TAG, "requestMissingPermissions: ")
        if (!alreadyTriedPermissionRequest) {
            requestPermissions(getMissingPermissions().toTypedArray(), 0)
        } else {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.request_permissions)
            builder.setCancelable(false)
            builder.setPositiveButton(R.string.ok) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            builder.show()
        }
    }

    private fun checkBtCompatibility(): Boolean {
        return bluetoothManager.adapter != null
    }

    private fun checkBtEnabled(): Boolean {
        return bluetoothManager.adapter.isEnabled
    }

    private fun requestBtEnable() {
        Log.d(TAG, "requestBtEnable: ")
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        btIntentLauncher.launch(enableBtIntent)
    }

    private fun checkBtLeCompatibility(): Boolean {
        return bluetoothManager.adapter.isLe2MPhySupported && bluetoothManager.adapter.isLeCodedPhySupported
    }

    private fun checkGpsEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun requestGpsEnable() {
        Log.d(TAG, "requestGpsEnable: ")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.request_gps)
        builder.setCancelable(false)
        builder.setPositiveButton(R.string.ok) { _, _ ->
            val enableGpsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            gpsIntentLauncher.launch(enableGpsIntent)
        }
        builder.show()
    }

    private fun startApp() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getMissingPermissions(): List<String> {
        val permissionNeeded = mutableListOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )

        if (Build.VERSION.SDK_INT > 30) {
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
        return missingPermissions
    }

    override fun onNewIntent(intent: Intent?) {
        Log.d(TAG, "onNewIntent() called with: intent = $intent")
        super.onNewIntent(intent)
        this.shutdownService(intent)
        this.shutdownService(intent)
        finishAffinity()
        return
    }

    private fun shutdownService(intent: Intent?) {
        Log.d(TAG, "shutdownService() called")
        val nearbyChatServiceCon = NearbyChatServiceCon(null)
        nearbyChatServiceCon.closeService(this)
    }
}