package com.Other.ConfirmEmail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendEmail {



    public static void sendMail(String recepient,String code)  throws Exception {
        System.out.println("Preparing message");
        Properties properties = new Properties();

        //Simple Mail Transfer Protocol
        //các ứng dụng thư điện tử thường sử dụng SMTP chỉ để gửi thư đến một máy chủ thư điện tử để được chuyển tiếp
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.port","25");

        String myEmail = "nguyenvinhanchelseafc030499@gmail.com";
        String password = "ANCHELSEA179902";

        //API mail
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myEmail,password);
            }
        });

        Message message = prepareMessage(session,myEmail,recepient,code);
        try {
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        System.out.println("Send message successful");
    }

    private static Message prepareMessage(Session session,String email,String recepient,String code){
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(email));
            message.setRecipient(Message.RecipientType.TO,new InternetAddress(recepient));

            message.setSubject("Please confirm your account");
            String htmlCode = String.format("<h3>Hello %s </h3> <br> <p> Code To confirm your account: <h1>%s<h1> </p>",recepient,code);
            message.setContent(htmlCode,"text/html");

            return message;

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return null;
    }

}
