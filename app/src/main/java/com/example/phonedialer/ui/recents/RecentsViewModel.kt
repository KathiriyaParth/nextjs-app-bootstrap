package com.example.phonedialer.ui.recents

import android.app.Application
import android.database.Cursor
import android.provider.CallLog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

data class CallLogEntry(
    val number: String,
    val name: String?,
    val date: Date,
    val duration: Long,
    val type: Int
)

class RecentsViewModel(application: Application) : AndroidViewModel(application) {
    private val _recentCalls = MutableLiveData<List<CallLogEntry>>()
    val recentCalls: LiveData<List<CallLogEntry>> = _recentCalls

    fun loadRecentCalls() {
        viewModelScope.launch(Dispatchers.IO) {
            val calls = mutableListOf<CallLogEntry>()
            
            getApplication<Application>().contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                arrayOf(
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.CACHED_NAME,
                    CallLog.Calls.DATE,
                    CallLog.Calls.DURATION,
                    CallLog.Calls.TYPE
                ),
                null,
                null,
                "${CallLog.Calls.DATE} DESC"
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    calls.add(cursor.toCallLogEntry())
                }
            }
            
            _recentCalls.postValue(calls)
        }
    }

    private fun Cursor.toCallLogEntry(): CallLogEntry {
        return CallLogEntry(
            number = getString(getColumnIndexOrThrow(CallLog.Calls.NUMBER)),
            name = getString(getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)),
            date = Date(getLong(getColumnIndexOrThrow(CallLog.Calls.DATE))),
            duration = getLong(getColumnIndexOrThrow(CallLog.Calls.DURATION)),
            type = getInt(getColumnIndexOrThrow(CallLog.Calls.TYPE))
        )
    }
}
