package com.demircandemir.todoapp.data.repositories

import com.demircandemir.todoapp.data.ToDoDao
import com.demircandemir.todoapp.data.models.Task
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class ToDoRepo @Inject constructor(private val toDoDao: ToDoDao) {

    val getAllTasks: Flow<List<Task>> = toDoDao.getAllTasks()
    val sortByLowPriority: Flow<List<Task>> = toDoDao.sortByLowPriority()
    val sortByHighPriority: Flow<List<Task>> = toDoDao.sortByHighPriority()

    fun getSelectedTask(taskId : Int) : Flow<Task> {
        return toDoDao.getSelectedTask(taskId = taskId)
    }

    suspend fun addTask(task: Task) {
        toDoDao.addTask(task = task)
    }

    suspend fun updateTask(task: Task) {
        toDoDao.updateTask(task = task)
    }

    suspend fun deleteTask(task: Task) {
        toDoDao.deleteTask(task = task)
    }

    suspend fun deleteAllTasks(){
        toDoDao.deleteAllTasks()
    }

    fun searchDatabase(searchQuery : String): Flow<List<Task>> {
        return toDoDao.searchDatabase(searchQuery = searchQuery)
    }

}