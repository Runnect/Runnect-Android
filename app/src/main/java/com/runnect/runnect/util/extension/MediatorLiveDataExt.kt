package com.runnect.runnect.util.extension

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

// MediatorLiveData 구현 시 addSource의 중복을 피하기 위해서 만든 확장함수
// vararg liveDataArgument로 여러개의 LiveData를 인자로 받을 수 있다.
// forEach로 인자를 돌아가면서 onChanged의 로직을 실행
fun <T> MediatorLiveData<T>.addSourceList(vararg liveDataArgument: MutableLiveData<*>, onChanged: () -> T){
    liveDataArgument.forEach {
        addSource(it){value = onChanged()}
    }
}