package ross.nslookup;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

/*
 * An InputStream with seek() to seek previous bytes having been read.
 * Especially good for parsing compressed domain name string.
 */
public class SeekableInputStream {

	private ByteArrayOutputStream m_byteOut;
	private DataOutputStream m_dataOut;
	private DataInputStream m_dataIn;
	
	public SeekableInputStream(InputStream in) {
		m_byteOut = new ByteArrayOutputStream();
		m_dataOut = new DataOutputStream(m_byteOut);
		
		m_dataIn = new DataInputStream(in);
	}

	public int readInt() throws IOException {
		int v = m_dataIn.readInt();
		m_dataOut.writeInt(v);
		return v;
	}
	
	public short readShort() throws IOException {
		short v = m_dataIn.readShort();
		m_dataOut.writeShort(v);
		return v;
	}
	
	public byte readByte() throws IOException {
		byte v = m_dataIn.readByte();
		m_dataOut.writeByte(v);
		return v;
	}
	
	public byte[] readBytes(int n) throws IOException {
		byte[] bytes = new byte[n];
		m_dataIn.readFully(bytes);
		m_dataOut.write(bytes);
		return bytes;
	}
	
	public SeekableInputStream seek(int i) {
		byte[] bytes = m_byteOut.toByteArray();
		SeekableInputStream bufferedIn = new SeekableInputStream(
				new ByteArrayInputStream(bytes, i, bytes.length - i));
		bufferedIn.m_byteOut = this.m_byteOut;
		return bufferedIn;
	}
	
}
