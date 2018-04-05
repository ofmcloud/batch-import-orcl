package com.onfacemind.batchimportorcl.business;

import com.onfacemind.batchimportorcl.read.PersonRowMapper;
import com.onfacemind.batchimportorcl.read.beans.Person;
import com.onfacemind.batchimportorcl.service.PopulationRecordService;
import com.onfacemind.batchimportorcl.write.pojo.Area;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(1)
public class ImportDataBusiness implements CommandLineRunner{

    @Autowired
    PopulationRecordService recordService;

    @Autowired
    @Qualifier("readJdbcTemplate")
    protected JdbcTemplate readJdbcTemplate;

    private static ExecutorService executor = Executors.newFixedThreadPool(2);

    public static final AtomicInteger atomicInt = new AtomicInteger(0);

    private int pageSize = 1000;

    @Override
    public void run(String... args) throws Exception {

        if (args == null || args.length < 1){
            System.out.println("ERROR:请填写档案分类ID！");
            return;
        }

        String areaId = args[0];
        Area area = recordService.findAreaById(areaId);
        if (area ==null || StringUtils.isEmpty(area.getId())){
            System.out.println("ERROR:档案分类ID不存在！");
            return;
        }


        String countSql = "SELECT \"COUNT\"(*) FROM \"TEST\"";

        Integer total = readJdbcTemplate.queryForObject(countSql, Integer.class);

        System.out.println("数据总数:"+total);

        int totalPage = (total/pageSize) + ((total%pageSize) == 0?0:1);

        System.out.println("总页数:"+totalPage);

        for (int i = 1; i <= totalPage; i++) {

            int start = 1+(i-1)*pageSize;
            int end = i*pageSize;
            String pageSql = "SELECT A1.\"NAME\",A1.IDCARD,A1.ZP FROM (SELECT \"TEST\".*,ROWNUM rn FROM \"TEST\") A1 WHERE rn between "+start+" and "+end+"";

            List<Person> persons = readJdbcTemplate.query(pageSql, new PersonRowMapper());
            persons.forEach(person -> {
                CompletableFuture.supplyAsync(() -> recordService.saveRecord(person,total,area), executor);
            });
        }



    }
}
