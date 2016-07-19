package com.key.mail.apache;

import static com.key.mail.apache.ApaMailConfig.ENABLE;

/**
 * 邮件发送测试类
 *
 * @author Key.Xiao
 * @version 1.0
 */
public class ApaMailTest {

	public static void main(String[] args) {
		if (ENABLE)
			ApaSendMail.sendMail();
		System.out.println("send mail success");
	}
}
