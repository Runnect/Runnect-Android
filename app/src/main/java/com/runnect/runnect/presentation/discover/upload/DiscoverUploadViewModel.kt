package com.runnect.runnect.presentation.discover.upload

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.runnect.runnect.util.extension.addSourceList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiscoverUploadViewModel @Inject constructor(): ViewModel() {
    val title = MutableLiveData<String>()
    val desc = MutableLiveData<String>()

    val isUploadEnable = MediatorLiveData<Boolean>()

    init {
        isUploadEnable.value = false
        isUploadEnable.apply {
            addSourceList(title, desc) { checkIsUploadEnable() }
        }
    }

    private fun checkIsUploadEnable(): Boolean {
        return !(title.value.isNullOrEmpty() or desc.value.isNullOrEmpty())
    }
}
