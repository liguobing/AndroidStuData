package cn.lixyz.androidstudata.bean;

/**
 * Created by LGB on 2016/5/11.
 */
public class ShowFileBean {
    private String author;
    private String fileLink;
    private String localFileID;
    private String isCollect;

    public String getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(String isCollect) {
        this.isCollect = isCollect;
    }

    public String getLocalFileID() {
        return localFileID;
    }

    public void setLocalFileID(String localFileID) {
        this.localFileID = localFileID;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFileLink() {
        return fileLink;
    }

    public void setFileLink(String fileLink) {
        this.fileLink = fileLink;
    }
}
