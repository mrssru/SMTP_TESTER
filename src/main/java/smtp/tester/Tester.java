package smtp.tester;

import com.sun.mail.smtp.SMTPTransport;

import javax.mail.*;
import javax.mail.internet.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
/**
 * Created by oom on 01.06.2016.
 */
public class Tester {
    private static String host;
    private static int port;
    private static String protocol;
    private static boolean authentication;
    private static boolean starttls;
    private static String from;
    private static String to;
    private static String username;
    private static String password;

    private static final PropertyUtils UTILS = new PropertyUtils();
    private static void init() {
        host = UTILS.getValue("mail.smtp.host");
        port = UTILS.getInteger("mail.smtp.port");
        protocol = UTILS.getValue("email.protocol");
        authentication = UTILS.getBoolean("mail.smtp.auth");
        starttls = UTILS.getBoolean("mail.smtp.starttls.enable");
        username = UTILS.getValue("mail.user");
        password = UTILS.getValue("mail.password");
        from = UTILS.getValue("mail.from");
        to = UTILS.getValue("mail.to");
    }

    public static final void main(String[] args) throws MessagingException {
        System.out.println("Start smtp tester");
        init();
        Session mailSession = createMailSession();
        MimeMessage msg = new MimeMessage(mailSession);

        String subject = "MESSAGE SUBJECT";
        List<String> recipients = new ArrayList<String>();
        recipients.add(to);
        System.out.println("SEND EMAIL TO " + to);
        setHeaders(msg, subject, recipients);
        String textContent = "MESSAGE TEXT CONTENT";
        setContent(msg, textContent);
        send(msg, mailSession);
        System.out.println("Mail send successfully");
    }

    private static Session createMailSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", authentication);
        if(starttls) {
            properties.put("mail.smtp.starttls.enable", starttls);
            properties.put("mail.smtp.ssl.trust", host);
        }
        Authenticator auth = null;
        if(authentication) {
            properties.put("mail.user", username);
            properties.put("mail.password", password);
            auth = new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            };
        }

        return Session.getInstance(properties, auth);
    }


    private static void setHeaders(MimeMessage msg, String subject, List<String> recipients) throws MessagingException {
        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO, toAddresses(recipients));
        msg.setSubject(subject, Charset.forName("UTF-8").name());
        msg.setSentDate(new Date());
    }

    private static InternetAddress[] toAddresses(List<String> recipients) throws AddressException {
        List<InternetAddress> toAddresses = new ArrayList<InternetAddress>(recipients.size());

        for (String recipient : recipients) {
            toAddresses.add(new InternetAddress(recipient));
        }

        return toAddresses.toArray(new InternetAddress[recipients.size()]);
    }

    private static void setContent(Message message, String textContent) throws MessagingException {
        System.setProperty("mail.mime.encodefilename", "true");
        Multipart multipart = new MimeMultipart();
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(textContent,"utf-8");
        multipart.addBodyPart(messageBodyPart);

        message.setContent(multipart);
    }

    private static void send(Message message, Session mailSession) throws MessagingException {
        SMTPTransport smtpTransport = (SMTPTransport) mailSession.getTransport(protocol);

        String userName = null;
        String userPass = null;
        if(authentication) {
            userName = username;
            userPass = password;
        }
        smtpTransport.connect(host, userName, userPass);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }
}
