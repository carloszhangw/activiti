package com.workflow.utils;
/** * 
 * @author gaoLun * 
 * @date 2017年10月30日 下午1:53:16 * 
 * @version 1.0 * 
 * @parameter  * 
 * @since  * 
 * @return  */
public enum Stage {
	
	EMPLOYEE_LEAVE("employeeLeave","员工申请"),
	MANAGER_LEAVE("managerLeave","经理审批"),
	END_EVENT("endEvent","结束");
	
	private String name;
	private String value;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	private Stage(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
}