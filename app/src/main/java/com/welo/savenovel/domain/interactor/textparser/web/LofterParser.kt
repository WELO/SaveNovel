package com.welo.savenovel.domain.interactor.textparser.web

import com.welo.savenovel.domain.interactor.textparser.util.PaserUtility
import com.welo.savenovel.domain.interactor.textparser.util.Regex
import com.welo.savenovel.domain.interactor.textparser.util.TextParserDefine
import com.welo.savenovel.domain.model.Article
import com.welo.savenovel.exception.ParseURLFailedException
import io.reactivex.Single
import io.reactivex.SingleEmitter
import org.jsoup.Jsoup
import timber.log.Timber

/**
 * Created by Amy on 2019-10-08
 */
object LofterParser {
    @JvmStatic
    fun LofterPaser(queryUrl: String): Single<Article?> {
        return Single.create { emitter: SingleEmitter<Article?> ->
            try {
                val doc = Jsoup.connect(queryUrl).get()
                val title = doc.select("title")
                var content = ""
                val content_select = doc.select("p")
                Timber.d("content = " + content_select.text())
                content = content_select.text()
                content = content.replace("  ", "\n")
                        .replace(" ", "\n")
                val titleText = title.text()
                val getAuthorBytitle = titleText.split("-").toTypedArray()
                var author = ""
                if (getAuthorBytitle.size > 1) {
                    author = getAuthorBytitle[getAuthorBytitle.size - 1]
                } else {
                    author = queryUrl.replace("http://", "")
                    author = author.split(".").toTypedArray()[0]
                }
                val summury = PaserUtility.getMetaData(Regex.extendedTrim(doc.toString()), TextParserDefine.META_DESCRIPTION)
                if (content.isEmpty()) {
                    emitter.onError(ParseURLFailedException())
                }
                val article = Article.newBuilder()
                        .setTitle(titleText)
                        .setContent(content)
                        .setUrl(queryUrl)
                        .setAuthor(author)
                        .setSummary(summury)
                        .build()
                emitter.onSuccess(article)
            } catch (e: Exception) {
                e.printStackTrace()
                emitter.onError(e)
            }
        }
    }
}