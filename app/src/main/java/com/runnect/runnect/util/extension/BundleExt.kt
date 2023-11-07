package com.runnect.runnect.util.extension

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import java.io.Serializable

inline fun <reified T : Serializable> Bundle.getCompatibleSerializableExtra(key: String): T? = when {
    SDK_INT >= TIRAMISU -> getSerializable(key, T::class.java)
    else -> getSerializable(key) as? T
}
