package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    private var returnError = false

    var reminderServiceData: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()


    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (returnError) {
            return Result.Error("Error when retrieving reminders")
        }
        return Result.Success(reminderServiceData.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminderServiceData[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        reminderServiceData[id]?.let {
            return Result.Success(it)
        }
        return Result.Error("Could not find task")
    }

    override suspend fun deleteAllReminders() {
        reminderServiceData.clear()
    }

    fun saveReminders(vararg reminders: ReminderDTO) {
        for (reminder in reminders) {
            reminderServiceData[reminder.id] = reminder
        }
    }

    fun setReturnError(value: Boolean) {
        returnError = value
    }

}