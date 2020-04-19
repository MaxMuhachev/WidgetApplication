package com.maxim.widgets.models;

import java.util.GregorianCalendar;
import java.util.UUID;

public class Widget {
    private Long id;
    private Integer x;
    private Integer y;
    private Integer z;
    private Integer width;
    private Integer height;
    private String lastModifiedDate;

    public Widget() {
         new Widget();
    }

    public Widget(
            Integer x,
            Integer y,
            Integer z,
            Integer width,
            Integer height
    ) {
        id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        this.x = x != null ? x : 0;
        this.y = y != null ? y : 0;
        this.z = z;
        this.width = width;
        this.height = height;
        this.lastModifiedDate = new GregorianCalendar().toString();
    }

    public Widget update(
            Long id,
            Integer x,
            Integer y,
            Integer z,
            Integer width,
            Integer height
    ) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.lastModifiedDate = new GregorianCalendar().toString();
        return this;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getZ() {
        return z;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
}
