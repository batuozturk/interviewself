package com.batuhan.interv.presentation.settings

interface SettingsEventHandler {

    fun writeData(settingsType: SettingsType)

    fun readData(isDarkMode: Boolean, langCode: String, apiKey: String)
}