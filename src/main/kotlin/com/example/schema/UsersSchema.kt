package com.example.schema

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlinx.serialization.Serializable
import kotlinx.coroutines.Dispatchers
import mu.KotlinLogging
import org.jetbrains.exposed.sql.*

private val logger = KotlinLogging.logger {}

val database = Database.connect(
    url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
    user = "root",
    driver = "org.h2.Driver",
    password = ""
)

@Serializable
data class User(val name: String, val age: Int)
class UserService {
    object Users : Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", length = 50)
        val age = integer("age")

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            // 数据库初始化
            SchemaUtils.create(Users)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(user: User): Int = dbQuery {
        Users.insert {
            it[name] = user.name
            it[age] = user.age
        }[Users.id]
    }

    suspend fun read(id: Int): User? {
        return dbQuery {
            Users.select { Users.id eq id }
                .map { User(it[Users.name], it[Users.age]) }
                .singleOrNull()
        }
    }

    suspend fun list(): List<User>? {
        return dbQuery {
            Users.selectAll().map {
                logger.info { "查询结果 $it" }
                User(it[Users.name], it[Users.age])
            }
        }
    }

    suspend fun update(id: Int, user: User) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[name] = user.name
                it[age] = user.age
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }
}