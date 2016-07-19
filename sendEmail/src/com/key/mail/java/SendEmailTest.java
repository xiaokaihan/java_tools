package com.key.mail.java;

import static com.key.mail.java.JavaMailConfig.*;

/**
 * 邮件发送测试类
 *
 * @author Key.Xiao
 * @version 1.0
 */
public class SendEmailTest {

	public void checkSend() {
		this.startSend();
	}

	public void startSend() {
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost(HOST_NAME);
		mailInfo.setMailServerPort(HOST_PORT);
		mailInfo.setValidate(true);
		mailInfo.setUserName(USER_NAME);
		mailInfo.setPassword(USER_PASSWORD);
		mailInfo.setFromAddress(FROM);
		mailInfo.setToAddress(RECEIVER);
		mailInfo.setSubject(SUBJECT);

		String content = "";
		mailInfo.setContent(content);
		SimpleMailSender simpleMailSender = new SimpleMailSender();
		simpleMailSender.sendTextMail(mailInfo);
	}

	public static void main(String[] args) {

		new SendEmailTest().startSend();
		System.out.println("send successful");
	}

}
