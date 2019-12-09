package com.welo.savenovel.domain.interactor.textparser.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;

/**
 * Created by Amy on 2019-10-07
 */

public class TextParserDefine {
    public static final String META_URL = "url";
    public static final String META_TITLE = "title";
    public static final String META_DESCRIPTION = "description";
    public static final String META_IMAGE = "image";

    @StringDef({META_URL, META_TITLE, META_DESCRIPTION, META_IMAGE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MetaType {
    }


    public enum WebSupportType {
        UNKOWN(""),
        AO3("archiveofourown"),
        LOFTER("lofter");
        public String symbol = "";

        WebSupportType(String symbol) {
            this.symbol = symbol;
        }
    }

}
