package com.workflow.start;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/** * 
 * @author gaoLun * 
 * @date 2017年10月25日 上午8:34:42 * 
 * @version 1.0 * 
 * @parameter  * 
 * @since  * 
 * @return  */
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {
	
	private Logger LOGGER = LoggerFactory.getLogger(StartupListener.class);
	
	@Resource
	private ProcessEngine processEngine;
	@Resource
	private RepositoryService repositoryService;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskService taskService;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(event.getApplicationContext().getParent()== null){
			deploy();
		}
	}
	/**
	 * 
	  * TODO 部署一个工作流
	  * @Title: StartupListener.java 
	  * @Package: com.workflow.start 
	  * @author: gaoLun
	 * @throws IOException 
	  * @date : 2017年10月25日 上午8:45:04
	 */
	public void deploy(){
		//部署
		repositoryService.createDeployment().addClasspathResource("diagrams/leaveProcess.bpmn").deploy();
		LOGGER.info("---- workflow deploy ----");
		ProcessDefinition ProcessDefinition =  processEngine.getRepositoryService().createProcessDefinitionQuery().list().get(0);
		String deploymentId = ProcessDefinition.getDeploymentId();
        //获取图片资源名称
        List<String> list = processEngine.getRepositoryService().getDeploymentResourceNames(deploymentId);
        //定义图片资源的名称
        String resourceName = "";
        if(list!=null && list.size()>0){
            for(String name:list){
                if(name.indexOf(".png")>=0){
                    resourceName = name;
                }
            }
        }
        //获取图片的输入流
        InputStream in = processEngine.getRepositoryService().getResourceAsStream(deploymentId, resourceName);
        //将图片生成到D盘的目录下
        File file = new File("E:/"+resourceName);
        //将输入流的图片写到E盘下
        try {
			FileUtils.copyInputStreamToFile(in, file);
		} catch (IOException e) {
			LOGGER.info("file copy error : {} ",e.getMessage());
		}
	}

}
