package uk.co.novinet.e2e;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;

import static uk.co.novinet.e2e.TestUtils.getTextFromMessage;

public class StaticMessage {
    private String contentType;
    private String content;
    private String subject;
    private String from;

    public StaticMessage(Message message) throws IOException, MessagingException {
        this.contentType = message.getContentType();
        this.content = getTextFromMessage(message);
        this.subject = message.getSubject();
        this.from = ((InternetAddress) message.getFrom()[0]).getAddress();
    }

    public String getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }

    public String getSubject() {
        return subject;
    }

    public String getFrom() {
        return from;
    }
}
