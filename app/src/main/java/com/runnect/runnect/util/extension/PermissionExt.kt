package com.runnect.runnect.util.extension

import android.Manifest
import android.content.Context
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.runnect.runnect.R
import com.runnect.runnect.presentation.coursemain.CourseMainFragment

object PermissionUtil {
    fun requestLocationPermission(context: Context, onPermissionGranted: () -> Unit) {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    onPermissionGranted()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(
                        context,
                        R.string.location_permission_denied,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            .setRationaleTitle(R.string.location_permission_title)
            .setRationaleMessage(R.string.location_permission_content)
            .setDeniedMessage(R.string.location_permission_guide)
            .setPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .check()
    }
}