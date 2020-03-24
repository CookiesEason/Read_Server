package com.xzy.read.service.impl;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.xzy.read.VO.ResultVo;
import com.xzy.read.service.FileService;
import com.xzy.read.util.ResultVoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

/**
 * @author XieZhongYi
 * 2020/03/24 20:58
 */
@Service
public class FileServiceImpl implements FileService {

    @Value("${tx.bucketName}")
    private  String bucketName;

    @Value("${tx.bucket}")
    private String bucket;

    @Value("${tx.accessKey}")
    private String accessKey;

    @Value("${tx.secretKey}")
    private String secretKey;

    @Value("${tx.path}")
    private String path;


    @Override
    public ResultVo uploadFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()){
            return ResultVoUtil.error(0,"文件不能为空");
        }
        String oldFileName = multipartFile.getOriginalFilename();
        String eName = oldFileName.substring(oldFileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID()+eName;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH)+1;
        int day=cal.get(Calendar.DATE);
        BasicCOSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(bucket));
        COSClient cosClient = new COSClient(cred,clientConfig);
        try {
            File localFile = File.createTempFile("temp", null);
            multipartFile.transferTo(localFile);
            String key = "/"+year+"/"+month+"/"+day+"/"+newFileName;
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            return ResultVoUtil.success(path+putObjectRequest.getKey());
        }catch (IOException e){
            e.printStackTrace();
        }
        finally {
            cosClient.shutdown();
        }
        return ResultVoUtil.error(0,"发生错误,请稍后重试");
    }
}
