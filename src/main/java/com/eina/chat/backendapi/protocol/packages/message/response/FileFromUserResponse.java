package com.eina.chat.backendapi.protocol.packages.message.response;

import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.TypesOfMessage;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(TypesOfMessage.FILE_FROM_USER)
public class FileFromUserResponse extends BasicPackage {
    private String from;
    private byte[] file;

    @SuppressWarnings("unused")
    public FileFromUserResponse() {
        super();
    }

    @SuppressWarnings("unused")
    public FileFromUserResponse(int messageId, String from, byte[] file) {
        super(messageId);
        this.from = from;
        this.file = file;
    }

    public FileFromUserResponse(String from, byte[] file) {
        super(0);
        this.from = from;
        this.file = file;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
