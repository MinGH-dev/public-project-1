package com.gh.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class FileUtil {
	public static Properties getProperties (String path) throws Exception {
		Properties props = new Properties();
		
		props.load(new FileInputStream(new File(path)));
		
		return props;
	}
}
