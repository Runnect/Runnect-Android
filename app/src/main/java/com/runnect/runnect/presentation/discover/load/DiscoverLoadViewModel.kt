package com.runnect.runnect.presentation.discover.load

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DiscoverLoadViewModel @Inject constructor(): ViewModel() {
    private var _idSelectedItem: MutableLiveData<Int> = MutableLiveData(0)
    val idSelectedItem: LiveData<Int>
        get() = _idSelectedItem

    fun checkSelectEnable(id: Int) {
        Timber.d("3. 선택된 아이템의 아이디값을 Adapter로부터 받아와서 라이브 데이터 변경")
        _idSelectedItem.value = id
    }

}