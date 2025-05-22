package com.yamyam.dto.request;

public class ImageInfo {
    private String name;
    private String format;

    public ImageInfo() {
    }

    public ImageInfo(String name, String format) {
        this.name = name;
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return "ImageInfo{name='" + name + "', format='" + format + "'}";
    }
}
