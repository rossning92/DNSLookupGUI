package ross.nslookup;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import ross.nslookup.datastruct.DNSMessage;
import ross.nslookup.datastruct.Question;
import ross.nslookup.datastruct.ResourceRecord;

public class MainFrame extends JFrame {
	
	JEditorPane m_webView;
	
	
	public MainFrame() {
		this.setTitle("DNS Lookup GUI");
		this.setLayout(new BorderLayout());
		this.setSize(800, 600);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		
		
	    
	    

	    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    
	    
	    topPanel.add(new JLabel("Host:"));
	    
	    JTextField field1 = new JTextField("www.baidu.com");
	    topPanel.add(field1);
	    
	    topPanel.add(new JLabel("DNS server:"));
	    
	    JTextField field2 = new JTextField("8.8.8.8");
	    topPanel.add(field2);

	    
	    // request button
	    JButton requestBtn = new JButton("REQUEST");
	    requestBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				request();
			}
	    });
	    topPanel.add(requestBtn);
	    this.add(topPanel, BorderLayout.NORTH);
	    
	    // web view to display result
		m_webView = new JEditorPane("text/html", "");
		JScrollPane scrollPane = new JScrollPane(m_webView);
		this.add(scrollPane, BorderLayout.CENTER);		
	}

	private void request() {
		StringBuilder sb = new StringBuilder();
		sb.append("<style>*{font-size:12pt;}div{margin:8px;margin-top:4px;margin-bottom:4px;padding:0px;border:1px solid;}h1{border-bottom:1px solid;margin:1px;font-size:10pt;background-color:#c0c0c0;}td{margin:0;padding:0 4px 0 4px;}span{font-family:consolas;}</style>");
		
		try {
			int DEFAULT_PORT = 53;
			Socket socket = new Socket("8.8.8.8", DEFAULT_PORT);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			
			// send request
			DNSMessage requestMsg = DNSMessage.createRequest("www.baidu.com");
			
			
			
			byte[] msgBytes = requestMsg.toBytes();
			out.writeShort(msgBytes.length);
			out.write(msgBytes);
			
			// get response
			DataInputStream in = new DataInputStream(socket.getInputStream());
			int len = in.readUnsignedShort();
			System.out.println(len);
			
			
//			byte[] respMsg = new byte[len];
//			in.readFully(respMsg);
//			FileOutputStream fos = new FileOutputStream("123.bin");
//			fos.write(respMsg);
//			fos.close();
			
			DNSMessage responseMsg = DNSMessage.read(new SeekableInputStream(in));
			
			
			appendMessage(sb, requestMsg, "¡ú REQUEST MESSAGE");
			appendMessage(sb, responseMsg, "¡û RESPONSE MESSAGE");
			
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		m_webView.setText("");
		m_webView.setText(sb.toString());
	}
	
	private void appendMessage(StringBuilder sb, DNSMessage msg, String name) {
		sb.append("<div>");
		sb.append("<h1>" + name + "</h1>");
		
		sb.append("<table>");
		sb.append("<tr><td>ID</td><td>" + Helper.toHex(msg.m_id & 0xFFFF) + "</td></tr>");
		sb.append("<tr><td>Flag</td><td>" + Helper.toHex(msg.m_flag & 0xFFFF) + "</td></tr>");
		sb.append("<tr><td>NumQuestions</td><td>" + Helper.toHex(msg.m_questions.size()) + "</td></tr>");
		sb.append("<tr><td>NumAnwsers</td><td>" + Helper.toHex(msg.m_anwsers.size()) + "</td></tr>");
		sb.append("<tr><td>NumAuthorities</td><td>" + Helper.toHex(msg.m_authorities.size()) + "</td></tr>");
		sb.append("<tr><td>NumAdditional</td><td>" + Helper.toHex(msg.m_additional.size()) + "</td></tr>");
		sb.append("</table>");
		
		appendQuestion(sb, msg);
		appendSection(sb, msg.m_anwsers, "ANWSERS");
		appendSection(sb, msg.m_authorities, "ATHORITIES");
		appendSection(sb, msg.m_additional, "ADDITIONAL");
		
		
		sb.append("</div>");
	}

	private void appendQuestion(StringBuilder sb, DNSMessage msg) {
		if (msg.m_questions.size() == 0) return;
		
		sb.append("<div>");
		sb.append("<h1>QUESTIONS(" + msg.m_questions.size() + ")</h1>");
		for (int i=0; i<msg.m_questions.size(); i++) {
			sb.append("<div>");
			sb.append("<h1>RR</h1>");
			sb.append("<table>");
			Question q = msg.m_questions.get(i);
			
			sb.append("<tr><td>NAME</td><td>" + q.m_name + "</td></tr>");
			sb.append("<tr><td>TYPE</td><td>" + q.m_type + "</td><td>" 
					+ Question.getTypeString(q.m_type) + "</td></tr>");
			sb.append("<tr><td>CLASS</td><td>" + q.m_class + "</td><td>" + Question.getClassString(q.m_class)
					+ "</td></tr>");
			

			sb.append("</table>");
			sb.append("</div>");
		}
		sb.append("</div>");
	}

	private void appendSection(StringBuilder sb, ArrayList<ResourceRecord> section, String name) {
		if (section.size() == 0) return;
		
		sb.append("<div>");
		sb.append("<h1>" + name + "(" + section.size() + ")</h1>");
		for (int i=0; i<section.size(); i++) {
			sb.append("<div>");
			sb.append("<h1>RR</h1>");
			sb.append("<table>");
			ResourceRecord rr = section.get(i);
			
			sb.append("<tr><td>NAME</td><td>" + rr.m_name + "</td></tr>");
			sb.append("<tr><td>TYPE</td><td>" + rr.m_type + "</td><td>" + ResourceRecord.getTypeString(rr.m_type)
					+ "</td></tr>");
			sb.append("<tr><td>CLASS</td><td>" + rr.m_class + "</td><td>"
					+ ResourceRecord.getClassString(rr.m_class) + "</td></tr>");
			sb.append("<tr><td>TTL</td><td>" + Helper.toHex(rr.m_ttl) + "</td></tr>");
			sb.append("<tr><td>RDLENGTH</td><td>" + Helper.toHex(rr.m_data.length) + "</td></tr>");
			sb.append("<tr><td>DATA</td><td><span>" + Helper.toHex(rr.m_data) + "</span></td><td>" + rr.getDataString()
					+ "</td></tr>");

			sb.append("</table>");
			sb.append("</div>");
		}
		sb.append("</div>");
	}
}
