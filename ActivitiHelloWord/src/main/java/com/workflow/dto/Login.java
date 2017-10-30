package com.workflow.dto;
/** * 
 * @author gaoLun * 
 * @date 2017年10月30日 下午3:58:26 * 
 * @version 1.0 * 
 * @parameter  * 
 * @since  * 
 * @return  */
public class Login {
	private String loginName;
	private String loginPwd;
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getLoginPwd() {
		return loginPwd;
	}
	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}
}
