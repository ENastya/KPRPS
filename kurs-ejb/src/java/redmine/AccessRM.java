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
import java.net.Socket;
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

/**
 *
 * @author Настя
 */
@Stateless
@LocalBean


public class AccessRM {

    @PersistenceContext(unitName = "kurs-ejbPU")
    private EntityManager em;
    
    public NodeList getIdFromXML(String xml, String split){
        //"author id=\""
    NodeList s = split(xml, split);
            for (int i = 1; i < s.getLength(); i++) {
                    String k = s.item(i).getTextContent();
            s.item(i).setTextContent(k.substring(0, k.indexOf('"')));
            }
    return s;
    }

    public void ParseXMLtoTask(){
        String xml = ReqRM("GET /redmine/issues.xml");
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
            for (int i = 0; i< a;i++){
            Task task = new Task();
            task.setId(Integer.parseInt(listId.item(i).getTextContent()));
            int id = Integer.parseInt(listUserId.item(i+1).getTextContent());
            task.setUserId(em.find(User.class, id));
            id = Integer.parseInt(listProjId.item(i+1).getTextContent());
            task.setProjectId(em.find(Project.class, id));
            task.setName(listSub.item(i).getTextContent());
            task.setDescription(listDesc.item(i).getTextContent());
            task.setStausId(em.find(Status.class, 1));
            em.merge(task);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println("Я не хочу работать, иди спать");
        }
    }
    
    public String ReqRM(String req){
    try {

            Socket clientSocket = null;

            clientSocket = new Socket("127.0.0.1", 81);

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            BufferedReader inu = new BufferedReader(new InputStreamReader(System.in));

            String fuser, fserver;

            out = new PrintWriter(clientSocket.getOutputStream(),
                    true);
            
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

    public void persist(Object object) {
        em.persist(object);
    }


}
