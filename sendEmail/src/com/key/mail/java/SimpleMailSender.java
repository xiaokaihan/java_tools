package com.key.mail.java;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 发送文本邮件
 *
 * @author Key.Xiao
 * @version 1.0
 */
public class SimpleMailSender {

	public boolean sendTextMail(MailSenderInfo mailInfo) {
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		if (mailInfo.isValidate()) {
			authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
		}
		Session sendMailSession = Session.getDefaultInstance(pro, authenticator);

		try {
			Message mailMessage = new MimeMessage(sendMailSession);
			Address from = new InternetAddress(mailInfo.getFromAddress());
			mailMessage.setFrom(from);
			String to = mailInfo.getToAddress();
			String cc = mailInfo.getCcAddress();
			/** 创建收件人列表, 收件人列表，以","分割 */
			if (to != null && to.trim().length() > 0) {
				String[] arr = to.split(",");
				int receiverCount = arr.length;

				if (receiverCount > 0) {
					InternetAddress[] address = new InternetAddress[receiverCount];
					for (int i = 0; i < receiverCount; i++) {
						address[i] = new InternetAddress(arr[i]);
					}
					mailMessage.addRecipients(Message.RecipientType.TO, address);
				}
			}
			/** 创建抄送列表, 抄送人列表，以","分割 */
			if (cc != null && cc.trim().length() > 0) {
				String[] arr = cc.split(",");
				int receiverCount = arr.length;

				if (receiverCount > 0) {
					InternetAddress[] address = new InternetAddress[receiverCount];
					for (int i = 0; i < receiverCount; i++) {
						address[i] = new InternetAddress(arr[i]);
					}
					mailMessage.addRecipients(Message.RecipientType.CC, address);
				}
			}
			mailMessage.setSubject(mailInfo.getSubject());
			mailMessage.setSentDate(new Date());
			String mailContent = mailInfo.getContent();
			mailMessage.setText(mailContent);
			Transport.send(mailMessage);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

}