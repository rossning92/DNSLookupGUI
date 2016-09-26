package ross.nslookup;
import java.io.IOException;

public class Helper {
	public static String readDomainName(SeekableInputStream in) throws IOException {
		byte n = in.readByte();
		if ((n & 0xC0) != 0) { // n is a pointer (for compression purpose)
			int offs = ((n & 0x3F) << 8) | in.readByte();
			return readDomainName(in.seek(offs));			
		} else { // n is the length of next label
			String name = "";
			do {
				byte[] str = in.readBytes(n);
				if ( !name.isEmpty() ) name += ".";
				name += new String(str, "latin1");
			} while ((n = in.readByte()) > 0);
			return name;
		}
	}
	
	public static String toHex(int n) {
		return "0x" + Integer.toHexString(n & 0xFFFFFFFF);
	}

	public static String toHex(short n) {
		return "0x" + Integer.toHexString(n & 0xFFFF);
	}

	public static String toHex(byte n) {
		return "0x" + Integer.toHexString(n & 0xFF);
	}
	
	public static String toHex(byte[] b) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<b.length; i++) {
			if (i > 0 && i % 8 == 0) sb.append("<br/>");
			sb.append(String.format("%02X ", b[i] & 0xFF));
		}
		return sb.toString();
	}
}