package com.demircandemir.todoapp.data.models

import androidx.room.Database
import androidx.room.RoomDatabase
import com.demircandemir.todoapp.data.ToDoDao

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {
    abstract fun toDoDao() : ToDoDao
}