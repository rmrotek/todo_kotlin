package com.fofism.todo

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.fofism.todo.db.TaskDbHelper


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var mHelper: TaskDbHelper
    private lateinit var mTaskListView: ListView
    private var mAdapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTaskListView = findViewById(R.id.list_todo)

        mHelper = TaskDbHelper(this)
        updateUI()
    }

    fun deleteTask(view: View) {
        val parent = view.parent as View
        val taskTextView = parent.findViewById<TextView>(R.id.task_title)
        val task = taskTextView.text.toString()
        val db = mHelper.writableDatabase
        db.delete(TaskContract.TaskEntry.TABLE,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                arrayOf(task))
        db.close()
        updateUI()
    }

    private fun updateUI() {
        val taskList = ArrayList<String>()
        val db = mHelper.readableDatabase
        val cursor = db.query(TaskContract.TaskEntry.TABLE,
                arrayOf(TaskContract.TaskEntry.ID, TaskContract.TaskEntry.COL_TASK_TITLE), null, null, null, null, null)
        while (cursor.moveToNext()) {
            val idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE)
            taskList.add(cursor.getString(idx))
        }

        if (mAdapter == null) {
            mAdapter = ArrayAdapter(this,
                    R.layout.item_todo,
                    R.id.task_title,
                    taskList)
            mTaskListView.adapter = mAdapter
        } else {
            mAdapter?.clear()
            mAdapter?.addAll(taskList)
            mAdapter?.notifyDataSetChanged()
        }

        cursor.close()
        db.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_task -> {
                val taskEditText = EditText(this)
                val dialog = AlertDialog.Builder(this)
                        .setTitle("Dodaj nowe zadanie")
                        .setView(taskEditText)
                        .setPositiveButton("Dodaj") { _, _ ->
                            val task = taskEditText.text.toString()
                            val db = mHelper.writableDatabase
                            val values = ContentValues()
                            values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task)
                            db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                                    null,
                                    values,
                                    SQLiteDatabase.CONFLICT_REPLACE)
                            db.close()

                            Log.d(TAG, "Dodaj zadanie: $task")
                            updateUI()
                        }
                        .setNegativeButton("Anuluj", null)
                        .create()
                dialog.show()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}
