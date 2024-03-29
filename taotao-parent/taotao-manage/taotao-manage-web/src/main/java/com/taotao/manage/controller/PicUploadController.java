package com.taotao.manage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.bean.PicUploadResult;
import com.taotao.manage.service.PropertiesService;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;


/**
 * 图片上传
 */
@Controller
@RequestMapping("pic")
public class PicUploadController {
    /**
     * Log4J为同一类别的线程生成一个Logger，多个线程共享使用，而它仅在日志信息中添加能够区分不同线程的信息,
     * 使用指定的类XXX初始化日志对象，方便在日志输出的时候，可以打印出日志信息所属的类。
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PicUploadController.class);
    /**
     * ObjectMapper类是Jackson库的主要类。它提供一些功能将转换成Java对象匹配JSON结构，
     * 反之亦然。它使用JsonParser和JsonGenerator的实例实现JSON实际的读/写。
     */
    private static final ObjectMapper mapper = new ObjectMapper();

    //允许上传的格式
    private static final String[] IMAGE_TYPE = new String[] {".bmp", ".jpg", ".jpeg", ".gif", ".png" };

    @Autowired
    private PropertiesService propertiesService;
    /**
     * produces : 选择文件响应类型 文本类型的json数据
     * @param uploadFile
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/upload" , method = RequestMethod.POST , produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String upload(@RequestParam("uploadFile")MultipartFile uploadFile , HttpServletResponse response)throws Exception{

        //校验图片格式
        boolean isLegal = false;
        for(String type : IMAGE_TYPE){
            if(StringUtils.endsWithIgnoreCase(uploadFile.getOriginalFilename() , type)){
                isLegal = true;
                 break;
            }
        }

        //封装Result对象，并且将文件的type数组放置到result对象中
        PicUploadResult fileUploadResult = new PicUploadResult();

        //状态
        fileUploadResult.setError(isLegal ? 0 : 1);

        //新文件路径
        String filePath = getFilePath(uploadFile.getOriginalFilename());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Pic file upload .[{}] to [{}] .", uploadFile.getOriginalFilename(), filePath);
        }

        //生成图片的绝对引用地址
        String picUrl = StringUtils.replace(StringUtils.substringAfter(filePath, propertiesService.REPOSITORY_PATH), "\\", "/");

        fileUploadResult.setUrl(propertiesService.IMAGE_BASE_URL + picUrl);

        File newFile = new File(filePath);

        //写文件到磁盘
        uploadFile.transferTo(newFile);

        //校验图片是否合格
        isLegal = false;
        try{
            BufferedImage image = ImageIO.read(newFile);
            if(image != null){
                fileUploadResult.setWidth(image.getWidth()+"");
                fileUploadResult.setHeight(image.getHeight()+"");
                isLegal = true;
            }
        }catch (Exception e){

        }

        //状态
        fileUploadResult.setError(isLegal ? 0:1);
        if(!isLegal){
            //不合法，将磁盘上的文件删除
            newFile.delete();
        }

//        response.setContentType(MediaType.TEXT_HTML_VALUE);
        //将java对象转换成json数据
        return mapper.writeValueAsString(fileUploadResult);

    }

    private String getFilePath(String sourceFileName){
        String baseFolder = propertiesService.REPOSITORY_PATH + File.separator +"images";
        Date nowDate = new Date();

        //yyyy/MM/dd
        String fileFolder = baseFolder + File.separator + new DateTime(nowDate).toString("yyyy") + File.separator
                + new DateTime(nowDate).toString("MM") + File.separator
                + new DateTime(nowDate).toString("dd");
        File file = new File(fileFolder);
        if(!file.isDirectory()){
            //如果目录不存在，则创建目录
            file.mkdirs();
        }

        //生成新的文件名
        String fileName = new DateTime(nowDate).toString("yyyyMMddhhmmssSSSS") + RandomUtils.nextInt(100, 9999) + "."
                + StringUtils.substringAfterLast(sourceFileName, ".");
        return fileFolder + File.separator + fileName;
    }

}
