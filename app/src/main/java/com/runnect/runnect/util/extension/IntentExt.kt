package com.runnect.runnect.util.extension

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Parcelable
import java.io.Serializable

/** Retrieve parcelable extra data from the intent and support app compatibility */
inline fun <reified T : Parcelable> Intent.getCompatibleParcelableExtra(key: String): T? = when {
    SDK_INT >= TIRAMISU -> getParcelableExtra(key, T::class.java)
    else -> getParcelableExtra(key) as? T
}

inline fun <reified T : Serializable> Intent.getCompatibleSerializableExtra(key: String): T? = when {
    SDK_INT >= TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> getSerializableExtra(key) as? T
}
