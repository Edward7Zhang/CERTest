package edwardZhangCERTest;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.util.Util;

public class BaiduAI {
	//����APPID/AK/SK
    public static final String APP_ID = "11027741";
    public static final String API_KEY = "hPDtclLIAwSyFL9HRuDuLLaU";
    public static final String SECRET_KEY = "azs3q6kaG8ynOTASXyfh50w9RRGMEglU";
    
    public AipSpeech getClient() {
    	// ��ʼ��һ��AipSpeech
    	AipSpeech instance = null;
    		if(instance == null) {
    			synchronized(AipSpeech.class) {
    				if(instance == null) {
    					instance = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);
    				}
    			}
    		}
    		return instance;
    }

    public String getResult(String URL) throws JSONException,ParseException {
        

        // ��ѡ�������������Ӳ���
        getClient().setConnectionTimeoutInMillis(2000);
        getClient().setSocketTimeoutInMillis(60000);

        // ���ýӿ�
        JSONObject res = getClient().asr(URL, "wav", 16000, null);
        System.out.println(res);
        JSONArray resMsg = res.getJSONArray("result");
        String resMSG = new String();
        for(int i = 0; i< resMsg.length();i++) {
        	resMSG = resMsg.getString(i) + resMSG;
        }
        resMSG = resMSG.replace("��", "").replace("��", "").replace("��","").replace("!", "");
        System.out.println("�ٶ�AI���������" + res);
		return resMSG;
        
    }
   
    
}
