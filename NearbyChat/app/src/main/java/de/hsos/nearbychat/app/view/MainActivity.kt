package de.hsos.nearbychat.app.view

import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import de.hsos.nearbychat.PermissionManager
import de.hsos.nearbychat.R
import de.hsos.nearbychat.service.bluetooth.MeshController

class MainActivity : AppCompatActivity() {
    private lateinit var bleController: MeshController
    private lateinit var input: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PermissionManager.permissionCheck(this,this)
        val bluetoothManager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager;
        this.bleController = MeshController(bluetoothManager.adapter)
        this.input = findViewById<EditText>(R.id.input)
        findViewById<Button>(R.id.scan).setOnClickListener { this.bleController.startScan() }
        findViewById<Button>(R.id.advertise).setOnClickListener { this.bleController.startAdvertise() }
        findViewById<Button>(R.id.send).setOnClickListener{this.bleController.sendMessage(this.input.text.toString())}
        this.input.keepScreenOn
    }
}