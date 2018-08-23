package com.yzh.myfeign;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.yzh.myfeign.client.CephClient;

@RunWith(SpringJUnit4ClassRunner.class)  
@SpringApplicationConfiguration(classes = MyfeignApplication.class)  
@WebAppConfiguration 
public class CephTest {

	@Autowired
	CephClient cephClient;
	
	private String bucketName = "testFeignBucket";

	private String cephProjectName = "testFeignProject";
	
	@Test
	public void createBucket() {
		try {
			Bucket b = cephClient.createBucket(bucketName,cephProjectName);
			System.out.println("=====Bucket: " + b.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getBuckets() {
		try {
			List<Bucket> list = cephClient.getBuckets();
			System.out.println(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void saveDataInputStream() throws Exception {
		File file = new File("D:\\1111.png");
		DiskFileItem fileItem = (DiskFileItem) new DiskFileItemFactory().createItem("file",
                MediaType.TEXT_PLAIN_VALUE, true, file.getName());

        try (InputStream input = new FileInputStream(file); OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copy(input, os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file: " + e, e);
        }

        MultipartFile multi = new CommonsMultipartFile(fileItem);
		try {
			long start = System.currentTimeMillis();
//			key 设计一套业务规则
			String cr = cephClient.uploadFile(multi, bucketName,cephProjectName);

			long end = System.currentTimeMillis();

			System.out.println(" ================耗时： " + (end - start));
			System.out.println(cr);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	@Test
	public void getObject() throws Exception {
		String key ="8871b2b1463e499e9a2b5991aa7c111c";
		byte[] s3is =null;
		try {
			s3is = cephClient.getObject(bucketName, key,cephProjectName);
		} catch (AmazonS3Exception e) {
			System.out.println(e.getErrorCode());
			e.printStackTrace();
		}
		File file = new File("D:\\"+key+".png");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(file);
		
		fos.write(s3is);
		
		fos.flush();

		fos.close();
	}

	

	@Test
	public void getFiles() {
		try {
			List<String> list = cephClient.getFiles(bucketName,cephProjectName);
			System.out.println(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void deleteFile() {
		String key="8871b2b1463e499e9a2b5991aa7c111c";
		try {
			cephClient.deleteFile(bucketName, key,cephProjectName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void deleteBucket() {
		try {
			cephClient.deleteBucket(bucketName,cephProjectName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void deleteBucketAndFiles() {
		try {
			cephClient.deleteBucketAndFiles(bucketName,cephProjectName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}