package com.key.mail.apache;

import java.io.File;

import com.key.mail.comm.IniReader;

/**
 * 配置文件读取类。
 *
 * @author Key.Xiao
 * @version 1.0
 */
public class ApaMailConfig {
	private static IniReader config;
	static {
		if (new File("conf/apache_mail.ini").exists()) {
			config = new IniReader("conf/apache_mail.ini");
		} else if (new File("apache_mail.ini").exists()) {
			config = new IniReader("apache_mail.ini");
		}
	}

	// sections
	private static String settings = "settings";
	private static String email_host = "email_host";
	private static String mail_address = "mail_address";
	private static String subject = "subject";

	/** settings */
	public static boolean ENABLE = config.getBoolean(settings, "enable");

	// email_host 邮件主机s
	public static String HOST_NAME = config.getString(email_host, "host_name");
	public static String CHARSET = config.getString(email_host, "charset");
	public static String FROM = config.getString(email_host, "from");
	public static String SENDER = config.getString(email_host, "sender");
	public static String AUTH_NAME = config.getString(email_host, "authentication_name");
	public static String AUTH_PASSWORD = config.getString(email_host, "auth_password");

	// 收件人地址
	public static String[] RECEIVER = config.getStringArray(mail_address, "receiver");
	public static String[] CC = config.getStringArray(mail_address, "cc");

	// 邮件主题
	public static String SUBJECT = config.getString(subject, "subject");

	/*public static void main(String[] args) {
		System.out.println(ENABLE);
	}*/
}
