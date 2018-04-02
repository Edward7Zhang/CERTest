package edwardZhangCERTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import org.json.JSONException;

public class CERTest_Main {
	static String URI;
	static String LRUURI;
	final static String PATHNAME = ".\\logs\\CERTest.log";
	static String resultBuffer;
	static String realLrc;
	static BaiduAI baiduAI = new BaiduAI();
	static XFyunAI xfyunAI = new XFyunAI();
	static MyLevenshtein myLevenshtein = new MyLevenshtein();
	static String baiduResult;
	static String xfyunResult;
	static String myResultBaidu;
	static String myResultXunfei;

	public static void main(String[] args) throws JSONException, ParseException {
		init();
	}

	public static void init() throws JSONException, ParseException {

		for (int i = 0, j = 0; i < 40 && j < 40; i++, j++) {
			try { // ��ֹ�ļ��������ȡʧ�ܣ���catch��׽���󲢴�ӡ��Ҳ����throw
				if (i == 10 || i == 20 || i == 30) {
					j = 0;
				}
				URI = ".\\TestFile\\white\\" + i + ".wav";
				LRUURI = ".\\TestFile\\lrc\\" + j + ".wav-�ĸ�.txt";
				realLrc = readToString(LRUURI);
				baiduResult = baiduAI.getResult(URI);// �ٶ�AI�������
				xfyunResult = xfyunAI.getResult(URI);// Ѷ�������������
				myResultBaidu = myLevenshtein.getlevenshtein(realLrc, baiduResult);
				myResultXunfei = myLevenshtein.getlevenshtein(realLrc, xfyunResult);
				resultBuffer = "��ȷ�ı���" + "\r\n" + realLrc + "\r\n" + "�ٶ�AI���������" + "\r\n" + baiduResult + "\r\n"
						+ myResultBaidu + "\r\n" + "Ѷ���������������" + "\r\n" + xfyunResult + "\r\n" + myResultXunfei + "\r\n"
						+ "\r\n";// \r\n��Ϊ����
				writeToFile(PATHNAME, resultBuffer);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public static void writeToFile(String fileName, String resultBuffer) throws IOException {
		/* д��Txt�ļ� */
		BufferedWriter out = null;
		out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PATHNAME, true), "utf-8"));
		out.write(resultBuffer); 
		out.flush(); // �ѻ���������ѹ���ļ�
		out.close(); // ���ǵùر��ļ�
	}

	public static String readToString(String fileName) {
		String encoding = "utf-8";
		File file = new File(fileName);
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			return new String(filecontent, encoding);
		} catch (UnsupportedEncodingException e) {
			System.err.println("The OS does not support " + encoding);
			e.printStackTrace();
			return null;
		}
	}

}
