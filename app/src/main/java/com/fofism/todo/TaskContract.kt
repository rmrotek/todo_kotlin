package com.fofism.todo

import android.provider.BaseColumns

class TaskContract {
    companion object {
        const val DB_NAME = "com.fofism.todo.db"
        const val DB_VERSION = 1
    }

    class TaskEntry : BaseColumns {

        companion object {
            const val TABLE = "tasks"
            const val COL_TASK_TITLE = "title"
            const val ID = BaseColumns._ID
        }
    }
}