package com.example.route

import com.example.schema.User
import com.example.schema.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureDatabases() {

    val userService = UserService()
    routing {
        // Create user
        post("/users") {
            val user = call.receive<User>()
            val id = userService.create(user)
            call.respond(HttpStatusCode.Created, "数据库id：" + id)
        }
        // Read user
        get("/users/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val user = userService.read(id)
            if (user != null) {
                call.respond(HttpStatusCode.OK, user)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        // Read user list
        get("/usersList") {
            val userList = userService.list()
            if (userList != null && userList.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, userList)
            } else {
                call.respond(HttpStatusCode.NotFound, "数据库没有数据。可以尝试先添加几条数据")
            }
        }
        // Update user
        put("/users/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val user = call.receive<User>()
            userService.update(id, user)
            call.respond(HttpStatusCode.OK)
        }
        // Delete user
        delete("/users/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            userService.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}
