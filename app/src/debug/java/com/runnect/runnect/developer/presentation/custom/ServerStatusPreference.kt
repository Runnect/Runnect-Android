package com.runnect.runnect.developer.presentation.custom

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.runnect.runnect.R
import com.runnect.runnect.developer.enum.ServerStatus

class ServerStatusPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = androidx.preference.R.attr.preferenceStyle,
    defStyleRes: Int = 0
) : Preference(context, attrs, defStyleAttr, defStyleRes) {

    init {
        widgetLayoutResource = R.layout.pref_server_status_layout
    }

    private var serverStatus: ServerStatus = ServerStatus.CHECKING

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val indicator: View = holder.findViewById(R.id.viewIndicator)
        val statusText: TextView = holder.findViewById(R.id.tvStatus) as TextView
        val background = indicator.background as? GradientDrawable

        holder.itemView.post {
            background?.setColor(serverStatus.getColor(context))
            statusText.text = serverStatus.statusText
            summary = serverStatus.summary
        }
    }

    fun setServerStatus(status: ServerStatus) {
        serverStatus = status
        notifyChanged()
    }
}