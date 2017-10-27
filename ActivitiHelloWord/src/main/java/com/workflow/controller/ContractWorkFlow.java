package com.workflow.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/** * 
 * @TODO 合同工作流
 * @author gaoLun * 
 * @date 2017年10月24日 上午8:34:49 * 
 * @version 1.0 * 
 * @parameter  * 
 * @since  * 
 * @return  */
@Controller
public class ContractWorkFlow {
	
	private Logger LOGGER = LoggerFactory.getLogger(ContractWorkFlow.class);
	@Resource
	private ProcessEngine processEngine;
	@Resource
	private RepositoryService repositoryService;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskService taskService;
	/**
	 * 
	  * TODO 已部署流程列表
	  * @Title: ContractWorkFlow.java 
	  * @Package: com.workflow.controller 
	  * @return
	  * @author: gaoLun
	  * @date : 2017年10月24日 上午11:21:00
	 */
	@RequestMapping("/workflow/contract/deploy/list")
	@ResponseBody
	public String queryDeployWorkFlowList(){
		List<ProcessDefinition> ProcessDefinitionList = repositoryService.createProcessDefinitionQuery().list();
		LOGGER.info("ProcessDefinitionList size : " + ProcessDefinitionList.size());
		JSONArray jsonArray = new JSONArray();
		for(ProcessDefinition processDefinition : ProcessDefinitionList){
			JSONObject object = new JSONObject();
			object.put("id", processDefinition.getId());
			object.put("description",processDefinition.getDescription());
			object.put("deploymentId", processDefinition.getDeploymentId());
			object.put("name", processDefinition.getName());
			jsonArray.add(object);
		}
		return jsonArray.toJSONString();
	}
	/**
	  * TODO 获取流程定义信息
	  * @Title: ContractWorkFlow.java 
	  * @Package: com.workflow.controller 
	  * @return
	  * @author: gaoLun
	  * @date : 2017年10月27日 上午9:03:54
	 */
	@RequestMapping("/trial/flow/definetion/get")
	@ResponseBody
	public String getProcessDefinetionInfo(@RequestParam("deploymentId") String deploymentId){
		JSONArray jsonArray = new JSONArray();
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId);
		List<ProcessDefinition> processDefinitions = processDefinitionQuery.list();
		for(ProcessDefinition processDefinition : processDefinitions){
			JSONObject object = new JSONObject();
			object.put("definitionkey", processDefinition.getId());
			object.put("processKey", processDefinition.getKey());
			object.put("name", processDefinition.getName());
			object.put("version", processDefinition.getVersion());
			object.put("resourceName", processDefinition.getResourceName());
			object.put("dgrmResourceName", processDefinition.getDiagramResourceName());
			jsonArray.add(object);
		}
		return jsonArray.toJSONString();
	}
	/**
	 * 
	  * TODO 使用流程的key发布一个请假流程
	  * @Title: ContractWorkFlow.java 
	  * @Package: com.workflow.controller 
	  * @param userId  员工
	  * @param contractId  业务Id（合同id）
	  * @return
	  * @author: gaoLun
	  * @date : 2017年10月27日 下午2:11:17
	 */
	@RequestMapping("/trial/flow/contract/start")
	@ResponseBody
	public String doStartProcessInstance(
				@RequestParam("processKey") String processKey,
				@RequestParam("userId") String userId,
				@RequestParam("contractId") String contractId){
		JSONObject object = new JSONObject();
		Map<String, Object> variables = new HashMap<String, Object>(); 
			variables.put("userId", userId);
			variables.put("contractID", contractId);
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey,variables);
        	object.put("id", processInstance.getId());
        	object.put("activitiId", processInstance.getActivityId());
        	object.put("businessKey", processInstance.getBusinessKey());
        	object.put("deploymentId", processInstance.getDeploymentId());
        	object.put("processVariables", processInstance.getProcessVariables());
		return object.toJSONString();
	}
	/**
	  * TODO 查询当前自己的任务
	  * @Title: ContractWorkFlow.java 
	  * @Package: com.workflow.controller 
	  * @param userId 员工id
	  * @return
	  * @author: gaoLun
	  * @date : 2017年10月27日 下午2:17:36
	 */
	@RequestMapping("/trial/flow/contract/tasks/query")
	@ResponseBody
	public String queryTask(@RequestParam("userId") String userId){
		JSONArray jsonArray = new JSONArray();
		List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).list();
		for(Task task: tasks){
			JSONObject object = new JSONObject();
			object.put("taskId", task.getId());
			object.put("name", task.getName());
			object.put("assignee", task.getAssignee());
			object.put("createTime", task.getCreateTime().toString());
				Map<String,Object> variables =  task.getProcessVariables();
			object.put("variables", variables);
			jsonArray.add(object);
		}
		return jsonArray.toJSONString();
	}
	/**
	 * 
	  * TODO 查询自己完成的任务
	  * @Title: ContractWorkFlow.java 
	  * @Package: com.workflow.controller 
	  * @param userId
	  * @return
	  * @author: gaoLun
	  * @date : 2017年10月27日 下午6:21:50
	 */
	@RequestMapping("/trial/flow/contract/tasks/success/query")
	@ResponseBody
	public String querySuccessTask(@RequestParam("userId") String userId){
		JSONArray jsonArray = new JSONArray();
		List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).list();
		for(Task task: tasks){
			JSONObject object = new JSONObject();
			object.put("taskId", task.getId());
			object.put("name", task.getName());
			object.put("assignee", task.getAssignee());
			object.put("createTime", task.getCreateTime().toString());
				Map<String,Object> variables =  task.getProcessVariables();
			object.put("variables", variables);
			jsonArray.add(object);
		}
		return jsonArray.toJSONString();
	}
	/**
	  * TODO 完成一个任务
	  * @Title: ContractWorkFlow.java 
	  * @Package: com.workflow.controller 
	  * @param taskId
	  * @return
	  * @author: gaoLun
	  * @date : 2017年10月27日 下午6:20:48
	 */
	@RequestMapping("/trial/flow/contract/tasks/complete")
	@ResponseBody
	public String completeTask(@RequestParam("taskId") String taskId){
		JSONObject object = new JSONObject();
		try {
			taskService.complete(taskId);
		} catch (Exception e) {
			LOGGER.info("--- complete --- :" + e.getMessage());
			object.put("code", 500);
			object.put("msg", "完成失败");
			return object.toJSONString();
		}
		object.put("code", 200);
		object.put("msg", "ok");
		return object.toJSONString();
	}
	
	
}
