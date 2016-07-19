package com.key.mail.java;

import java.io.File;

import com.key.mail.comm.IniReader;

/**
 * 配置文件读取类。
 *
 * @author Key.Xiao
 * @version 1.0
 */
public class JavaMailConfig {

	private static IniReader config;

	static {
		if (new File("conf/java_mail.ini").exists()) {
			config = new IniReader("conf/java_mail.ini");
		} else if (new File("java_mail.ini").exists()) {
			config = new IniReader("java_mail.ini");
		}
	}

	/** sections */
	private static String settings = "settings";
	private static String email_host = "email_host";
	private static String mail_address = "mail_address";
	private static String subject = "subject";

	/** settings */
	public static boolean ENABLE = config.getBoolean(settings, "enable");

	/** email_host */
	public static String HOST_NAME = config.getString(email_host, "host_name");
	public static String HOST_PORT = config.getString(email_host, "host_port");
	public static String FROM = config.getString(email_host, "from");
	public static String USER_NAME = config.getString(email_host, "user_name");
	public static String USER_PASSWORD = config.getString(email_host, "user_password");

	/** receiver */
	public static String RECEIVER = config.getString(mail_address, "receiver");
	public static String CC = config.getString(mail_address, "cc");

	/** subject */
	public static String SUBJECT = config.getString(subject, "subject");

	/*public static void main(String[] args) {
		System.out.println(ENABLE);
	}*/
}
