package com.onfacemind.batchimportorcl.business;

import com.onfacemind.batchimportorcl.config.props.ColumnProps;
import com.onfacemind.batchimportorcl.read.PersonRowMapper;
import com.onfacemind.batchimportorcl.read.beans.Person;
import com.onfacemind.batchimportorcl.service.PopulationRecordService;
import com.onfacemind.batchimportorcl.write.pojo.Area;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
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

    @Autowired
    ColumnProps columnProps;


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

        if (StringUtils.isEmpty(columnProps.getTable()) || StringUtils.isEmpty(columnProps.getFace())){
            System.out.println("ERROR:配置项中缺少必要的参数！\r\n"+columnProps);
        }

        //开始计时时间戳
        long startTime = System.currentTimeMillis();

        String countSql = "SELECT COUNT(*) FROM " + columnProps.getTable() + "";

        Integer total = readJdbcTemplate.queryForObject(countSql, Integer.class);

        System.out.println("数据总数:"+total);

        if (total <= 0){
            System.out.println("目标库中无数据, 数据导入终止......");
            return;
        }

        //先从数据库中查询出一条数据，检查配置的字段是否合法
        try {
            readJdbcTemplate.query(sqlGenerator(columnProps,0,1), new PersonRowMapper(columnProps));
        } catch (DataAccessException e) {
            e.printStackTrace();
            System.out.println("ERROR:查询数据异常, 请确认字段名称是否正确！\r\n"+columnProps);
            return;
        }

        int totalPage = (total/pageSize) + ((total%pageSize) == 0?0:1);

        System.out.println("总页数:"+totalPage);

        CountDownLatch countDownLatch = new CountDownLatch(total);
        for (int i = 1; i <= totalPage; i++) {

            int start = 1+(i-1)*pageSize;
            int end = i*pageSize;
//            String pageSql = "SELECT A1.ZP,A1.NAME,A1.IDCARD FROM (SELECT TEST.*,ROWNUM rn FROM TEST) A1 WHERE rn between "+start+" and "+end+"";
            String pageSql = sqlGenerator(columnProps, start, end);

            List<Person> persons = readJdbcTemplate.query(pageSql, new PersonRowMapper(columnProps));
            persons.forEach(person -> {
                CompletableFuture.supplyAsync(() -> recordService.saveRecord(person,total,area,countDownLatch), executor);
            });
        }

        countDownLatch.await();
        long endTime = System.currentTimeMillis();
        System.out.println("数据导入完成!");
        System.out.println("耗时: "+(endTime-startTime)+" ms.");

    }

    private String sqlGenerator(ColumnProps props,Integer start ,Integer end){
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT A1.ZP");
        if (StringUtils.isNotEmpty(props.getName())){
            sb.append(",A1.");
            sb.append(props.getName());
        }
        if (StringUtils.isNotEmpty(props.getIdcard())){
            sb.append(",A1.");
            sb.append(props.getIdcard());
        }

        sb.append(" FROM (SELECT ");
        sb.append(props.getTable());
        sb.append(".*,ROWNUM rn FROM ");
        sb.append(props.getTable());
        sb.append(") A1 WHERE rn between ");
        sb.append(start);
        sb.append(" and ");
        sb.append(end);

        return sb.toString();
    }

}
