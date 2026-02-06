package com.pankajgadge.impl

import com.pankajgadge.api.DispatcherProvider
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider {
    val io = Dispatchers.IO
    val main = Dispatchers.Main
    val default = Dispatchers.Default
}