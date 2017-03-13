package io.craigmiller160.email;

import io.craigmiller160.email.model.MessageModel;
import io.craigmiller160.email.model.SendFromModel;
import io.craigmiller160.email.model.SendToModel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;

/**
 * Created by craig on 3/12/17.
 */
public class PersistConfiguration {

    private static final String ROOT_ELEMENT = "EmailConfig";
    private static final String RECIPIENTS_ELEMENT = "Recipients";
    private static final String TO_ELEMENT = "To";
    private static final String CC_ELEMENT = "CC";
    private static final String BCC_ELEMENT = "BCC";
    private static final String EMAIL_ELEMENT = "Email";
    private static final String FROM_ELEMENT = "From";
    private static final String HOST_ELEMENT = "Host";
    private static final String PORT_ELEMENT = "Port";
    private static final String AUTH_ELEMENT = "Auth";
    private static final String START_TLS_ELEMENT = "StartTLS";
    private static final String USERNAME_ELEMENT = "Username";
    private static final String PASSWORD_ELEMENT = "Password";
    private static final String MESSAGE_ELEMENT = "Message";
    private static final String SUBJECT_ELEMENT = "Subject";
    private static final String BODY_ELEMENT = "Body";
    private static final String ATTACHMENTS_ELEMENT = "Attachments";
    private static final String ATTACHMENT_ELEMENT = "Attachment";

    private static final String TO_EMAILS_XPATH = String.format("%s/%s/%s/%s", ROOT_ELEMENT, RECIPIENTS_ELEMENT, TO_ELEMENT, EMAIL_ELEMENT);
    private static final String CC_EMAILS_XPATH = String.format("%s/%s/%s/%s", ROOT_ELEMENT, RECIPIENTS_ELEMENT, CC_ELEMENT, EMAIL_ELEMENT);
    private static final String BCC_EMAILS_XPATH = String.format("%s/%s/%s/%s", ROOT_ELEMENT, RECIPIENTS_ELEMENT, BCC_ELEMENT, EMAIL_ELEMENT);
    private static final String HOST_XPATH = String.format("%s/%s/%s", ROOT_ELEMENT, FROM_ELEMENT, HOST_ELEMENT);
    private static final String PORT_XPATH = String.format("%s/%s/%s", ROOT_ELEMENT, FROM_ELEMENT, PORT_ELEMENT);
    private static final String AUTH_XPATH = String.format("%s/%s/%s", ROOT_ELEMENT, FROM_ELEMENT, AUTH_ELEMENT);
    private static final String START_TLS_XPATH = String.format("%s/%s/%s", ROOT_ELEMENT, FROM_ELEMENT, START_TLS_ELEMENT);
    private static final String USERNAME_XPATH = String.format("%s/%s/%s", ROOT_ELEMENT, FROM_ELEMENT, USERNAME_ELEMENT);
    private static final String PASSWORD_XPATH = String.format("%s/%s/%s", ROOT_ELEMENT, FROM_ELEMENT, PASSWORD_ELEMENT);
    private static final String SUBJECT_XPATH = String.format("/%s/%s/%s", ROOT_ELEMENT, MESSAGE_ELEMENT, SUBJECT_ELEMENT);
    private static final String BODY_XPATH = String.format("/%s/%s/%s", ROOT_ELEMENT, MESSAGE_ELEMENT, BODY_ELEMENT);
    private static final String ATTACHMENTS_XPATH = String.format("/%s/%s/%s/%s", ROOT_ELEMENT, MESSAGE_ELEMENT, ATTACHMENTS_ELEMENT, ATTACHMENT_ELEMENT);

    public static void loadConfig(File file, SendToModel sendToModel, SendFromModel sendFromModel, MessageModel messageModel) throws Exception{
        byte[] encrypted = IOUtils.toByteArray(new FileInputStream(file));
        byte[] decrypted = CryptoUtil.getInstance().decryptData(encrypted);

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(decrypted));
        XPath xPath = XPathFactory.newInstance().newXPath();

        parseSendTo(sendToModel, doc, xPath);
        parseSendFrom(sendFromModel, doc, xPath);
        parseMessage(messageModel, doc, xPath);
        System.out.println("Configuration loaded: " + file.getAbsolutePath());
    }

    public static void saveConfig(File file, SendToModel sendToModel, SendFromModel sendFromModel, MessageModel messageModel) throws Exception{
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = doc.createElement(ROOT_ELEMENT);
        doc.appendChild(root);

        appendSendTo(sendToModel, root, doc);
        appendSendFrom(sendFromModel, root, doc);
        appendMessage(messageModel, root, doc);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(byteStream);
        transformer.transform(source, result);

        byte[] bytes = byteStream.toByteArray();
        byte[] encrypted = CryptoUtil.getInstance().encryptData(bytes);
        try(FileOutputStream outputStream = new FileOutputStream(file)){
            outputStream.write(encrypted);
        }

        System.out.println("Configuration saved: " + file.getAbsolutePath());
    }

    private static void parseMessage(MessageModel messageModel, Document doc, XPath xPath) throws Exception{
        XPathExpression expression = xPath.compile(SUBJECT_XPATH);
        String subject = (String) expression.evaluate(doc, XPathConstants.STRING);
        if(!StringUtils.isEmpty(subject)){
            messageModel.setSubject(StringEscapeUtils.unescapeXml(subject));
        }

        expression = xPath.compile(BODY_XPATH);
        String body = (String) expression.evaluate(doc, XPathConstants.STRING);
        if(!StringUtils.isEmpty(body)){
            messageModel.setBody(StringEscapeUtils.unescapeXml(body));
        }

        expression = xPath.compile(ATTACHMENTS_XPATH);
        NodeList attachments = (NodeList) expression.evaluate(doc, XPathConstants.NODESET);
        for(int i = 0; i < attachments.getLength(); i++){
            Node attachment = attachments.item(i);
            if(attachment.getNodeType() == Node.ELEMENT_NODE){
                messageModel.addAttachment(StringEscapeUtils.unescapeXml(attachment.getTextContent()));
            }
        }
    }

    private static void parseSendFrom(SendFromModel sendFromModel, Document doc, XPath xPath) throws Exception{
        XPathExpression expression = xPath.compile(HOST_XPATH);
        String host = (String) expression.evaluate(doc, XPathConstants.STRING);
        if(!StringUtils.isEmpty(host)){
            sendFromModel.setHost(StringEscapeUtils.unescapeXml(host));
        }

        expression = xPath.compile(PORT_XPATH);
        String port = (String) expression.evaluate(doc, XPathConstants.STRING);
        if(!StringUtils.isEmpty(port)){
            sendFromModel.setPort(Integer.parseInt(port));
        }

        expression = xPath.compile(AUTH_XPATH);
        boolean auth = (Boolean) expression.evaluate(doc, XPathConstants.BOOLEAN);
        sendFromModel.setAuth(auth);

        expression = xPath.compile(START_TLS_XPATH);
        boolean startTLS = (Boolean) expression.evaluate(doc, XPathConstants.BOOLEAN);
        sendFromModel.setStartTLS(startTLS);

        if(auth){
            expression = xPath.compile(USERNAME_XPATH);
            String username = (String) expression.evaluate(doc, XPathConstants.STRING);
            if(!StringUtils.isEmpty(username)){
                sendFromModel.setUsername(StringEscapeUtils.unescapeXml(username));
            }

            expression = xPath.compile(PASSWORD_XPATH);
            String password = (String) expression.evaluate(doc, XPathConstants.STRING);
            if(!StringUtils.isEmpty(password)){
                sendFromModel.setPassword(StringEscapeUtils.escapeXml10(password));
            }
        }
    }

    private static void parseSendTo(SendToModel sendToModel, Document doc, XPath xPath) throws Exception{
        XPathExpression expression = xPath.compile(TO_EMAILS_XPATH);
        NodeList toEmailNodes = (NodeList) expression.evaluate(doc, XPathConstants.NODESET);
        for(int i = 0; i < toEmailNodes.getLength(); i++){
            Node node = toEmailNodes.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){
                sendToModel.addToEmail(StringEscapeUtils.unescapeXml(node.getTextContent()));
            }
        }

        expression = xPath.compile(CC_EMAILS_XPATH);
        NodeList ccEmailNodes = (NodeList) expression.evaluate(doc, XPathConstants.NODESET);
        for(int i = 0; i < ccEmailNodes.getLength(); i++){
            Node node = ccEmailNodes.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){
                sendToModel.addCCEmail(StringEscapeUtils.unescapeXml(node.getTextContent()));
            }
        }

        expression = xPath.compile(BCC_EMAILS_XPATH);
        NodeList bccEmailNodes = (NodeList) expression.evaluate(doc, XPathConstants.NODESET);
        for(int i = 0; i < bccEmailNodes.getLength(); i++){
            Node node = bccEmailNodes.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){
                sendToModel.addBCCEmail(StringEscapeUtils.unescapeXml(node.getTextContent()));
            }
        }
    }

    private static void appendSendFrom(SendFromModel sendFromModel, Element root, Document doc){
        Element from = doc.createElement(FROM_ELEMENT);
        root.appendChild(from);

        Element host = doc.createElement(HOST_ELEMENT);
        from.appendChild(host);
        if(!StringUtils.isEmpty(sendFromModel.getHost())){
            host.setTextContent(StringEscapeUtils.escapeXml10(sendFromModel.getHost()));
        }

        Element port = doc.createElement(PORT_ELEMENT);
        from.appendChild(port);
        port.setTextContent("" + sendFromModel.getPort());

        Element auth = doc.createElement(AUTH_ELEMENT);
        from.appendChild(auth);
        auth.setTextContent("" + sendFromModel.isAuth());

        Element startTLS = doc.createElement(START_TLS_ELEMENT);
        from.appendChild(startTLS);
        startTLS.setTextContent("" + sendFromModel.isStartTLS());

        Element username = doc.createElement(USERNAME_ELEMENT);
        from.appendChild(username);
        Element password = doc.createElement(PASSWORD_ELEMENT);
        from.appendChild(password);

        if(sendFromModel.isAuth() && !StringUtils.isEmpty(sendFromModel.getUsername())){
            username.setTextContent(StringEscapeUtils.escapeXml10(sendFromModel.getUsername()));
        }

        if(sendFromModel.isAuth() && !StringUtils.isEmpty(sendFromModel.getPassword())){
            password.setTextContent(StringEscapeUtils.escapeXml10(sendFromModel.getPassword()));
        }
    }

    private static void appendMessage(MessageModel messageModel, Element root, Document doc){
        Element message = doc.createElement(MESSAGE_ELEMENT);
        root.appendChild(message);

        Element subject = doc.createElement(SUBJECT_ELEMENT);
        message.appendChild(subject);
        if(!StringUtils.isEmpty(messageModel.getSubject())){
            subject.setTextContent(StringEscapeUtils.escapeXml10(messageModel.getSubject()));
        }

        Element body = doc.createElement(BODY_ELEMENT);
        message.appendChild(body);
        if(!StringUtils.isEmpty(messageModel.getBody())){
            body.setTextContent(StringEscapeUtils.escapeXml10(messageModel.getBody()));
        }

        Element attachments = doc.createElement(ATTACHMENTS_ELEMENT);
        message.appendChild(attachments);

        if(messageModel.getAttachments().size() > 0){
            for(String s : messageModel.getAttachments()){
                Element attachment = doc.createElement(ATTACHMENT_ELEMENT);
                attachment.setTextContent(StringEscapeUtils.escapeXml10(s));
                attachments.appendChild(attachment);
            }
        }
    }

    private static void appendSendTo(SendToModel sendToModel, Element root, Document doc){
        Element recipients = doc.createElement(RECIPIENTS_ELEMENT);
        root.appendChild(recipients);

        Element to = doc.createElement(TO_ELEMENT);
        recipients.appendChild(to);

        Element cc = doc.createElement(CC_ELEMENT);
        recipients.appendChild(cc);

        Element bcc = doc.createElement(BCC_ELEMENT);
        recipients.appendChild(bcc);

        if(sendToModel.getToEmails().size() > 0){
            for(String s : sendToModel.getToEmails()){
                Element email = doc.createElement(EMAIL_ELEMENT);
                email.setTextContent(StringEscapeUtils.escapeXml10(s));
                to.appendChild(email);
            }
        }

        if(sendToModel.getCCEmails().size() > 0){
            for(String s : sendToModel.getCCEmails()){
                Element email = doc.createElement(EMAIL_ELEMENT);
                email.setTextContent(StringEscapeUtils.escapeXml10(s));
                cc.appendChild(email);
            }
        }

        if(sendToModel.getBCCEmails().size() > 0){
            for(String s : sendToModel.getCCEmails()){
                Element email = doc.createElement(EMAIL_ELEMENT);
                email.setTextContent(StringEscapeUtils.escapeXml10(s));
                bcc.appendChild(email);
            }
        }
    }

}
