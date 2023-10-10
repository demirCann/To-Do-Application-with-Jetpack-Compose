package com.demircandemir.todoapp.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.demircandemir.todoapp.data.models.Priority
import com.demircandemir.todoapp.data.models.Task
import com.demircandemir.todoapp.data.repositories.DataStoreRepository
import com.demircandemir.todoapp.data.repositories.ToDoRepo
import com.demircandemir.todoapp.util.Action
import com.demircandemir.todoapp.util.RequestState
import com.demircandemir.todoapp.util.SearchAppBarState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: ToDoRepo,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {


    val action: MutableState<Action> = mutableStateOf(Action.NO_ACTION)


    val id: MutableState<Int> = mutableStateOf(0)
    val title: MutableState<String> = mutableStateOf("")
    val description: MutableState<String> = mutableStateOf("")
    val priority: MutableState<Priority> = mutableStateOf(Priority.Low)



    var searchAppBarState : MutableState<SearchAppBarState> =
        mutableStateOf(SearchAppBarState.CLOSED)
    var searchTextState : MutableState<String> = mutableStateOf("")

    private val _searchedTasks = MutableStateFlow<RequestState<List<Task>>>(RequestState.Idle)
    val searchedTasks: StateFlow<RequestState<List<Task>>> = _searchedTasks

    fun searchDatabase(searchQuery: String) {
        _searchedTasks.value = RequestState.Loading
        try {
            viewModelScope.launch {
                repository.searchDatabase(searchQuery = "%$searchQuery%")
                    .collect { searchedTasks ->
                        _searchedTasks.value = RequestState.Success(searchedTasks)
                    }
            }
        } catch (e: Exception) {
            _searchedTasks.value = RequestState.Error(e)
        }
        searchAppBarState.value = SearchAppBarState.TRİGGERED
    }

    val lowPriorityTasks: StateFlow<List<Task>> =
        repository.sortByLowPriority.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )
    val highPriorityTasks: StateFlow<List<Task>> =
        repository.sortByHighPriority.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )



    private val _sortState = MutableStateFlow<RequestState<Priority>>(RequestState.Idle)
    val sortState: StateFlow<RequestState<Priority>> = _sortState

   fun readSortState() {
       _sortState.value = RequestState.Loading
       try {
           viewModelScope.launch {
               dataStoreRepository.readSortState
                   .map { Priority.valueOf(it) }
                   .collect {
                       _sortState.value = RequestState.Success(it)
                   }
           }
       } catch (e: Exception) {
           _sortState.value = RequestState.Error(error = e)
       }
   }


    fun persistSortState(priority: Priority) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("sortState", "persistSort${priority}")
            dataStoreRepository.persistSortState(priority = priority)
        }
    }


    private val _allTasks = MutableStateFlow<RequestState<List<Task>>>(RequestState.Idle)
    val allTasks: StateFlow<RequestState<List<Task>>> = _allTasks

    fun getAllTasks(){
        _allTasks.value = RequestState.Loading
        try {
            viewModelScope.launch {
                repository.getAllTasks.collect {
                    _allTasks.value = RequestState.Success(it)
                }
            }
        } catch (e: Exception) {
            _allTasks.value = RequestState.Error(e)
        }
    }

    private val _selectedTask: MutableStateFlow<Task?> = MutableStateFlow(null)
    val selectedTask: StateFlow<Task?> = _selectedTask

    fun getSelectedTask(taskId: Int) {
        viewModelScope.launch {
            repository.getSelectedTask(taskId = taskId).collect { task ->
                _selectedTask.value = task
            }
        }
    }

    fun handleDatabaseActions(action: Action) {
        when(action) {
            Action.ADD -> {
                addTask()
            }
            Action.UPDATE -> {
                updateTask()
            }
            Action.DELETE -> {
                deleteTask()
            }
            Action.DELETE_ALL-> {
                deleteAllTasks()
            }
            Action.UNDO -> {
                addTask()
            }
            else -> {

            }
        }
        this.action.value = Action.NO_ACTION
    }

    private fun addTask() {
        viewModelScope.launch(Dispatchers.IO) {
            val task = Task(
                title = title.value,
                description = description.value,
                priority = priority.value
            )
            repository.addTask(task = task)
        }
    }

    private fun updateTask() {
        viewModelScope.launch(Dispatchers.IO) {
            val task = Task(
                id = id.value,
                title = title.value,
                description = description.value,
                priority = priority.value
            )
            repository.updateTask(task = task)
        }
    }

    private fun deleteTask() {
        viewModelScope.launch(Dispatchers.IO) {
            val task = Task(
                id = id.value,
                title = title.value,
                description = description.value,
                priority = priority.value
            )
            repository.deleteTask(task = task)
        }
    }

    private fun deleteAllTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllTasks()
        }
    }




    fun updateTaskFields(selectedTask: Task?) {
        if(selectedTask != null) {
            id.value = selectedTask.id
            title.value = selectedTask.title
            description.value = selectedTask.description
            priority.value = selectedTask.priority
        } else {
            // Log.d("selected","ADDTASK")
            id.value = 0
            title.value = ""
            description.value = ""
            priority.value = Priority.Low
        }
    }

    fun validateFields() : Boolean{
        return title.value.isNotEmpty() && description.value.isNotEmpty()
    }


}