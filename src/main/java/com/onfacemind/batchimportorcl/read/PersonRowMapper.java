package com.onfacemind.batchimportorcl.read;


import com.onfacemind.batchimportorcl.read.beans.Person;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonRowMapper implements RowMapper<Person> {
    @Override
    public Person mapRow(ResultSet resultSet, int i) throws SQLException {
        Person person = new Person();
        person.setName(resultSet.getString("NAME"));
        person.setIdcard(resultSet.getString("IDCARD"));

        Blob zp = resultSet.getBlob("ZP");
        person.setPhoto(blobToBytes(zp));
        return person;
    }

    /**
     * 将blob转化为byte[],可以转化二进制流的
     *
     * @param blob
     * @return
     */
    private byte[] blobToBytes(Blob blob) {
        InputStream is = null;
        byte[] b = null;
        try {
            is = blob.getBinaryStream();
            b = new byte[(int) blob.length()];
            is.read(b);
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                is = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return b;
    }

}
