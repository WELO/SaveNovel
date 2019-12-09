package com.welo.savenovel.domain.model;

/**
 * Created by Amy on 2019-10-07
 */

public class Article {
    private String title = "";
    private String content = "";
    private String author = "";
    private String url = "";
    private String summary = "";

    private Article(Builder builder) {
        title = builder.title;
        content = builder.content;
        author = builder.author;
        url = builder.url;
        summary = builder.summary;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private String title;
        private String content;
        private String author;
        private String url;
        private String summary;

        private Builder() {
        }

        public Builder setTitle(String val) {
            title = val;
            return this;
        }

        public Builder setContent(String val) {
            content = val;
            return this;
        }

        public Builder setAuthor(String val) {
            author = val;
            return this;
        }

        public Builder setUrl(String val) {
            url = val;
            return this;
        }

        public Builder setSummary(String val) {
            summary = val;
            return this;
        }

        public Article build() {
            return new Article(this);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }

    public String getSummary() {
        return summary;
    }

    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", url='" + url + '\'' +
                ", summary='" + summary + '\'' +
                '}';
    }
}
