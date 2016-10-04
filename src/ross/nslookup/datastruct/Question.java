package ross.nslookup.datastruct;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import ross.nslookup.SeekableInputStream;
import ross.nslookup.Helper;

public class Question {
	public String m_name;
	public short m_type = RR.TYPE_A;
	public short m_class = RR.CLASS_IN;
	
	public static Question create(String name) {
		Question q = new Question();
		q.m_name = name;
		return q;
	}

	public static Question read(SeekableInputStream in) throws IOException {
		Question q = new Question();
		
		q.m_name = Helper.readDomainName(in);
    	System.out.println("QNAME: " + q.m_name);
    	
	    q.m_type = in.readShort();
	    System.out.println("QTYPE: " + q.m_type);
	    
	    q.m_class = in.readShort();
	    System.out.println("QCLASS: " + q.m_class);
	    
	    return q;
	}
	
	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream outByte = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(outByte);
		
		// name
		StringTokenizer labels = new StringTokenizer(m_name, ".");
		while (labels.hasMoreTokens()) {
			String label = labels.nextToken();
			out.writeByte(label.length());
			out.writeBytes(label);
		}
		out.writeByte(0);
		
		out.writeShort(m_type);
		out.writeShort(m_class);
		
		return outByte.toByteArray();
	}

	public static String getTypeString(short t) {
		switch (t) {
		case 252: return "A request for a transfer of an entire zone";
		case 253: return "A request for mailbox-related records (MB, MG or MR)";
		case 254: return "A request for mail agent RRs (Obsolete - see MX)";
		case 255: return "A request for all records";
		default: return RR.getTypeString(t);
		}
	}

	public static String getClassString(short cls) {
		if (cls == 255) {
			return "any class";
		} else {
			return RR.getClassString(cls);
		}
	}
}
