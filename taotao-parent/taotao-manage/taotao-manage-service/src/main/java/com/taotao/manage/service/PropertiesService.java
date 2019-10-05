package com.taotao.manage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 图片存储路径存在硬编码问题：将路径配置到外部的配置文件中
 * @Value ：获取配置文件的值
 * 注入值：在Spring容器初始化（所以bean）之后，在当前的所在容器中获取值，然后注入
 * Spring容器：父容器
 * SpringMVC容器：子容器
 * 子容器能够访问父容器的资源（bean），但是父容器不能访问子容器的资源（bean）
 *
 * 此类专门用于获取配置文件的内容，此类交由spring容器管理，在父容器读取配置文件并初始化，注入值，
 * 然后通过在子容器controller中注入service，通过子容器访问父容器的资源
 */
@Service
public class PropertiesService {

    @Value("${REPOSITORY_PATH}")
    public String REPOSITORY_PATH ;

    @Value("${IMAGE_BASE_URL}")
    public String IMAGE_BASE_URL ;

}
