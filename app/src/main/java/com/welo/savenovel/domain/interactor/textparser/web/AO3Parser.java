package com.welo.savenovel.domain.interactor.textparser.web;

import com.welo.savenovel.domain.model.Article;
import com.welo.savenovel.exception.ParseURLFailedException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import io.reactivex.Single;
import timber.log.Timber;

/**
 * Created by Amy on 2019-10-08
 */

public class AO3Parser {

    private final static String allowAdult = "?view_adult=true";

    public static Single<Article> AO3Paser(String queryUrl) {

        String url = getAllowAdultUrl(queryUrl);
        return Single.create(emitter -> {
            try {
                Document doc = Jsoup.connect(url).get();
                Elements title = doc.select("h2[class=\"title heading\"]");
                Elements author = doc.select("a[rel=\"author\"]");
                Elements summury = doc.select("div[class=\"summary module\"]");

                String content = "";
                if (!url.contains("chapters")) {
                    content = getAO3SingleArticle(doc);
                } else {
                    String prefixUrl = "";
                    String[] prefixUrls = url.split("chapters");
                    if (prefixUrls.length > 1) {
                        prefixUrl = prefixUrls[0] + "/chapters/";
                    }
                    content = getAO3MutiArticle(doc, prefixUrl);
                }
                Timber.d("title = " + title.text());
                Timber.d("author = " + author.text());
                Timber.d("summury = " + summury.text());

                if (content.isEmpty()) {
                    emitter.onError(new ParseURLFailedException());
                }

                Article article = Article.newBuilder()
                        .setTitle(title.text())
                        .setContent(content)
                        .setUrl(queryUrl)
                        .setAuthor(author.text())
                        .setSummary(summury.text())
                        .build();
                emitter.onSuccess(article);

            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }

        });
    }

    private static String getAO3SingleArticle(Document doc) {
        Elements contentDiv = doc.select("div[role=\"article\"]");
        if (contentDiv.first() == null) {
            String content = contentDiv.text();
            Timber.d("content = " + content);
            return content;
        } else {
            String contentHtml = contentDiv.first().html();
            String[] pIndex = contentHtml.split("<p>");
            if (pIndex.length > 0) {
                String content = contentHtml.substring(pIndex[0].length())
                        .replace("<br/>", "\n")
                        .replace("<br>", "\n")
                        .replace("</br>", "\n")
                        .replace("<p>", "")
                        .replace("</p>", "")
                        .replace("</div>", "")
                        .replace("&nbsp;", "\n");
                Timber.d("content = " + content);
                return content;
            }
        }
        return "";
    }

    private static String getAO3SingleArticleP(Document doc) {
        String content = "";
        Elements content_select = doc.select("p");

        Timber.d("content = " + content_select.text());
        content = content_select.text();
        content = content.replace("  ", "\n")
                .replace(" ", "\n");
        return content;
    }

    private static String getAO3MutiArticle(Document doc, String prefixUrl) throws IOException {
        String content = "";
        Elements chapterIndex = doc.select("ul[id=\"chapter_index\"]");
        Elements chapterValue = chapterIndex.select("option");
        for (int i = 0; i < chapterValue.size(); i++) {
            String chapterId = chapterValue.get(i).attr("value");
            Timber.d("chapterId " + i + " = " + chapterId);
            String chapterUrl = getAllowAdultUrl(prefixUrl + chapterId);
            Document chapterDoc = Jsoup.connect(chapterUrl).get();
            Elements chapterTitle = chapterDoc.select("h3[class=\"title\"]");
            content = content + "\n\n" + chapterTitle.text() + "\n\n" + getAO3SingleArticle(chapterDoc);
        }
        return content;
    }

    private static String getAllowAdultUrl(String queryUrl) {
        String allowAdultQueryUrl = queryUrl;
        if (queryUrl.contains("#")) {
            allowAdultQueryUrl = queryUrl.split("#")[0];
        }
        if (!queryUrl.contains(allowAdult)) {
            allowAdultQueryUrl = allowAdultQueryUrl + allowAdult;
        }
        return allowAdultQueryUrl;
    }

}
