package com.onfacemind.batchimportorcl.write.mapper;

import com.onfacemind.batchimportorcl.write.pojo.Record;
import com.onfacemind.batchimportorcl.write.pojo.RecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RecordMapper {
    int countByExample(RecordExample example);

    int deleteByExample(RecordExample example);

    int deleteByPrimaryKey(String id);

    int insert(Record record);

    int insertSelective(Record record);

    List<Record> selectByExample(RecordExample example);

    Record selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") Record record, @Param("example") RecordExample example);

    int updateByExample(@Param("record") Record record, @Param("example") RecordExample example);

    int updateByPrimaryKeySelective(Record record);

    int updateByPrimaryKey(Record record);
}