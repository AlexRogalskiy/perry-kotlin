package com.beust.perry

import com.google.inject.Inject
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response

class SummariesDaoExposed @Inject constructor(private val cyclesDao: CyclesDao)
        : SummariesDao {
    override fun findEnglishSummaries(start: Int, end: Int, user: User?): SummariesDao.SummariesResponse {
        val result = arrayListOf<FullSummary>()


        transaction {
            (Hefte crossJoin Summaries)
                .slice(Hefte.number, Hefte.title, Summaries.englishTitle, Hefte.author, Summaries.authorName,
                        Summaries.authorEmail, Summaries.date, Summaries.summary, Summaries.time)
                .select { Summaries.number eq Hefte.number and Summaries.number.greaterEq(start) and
                        Summaries.number.lessEq(end)}
                .forEach { row ->
                    val bookNumber = row[Hefte.number]
                    val cycleNumber = cyclesDao.cycleForBook(bookNumber)
                    val cycleForBook = cyclesDao.findCycle(cycleNumber)
                    if (cycleForBook != null) {
                        result.add(FullSummary(bookNumber, cycleNumber, row[Hefte.title],
                                row[Summaries.englishTitle], row[Hefte.author], row[Summaries.authorName],
                                row[Summaries.authorEmail], row[Summaries.date], row[Summaries.summary],
                                row[Summaries.time], user?.name, cycleForBook.germanTitle))
                    } else {
                        throw WebApplicationException("Couldn't find cycle $cycleNumber")
                    }
                }
        }
        return SummariesDao.SummariesResponse(result)
    }

    override fun saveSummary(summary: FullSummary): Response {
        fun summaryToRow(it: UpdateBuilder<Int>, summary: FullSummary) {
            it[Summaries.englishTitle] = summary.englishTitle
            it[Summaries.authorName] = summary.bookAuthor
            it[Summaries.authorEmail] = summary.authorEmail
            it[Summaries.date] = summary.date
            it[Summaries.summary] = summary.summary
            it[Summaries.time] = summary.time
        }

        transaction {
            val foundSummary = findEnglishSummary(summary.number)
            if (foundSummary == null) {
                Summaries.insert {
                    it[number] = summary.number
                    summaryToRow(it, summary)
                }
            } else {
                Summaries.update({ Summaries.number eq summary.number}) {
                    summaryToRow(it, summary)
                }
            }
        }
        return Response.ok().build()
    }

}
