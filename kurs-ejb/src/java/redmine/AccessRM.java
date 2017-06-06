/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redmine;

import static com.sun.org.apache.xalan.internal.lib.ExsltStrings.split;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import models.Project;
import models.Status;
import models.Task;
import models.User;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import static com.sun.org.apache.xalan.internal.lib.ExsltStrings.split;
import java.util.Date;
import static com.sun.org.apache.xalan.internal.lib.ExsltStrings.split;
import static com.sun.org.apache.xalan.internal.lib.ExsltStrings.split;
import static java.lang.Math.random;
import static java.lang.StrictMath.random;
import java.util.Properties;
import java.util.Random;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;

/**
 *
 * @author Настя
 */
@Stateless
@LocalBean

public class AccessRM {

    @PersistenceContext(unitName = "kurs-ejbPU")
    private EntityManager em;

    private Session session;

    public NodeList getIdFromXML(String xml, String split) {
        //"author id=\""
        NodeList s = split(xml, split);
        for (int i = 1; i < s.getLength(); i++) {
            String k = s.item(i).getTextContent();
            s.item(i).setTextContent(k.substring(0, k.indexOf('"')));
        }
        return s;
    }

    public void ParseXMLtoTask() throws UnsupportedEncodingException {
        String xmlresp = ReqRM("GET /redmine/issues.xml");
        String xml = new String(xmlresp.getBytes("windows-1251"), StandardCharsets.UTF_8);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        InputSource is;
        try {
            builder = factory.newDocumentBuilder();
            is = new InputSource(new StringReader(xml));
            Document doc;
            doc = builder.parse(is);
            NodeList listSub = doc.getElementsByTagName("subject");
            NodeList listDesc = doc.getElementsByTagName("description");
            NodeList listId = doc.getElementsByTagName("id");
            NodeList listUserId = getIdFromXML(xml, "author id=\"");
            NodeList listProjId = getIdFromXML(xml, "project id=\"");
            //List <Task> tasks;
            int a = listId.getLength();
            for (int i = 0; i < a; i++) {
                Task task = new Task();
                task.setId(Integer.parseInt(listId.item(i).getTextContent()));
                int id = Integer.parseInt(listUserId.item(i + 1).getTextContent());
                task.setUserId(em.find(User.class, id));
                id = Integer.parseInt(listProjId.item(i + 1).getTextContent());
                task.setProjectId(em.find(Project.class, id));
                task.setName(listSub.item(i).getTextContent());
                if ("".equals(listDesc.item(i).getTextContent())) {
                    listDesc.item(i).setTextContent("Описание отсутствует");
                }
                task.setDescription(listDesc.item(i).getTextContent());

                if (em.find(Task.class, task.getId()) == null) {
                    task.setStausId(em.find(Status.class, 1));
                } else {
                    task.setStausId(em.find(Task.class, task.getId()).getStausId());
                }

                em.merge(task);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println("Я не хочу работать, иди спать");
        }
    }

    public void ParseXMLtoProject() throws UnsupportedEncodingException {
        String xmlresp = ReqRM("GET /redmine/projects.xml");
        String xml = new String(xmlresp.getBytes("windows-1251"), StandardCharsets.UTF_8);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        InputSource is;
        try {
            builder = factory.newDocumentBuilder();
            is = new InputSource(new StringReader(xml));
            Document doc;
            doc = builder.parse(is);
            NodeList listSub = doc.getElementsByTagName("name");
            NodeList listDesc = doc.getElementsByTagName("description");
            NodeList listId = doc.getElementsByTagName("id");
            int a = listId.getLength();
            for (int i = 0; i < a; i++) {
                Project proj = new Project();
                proj.setId(Integer.parseInt(listId.item(i).getTextContent()));
                proj.setName(listSub.item(i).getTextContent());
                if ("".equals(listDesc.item(i).getTextContent())) {
                    listDesc.item(i).setTextContent("Описание отсутствует");
                }
                proj.setDescription(listDesc.item(i).getTextContent());

                if (em.find(Project.class, proj.getId()) == null) {
                    long curTime = System.currentTimeMillis();
                    Date curDate = new Date(curTime);
                    proj.setLastEstimate(curDate);
                } else {
                    proj.setLastEstimate(em.find(Project.class, proj.getId()).getLastEstimate());
                }

                em.merge(proj);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println("И я не хочу работать, иди спать");
        }
    }

    public void ParseXMLtoUser() throws UnsupportedEncodingException {
        String xmlresp = ReqRM("GET /redmine/users.xml?key=61c136acf0ac0ed07be22747015e5e843e77e9bc");
        String xml = new String(xmlresp.getBytes("windows-1251"), StandardCharsets.UTF_8);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        InputSource is;
        try {
            builder = factory.newDocumentBuilder();
            is = new InputSource(new StringReader(xml));
            Document doc;
            doc = builder.parse(is);
            NodeList listFN = doc.getElementsByTagName("firstname");
            NodeList listLN = doc.getElementsByTagName("lastname");
            NodeList listMail = doc.getElementsByTagName("mail");
            NodeList listId = doc.getElementsByTagName("id");
            int a = listId.getLength();
            for (int i = 0; i < a; i++) {
                User user = new User();
                user.setId(Integer.parseInt(listId.item(i).getTextContent()));
                user.setFio(listFN.item(i).getTextContent() + " " + listLN.item(i).getTextContent());
                user.setEmail(listMail.item(i).getTextContent());
                if (em.find(Project.class, user.getId()) == null) {
                    user.setRole("guest");
                    String symbols = "abcdfjhijklmnopqrstuvwxyz123456789";
                    StringBuilder randString = new StringBuilder();
                    int count = (int) (Math.random() * 30);
                    for (int j = 0; j < 10; j++) {
                        randString.append(symbols.charAt((int) (Math.random() * symbols.length())));
                    }
                    user.setPassword(randString.toString());
                    long curTime = System.currentTimeMillis();
                    Date curDate = new Date(curTime);
                    user.setLastEstimate(curDate);
                } else {
                    user.setPassword(em.find(User.class, user.getId()).getPassword());
                    user.setRole(em.find(User.class, user.getId()).getRole());
                    user.setLastEstimate(em.find(User.class, user.getId()).getLastEstimate());
                }

                em.merge(user);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println("И я не хочу работать, иди спать");
        }
    }

    public String ReqRM(String req) {
        try {

            Socket clientSocket = null;

            clientSocket = new Socket("127.0.0.1", 81);

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            BufferedReader inu = new BufferedReader(new InputStreamReader(System.in));

            String fuser, fserver;

            out = new PrintWriter(clientSocket.getOutputStream(), true);

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            fuser = req;
            out.println(fuser);
            fserver = in.readLine();
            System.out.println(fserver);
            out.close();
            in.close();
            inu.close();
            clientSocket.close();
            return fserver;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void sendPass(String email, String pass) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("timetrackingkurs@gmail.com", "passwordkurs");
            }
        }
        );
        //https://myaccount.google.com/lesssecureapps?rfn=27&rfnc=1&eid=4448369424949745717&et=1&asae=2&pli=1
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("timetrackingkurs@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Пароль доступа к системе учета времени ");
            message.setText("Ваш пароль: " + pass);
            Transport.send(message);
            JOptionPane.showMessageDialog(null, "сообщение отправлено!");
        } catch (Exception e) {
            System.out.print(e);
        }
    }

    public void persist(Object object) {
        em.persist(object);
    }

}
