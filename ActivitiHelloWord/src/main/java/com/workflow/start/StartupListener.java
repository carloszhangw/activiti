package com.workflow.start;


import javax.annotation.Resource;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
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
	  * @date : 2017年10月25日 上午8:45:04
	 */
	public void deploy(){
		//部署
		repositoryService.createDeployment().addClasspathResource("diagrams/leaveProcess.bpmn").deploy();
		LOGGER.info("---- workflow deploy ----");
	}

}
