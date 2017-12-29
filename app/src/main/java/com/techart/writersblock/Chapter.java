package com.techart.writersblock;

/**
 * Created by Kelvin on 30/08/2017.
 */

public class Chapter {
    private String chapterTitle;
    private String Content;

    public Chapter()
    {

    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }
}
