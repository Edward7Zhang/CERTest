package edwardZhangCERTest;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.iflytek.msp.cpdb.lfasr.model.ProgressStatus;

public class XFyunAI {
	// ԭʼ��Ƶ��ŵ�ַ
//	private static final String local_file = "C:\\Users\\����\\Desktop\\test-noise\\0db\\cafe\\D11_955.wav";
	/*
	 * תд����ѡ�񣺱�׼��͵绰��ֱ�Ϊ�� LfasrType.LFASR_STANDARD_RECORDED_AUDIO ��
	 * LfasrType.LFASR_TELEPHONY_RECORDED_AUDIO
	 */
	private static final LfasrType type = LfasrType.LFASR_STANDARD_RECORDED_AUDIO;
	// �ȴ�ʱ�����룩
	private static int sleepSecond = 20;

	public String getResult(String local_file) {
		// ���������ļ�
		// PropertyConfigurator.configure("log4j.properties");
		//ʶ����
		String resMSG = new String();
		// ��ʼ��LFASRʵ��
		LfasrClientImp lc = null;
		try {
			lc = LfasrClientImp.initLfasrClient();
		} catch (LfasrException e) {
			// ��ʼ���쳣�������쳣������Ϣ
			Message initMsg = JSON.parseObject(e.getMessage(), Message.class);
			System.out.println("ecode=" + initMsg.getErr_no());
			System.out.println("failed=" + initMsg.getFailed());
		}

		// ��ȡ�ϴ�����ID
		String task_id = "";
		HashMap<String, String> params = new HashMap<>();
		params.put("has_participle", "true");
		try {
			// �ϴ���Ƶ�ļ�
			Message uploadMsg = lc.lfasrUpload(local_file, type, params);

			// �жϷ���ֵ
			int ok = uploadMsg.getOk();
			if (ok == 0) {
				// ��������ɹ�
				task_id = uploadMsg.getData();
				System.out.println("task_id=" + task_id);
			} else {
				// ��������ʧ��-������쳣
				System.out.println("ecode=" + uploadMsg.getErr_no());
				System.out.println("failed=" + uploadMsg.getFailed());
			}
		} catch (LfasrException e) {
			// �ϴ��쳣�������쳣������Ϣ
			Message uploadMsg = JSON.parseObject(e.getMessage(), Message.class);
			System.out.println("ecode=" + uploadMsg.getErr_no());
			System.out.println("failed=" + uploadMsg.getFailed());
		}

		// ѭ���ȴ���Ƶ������
		while (true) {
			try {
				// ˯��1min������һ�����������û����Զ�λ�ȡ����һ�μ����1���ӣ���ȡ�ɹ���break��ʧ�ܵĻ����ӵ�2�����ٻ�ȡ����ȡ�ɹ���break����ʧ�ܵĻ��ӵ�4���ӣ�8���ӣ�����
				Thread.sleep(sleepSecond * 1000);
				System.out.println("waiting ...");
			} catch (InterruptedException e) {
			}
			try {
				// ��ȡ�������
				Message progressMsg = lc.lfasrGetProgress(task_id);

				// �������״̬������0��������ʧ��
				if (progressMsg.getOk() != 0) {
					System.out.println("task was fail. task_id:" + task_id);
					System.out.println("ecode=" + progressMsg.getErr_no());
					System.out.println("failed=" + progressMsg.getFailed());

					// ����˴����쳣-������ڲ������Ի��ƣ����Ų鼫���޷��ָ�������
					// �ͻ��˿ɸ���ʵ�����ѡ��
					// 1. �ͻ���ѭ�����Ի�ȡ����
					// 2. �˳����򣬷�������
					continue;
				} else {
					ProgressStatus progressStatus = JSON.parseObject(progressMsg.getData(), ProgressStatus.class);
					if (progressStatus.getStatus() == 9) {
						// �������
						System.out.println("task was completed. task_id:" + task_id);
						break;
					} else {
						// δ�������
						System.out.println(
								"task was incomplete. task_id:" + task_id + ", status:" + progressStatus.getDesc());
						continue;
					}
				}
			} catch (LfasrException e) {
				// ��ȡ�����쳣�������ݷ�����Ϣ�Ų�������ٴν��л�ȡ
				Message progressMsg = JSON.parseObject(e.getMessage(), Message.class);
				System.out.println("ecode=" + progressMsg.getErr_no());
				System.out.println("failed=" + progressMsg.getFailed());
			}
		}

		// ��ȡ������
		try {
			Message resultMsg = lc.lfasrGetResult(task_id);

			// �������״̬����0����������ɹ�
			if (resultMsg.getOk() == 0) {
				// ��ӡתд���
//				System.out.println(resultMsg.getData());
				String data = resultMsg.getData();
//				int i = data.indexOf("[");
//				data = data.substring(i);
				JSONArray dataJson = new JSONArray(data);
				
				for(int i = 0;i<dataJson.length();i++) {
					JSONObject res = dataJson.getJSONObject(i);
					String resMsg = res.getString("onebest");
					resMSG = resMSG + resMsg;
				}
				resMSG = resMSG.replace("��", "").replace("��", "").replace("��","").replace("!", "");
//				JSONArray info = res.getJSONArray("onebest");
//				System.out.println("Ѷ���������������" + resMSG);
			} else {
				// תдʧ�ܣ�����ʧ����Ϣ���д���
				System.out.println("ecode=" + resultMsg.getErr_no());
				System.out.println("failed=" + resultMsg.getFailed());
			}
		} catch (LfasrException e) {
			// ��ȡ����쳣���������쳣������Ϣ
			Message resultMsg = JSON.parseObject(e.getMessage(), Message.class);
			System.out.println("ecode=" + resultMsg.getErr_no());
			System.out.println("failed=" + resultMsg.getFailed());
		}
		return resMSG;
		
	}
}
