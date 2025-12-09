package com.example.repository

import com.example.config.SupabaseConfig
import com.example.models.Task
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class TaskRepository {
    private val supabase = SupabaseConfig.client

    suspend fun getAllTasks(): List<Task> {
        return supabase.from("tasks")
            .select()
            .decodeList<Task>()
    }

    suspend fun getTaskById(id: Long): Task? {
        return try {
            supabase.from("tasks")
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<Task>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createTask(title: String): Task {
        return supabase.from("tasks")
            .insert(mapOf("title" to title)) {
                select()
            }
            .decodeSingle<Task>()
    }

    suspend fun updateTask(id: Long, title: String?, completed: Boolean?): Task? {
        return try {
            if (title == null && completed == null) return getTaskById(id)
            
            println("Updating task $id: title=$title, completed=$completed")
            
            // Використовуємо @Serializable клас замість Map
            val update = com.example.models.TaskUpdate(
                title = title,
                completed = completed
            )
            
            val result = supabase.from("tasks")
                .update(update) {
                    filter {
                        eq("id", id)
                    }
                    select()
                }
                .decodeList<Task>()
            
            println("Update result: $result")
            result.firstOrNull()
        } catch (e: Exception) {
            println("Error updating task: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteTask(id: Long): Boolean {
        return try {
            supabase.from("tasks")
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
            true
        } catch (e: Exception) {
            false
        }
    }
}
