package com.example.schema

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SizedIterable

object Books : IntIdTable() {
    //    val id = integer("id").autoIncrement()
    val name = varchar("name", length = 50)
    val price = integer("price")

//    override val primaryKey = PrimaryKey(id)
}

class Book(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Book>(Books)

    //    var id by Books.id
    var name by Books.name
    var price by Books.price
}

class BookService {

    suspend fun create(book: Book) {

        val new = Book.new {
            name = book.name
            price = book.price
        }

    }

    suspend fun selectOne(book: Book): SizedIterable<Book> {
        return Book.find { Books.id eq book.id }
    }


}