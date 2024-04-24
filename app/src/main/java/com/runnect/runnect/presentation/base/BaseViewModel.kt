package com.runnect.runnect.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.net.SocketException
import java.net.UnknownHostException

abstract class BaseViewModel : ViewModel() {

    open val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.tag(throwable::class.java.simpleName).e(throwable)

        when (throwable) {
            is SocketException,
            is HttpException,
            is UnknownHostException -> {
                Timber.e(throwable)
            }
            else -> {
                Timber.e(throwable)
            }
        }
    }

    fun ViewModel.launchWithHandler(
        dispatcher: CoroutineDispatcher = Dispatchers.Main,
        exceptionHandler: CoroutineExceptionHandler = this@BaseViewModel.exceptionHandler,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch(dispatcher + exceptionHandler, start, block)
    }
}