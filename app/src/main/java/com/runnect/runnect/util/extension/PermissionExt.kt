package com.runnect.runnect.util.extension

import android.Manifest
import android.content.Context
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
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
                        "위치 권한이 거부되었습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            .setRationaleTitle(CourseMainFragment.PERMISSION_TITLE)
            .setRationaleMessage(CourseMainFragment.PERMISSION_CONTENT)
            .setDeniedMessage(CourseMainFragment.PERMISSION_GUIDE)
            .setPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .check()
    }
}