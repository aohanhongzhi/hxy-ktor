
```kotlin
    suspend fun list(): List<User>? {
        val users = mutableListOf<User>()
        transaction {
            SchemaUtils.create(Users)
            Users.selectAll().map {
                logger.info { "查询结果 $it" }

                users.add(User(it[Users.name], it[Users.age]))
            }

        }

        return users
    }
```