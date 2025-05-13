package com.example.phonedialer.ui.contacts

import android.app.Application
import android.database.Cursor
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val photoUri: String?
)

class ContactsViewModel(application: Application) : AndroidViewModel(application) {
    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> = _contacts

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .collect { query ->
                    loadContacts(query)
                }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun loadContacts(query: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val contacts = mutableListOf<Contact>()
            
            val selection = if (query.isNotEmpty()) {
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
            } else null
            
            val selectionArgs = if (query.isNotEmpty()) {
                arrayOf("%$query%")
            } else null

            getApplication<Application>().contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                ),
                selection,
                selectionArgs,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    contacts.add(cursor.toContact())
                }
            }
            
            _contacts.postValue(contacts)
        }
    }

    private fun Cursor.toContact(): Contact {
        return Contact(
            id = getString(getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)),
            name = getString(getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
            phoneNumber = getString(getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)),
            photoUri = getString(getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
        )
    }
}
