package com.onfacemind.batchimportorcl.read.beans;

import java.util.Arrays;

public class Person {

    private String name;

    private String idcard;

    private byte[] photo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", idcard='" + idcard + '\'' +
                ", photo=" + Arrays.toString(photo) +
                '}';
    }
}
