package com.yzh.myfeign.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.yzh.myfeign.client.CephClient;
import com.yzh.myfeign.client.ComputeClient;

@RestController
public class ConsumerController {

    @Autowired
    ComputeClient computeClient;
    
    @Autowired
    CephClient cephClient;

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public Integer add(HttpServletRequest req) {
    	System.out.println("====="+req.getLocalAddr()+"=="+req.getServerPort());
        return computeClient.add(10, 20);
    }

    @ResponseBody
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public List<String> uploadFile(HttpServletRequest req, MultipartHttpServletRequest multiReq) {
    	List<String> list = new ArrayList<>();
    	String bucketName = "yzh";
    	String cephProjectName = "web";
    	List<MultipartFile> files = multiReq.getFiles("file");
		if (files == null || files.isEmpty()) {
			list.add("no file upload");
			return list;
		}
		// 处理上传的文件
		for (MultipartFile file : files) {
			String str = cephClient.uploadFile(file, bucketName, cephProjectName);
			list.add(str);
		}
    	return list;
    }
    
    @ResponseBody
	@RequestMapping(value = "/getObject/{cephKey}", method = RequestMethod.POST)
	public Map<String,byte[]> getObject(@PathVariable String cephKey) throws IOException {
		Map<String,byte[]> map = new HashMap<>();
    	String bucketName = "yzh";
    	String cephProjectName = "web";
    	byte[] arr = cephClient.getObject(bucketName, cephKey, cephProjectName);
    	map.put("file", arr);
    	
    	FileOutputStream fis = new FileOutputStream(new File("D:\\bgy\\test.png"));
    	fis.write(arr);
    	fis.flush();
    	fis.close();
    	return map;
    }
}