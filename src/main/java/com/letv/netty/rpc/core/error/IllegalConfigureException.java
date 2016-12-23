package com.letv.netty.rpc.core.error;

/**
 * Title: 非法配置异常<br>
 * 
 * Description: 初始化时候就抛出<br>
 * 
 * Company: <a href=www.le.com>LeEco</a><br/>
 * 
 */
public class IllegalConfigureException extends InitErrorException {

	/**
	 * 错误的配置项，例如refernce.loadblance
	 */
	private String configKey;

	/**
	 * 错误的配置值，例如random（正确的是random）
	 */
	private String configValue;

    protected IllegalConfigureException() {
    }



	/**
	 * @param configKey
	 * @param configValue
	 */
	public IllegalConfigureException(int code, String configKey, String configValue) {
		super(code,"The value of config " + configKey + " [" + configValue + "] is illegal, please check it.");
		this.configKey = configKey;
		this.configValue = configValue;
	}

    /**
     *
     * @param configKey
     * @param configValue
     * @param message
     */
    public IllegalConfigureException(int code, String configKey, String configValue, String message) {
        super(code,"The value of config " + configKey + " [" + configValue + "] is illegal, " + message+getErrorCodeResource(code));
        this.configKey = configKey;
        this.configValue = configValue;
    }

	/**
	 * @return the configKey
	 */
	public String getConfigKey() {
		return configKey;
	}

	/**
	 * @return the configValue
	 */
	public String getConfigValue() {
		return configValue;
	}

}
