package cn.andrewlu.app.securediary;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by andrewlu on 2015/12/2.
 */
public class Diary implements Serializable {
    private int id;

    private String texts;//文字分段存储.以图片隔开.
    //private String[] images;//中间插入的图片.
    private Date createAt;//创建日期.
    private String weather;//天气状况.
    private String ownerId;//所有者ID.为同步到网络做准备.
    private boolean isSynced;//是否与网络数据同步了.是否上传网络了.

    public String getClearText() {
        return clearText;
    }

    public void setClearText(String clearText) {
        this.clearText = clearText;
    }

    private String clearText;//纯文字内容,不包含图片信息.

    //test.
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTexts() {
        return texts;
    }

    public void setTexts(String texts) {
        this.texts = texts;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setIsSynced(boolean isSynced) {
        this.isSynced = isSynced;
    }
}
