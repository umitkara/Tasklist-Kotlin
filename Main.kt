package tasklist

import java.util.Scanner
import kotlin.system.exitProcess
import kotlinx.datetime.*
import com.squareup.moshi.*
import java.io.File

fun main() {
    Tasklist.loadTasksFromJson()
    while (true) {
        println("Input an action (add, print, edit, delete, end):")
        val scanner = Scanner(System.`in`)
        when (scanner.nextLine().trim()) {
            "add" -> addNewTask(scanner)
            "delete" -> deleteTask(scanner)
            "edit" -> editTask(scanner)
            "print" -> Tasklist.printTasks()
            "end" -> {
                Tasklist.saveTasksIntoJson()
                println("Tasklist exiting!")
                exitProcess(0)
            }

            else -> {
                println("The input action is invalid")
            }
        }
    }
}

/**
 * Top level function to add a new task to the tasklist
 * @param scanner Scanner object to read user input
 */
fun addNewTask(scanner: Scanner) {
    // a variable to hold the task description.
    val taskDescription: MutableList<String> = mutableListOf()
    val newTaskPriority: String = parseTaskPriority(scanner)
    val newTaskDate: String = parseTaskDate(scanner)
    val newTaskTime: String = parseTaskTime(scanner)

    println("Input a new task (enter a blank line to end):")
    // Read until the blank line
    while (scanner.hasNextLine()) {
        // Read a line
        val line = scanner.nextLine()
        // Check if the entered line is empty.
        if (line.isNotBlank()) {
            // Add line to the task description
            taskDescription.add(line.trim())
        } else {
            // If entered line is empty check if the task description is not empty
            if (taskDescription.isNotEmpty()) {
                // Add task description to the list
                Tasklist.addTask(taskDescription, newTaskPriority, newTaskDate, newTaskTime)
            } else {
                // If the task description is empty, print an error message
                println("The task is blank")
            }
            break
        }
    }
}

/**
 * Top level function to delete a task from the tasklist
 * @param scanner Scanner object to read user input
 */
fun deleteTask(scanner: Scanner) {
    if (Tasklist.length > 0) {
        Tasklist.printTasks()
        while (true) {
            println("Input the task number (1-${Tasklist.length}):")
            val taskNumber = scanner.nextLine().trim().toIntOrNull()
            if ((taskNumber != null) && (taskNumber in (1..Tasklist.length))) {
                Tasklist.deleteTask(taskNumber)
                println("The task is deleted")
                break
            } else {
                println("Invalid task number")
            }
        }
    } else {
        println("No tasks have been input")
    }
}

/**
 * Top level function to edit a task from the tasklist
 * @param scanner Scanner object to read user input
 */
fun editTask(scanner: Scanner) {
    // Check if tasklist is not empty
    if (Tasklist.length > 0) {
        Tasklist.printTasks()
        while (true) {
            // As for the task number to edit
            println("Input the task number (1-${Tasklist.length}):")
            val taskNumber = scanner.nextLine().trim().toIntOrNull()
            // Check if the task number is valid
            if ((taskNumber != null) && (taskNumber in (1..Tasklist.length))) {
                while (true) {
                    // Ask for the field to edit
                    println("Input a field to edit (priority, date, time, task):")
                    val editFieldInput = scanner.nextLine().trim()
                    // Check if the field to edit is valid
                    if (editFieldInput in arrayOf("priority", "date", "time", "task")) {
                        when (editFieldInput) {
                            "priority" -> {
                                val newTaskPriority: String = parseTaskPriority(scanner)
                                Tasklist.editTask(taskNumber, editFieldInput, newTaskPriority)
                            }

                            "date" -> {
                                val newTaskDate: String = parseTaskDate(scanner)
                                Tasklist.editTask(taskNumber, editFieldInput, newTaskDate)
                            }

                            "time" -> {
                                val newTaskTime: String = parseTaskTime(scanner)
                                Tasklist.editTask(taskNumber, editFieldInput, newTaskTime)
                            }

                            "task" -> {
                                val taskDescription: MutableList<String> = mutableListOf()
                                println("Input a new task (enter a blank line to end):")
                                // Read until the blank line
                                while (scanner.hasNextLine()) {
                                    // Read a line
                                    val line = scanner.nextLine()
                                    // Check if the entered line is empty.
                                    if (line.isNotBlank()) {
                                        // Add line to the task description
                                        taskDescription.add(line.trim())
                                    } else {
                                        // If entered line is empty check if the task description is not empty
                                        if (taskDescription.isNotEmpty()) {
                                            // Add task description to the list
                                            Tasklist.editTask(taskNumber, editFieldInput, taskDescription)
                                        } else {
                                            // If the task description is empty, print an error message
                                            println("The task is blank")
                                        }
                                        break
                                    }
                                }
                            }
                        }
                        println("The task is changed")
                        // Break the inner, field to edit, loop
                        break
                    } else {
                        println("Invalid field")
                    }
                }
                // Break the outer, task number, loop
                break
            } else {
                println("Invalid task number")
            }
        }
    } else {
        println("No tasks have been input")
    }
}

/**
 * Function to parse the task priority
 * @param scanner Scanner
 * @return String - task priority
 */
fun parseTaskPriority(scanner: Scanner): String {
    var newTaskPriority: String
    while (true) {
        println("Input the task priority (C, H, N, L):")
        newTaskPriority = scanner.nextLine().trim()
        if (newTaskPriority.lowercase() in arrayOf("c", "h", "n", "l")) {
            break
        }
    }
    return newTaskPriority.uppercase()
}

/**
 * Function to parse the task due date
 * @param scanner Scanner
 * @return String - task due date
 */
fun parseTaskDate(scanner: Scanner): String {
    var newTaskDate: String
    while (true) {
        println("Input the date (yyyy-mm-dd):")
        newTaskDate = scanner.nextLine().trim()
        // Try to validate the entered date
        try {
            // Split and unpack the date
            val (year, month, day) = newTaskDate.split("-").map { it.toInt() }
            // Check if the date is valid
            val parsedDate = LocalDate(year, month, day)
            // Convert the date to the string
            newTaskDate = parsedDate.toString()
            break
        } catch (e: Exception) {
            // If the date is invalid, print an error message
            println("The input date is invalid")
        }
    }
    return newTaskDate
}

/**
 * Function to parse the task due time
 * @param scanner Scanner
 * @return String - task due time
 */
fun parseTaskTime(scanner: Scanner): String {
    var newTaskTime: String
    while (true) {
        println("Input the time (hh:mm):")
        newTaskTime = scanner.nextLine().trim()
        // Try to validate the entered time
        try {
            // Split and unpack the time
            val (hour, minute) = newTaskTime.split(":").map { it.toInt() }
            // Parse the time using kotlinx.datetime
            val parsedTime = LocalDateTime(2022, 1, 1, hour, minute)
            // Convert the parsed time to a string
            newTaskTime = parsedTime.toString().split("T")[1].split(":").take(2).joinToString(":")
            break
        } catch (e: Exception) {
            // If the entered time is invalid, print an error message
            println("The input time is invalid")
        }
    }
    return newTaskTime
}

/**
 * Task class which instantiate a task object.
 * Instead, Data Class cloud be used.
 * @param priority task priority character. C, H, N, L: Critical, High, Normal, Low
 * @param date task due date as string: yyyy-mm-dd
 * @param time task due time as string: hh:mm
 * @param taskDescription list of strings which contains the task description, definition, details etc.
 */
class Task(
    val id: Int = 1,
    var taskDescription: MutableList<String>,
    var date: String,
    var time: String,
    var priority: String,
    var dueTag: String = "I"
)

/**
 * Tasklist object which contains the list of tasks.
 */
object Tasklist {
    private val tasks = mutableListOf<Task>()
    private var taskCount = 0
    val length: Int
        get() = tasks.size

    /**
     * Add a task to the list.
     * @param taskDescription the description of the task.
     */
    fun addTask(taskDescription: MutableList<String>, priority: String, date: String, time: String) {
        taskCount++
        tasks.add(Task(taskCount, taskDescription, date, time, priority, computeDueTag(date)))
    }

    /**
     * Computes tag's due tag from the date.
     * @param taskDateString the date of the task.
     * @return String - the due tag.
     */
    private fun computeDueTag(taskDateString: String): String {
        val taskDate = LocalDate.parse(taskDateString)
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
        val numberOfDays = currentDate.daysUntil(taskDate)
        return when {
            numberOfDays < 0 -> "O"
            numberOfDays == 0 -> "T"
            else -> "I"
        }
    }

    /**
     * Delete a task from the list.
     * @param taskNumber the number of the task.
     */
    fun deleteTask(taskNumber: Int) {
        taskCount--
        tasks.removeAt(taskNumber - 1)
    }

    /**
     * Edit a task from the list.
     * @param taskNumber the number of the task.
     * @param editField the field to edit.
     * @param editValue the new value of the field.
     */
    fun editTask(taskNumber: Int, editField: String, editValue: Any) {
        val task = tasks[taskNumber - 1]
        when (editField) {
            "priority" -> {
                task.priority = editValue as String
            }

            "date" -> {
                task.date = editValue as String
                task.dueTag = computeDueTag(editValue)
            }

            "time" -> {
                task.time = editValue as String
            }

            "task" -> {
                @Suppress("UNCHECKED_CAST")
                task.taskDescription = editValue as MutableList<String>
            }
        }
    }

    /**
     * Function to get ANSI escape code for the task priority.
     * @param priority task priority character. C, H, N, L: Critical, High, Normal, Low
     * @return String - ANSI escape code for the task priority.
     */
    private fun getPriorityAnsiColorCode(priority: String): String = when (priority) {
        "C" -> "\u001B[101m \u001B[0m"
        "H" -> "\u001B[103m \u001B[0m"
        "N" -> "\u001B[102m \u001B[0m"
        else -> "\u001B[104m \u001B[0m" // L
    }

    /**
     * Function to get ANSI escape code for the task due tag.
     * @param dueTag task due tag character. I, T, O: In time, Today, Overdue
     * @return String - ANSI escape code for the task due tag.
     */
    private fun getDueTagAnsiColorCode(dueTag: String): String = when (dueTag) {
        "I" -> "\u001B[102m \u001B[0m"
        "T" -> "\u001B[103m \u001B[0m"
        else -> "\u001B[101m \u001B[0m" // "O"
    }

    /**
     * Print the list of tasks.
     */
    fun printTasks() {
        if (tasks.isEmpty()) {
            println("No tasks have been input")
        } else {
            // Print the header
            println("+----+------------+-------+---+---+--------------------------------------------+")
            println("| N  |    Date    | Time  | P | D |                   Task                     |")
            println("+----+------------+-------+---+---+--------------------------------------------+")
            tasks.forEachIndexed { index, task ->
                // create a string list from task description.
                val chunkedTaskDescription = mutableListOf<String>()
                // every element in the new array should be max 44 characters long
                for (taskDesc in task.taskDescription) {
                    chunkedTaskDescription.addAll(taskDesc.chunked(44))
                }
                // Print the first line of the task
                println(
                    "| %-2d | %-10s | %-5s | %-1s | %-1s |%-44s|".format(
                        index + 1,
                        task.date,
                        task.time,
                        getPriorityAnsiColorCode(task.priority),
                        getDueTagAnsiColorCode(task.dueTag),
                        chunkedTaskDescription[0]
                    )
                )
                // Check if the task description is longer than 44 characters
                if (chunkedTaskDescription.size > 1) {
                    // Print the rest of the task description
                    for (i in 1 until chunkedTaskDescription.size) {
                        println(
                            "| %-2s | %-10s | %-5s | %-1s | %-1s |%-44s|".format(
                                "",
                                "",
                                "",
                                "",
                                "",
                                chunkedTaskDescription[i]
                            )
                        )
                    }
                }
                println("+----+------------+-------+---+---+--------------------------------------------+")
            }
        }
    }

    fun loadTasksFromJson() {
        val file = File("tasklist.json")
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val listTaskType = Types.newParameterizedType(MutableList::class.java, Task::class.java)
        val adapter: JsonAdapter<MutableList<Task>> = moshi.adapter(listTaskType)
        if (file.exists()) {
            val json = file.readText()
            tasks.addAll(adapter.fromJson(json)!!)
        }
    }

    fun saveTasksIntoJson() {
        // serialize the tasks list into json
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val listTaskType = Types.newParameterizedType(MutableList::class.java, Task::class.java)
        val adapter: JsonAdapter<MutableList<Task>> = moshi.adapter(listTaskType)
        val json = adapter.toJson(tasks)
        // write the json into a file
        val file = File("tasklist.json")
        file.writeText(json)
    }
}