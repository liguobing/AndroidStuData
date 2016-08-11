package cn.lixyz.androidstudata.bean;

/**
 * 收藏文章基础类
 * Created by LGB on 2016/5/18.
 */
public class CollectFileBean {
    private String fileID;
    private String fileName;
    private String fileCotegory;
    private String fileURL;

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileCotegory() {
        return fileCotegory;
    }

    public void setFileCotegory(String fileCotegory) {
        this.fileCotegory = fileCotegory;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }
}
