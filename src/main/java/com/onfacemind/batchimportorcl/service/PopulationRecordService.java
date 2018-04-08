package com.onfacemind.batchimportorcl.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.onfacemind.batchimportorcl.business.ImportDataBusiness;
import com.onfacemind.batchimportorcl.compare.CompareService;
import com.onfacemind.batchimportorcl.read.beans.Person;
import com.onfacemind.batchimportorcl.util.PKgen;
import com.onfacemind.batchimportorcl.write.mapper.AreaMapper;
import com.onfacemind.batchimportorcl.write.mapper.FaceMapper;
import com.onfacemind.batchimportorcl.write.mapper.RecordMapper;
import com.onfacemind.batchimportorcl.write.pojo.Area;
import com.onfacemind.batchimportorcl.write.pojo.Face;
import com.onfacemind.batchimportorcl.write.pojo.Record;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Service
public class PopulationRecordService {

    @Resource
    RecordMapper recordMapper;

    @Resource
    FaceMapper faceMapper;

    @Resource
    AreaMapper areaMapper;

    @Autowired
    FastFileStorageClient storageClient;

    @Autowired
    CompareService compareService;

    public int saveRecord(Person person, int total, Area area, CountDownLatch countDownLatch){
        byte[] photo = person.getPhoto();
        if (photo != null && photo.length > 0){

            ByteArrayInputStream inputStream = null;
            try {
                //上传图片
                inputStream = new ByteArrayInputStream(photo);
                StorePath path = storageClient.uploadFile(inputStream, (long) photo.length, "jpg", null);
                String fullPath = path.getFullPath();

                if (StringUtils.isNotEmpty(fullPath)){

                    Record record = new Record();
                    record.setId(PKgen.getId());
                    record.setName(person.getName());
                    record.setIdCard(person.getIdcard());
                    record.setFaceTotal(1);
                    record.setTempId(area.getId());
                    record.setState("Y");
                    record.setHasWarn(area.getState());
                    record.setWarnNum(area.getWarningValue());
                    record.setWarnAnalyse(area.getState());

                    Face face = new Face();
                    face.setId(PKgen.getId());
                    face.setImage("/"+fullPath);
                    face.setRecordId(record.getId());

                    //发算法
                    int is_pass = compareService.addStandardFaceByPic(record.getId(), photo);
                    face.setIsPass(is_pass);

                    recordMapper.insert(record);
                    faceMapper.insert(face);

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }
        }

        System.out.println("总数:"+total+", 已完成:"+ImportDataBusiness.atomicInt.addAndGet(1));
        return 1;
    }

    public Area findAreaById(String id){
        if (StringUtils.isEmpty(id))
            return null;
        return areaMapper.selectByPrimaryKey(id);
    }

}
