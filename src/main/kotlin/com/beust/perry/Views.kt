package com.beust.perry

import com.google.inject.Inject
import io.dropwizard.views.View

@Suppress("unused")
class BannerInfo(user: User?) {
    val username: String? = user?.fullName
    val adminText: String? = if (user?.level == 0) "Admin" else null
    val adminLink: String? = if (user?.level == 0) "/admin" else null
}

@Suppress("unused", "MemberVisibilityCanBePrivate", "CanBeParameter")
class CyclesView(val cycles: List<Cycle>, val recentSummaries: List<ShortSummary>, val summaryCount: Int,
        val bookCount: Int, val bannerInfo: BannerInfo) : View("cycles.mustache") {
    val percentage: Int = summaryCount * 100 / bookCount
}

class CycleView(val bannerInfo: BannerInfo) : View("cycle.mustache")

@Suppress("unused")
class SummaryView(val bannerInfo: BannerInfo) : View("summary.mustache")

@Suppress("unused")
class EditSummaryView(val summary: Summary?, val authorName: String?, val authorEmail: String?)
    : View("editSummary.mustache")

class ThankYouForSubmittingView: View("thankYouForSubmitting.mustache")

@Suppress("unused")
class RssView @Inject constructor(private val summariesDao: SummariesDao, private val urls: Urls,
        private val booksDao: BooksDao)
    : View("rss.mustache")
{
    @Suppress("unused")
    class Item(val number: Int ,val englishTitle: String, val url: String, val germanTitle: String?, val date: String)
    val items: List<Item>
        get() {
            return summariesDao.findRecentSummaries(10).map {
                val book = booksDao.findBook(it.number)
                Item(it.number, it.englishTitle, urls.summaries(it.number, fqdn = true), book?.germanTitle, it.date)
            }
        }
}