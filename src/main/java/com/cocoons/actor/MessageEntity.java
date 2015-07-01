package com.cocoons.actor;

/**
 *
 * @author qinguofeng
 */
public class MessageEntity implements java.io.Serializable {
	private static final long serialVersionUID = 4495291034002788630L;

	private String funcName;
	private Object[] params;

	public MessageEntity(String funcName, Object... params) {
		this.funcName = funcName;
		this.params = params;
	}

	/**
	 * @return the funcName
	 */
	public String getFuncName() {
		return funcName;
	}

	/**
	 * @param funcName
	 *            the funcName to set
	 */
	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	/**
	 * @return the params
	 */
	public Object[] getParams() {
		return params;
	}

	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams(Object[] params) {
		this.params = params;
	}

}
