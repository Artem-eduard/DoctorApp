package com.cb.softwares.doctorapp.gmail;

import android.content.Context;
import android.util.Log;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

public class GMailSender {

    private String mailhost = "smtp.gmail.com";
    private String user;
    private String password;
    private Session session;
    Context context;


   /* static {
        Security.addProvider(new JSSEProvider());
    }*/

    public GMailSender(String user, String password,Context context) {
        this.user = user;
        this.password = password;
        this.context = context;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = (Session) Session.getDefaultInstance(props);
    }

    public PasswordAuthentication getPasswordAuthentication() {


        return new PasswordAuthentication(user, password);
    }

    public void setSession(Session session){
        this.session = session;
    }


    public synchronized void sendMail(String subject, String body, String sender, String recipients) throws Exception {
        try{
            MimeMessage message = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
            message.setSender(new InternetAddress(sender));
            message.setSubject(subject);
            message.setDataHandler(handler);
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            Transport.send(message);
        }catch(Exception e){
            Log.w("MainActivity", "Error in sending: " + e.toString());
        }
    }
}
