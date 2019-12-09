package com.welo.savenovel.domain.interactor.textparser.web;

import com.welo.savenovel.domain.interactor.textparser.util.PaserUtility;
import com.welo.savenovel.domain.interactor.textparser.util.Regex;
import com.welo.savenovel.domain.interactor.textparser.util.TextParserDefine;
import com.welo.savenovel.domain.model.Article;
import com.welo.savenovel.exception.ParseURLFailedException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import io.reactivex.Single;
import timber.log.Timber;

/**
 * Created by Amy on 2019-10-08
 */

public class LofterParser {

    public static Single<Article> LofterPaser(String queryUrl) {
        return Single.create(emitter -> {
            try {
                Document doc = Jsoup.connect(queryUrl).get();
                Elements title = doc.select("title");

                String content = "";
                Elements content_select = doc.select("p");

                Timber.d("content = " + content_select.text());
                content = content_select.text();
                content = content.replace("  ", "\n")
                        .replace(" ", "\n");

                String titleText = title.text();
                String[] getAuthorBytitle = titleText.split("-");
                String author = "";
                if (getAuthorBytitle.length > 1) {
                    author = getAuthorBytitle[getAuthorBytitle.length - 1];
                } else {
                    author = queryUrl.replace("http://", "");
                    author = author.split(".")[0];
                }

                String summury = PaserUtility.getMetaData(Regex.extendedTrim(doc.toString()), TextParserDefine.META_DESCRIPTION);
                if (content.isEmpty()) {
                    emitter.onError(new ParseURLFailedException());
                }
                Article article = Article.newBuilder()
                        .setTitle(titleText)
                        .setContent(content)
                        .setUrl(queryUrl)
                        .setAuthor(author)
                        .setSummary(summury)
                        .build();
                emitter.onSuccess(article);
            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });
    }
}
