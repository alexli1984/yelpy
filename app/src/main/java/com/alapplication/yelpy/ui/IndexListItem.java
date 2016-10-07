package com.alapplication.yelpy.ui;

/**
 * Wrapper for items in IndexedListAdapter
 */
public class IndexListItem<T> {
    private T content;
    private String indexFieldValue;
    private int sectionPos;


    public IndexListItem(String indexFieldValue, T content, int sectionPos) {
        this.content = content;
        this.indexFieldValue = indexFieldValue;
        this.sectionPos = sectionPos;
    }

    public IndexListItem(T content, String indexFieldValue) {
        this.content = content;
        this.indexFieldValue = indexFieldValue;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public int getSectionPos() {
        return sectionPos;
    }

    public void setSectionPos(int sectionPos) {
        this.sectionPos = sectionPos;
    }

    public String getIndexFieldValue() {
        return indexFieldValue;
    }

    public void setIndexFieldValue(String indexFieldValue) {
        this.indexFieldValue = indexFieldValue;
    }

    public boolean isSectionItem(int posAtList) {
        return sectionPos == posAtList;
    }
}
