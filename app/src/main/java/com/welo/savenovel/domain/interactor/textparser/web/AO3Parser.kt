package com.welo.savenovel.domain.interactor.textparser.web

import com.welo.savenovel.domain.model.Article
import com.welo.savenovel.exception.ParseURLFailedException
import io.reactivex.Single
import io.reactivex.SingleEmitter
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import timber.log.Timber
import java.io.IOException

/**
 * Created by Amy on 2019-10-08
 */
object AO3Parser {
    private const val allowAdult = "?view_adult=true"
    @JvmStatic
    fun AO3Paser(queryUrl: String): Single<Article?> {
        val url = getAllowAdultUrl(queryUrl)
        return Single.create { emitter: SingleEmitter<Article?> ->
            try {
                val doc = Jsoup.connect(url).get()
                val title = doc.select("h2[class=\"title heading\"]")
                val author = doc.select("a[rel=\"author\"]")
                val summury = doc.select("div[class=\"summary module\"]")
                var content = ""
                if (!url.contains("chapters")) {
                    content = getAO3SingleArticle(doc)
                } else {
                    var prefixUrl = ""
                    val prefixUrls = url.split("chapters").toTypedArray()
                    if (prefixUrls.size > 1) {
                        prefixUrl = prefixUrls[0] + "/chapters/"
                    }
                    content = getAO3MutiArticle(doc, prefixUrl)
                }
                Timber.d("title = " + title.text())
                Timber.d("author = " + author.text())
                Timber.d("summury = " + summury.text())
                if (content.isEmpty()) {
                    emitter.onError(ParseURLFailedException())
                }
                val article = Article.newBuilder()
                        .setTitle(title.text())
                        .setContent(content)
                        .setUrl(queryUrl)
                        .setAuthor(author.text())
                        .setSummary(summury.text())
                        .build()
                emitter.onSuccess(article)
            } catch (e: Exception) {
                e.printStackTrace()
                emitter.onError(e)
            }
        }
    }

    private fun getAO3SingleArticle(doc: Document): String {
        val contentDiv = doc.select("div[role=\"article\"]")
        if (contentDiv.first() == null) {
            val content = contentDiv.text()
            Timber.d("content = $content")
            return content
        } else {
            val contentHtml = contentDiv.first().html()
            val pIndex = contentHtml.split("<p>").toTypedArray()
            if (pIndex.size > 0) {
                val content = contentHtml.substring(pIndex[0].length)
                        .replace("<br/>", "\n")
                        .replace("<br>", "\n")
                        .replace("</br>", "\n")
                        .replace("<p>", "")
                        .replace("</p>", "")
                        .replace("</div>", "")
                        .replace("&nbsp;", "\n")
                Timber.d("content = $content")
                return content
            }
        }
        return ""
    }

    private fun getAO3SingleArticleP(doc: Document): String {
        var content = ""
        val content_select = doc.select("p")
        Timber.d("content = " + content_select.text())
        content = content_select.text()
        content = content.replace("  ", "\n")
                .replace(" ", "\n")
        return content
    }

    @Throws(IOException::class)
    private fun getAO3MutiArticle(doc: Document, prefixUrl: String): String {
        var content = ""
        val chapterIndex = doc.select("ul[id=\"chapter_index\"]")
        val chapterValue = chapterIndex.select("option")
        for (i in chapterValue.indices) {
            val chapterId = chapterValue[i].attr("value")
            Timber.d("chapterId $i = $chapterId")
            val chapterUrl = getAllowAdultUrl(prefixUrl + chapterId)
            val chapterDoc = Jsoup.connect(chapterUrl).get()
            val chapterTitle = chapterDoc.select("h3[class=\"title\"]")
            content = content + "\n\n" + chapterTitle.text() + "\n\n" + getAO3SingleArticle(chapterDoc)
        }
        return content
    }

    private fun getAllowAdultUrl(queryUrl: String): String {
        var allowAdultQueryUrl = queryUrl
        if (queryUrl.contains("#")) {
            allowAdultQueryUrl = queryUrl.split("#").toTypedArray()[0]
        }
        if (!queryUrl.contains(allowAdult)) {
            allowAdultQueryUrl = allowAdultQueryUrl + allowAdult
        }
        return allowAdultQueryUrl
    }
}