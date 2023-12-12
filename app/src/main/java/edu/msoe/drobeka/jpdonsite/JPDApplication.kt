package edu.msoe.drobeka.jpdonsite

import android.app.Application

class JPDApplication : Application() {
    override fun onCreate() {
        super.onCreate()
//        JobRepository.initialize(this)
    }
}