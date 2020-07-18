package com.v.im.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.v.im.common.service.FileMangeService;
import com.v.im.common.utils.ChatUtils;
import com.v.im.user.entity.FileDesc;
import com.v.im.user.mapper.FileDescMapper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * 文件上传控制器
 *
 * @author 乐天
 * @since 2018-10-07
 */
@CrossOrigin
@RestController
@RequestMapping("api")
public class UploadController {
    private static final String LOCK = "LOCK";
//    @Value("${web.upload-path}")
//    private String uploadPath;
    @Autowired
    FileDescMapper fileDescMapper;
    /**
     * 上传接口
     *
     * @param file    文件
     * @param request 请求
     * @return json
     */
//    @RequestMapping(value = "upload")
//    @ResponseBody
//    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
//        JSONObject json = new JSONObject();
//        try {
//            String host = ChatUtils.getHost(request);
//            String fileName = UUID.randomUUID() + "." + file.getOriginalFilename().substring(Objects.requireNonNull(file.getOriginalFilename()).lastIndexOf(".") + 1);
//            File targetFile = new File(uploadPath);
//            if (!targetFile.exists()) {
//                if (!targetFile.mkdirs()) {
//                    json.put("msg", "error");
//                    return json.toJSONString();
//                }
//            }
//            File tempFile = new File(uploadPath, fileName);
//            file.transferTo(tempFile);
//            json.put("msg", "success");
//            json.put("filePath", host + "/" + fileName);
//            System.out.println(host + "/" + fileName);
//        } catch (Exception e) {
//            e.printStackTrace();
//            json.put("msg", "error");
//            return json.toJSONString();
//        }
//        return json.toJSONString();
//    }
//}
    @RequestMapping(value = "upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request){
        FileMangeService fileMangeService = new FileMangeService();
        String arr[];
        JSONObject json = new JSONObject();
//		for (int i=0;i<=fileInfo1.length;i++) {
//			MultipartFile fileInfo=fileInfo1[i];
        try {
            arr = fileMangeService.uploadFile(file.getBytes(), String.valueOf(-1));
            FileDesc fileDesc = new FileDesc();
            fileDesc.setFileName(file.getName());
            fileDesc.setGroupName(arr[0]);
            fileDesc.setRemoteFilename(arr[1]);
            fileDesc.setUserId(-1);
            fileDesc.setCreateTime(LocalDateTime.now());
            fileDesc.setModifyTime(LocalDateTime.now());
            fileDesc.setIsDeleted((short) 0);
            fileDescMapper.insert(fileDesc);
            json.put("msg", "success");
            json.put("filePath", "http://localhost:8080/api/getFile?fileId="+fileDesc.getId());
            System.out.println("http://localhost:8080/api/getFile?fileId="+fileDesc.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json.toJSONString();
    }

    @ApiOperation(value = "获取图片", notes = "获取图片")
    @RequestMapping(value = "/getFile", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "fileId", value = "文件id", required = true, type = "Integer") })
    public void getFile(@RequestParam(name = "fileId") Integer fileId, HttpServletResponse response) throws Exception {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Content-Type", "image/x-icon");
        FileDesc fileDesc = fileDescMapper.FileDesc(fileId);
        System.out.println(fileDesc);
        System.out.println(fileDesc.getGroupName());
        System.out.println(fileDesc.getRemoteFilename());
        if (fileDesc == null) {
            throw new Exception("file not exists");
        }
        FileMangeService fileManageService = new FileMangeService();
        synchronized (LOCK) {
            byte[] file = fileManageService.downloadFile(fileDesc.getGroupName(), fileDesc.getRemoteFilename());
            ByteArrayInputStream stream = new ByteArrayInputStream(file);
            BufferedImage readImg = ImageIO.read(stream);
            stream.reset();
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(readImg, "png", outputStream);

            outputStream.close();
        }
    }
}
