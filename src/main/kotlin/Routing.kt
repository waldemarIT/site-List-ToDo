package com.example

import com.example.models.CreateTaskRequest
import com.example.models.UpdateTaskRequest
import com.example.repository.TaskRepository
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun Application.configureRouting() {
    val taskRepository = TaskRepository()
    
    routing {
        route("/api/tasks") {
            // Отримати всі завдання
            get {
                try {
                    val tasks = taskRepository.getAllTasks()
                    call.respond(tasks)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }
            
            // Отримати завдання за ID
            get("/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                    return@get
                }
                
                try {
                    val task = taskRepository.getTaskById(id)
                    if (task == null) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Task not found"))
                    } else {
                        call.respond(task)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }
            
            // Створити нове завдання
            post {
                try {
                    val request = call.receive<CreateTaskRequest>()
                    if (request.title.isBlank()) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Title cannot be empty"))
                        return@post
                    }
                    
                    val newTask = taskRepository.createTask(request.title)
                    call.respond(HttpStatusCode.Created, newTask)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }
            
            // Оновити завдання
            put("/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                    return@put
                }
                
                try {
                    val request = call.receive<UpdateTaskRequest>()
                    val updatedTask = taskRepository.updateTask(
                        id = id,
                        title = request.title,
                        completed = request.completed
                    )
                    
                    if (updatedTask == null) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Task not found"))
                    } else {
                        call.respond(updatedTask)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }
            
            // Видалити завдання
            delete("/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                    return@delete
                }
                
                try {
                    val deleted = taskRepository.deleteTask(id)
                    if (deleted) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Task not found"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }
        }
        
        // Static plugin. Try to access `/static/index.html`
        staticResources("/", "static")
    }
}
