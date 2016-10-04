package ross.nslookup.datastruct;

import java.io.IOException;

import ross.nslookup.Helper;
import ross.nslookup.SeekableInputStream;

/*
 * RR stands for ResourceRecord
 */
public class RR {
	public final static short
			TYPE_A     = 1,
			TYPE_NS    = 2,
			TYPE_MD    = 3,
			TYPE_MF    = 4,
			TYPE_CNAME = 5,
			TYPE_SOA   = 6,
			TYPE_MB    = 7,
			TYPE_MG    = 8,
			TYPE_MR    = 9,
			TYPE_NULL  = 10,
			TYPE_WKS   = 11,
			TYPE_PTR   = 12,
			TYPE_HINFO = 13,
			TYPE_MINFO = 14,
			TYPE_MX    = 15,
			TYPE_TXT   = 16;
	
	public final static short
			CLASS_IN = 1,
			CLASS_CS = 2,
			CLASS_CH = 3,
			CLASS_HS = 4;
	
	public String m_name;
	public short m_type;
	public short m_class;
	public int m_ttl;
	public byte[] m_rdataBytes;
	public Object m_rdata;

	public static String getTypeString(short type) {
		switch (type) {
		case 1: return "A (a host address)";
		case 2: return "NS (an authoritative name server)";
		case 3: return "MD (a mail destination (Obsolete - use MX))";
		case 4: return "MF (a mail forwarder (Obsolete - use MX))";
		case 5: return "CNAME (the canonical name for an alias)";
		case 6: return "SOA (marks the start of a zone of authority)";
		case 7: return "MB (a mailbox domain name (EXPERIMENTAL))";
		case 8: return "MG (a mail group member (EXPERIMENTAL))";
		case 9: return "MR (a mail rename domain name (EXPERIMENTAL))";
		case 10: return "NULL (a null RR (EXPERIMENTAL))";
		case 11: return "WKS (a well known service description)";
		case 12: return "PTR (a domain name pointer)";
		case 13: return "HINFO (host information)";
		case 14: return "MINFO (mailbox or mail list information)";
		case 15: return "MX (mail exchange)";
		case 16: return "TXT (text strings)";
		default: return "INVALID TYPE";
		}
	}
	
	public static String getClassString(short cls) {
		switch (cls) {
		case 1: return "IN (the Internet)";
		case 2: return "CS (the CSNET class (Obsolete - used only for examples in some obsolete RFCs))";
		case 3: return "CH (the CHAOS class)";
		case 4: return "HS (Hesiod [Dyer 87])";
		default: return "INVALID CLASS";
		}
	}
	
	public String getDataString() {
		if (m_type == TYPE_A) {
			return "IP Address: " +
					(m_rdataBytes[0] & 0xFF) + "." + 
					(m_rdataBytes[1] & 0xFF) + "." +
					(m_rdataBytes[2] & 0xFF) + "." + 
					(m_rdataBytes[3] & 0xFF);
		}
		return "";
	}
	
	public static RR read(SeekableInputStream in) throws IOException {
		RR rr = new RR();

		rr.m_name = Helper.readDomainName(in);
		System.out.println("NAME: " + rr.m_name);

		rr.m_type = in.readShort();
		System.out.println("TYPE: " + rr.m_type + ": " + getTypeString(rr.m_type));

		rr.m_class = in.readShort();
		System.out.println("CLASS: " + rr.m_class);

		rr.m_ttl = in.readInt();
		System.out.println("TTL: " + rr.m_ttl);

		int dataLen = in.readShort();
		rr.m_rdataBytes = in.readBytes(dataLen);
		System.out.println("DATA(" + dataLen + "): " + rr.m_rdataBytes);
		System.out.println(rr.getDataString());
		
		if (rr.m_type == TYPE_SOA) {
			rr.m_rdata = RDataSoa.read( in.seek(in.curPos() - dataLen) );
		}

		return rr;
	}
}
