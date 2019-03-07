package com.beust.perry

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class BooksDaoExposed: BooksDao {
    private fun createBookFromRow(row: ResultRow) = Book(
            row[Hefte.number], row[Hefte.title],row[Hefte.author], row[Hefte.published],
            row[Hefte.germanFile])

    override fun findBooks(start: Int, end: Int): BooksDao.BooksResponse {
        val result = arrayListOf<Book>()
        transaction {
            Hefte.select {
                Hefte.number.greaterEq(start) and Hefte.number.lessEq(end)
            }.forEach { row ->
                result.add(createBookFromRow(row))
            }
        }
        return BooksDao.BooksResponse(result)
    }

}