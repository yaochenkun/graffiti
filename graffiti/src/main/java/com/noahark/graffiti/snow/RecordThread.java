package com.noahark.graffiti.snow;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

public class RecordThread extends Thread 
{
	private AudioRecord audioRecord;
	private int bufferSize = 100;
	// 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
	private static int SAMPLE_RATE_IN_HZ = 44100;
	private Handler handler;
	private int what;

	private boolean stop = false;

	// 到达该值之后 触发事件
	private static int BLOW_BOUNDARY = 35;

	public RecordThread(Handler handler, int what) 
	{
		super();
		bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		// 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
		// 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
				SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, bufferSize);
		this.handler = handler;
		this.what = what;
	}
	
	public void stopRecord()
	{
		stop = true;
	}
	public boolean getRecordStatus()
	{
		return stop;
	}

	@Override
	public void run() 
	{
		System.out.println("RUN");
		stop = false;
		try {
			audioRecord.startRecording();
			// 用于读取的 buffer
			byte[] buffer = new byte[bufferSize];

			int total = 0;
			int number = 0;
			while (!stop) {
				number++;
				sleep(8);
				long currenttime = System.currentTimeMillis();
				int r = audioRecord.read(buffer, 0, bufferSize) + 1;// 读取到的数据
				int v = 0;
				for (int i = 0; i < buffer.length; i++) {
					v += Math.abs(buffer[i]);//取绝对值，因为可能为负
				}
				int value = Integer.valueOf(v / r);//算得当前所有值的平均值
				System.out.println("value:" + value);
				total = total + value;
				long endtime = System.currentTimeMillis();
				long time = endtime - currenttime;
				//如果时间大于100毫秒并且次数多于5次
				if (time >= 100 || number > 5) {
					int tmp = total / number;
					total = 0;
					number = 0;
					//声音的大小达到一定的值
					if (tmp > BLOW_BOUNDARY) {
						// 发送消息通知到界面 触发动画
						// 利用传入的handler 给界面发送通知
						handler.sendEmptyMessage(what);
						number = 1;
						time = 1;
					}
				}
			}
			audioRecord.stop();
			audioRecord.release();
			bufferSize = 100;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}