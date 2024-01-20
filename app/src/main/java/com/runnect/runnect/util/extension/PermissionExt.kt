package com.runnect.runnect.util.extension

import android.Manifest
import android.content.Context
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.runnect.runnect.R

object PermissionUtil {
    fun requestLocationPermission(
        context: Context,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit = {
            Toast.makeText(
                context,
                R.string.common_permission_denied,
                Toast.LENGTH_SHORT
            ).show()
        },
        permissionType: PermissionType
    ) {
        val permission = setUpPermissionByType(permissionType)
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    onPermissionGranted()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    onPermissionDenied()
                }
            })
            .setRationaleTitle(permission.title)
            .setRationaleMessage(permission.content)
            .setDeniedMessage(permission.guide)
            .setPermissions(*permission.permissions.toTypedArray())
            .check()
    }

    enum class PermissionType {
        LOCATION,
        // 필요한 권한 있을 시 추가
    }

    data class PermissionInfo(
        val permissions: List<String>,
        val title: Int,
        val content: Int,
        val guide: Int
    )

    private fun setUpPermissionByType(permissionType: PermissionType): PermissionInfo {
        return when (permissionType) {
            PermissionType.LOCATION -> {
                PermissionInfo(
                    listOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    R.string.location_permission_title,
                    R.string.location_permission_content,
                    R.string.location_permission_guide
                )
            }
        }
    }
}