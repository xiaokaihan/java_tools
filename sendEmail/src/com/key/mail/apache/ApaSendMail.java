package com.key.mail.apache;

import static com.key.mail.apache.ApaMailConfig.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

/**
 * 邮件发送类
 *
 * @author Key.Xiao
 * @version 1.0
 */
public class ApaSendMail {

	public static void sendMail() {
		MultiPartEmail email = new MultiPartEmail();
		try {
			email.setHostName(HOST_NAME);
			email.setFrom(FROM, SENDER);
			for (String receiver : RECEIVER) {
				if (StringUtils.isNotBlank(receiver))
					email.addTo(receiver);
			}
			for (String cc : CC) {
				if (StringUtils.isNotBlank(cc))
					email.addCc(cc);
			}
			// 设置认证：用户名-密码。
			if (StringUtils.isNotBlank(AUTH_NAME) && StringUtils.isNotBlank(AUTH_PASSWORD)) {
				email.setAuthentication(AUTH_NAME, AUTH_PASSWORD);
			}
			email.setSubject(SUBJECT);

			// 发送
			email.send();
		} catch (EmailException e) {
			e.printStackTrace();
		}
	}
}