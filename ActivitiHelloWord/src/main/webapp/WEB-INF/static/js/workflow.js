$(function(){
	var h = $(document).height();
	$("#leftContent").height(h);
	$("#rightContent").height(h);
	$(document.body).css({"overflow-y": "hidden"}); 
	$(document.body).css({"overflow-x": "hidden"}); 
})
function menuEvent(obj){
	$(obj).siblings("a").css("background","");
	$(obj).siblings("a").css("color","#d9d9d9");
	$(obj).css("color","#454545");
	$(obj).css("background","#FFFFFF");
}
		
/**
 * @todo 获取系统部署列表
 */
function queryDeployList(){
	$("#deployListContrainer").show();
	$.ajax({
	        type: "GET",
	        url: "/ActivitiHelloWord/workflow/contract/deploy/list",
	        dataType: "json",
	        success: function(data) {
	        	var tbody = $("#deployListContrainer tbody").eq(0);
	        	$(tbody).html("");
	        	$.each(data,function(index,item){
	        		var id = item.id;
	        		var deploymentId = item.deploymentId;
	        		var node = "<tr><td>"+id+"</td><td>"+deploymentId+"</td><td><button type='button' class='btn btn-success btn-xs' onclick='getDefinetion("+deploymentId+")'>流程定义信息</button>&nbsp;&nbsp;&nbsp;&nbsp;<button type='button' class='btn btn-success btn-xs' onclick='start()'>发布审批流程</button></td></tr>";
	        		$(tbody).append(node);
	        	});
	        },
	        error: function(data) {
	           	console.log(data);
	        }
	 });
}
/**
 * 发布一个流程
 */
function start(){
	$.ajax({
        type: "GET",
        url: "/ActivitiHelloWord/trial/flow/contract/start?processKey=leave_process&userId=100&contractId=001",
        dataType: "json",
        success: function(data) {
        	alert("发布成功");
        },
        error: function(data) {
        	alert("发布失败");
        }
	});
}
/**
 * @todo 获取流程定义信息
 * @param definetionId
 */
function getDefinetion(definetionId){
	$("#definetionInfoContrainer").show();
	$.ajax({
	        type: "GET",
	        url: "/ActivitiHelloWord/trial/flow/definetion/get?deploymentId="+definetionId,
	        dataType: "json",
	        success: function(data) {
	        	var tbody = $("#definetionInfoContrainer tbody").eq(0);
	        	$(tbody).html("");
	        	$.each(data,function(index,item){
	        		var definitionkey = item.definitionkey;
	        		var processKey = item.processKey;
	        		var name = item.name;
	        		var resourceName = item.resourceName;
	        		var dgrmResourceName = item.dgrmResourceName;
	        		var version = item.version;
	        		var node ="<tr><td>"+definitionkey+"</td><td>"+processKey+"</td><td>"+name+"</td><td>"+resourceName+"</td><td>"+dgrmResourceName+"</td><td>"+version+"</td></tr>";
	        		$(tbody).append(node);
	        	});
	        },
	        error: function(data) {
	           	console.log(data);
	        }
	 });
}


/**
 * 获取自己的任务列表
 */
function queryMyTasksList(){
	$("#myTaskListContrainer").show();
	$.ajax({
	        type: "GET",
	        url: "/ActivitiHelloWord/trial/flow/contract/tasks/query?userId=100",
	        dataType: "json",
	        success: function(data) {
	        	var tbody = $("#myTaskListContrainer tbody").eq(0);
	        	$(tbody).html("");
	        	$.each(data,function(index,item){
	        		var taskId = item.taskId;
	        		var name = item.name;
	        		var assignee = item.assignee;
	        		var node ="<tr><td>"+taskId+"</td><td>"+name+"</td><td>"+assignee+"</td><td><button type='button' class='btn btn-success btn-xs' onclick='completeEvent("+taskId+")'>完成</button></td></tr>";
	        		$(tbody).append(node);
	        	});
	        },
	        error: function(data) {
	           	console.log(data);
	        }
	 });
}
/**
 * 完成任务
 * @param taskId 任务ID
 */
function completeEvent(taskId){
	$.ajax({
        type: "GET",
        url: "/ActivitiHelloWord/trial/flow/contract/tasks/complete?taskId="+taskId,
        dataType: "json",
        success: function(data) {
        	if(data.code == 200){
        		alert("成功");
        	}else{
        		alert("失败");
        	}
        },
        error: function(data) {
        	alert("失败");
        }
	});
}
function queryMySuccessTasksList(){
	$("#successTaskListContrainer").show();
	$.ajax({
	        type: "GET",
	        url: "/ActivitiHelloWord/trial/flow/contract/tasks/success/query?userId=100",
	        dataType: "json",
	        success: function(data) {
	        	var tbody = $("#successTaskListContrainer tbody").eq(0);
	        	$(tbody).html("");
	        	$.each(data,function(index,item){
	        		var taskId = item.taskId;
	        		var name = item.name;
	        		var assignee = item.assignee;
	        		var node ="<tr><td>"+taskId+"</td><td>"+name+"</td><td>"+assignee+"</td></tr>";
	        		$(tbody).append(node);
	        	});
	        },
	        error: function(data) {
	           	console.log(data);
	        }
	 });
}