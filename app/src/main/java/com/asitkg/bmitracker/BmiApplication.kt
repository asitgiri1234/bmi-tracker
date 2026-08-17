package com.asitkg.bmitracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. Annotating with [HiltAndroidApp] generates the
 * dependency container that every @AndroidEntryPoint component draws from.
 */
@HiltAndroidApp
class BmiApplication : Application()
