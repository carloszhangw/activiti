package com.workflow.controller;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.workflow.dto.Login;
import com.workflow.utils.Stage;


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
	  * TODO 登录
	  * @Title: ContractWorkFlow.java 
	  * @Package: com.workflow.controller 
	  * @param login
	  * @return
	  * @author: gaoLun
	  * @date : 2017年10月30日 下午3:59:21
	 */
	@RequestMapping(value="/trial/flow/login",method=RequestMethod.POST,produces="text/html;charset=UTF-8")
	@ResponseBody
	public String login(@ModelAttribute Login login,HttpServletRequest request,HttpServletResponse response){
		JSONObject object = new JSONObject();
		object.put("data", login.getLoginName());
		object.put("code", 200);
		object.put("msg", "ok");
		return object.toJSONString();
	}
	/**
	 * 
	  * TODO 已部署流程列表
	  * @Title: ContractWorkFlow.java 
	  * @Package: com.workflow.controller 
	  * @return
	  * @author: gaoLun
	  * @date : 2017年10月24日 上午11:21:00
	 */
	@RequestMapping(value="/workflow/contract/deploy/list",produces="text/html;charset=UTF-8")
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
	@RequestMapping(value="/trial/flow/definetion/get",produces="text/html;charset=UTF-8")
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
	  * TODO 启动一个流程
	  * @Title: ContractWorkFlow.java 
	  * @Package: com.workflow.controller 
	  * @param userId  员工
	  * @param contractId  业务Id（合同id）
	  * @return
	  * @author: gaoLun
	  * @date : 2017年10月27日 下午2:11:17
	 */
	@RequestMapping(value="/trial/flow/contract/start",produces="text/html;charset=UTF-8")
	@ResponseBody
	public String doStartProcessInstance(
				@RequestParam("processKey") String processKey,
				@RequestParam("userId") String userId,
				@RequestParam("contractId") String contractId){
		JSONObject object = new JSONObject();
		Map<String, Object> varibales = new HashMap<String, Object>();
		varibales.put("userId", userId);
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey,varibales);
        	object.put("id", processInstance.getId());
        	object.put("activitiId", processInstance.getActivityId());
        	object.put("businessKey", processInstance.getBusinessKey());
        	object.put("deploymentId", processInstance.getDeploymentId());
        	object.put("processVariables", processInstance.getProcessVariables());
		return object.toJSONString();
	}
	/**
	  * TODO 查询当前自己当前的任务
	  * @Title: ContractWorkFlow.java 
	  * @Package: com.workflow.controller 
	  * @param userId 员工id
	  * @param processKey 流程部署key
	  * @return
	  * @author: gaoLun
	  * @date : 2017年10月27日 下午2:17:36
	 */
	@RequestMapping(value="/trial/flow/contract/tasks/query",produces="text/html;charset=UTF-8")
	@ResponseBody
	public String queryCurrentTask(@RequestParam("userId") String userId ,@RequestParam("processKey") String processKey){
		JSONObject object = new JSONObject();
		/**
		 * 当前流程的任务
		 */
		Task task = taskService.createTaskQuery().taskAssignee(userId).singleResult();
		if(null == task){
			return object.toJSONString();
		}
		object.put("taskId", task.getId());
		object.put("name", task.getName());
		object.put("assignee", task.getAssignee());
		object.put("createTime", task.getCreateTime().toString());
			Map<String,Object> variables = taskService.getVariables(task.getId());
		object.put("variables", variables);
		return object.toJSONString();
	}
	/**
	 * 
	  * TODO 查询自己的历史任务
	  * @Title: ContractWorkFlow.java 
	  * @Package: com.workflow.controller 
	  * @param userId
	  * @return
	  * @author: gaoLun
	  * @date : 2017年10月27日 下午6:21:50
	 */
	@RequestMapping(value="/trial/flow/contract/tasks/history/query",produces="text/html;charset=UTF-8")
	@ResponseBody
	public String queryHistoryTask(@RequestParam("userId") String userId , @RequestParam("processKey") String processKey){
		JSONArray jsonArray = new JSONArray();
		List<HistoricTaskInstance> HistoricTaskInstances = 
					processEngine.getHistoryService().createHistoricTaskInstanceQuery().taskAssignee(userId).processDefinitionKey(processKey).list();
		for(HistoricTaskInstance task: HistoricTaskInstances){
			JSONObject object = new JSONObject();
			object.put("taskId", task.getId());
			object.put("name", task.getName());
			object.put("assignee", task.getAssignee());
			object.put("createTime", task.getCreateTime().toString());
				Map<String,Object> variables =  task.getTaskLocalVariables();
			object.put("variables", variables);
			String taskDefinitionKey = task.getTaskDefinitionKey();
			String status = "";
			if(taskDefinitionKey.equals(Stage.EMPLOYEE_LEAVE.getName())){
				status = "发起审批阶段";
			}else if(taskDefinitionKey.equals(Stage.MANAGER_LEAVE.getName())){
				status = "经理审批阶段";
			}else if(taskDefinitionKey.equals(Stage.END_EVENT.getName())){
				status = "结束";
			}
			object.put("status", status);
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
	@RequestMapping(value="/trial/flow/contract/tasks/complete",produces="text/html;charset=UTF-8")
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
	/**
	 * 
	  * TODO 未完成任务
	  * @Title: ContractWorkFlow.java 
	  * @Package: com.workflow.controller 
	  * @param userId
	  * @param processKey
	  * @return
	  * @author: gaoLun
	  * @date : 2017年10月31日 下午2:55:45
	 */
	@RequestMapping(value="/trial/flow/contract/tasks/unfinished/query",produces="text/html;charset=UTF-8")
	@ResponseBody
	public String queryUnfinishedTask(@RequestParam("userId") String userId , @RequestParam("processKey") String processKey){
		JSONArray jsonArray = new JSONArray();
		List<HistoricTaskInstance> HistoricTaskInstances =  
				processEngine.getHistoryService().createHistoricTaskInstanceQuery().taskAssignee(userId).processDefinitionKey(processKey).unfinished().list();
		for(HistoricTaskInstance task: HistoricTaskInstances){
			JSONObject object = new JSONObject();
			object.put("taskId", task.getId());
			object.put("name", task.getName());
			object.put("assignee", task.getAssignee());
			object.put("createTime", task.getCreateTime().toString());
				Map<String,Object> variables =  task.getTaskLocalVariables();
			object.put("variables", variables);
			String taskDefinitionKey = task.getTaskDefinitionKey();
			String status = "";
			if(taskDefinitionKey.equals(Stage.EMPLOYEE_LEAVE.getName())){
				status = "发起审批阶段";
			}else if(taskDefinitionKey.equals(Stage.MANAGER_LEAVE.getName())){
				status = "经理审批阶段";
			}else if(taskDefinitionKey.equals(Stage.END_EVENT.getName())){
				status = "结束";
			}
			object.put("status", status);
			jsonArray.add(object);
		}
		return jsonArray.toJSONString();
	}
	/**
	 * 
	  * TODO 已完成任务
	  * @Title: ContractWorkFlow.java 
	  * @Package: com.workflow.controller 
	  * @param userId
	  * @param processKey
	  * @return
	  * @author: gaoLun
	  * @date : 2017年10月31日 下午3:32:17
	 */
	@RequestMapping(value="/trial/flow/contract/tasks/finished/query",produces="text/html;charset=UTF-8")
	@ResponseBody
	public String queryFinishedTask(@RequestParam("userId") String userId , @RequestParam("processKey") String processKey){
		JSONArray jsonArray = new JSONArray();
		List<HistoricTaskInstance> HistoricTaskInstances =  
				processEngine.getHistoryService().createHistoricTaskInstanceQuery().taskAssignee(userId).processDefinitionKey(processKey).finished().list();
		for(HistoricTaskInstance task: HistoricTaskInstances){
			JSONObject object = new JSONObject();
			object.put("taskId", task.getId());
			object.put("name", task.getName());
			object.put("assignee", task.getAssignee());
			object.put("createTime", task.getCreateTime().toString());
				Map<String,Object> variables =  task.getTaskLocalVariables();
			object.put("variables", variables);
			String taskDefinitionKey = task.getTaskDefinitionKey();
			String status = "";
			if(taskDefinitionKey.equals(Stage.EMPLOYEE_LEAVE.getName())){
				status = "发起审批阶段";
			}else if(taskDefinitionKey.equals(Stage.MANAGER_LEAVE.getName())){
				status = "经理审批阶段";
			}else if(taskDefinitionKey.equals(Stage.END_EVENT.getName())){
				status = "结束";
			}
			object.put("status", status);
			jsonArray.add(object);
		}
		return jsonArray.toJSONString();
	}
	/**
	 * 
	  * TODO 查看流程图
	  * @Title: ContractWorkFlow.java 
	  * @Package: com.workflow.controller 
	  * @param response
	  * @throws IOException
	  * @author: gaoLun
	  * @date : 2017年10月31日 下午4:13:48
	 */
	@RequestMapping(value="/trial/flow/contract/tasks/leaveprocess")
	public void viewWorkFlowPic( @RequestParam("processKey") String processKey,HttpServletResponse response) throws IOException{
		 FileInputStream in;  
         response.setContentType("application/octet-stream;charset=UTF-8");  
         try {
            in=new FileInputStream("E:\\diagrams\\leaveProcess.leave_process.png"); 
            int i=in.available();  
            byte[]data=new byte[i];  
            in.read(data);  
            in.close();  
            //写图片  
            OutputStream outputStream=new BufferedOutputStream(response.getOutputStream());  
            outputStream.write(data);  
            outputStream.flush();  
            outputStream.close();  
        } catch (Exception e) {
            e.printStackTrace();  
        }  
	}
}
