package com.example.remindme.screens.edit

import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.remindme.database.People
import com.example.remindme.database.PeopleDao
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class AddPersonViewModel(
    val dataSource: PeopleDao,
    val application: Application
) : ViewModel() {

    private val _firstName = MutableLiveData<String>("")
    val firstName: LiveData<String>
        get() = _firstName

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main+viewModelJob)

    init {
        Log.i("AddPersonViewModel","AddPersonViewModel started!")
    }


    private suspend fun insert(result: People){
        withContext(Dispatchers.IO){
            dataSource.insert(result)
        }
    }
    fun insertPerson(result:People) {
        uiScope.launch {
            insert(result)
        }
    }

    override fun onCleared() {
        super.onCleared()
        uiScope.cancel()
    }
    companion object{
        private const val TAG = "AddPersonViewModel"
    }
}