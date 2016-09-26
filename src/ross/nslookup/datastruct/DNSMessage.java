package ross.nslookup.datastruct;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ross.nslookup.SeekableInputStream;

public class DNSMessage {
	public final static short FLAG_QR = 1 << 0; // indicate the message is a response
	public final static short FLAG_OPCODE_QUERY = 0 << 1;
	public final static short FLAG_OPCODE_IQUERY = 1 << 1;
	public final static short FLAG_OPCODE_STATUS = 2 << 1;
	public final static short FLAG_RD = 1 << 7;
	public final static short FLAG_RA = 1 << 8;
	
	public short m_id;
	public short m_flag = 0;
	public ArrayList<Question> m_questions = new ArrayList<Question>();
	public ArrayList<ResourceRecord> m_anwsers = new ArrayList<ResourceRecord>();
	public ArrayList<ResourceRecord> m_authorities = new ArrayList<ResourceRecord>();
	public ArrayList<ResourceRecord> m_additional = new ArrayList<ResourceRecord>();
	
	
	public static short m_nextId;
	
	public static DNSMessage createRequest(String domainName) {
		DNSMessage msg = new DNSMessage();
		
		msg.m_id = m_nextId++;
		msg.m_flag = FLAG_OPCODE_QUERY | FLAG_RD | FLAG_RA;
		msg.m_questions.add(Question.create(domainName));
		
		return msg;
	}
	
	public static DNSMessage read(SeekableInputStream in) {
		try {
			DNSMessage msg = new DNSMessage();
			
			msg.m_id = in.readShort();
			msg.m_flag = in.readShort();
			
			int numQuestions = in.readShort();
		    int numAnwsers = in.readShort();
		    int numAuthorities = in.readShort();
		    int numAdditional = in.readShort();
			
		    System.out.println("numQuestions: " + numQuestions);
		    System.out.println("numAnwsers: " + numAnwsers);
		    System.out.println("numAuthorities: " + numAuthorities);
		    System.out.println("numAdditional: " + numAdditional);
		    
		    // read questions
		    for (int i=0; i<numQuestions; i++) {
		    	msg.m_questions.add(Question.read(in));
		    }
		    
		    for (int i=0; i<numAnwsers; i++) {
		    	msg.m_anwsers.add(ResourceRecord.read(in));
		    }
		    for (int i=0; i<numAuthorities; i++) {
		    	msg.m_authorities.add(ResourceRecord.read(in));
		    }
		    for (int i=0; i<numAdditional; i++) {
		    	msg.m_additional.add(ResourceRecord.read(in));
		    }
		    
		    return msg;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] toBytes() {
		try {
			ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
		    DataOutputStream out = new DataOutputStream (byteArrayOut);
		    
		    out.writeShort(m_id);
			out.writeShort(m_flag);
			
			out.writeShort(m_questions.size());
			out.writeShort(m_anwsers.size());
			out.writeShort(m_authorities.size());
			out.writeShort(m_additional.size());
			
			// 1st question section
			for (int i=0; i<m_questions.size(); i++) {
				out.write(m_questions.get(i).toBytes());
			}
			
			out.flush();
			return byteArrayOut.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}