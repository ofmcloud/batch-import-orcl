package com.onfacemind.batchimportorcl.read;


import com.onfacemind.batchimportorcl.config.props.ColumnProps;
import com.onfacemind.batchimportorcl.read.beans.Person;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonRowMapper implements RowMapper<Person> {

    ColumnProps columnProps;

    public PersonRowMapper(ColumnProps columnProps) {
        this.columnProps = columnProps;
    }

    @Override
    public Person mapRow(ResultSet resultSet, int i) throws SQLException {
        Person person = new Person();

        //解析照片字段
        Blob zp = resultSet.getBlob(columnProps.getFace());
        person.setPhoto(blobToBytes(zp));

        //解析名字字段
        if (StringUtils.isNotEmpty(columnProps.getName()))
            person.setName(resultSet.getString(columnProps.getName()));

        //解析身份证号字段
        if (StringUtils.isNotEmpty(columnProps.getIdcard()))
            person.setIdcard(resultSet.getString(columnProps.getIdcard()));


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
