package de.hsos.nearbychat

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat

class PermissionManager {
    companion object {
        private val TAG: String = PermissionManager::class.java.simpleName
        fun permissionCheck(context: Context, activity: Activity) {
            Log.d(TAG, "permissionCheck: ")
            val neededPermissions: MutableList<String> = mutableListOf()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    neededPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
                }
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_ADVERTISE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    neededPermissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
                }
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_SCAN
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    neededPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
                }
            }
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                neededPermissions.add(Manifest.permission.BLUETOOTH)
            }
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    neededPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
                }
            } else if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                neededPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            if (neededPermissions.size > 0) {
                Log.d(TAG, "permissionCheck: requested permissions: $neededPermissions")
                ActivityCompat.requestPermissions(activity, neededPermissions.toTypedArray(), 1)
            }
            Log.d(TAG, "permissionCheck: no permissions requested")
        }
    }
}