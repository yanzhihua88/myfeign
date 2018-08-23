package com.yzh.myfeign.client;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.Bucket;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;

@FeignClient(value = "ceph-file-service" , configuration = CephClient.MultipartSupportConfig.class)
@Service("cephClient")
public interface CephClient {
	
	@RequestMapping(value = "/createBucket", method = RequestMethod.POST)
	public Bucket createBucket(@RequestParam(value="bucketName") String bucketName,@RequestParam(value="cephProjectName") String cephProjectName);

	@RequestMapping(value = "/getBuckets", method = RequestMethod.POST)
	public List<Bucket> getBuckets();

	@RequestMapping(value = "/isBucketExists", method = RequestMethod.POST)
	public boolean isBucketExists(@RequestParam(value="bucketName") String bucketName,@RequestParam(value="cephProjectName") String cephProjectName);

	@RequestMapping(value = "/deleteBucket", method = RequestMethod.POST)
	public boolean deleteBucket(@RequestParam(value="bucketName") String bucketName,@RequestParam(value="cephProjectName") String cephProjectName);

	@RequestMapping(value = "/deleteBucketAndFiles", method = RequestMethod.POST)
	public boolean deleteBucketAndFiles(@RequestParam(value="bucketName") String bucketName,@RequestParam(value="cephProjectName") String cephProjectName);

	@RequestMapping(value = "/isFileExists", method = RequestMethod.POST)
	public boolean isFileExists(@RequestParam(value="bucketName") String bucketName,@RequestParam(value="cephKey") String cephKey,@RequestParam(value="cephProjectName") String cephProjectName);

	@RequestMapping(value = "/getFiles", method = RequestMethod.POST)
	public List<String> getFiles(@RequestParam(value="bucketName") String bucketName,@RequestParam(value="cephProjectName") String cephProjectName);

	@RequestMapping(value = "/deleteFile", method = RequestMethod.POST)
	public boolean deleteFile(@RequestParam(value="bucketName") String bucketName,@RequestParam(value="cephKey") String cephKey,@RequestParam(value="cephProjectName") String cephProjectName);

	@RequestMapping(value = "/getObject", method = RequestMethod.POST)
	public byte[] getObject(@RequestParam(value="bucketName") String bucketName,@RequestParam(value="cephKey") String cephKey,@RequestParam(value="cephProjectName") String cephProjectName);
	
    @RequestMapping(value = "/uploadFileWithKey", method = RequestMethod.POST,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFileWithKey(@RequestPart(value = "file") MultipartFile file,@RequestParam(value="bucketName") String bucketName,@RequestParam(value="cephKey") String cephKey,@RequestParam(value="cephProjectName") String cephProjectName);

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(@RequestPart(value = "file") MultipartFile file,@RequestParam(value="bucketName") String bucketName,@RequestParam(value="cephProjectName") String cephProjectName);
    
    @Configuration
    class MultipartSupportConfig {
        @Bean
        public Encoder feignFormEncoder() {
            return new SpringFormEncoder();
        }
    }
}