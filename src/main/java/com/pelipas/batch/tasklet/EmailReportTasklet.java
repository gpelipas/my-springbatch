/**
 * Genaro Pelipas (c) 2020
 */
package com.pelipas.batch.tasklet;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * [desc]
 * 
 * @author gpelipas
 *
 */
public class EmailReportTasklet implements Tasklet {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private Environment env;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		sendEmail();
		
		return RepeatStatus.FINISHED;
	}
	
	private void sendEmail() throws AddressException, MessagingException {
		String emailTo = env.getProperty("gmp.emailer.to");
		String emailFrom = env.getProperty("gmp.emailer.from");
		String emailSubject = env.getProperty("gmp.emailer.subject");
		String emailMessage = env.getProperty("gmp.emailer.message");
		
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");  
		message.setTo(InternetAddress.parse(emailTo));
		message.setFrom(emailFrom);
		message.setSubject(emailSubject);
		message.setText(emailMessage, true);
		
		javaMailSender.send(mimeMessage);
	}
	
}
