package com.example.phonedialer.ui.settings

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _isDarkMode = MutableLiveData<Boolean>()
    val isDarkMode: LiveData<Boolean> = _isDarkMode

    private val _isVibrationEnabled = MutableLiveData<Boolean>()
    val isVibrationEnabled: LiveData<Boolean> = _isVibrationEnabled

    private val _isSoundEnabled = MutableLiveData<Boolean>()
    val isSoundEnabled: LiveData<Boolean> = _isSoundEnabled

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _isDarkMode.value = prefs.getBoolean(KEY_DARK_MODE, false)
        _isVibrationEnabled.value = prefs.getBoolean(KEY_VIBRATION, true)
        _isSoundEnabled.value = prefs.getBoolean(KEY_SOUND, true)
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            _isDarkMode.value = enabled
            prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (enabled) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _isVibrationEnabled.value = enabled
            prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply()
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _isSoundEnabled.value = enabled
            prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
        }
    }

    companion object {
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_VIBRATION = "vibration"
        private const val KEY_SOUND = "sound"
    }
}
