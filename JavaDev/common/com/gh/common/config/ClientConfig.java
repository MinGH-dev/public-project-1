package com.gh.common.config;

import java.util.Properties;

import com.gh.common.ClientPath;
import com.gh.common.LogLevel;
import com.gh.common.context.PropsKey;
import com.gh.common.util.CommonUtil;
import com.gh.common.util.FileUtil;

public class ClientConfig {
	private static ClientConfig config = new ClientConfig();
	
	private Properties props = null;
	
	private ClientConfig () {
		init();
	}
	
	public static ClientConfig getConfig () {
		return config;
	}
	
	private void init () {
		try {
			Properties props = FileUtil.getProperties(ClientPath.CLIENT_CONFIG_FILE_PATH);
			System.out.println(props);
			boolean isLoadOk = true;
			
			for (PropsKey key : PropsKey.values()) {
				if (!props.containsKey(key.toString())) {
					CommonUtil.println(key.toString() + " Undefined Config Props in " + ClientPath.CLIENT_CONFIG_FILE_PATH, LogLevel.ERROR);
					isLoadOk = false;
				}
			}
			
			if (!isLoadOk) {
				CommonUtil.println("System ShutDown", LogLevel.SYSTEM);
				System.exit(0);
			} else {
				this.props = props;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getProps (PropsKey key) {
		return this.props.getProperty(key.toString());
	}
	
	
}
