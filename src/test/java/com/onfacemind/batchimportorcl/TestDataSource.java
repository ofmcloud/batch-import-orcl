package com.onfacemind.batchimportorcl;

import com.onfacemind.batchimportorcl.read.PersonRowMapper;
import com.onfacemind.batchimportorcl.read.beans.Person;
import com.onfacemind.batchimportorcl.write.mapper.RecordMapper;
import com.onfacemind.batchimportorcl.write.pojo.RecordExample;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.List;

public class TestDataSource extends BatchImportOrclApplicationTests{

    @Autowired
    @Qualifier("readJdbcTemplate")
    protected JdbcTemplate readJdbcTemplate;

    @Autowired
    @Qualifier("writeJdbcTemplate")
    protected JdbcTemplate writeJdbcTemplate;

    @Resource
    RecordMapper recordMapper;

    @Test
    public void testDatasource(){

        String sql1 = "SELECT \"COUNT\"(*) FROM \"TEST\"";

        Integer count = readJdbcTemplate.queryForObject(sql1, Integer.class);

        System.out.println(count);

        String sql2 = "SELECT COUNT(*) FROM capture_record";

        Integer count2 = writeJdbcTemplate.queryForObject(sql2, Integer.class);

        System.out.println(count2);
    }

    @Test
    public void testMapper(){
        int count = recordMapper.countByExample(new RecordExample());
        System.out.println("count:"+count);
    }

    @Test
    public void testRead(){
        String sql = "SELECT A1.\"NAME\",A1.IDCARD,A1.ZP FROM (SELECT \"TEST\".*,ROWNUM rn FROM \"TEST\") A1 WHERE rn between 1 and 10";

        List<Person> results = readJdbcTemplate.query(sql, new PersonRowMapper());

        System.out.println(results);

    }

}
