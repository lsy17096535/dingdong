package com.intexh.bidong.userentity;

import java.io.File;

/**
 * Created by shenxin on 15/12/28.
 */
public class ModelWorkItemEntity {
    private String id;
    private String user_id;
    private String title;
    private String uri;
    private boolean status;
    private String created_at;
    private String updated_at;
    private File file;
    private String name;
    private String remoteName;

    public void setId(String id){
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemoteName() {
        return remoteName;
    }

    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getTitle() {
        return title;
    }

    public String getUri() {
        return uri;
    }

    public boolean isStatus() {
        return status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
