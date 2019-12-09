package com.welo.savenovel.domain.interactor.textparser;

import com.welo.savenovel.domain.interactor.textparser.util.TextParserDefine;
import com.welo.savenovel.domain.interactor.textparser.web.AO3Parser;
import com.welo.savenovel.domain.interactor.textparser.web.LofterParser;
import com.welo.savenovel.domain.model.Article;
import com.welo.savenovel.exception.EmptyURLException;
import com.welo.savenovel.exception.NoSupportWebException;

import io.reactivex.Single;
import timber.log.Timber;


/**
 * Created by Amy on 2019-10-07
 */

public class TextParser {

    private final static String HTTP = "http:";
    private final static String HTTPS = "https:";

    public static Single<Article> UrlPaser(String queryUrl) {
        Timber.d("queryUrl = " + queryUrl);
        if (queryUrl.contains(HTTP)) {
            queryUrl = queryUrl.replace(HTTP, HTTPS);
        }
        queryUrl = queryUrl.trim();
        if (queryUrl.contains(TextParserDefine.WebSupportType.AO3.symbol)) {
            return AO3Parser.AO3Paser(queryUrl);
        } else if (queryUrl.contains(TextParserDefine.WebSupportType.LOFTER.symbol)) {
            return LofterParser.LofterPaser(queryUrl);
        } else if (queryUrl.isEmpty()){
            return Single.error(new EmptyURLException(queryUrl));
        }else{
            return Single.error(new NoSupportWebException(queryUrl));
        }
    }


}
