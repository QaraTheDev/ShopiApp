package com.example.shopiapp.presentation

import com.test.data.DataApp
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App @Inject constructor() : DataApp() {
}