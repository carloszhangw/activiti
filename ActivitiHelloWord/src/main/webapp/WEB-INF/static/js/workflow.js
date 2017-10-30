$(function(){
	var h = $(document).height();
	$("#leftContent").height(h);
	$("#rightContent").height(h);
	$(document.body).css({"overflow-y": "hidden"}); 
	$(document.body).css({"overflow-x": "hidden"}); 
	/**
	 * 获取cookie
	 */
	_userId = getCookie("userId");
})
function getCookie(name){
	var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
    if(arr=document.cookie.match(reg))
        return unescape(arr[2]); 
    else 
        return ""; 
}
function setCookie(name,value){
	var Days = 30; 
    var exp = new Date(); 
    exp.setTime(exp.getTime() + Days*24*60*60*1000); 
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString(); 
}
function menuEvent(obj){
	$(obj).siblings("a").css("background","");
	$(obj).siblings("a").css("color","#d9d9d9");
	$(obj).css("color","#454545");
	$(obj).css("background","#FFFFFF");
}
function showLoginContrainer(){
	$("#loginContrainer").siblings("div").hide();
	$("#loginContrainer").find("div").eq(0).show();
	$("#loginContrainer").show();
}
/**
 * 登录
 */
function login(){
	var loginName = $("#loginName").val();
	var loginPwd = $("#loginPwd").val();
	if("" == loginName || undefined == loginName){
		alert("请输入用户名");
		return;
	}
	if("" == loginPwd || undefined == loginPwd){
		alert("请输入密码");
		return;
	}
	$.ajax({
        type: "POST",
        url: "/ActivitiHelloWord/trial/flow/login",
        data:{
        	"loginName":loginName,
        	"loginPwd":loginPwd
        },
        dataType: "json",
        success: function(data) {
        	if(data.code == 200){
        		_userId = data.data;
        		setCookie("userId",_userId);
        		$("#loginContrainer").siblings("div").show();
        		$("#loginContrainer").find("div").eq(0).hide();
        		alert("登录成功");
        		window.location.href="http://localhost:8080/ActivitiHelloWord/page/workflow.html";
        	}else{
        		alert("登录失败");
        	}
        },
        error: function(data) {
           	console.log(data);
        }
 });
}

/**
 * @todo 获取系统部署列表
 */
function queryDeployList(){
	if("" == _userId){
		alert("请登录");
		return;
	}
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
	        		var name = item.name;
	        		var node = "<tr><td>"+id+"</td><td>"+deploymentId+"</td><td>"+name+"</td><td><button type='button' class='btn btn-success btn-xs' onclick='getDefinetion("+deploymentId+")'>流程定义信息</button>&nbsp;&nbsp;&nbsp;&nbsp;<button type='button' class='btn btn-success btn-xs' onclick='start("+deploymentId+")'>发布审批流程</button></td></tr>";
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
function start(deploymentId){
	if("" == _userId){
		alert("请登录");
		return;
	}
	$.ajax({
        type: "GET",
        url: "/ActivitiHelloWord/trial/flow/contract/start?processKey=leave_process&userId="+_userId+"&contractId=001",
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
	if("" == _userId){
		alert("请登录");
		return;
	}
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
	if("" == _userId){
		alert("请登录");
		return;
	}
	$("#myTaskListContrainer").show();
	$.ajax({
	        type: "GET",
	        url: "/ActivitiHelloWord/trial/flow/contract/tasks/query?userId="+_userId+"&processKey=leave_process",
	        dataType: "json",
	        success: function(data) {
	        	var tbody = $("#myTaskListContrainer tbody").eq(0);
	        	$(tbody).html("");
	        	var item = data;
	        	var taskId = item.taskId;
        		var name = item.name;
        		var assignee = item.assignee;
        		var variables = item.variables;
        		var body = "{userId:"+variables.userId+",contractId:"+variables.contractId+"}";
        		var node ="<tr><td>"+taskId+"</td><td>"+name+"</td><td>"+assignee+"</td><td>"+body+"</td><td><button type='button' class='btn btn-success btn-xs' onclick='completeEvent("+taskId+")'>完成</button></td></tr>";
        		$(tbody).append(node);
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
	if("" == _userId){
		alert("请登录");
		return;
	}
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
function queryMyHistoryTasksList(){
	if("" == _userId){
		alert("请登录");
		return;
	}
	$("#successTaskListContrainer").show();
	$.ajax({
	        type: "GET",
	        url: "/ActivitiHelloWord/trial/flow/contract/tasks/history/query?userId="+_userId+"&processKey=leave_process",
	        dataType: "json",
	        success: function(data) {
	        	var tbody = $("#successTaskListContrainer tbody").eq(0);
	        	$(tbody).html("");
	        	$.each(data,function(index,item){
	        		var taskId = item.taskId;
	        		var name = item.name;
	        		var assignee = item.assignee;
	        		var status = item.status;
	        		var node ="<tr><td>"+taskId+"</td><td>"+name+"</td><td>"+assignee+"</td><td>"+status+"</td></tr>";
	        		$(tbody).append(node);
	        	});
	        },
	        error: function(data) {
	           	console.log(data);
	        }
	 });
}