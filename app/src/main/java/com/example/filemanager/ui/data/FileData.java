package com.example.filemanager.ui.data;

public class FileData {
    private int fileIcon;
    private String fileName;
    private boolean isEmpty;

    public FileData(int fileIcon, String fileName, boolean isEmpty) {
        this.fileIcon = fileIcon;
        this.fileName = fileName;
        this.isEmpty = isEmpty;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public int getFileIcon() {
        return fileIcon;
    }

    public void setFileIcon(int fileIcon) {
        this.fileIcon = fileIcon;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
