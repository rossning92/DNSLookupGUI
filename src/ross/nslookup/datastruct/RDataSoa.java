package ross.nslookup.datastruct;

import java.io.IOException;

import ross.nslookup.Helper;
import ross.nslookup.SeekableInputStream;

public class RDataSoa {
	public String m_mname;
	public String m_rname;
	public int m_serial; 
	public int m_refresh;
	public int m_retry;
	public int m_expire;
	
	public static RDataSoa read(SeekableInputStream in) throws IOException {
		RDataSoa d = new RDataSoa();
		d.m_mname = Helper.readDomainName(in);
		d.m_rname = Helper.readDomainName(in);
		d.m_serial = in.readInt();
		d.m_refresh = in.readInt();
		d.m_retry = in.readInt();
		d.m_expire = in.readInt();
		return d;
	}
}
