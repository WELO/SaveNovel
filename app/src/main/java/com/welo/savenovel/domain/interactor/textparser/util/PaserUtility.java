package com.welo.savenovel.domain.interactor.textparser.util;

import org.jsoup.Jsoup;

import java.util.List;

/**
 * Created by Amy on 2019-10-08
 */

public class PaserUtility {
    public static String getMetaData(String content, @TextParserDefine.MetaType String metaType) {
        List<String> matches = Regex.pregMatchAll(content,
                Regex.METATAG_PATTERN, 1);
        for (String match : matches) {
            final String lowerCase = match.toLowerCase();
            if (metaType.equals(TextParserDefine.META_URL)
                    && (lowerCase.contains("property=\"og:url\"")
                    || lowerCase.contains("property='og:url'")
                    || lowerCase.contains("name=\"url\"")
                    || lowerCase.contains("name='url'")))
                return separeMetaTagsContent(match);
            else if (metaType.equals(TextParserDefine.META_TITLE)
                    && (lowerCase.contains("property=\"og:title\"")
                    || lowerCase.contains("property='og:title'")
                    || lowerCase.contains("name=\"title\"")
                    || lowerCase.contains("name='title'")))
                return separeMetaTagsContent(match);
            else if (metaType.equals(TextParserDefine.META_DESCRIPTION)
                    && (lowerCase.contains("property=\"og:description\"")
                    || lowerCase
                    .contains("property='og:description'")
                    || lowerCase.contains("name=\"description\"")
                    || lowerCase.contains("name='description'")))
                return separeMetaTagsContent(match);
            else if (metaType.equals(TextParserDefine.META_IMAGE)
                    && (lowerCase.contains("property=\"og:image\"")
                    || lowerCase.contains("property='og:image'")
                    || lowerCase.contains("name=\"image\"")
                    || lowerCase.contains("name='image'")))
                return separeMetaTagsContent(match);
        }
        return "";
    }

    /**
     * Gets content from metatag
     */
    private static String separeMetaTagsContent(String content) {
        String result = Regex.pregMatch(content, Regex.METATAG_CONTENT_PATTERN,
                1);
        return htmlDecode(result);
    }

    private static String htmlDecode(String content) {
        return Jsoup.parse(content).text();
    }
}
