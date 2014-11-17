package com.example.coder;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;

import com.example.Config;

public class WavDecoderActivity extends Activity implements Runnable {
	private Thread mPlaybackThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPlaybackThread = new Thread(this);
		mPlaybackThread.start();
	}

	@Override
	public void run() {
		
		BufferedInputStream bis = null;
		AudioTrack audioTrack = null;
		try {
			Header header = readHeader(Config.URI_WAV);
			if (header == null) {
				System.out.println("read Header error");
				return ;
			}
			System.out.println(header);
			
			int streamType = AudioManager.STREAM_MUSIC;
			int sampleRateInHz = header.sampleRateInHz;
			int channelConfig = header.channels == 2 ?  AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO ;
			int audioFormat = header.audioFormat == 16 ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT;
			int bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat) * 2;
			int mode = AudioTrack.MODE_STREAM;
			audioTrack = new AudioTrack(streamType, sampleRateInHz, channelConfig,
					audioFormat, bufferSizeInBytes, mode);
			audioTrack.play();
			bis = new BufferedInputStream(new FileInputStream(Config.URI_WAV));
			
			int position = header.position;
			if (bufferSizeInBytes < position) {
				bufferSizeInBytes = position;
			}
			
			byte[] buffer = new byte[bufferSizeInBytes];
			int len = 0;
			int read = bis.read(buffer, 0, header.position);
			if (position != read ) {
				System.out.println(" read  error");
				return ;
			}
			int dataSize = header.dataSize;
			int count = 0;
			
			while ((len = bis.read(buffer)) != -1 && !isFinishing() && count <= dataSize) {
				count += len;
				audioTrack.write(buffer, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
				}
			}
			if (audioTrack != null) {
				audioTrack.release();
			}
		}
	}
	
	private static Header readHeader(String path) throws IOException {
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(path);
			int fileLen = inputStream.available();
			
			byte[] buffer = new byte[1024];
			int len = inputStream.read(buffer);
			if (len < 50) {
				return null;
			}
			ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			
			/**
			 * RIFF WAVE Chunk
			 *==================================
			 *	| |所占字节数| 具体内容 |
			 *	==================================
			 *	| ID | 4 Bytes | 'RIFF' |
			 *	----------------------------------
			 *	| Size | 4 Bytes | |
			 *	----------------------------------
			 *	| Type | 4 Bytes | 'WAVE' |
			 *	----------------------------------
			 */
			if (!(buffer[0] == 'R' && buffer[1] == 'I' && buffer[2] == 'F' && buffer[3] == 'F')) {
				return null;
			}
			 
			//read len
			int size = byteBuffer.getInt(4);
			if (fileLen != (size + 8)) {
				return null;
			}
			System.out.println("file size " + size);
			
			//read type 
			if (!(buffer[8] == 'W' && buffer[9] == 'A' && buffer[10] == 'V' && buffer[11] == 'E')) {
				return null;
			}
			
			/**
			 * 
			 * Format Chunk
				====================================================================
				| | 字节数 | 具体内容 |
				====================================================================
				| ID | 4 Bytes | 'fmt ' |
				--------------------------------------------------------------------
				| Size | 4 Bytes | 数值为16或18，18则最后又附加信息 |
				-------------------------------------------------------------------- ----
				| FormatTag | 2 Bytes | 编码方式，一般为0x0001 | |
				-------------------------------------------------------------------- |
				| Channels | 2 Bytes | 声道数目，1--单声道；2--双声道 | |
				-------------------------------------------------------------------- |
				| SamplesPerSec | 4 Bytes | 采样频率 | |
				-------------------------------------------------------------------- |
				| AvgBytesPerSec| 4 Bytes | 每秒所需字节数 | |===> WAVE_FORMAT
				-------------------------------------------------------------------- |
				| BlockAlign | 2 Bytes | 数据块对齐单位(每个采样需要的字节数) | |
				-------------------------------------------------------------------- |
				| BitsPerSample | 2 Bytes | 每个采样需要的bit数 | |
				-------------------------------------------------------------------- |
				| | 2 Bytes | 附加信息（可选，通过Size来判断有无） | |
			 * 
			 */
			
			int fmtIndex = 12;
			if (!(buffer[fmtIndex] == 'f' && buffer[fmtIndex + 1] == 'm' &&  buffer[fmtIndex +2] == 't')) {
				return null;
			}
			
			int formatChunkSize = byteBuffer.getInt(fmtIndex + 4);
			System.out.println("formatChunkSize " + formatChunkSize);
			
			if (!(formatChunkSize == 18 || formatChunkSize == 16)) {
				return null;
			}
			 
			int channels = byteBuffer.getShort(fmtIndex + 10);
			System.out.println("channels " + channels);
			
			int sampleRateInHz = byteBuffer.getInt(fmtIndex +12);
			
			System.out.println(sampleRateInHz);
			
			int audioFormat = byteBuffer.getShort(fmtIndex + 22);
			if (!(audioFormat == 8 || audioFormat == 16)) {
				return null;
			}
			System.out.println(audioFormat);
			
			
			/**
			 * Fact Chunk
				==================================
				| |所占字节数| 具体内容 |
				==================================
				| ID | 4 Bytes | 'fact' |
				----------------------------------
				| Size | 4 Bytes | 数值为4 |
				----------------------------------
				| data | 4 Bytes | |
				----------------------------------
			 */
				
			/**
			 * ID | 4 Bytes | 'data' |
				----------------------------------
				| Size | 4 Bytes | |
				----------------------------------
				| data | | |
			 * 
			 */
			int dataIndex = formatChunkSize == 18 ? fmtIndex + 8 + 18 : fmtIndex + 8 + 16;
			System.out.println(dataIndex);
			if (!(buffer[dataIndex] == 'd' && buffer[dataIndex + 1] == 'a' && buffer[dataIndex + 2] == 't' && buffer[dataIndex + 3] == 'a')) {
				return null;
			}

			int dataSize = byteBuffer.getInt(dataIndex + 4);
			System.out.println("dataSize : " + dataSize);

			int position = dataIndex + 8;
			System.out.println("position : " + position);
			if ((dataSize + position) > fileLen) {
				return null;
			}
			Header header = new Header();
			header.channels = channels;
			header.sampleRateInHz = sampleRateInHz;
			header.audioFormat = audioFormat;
			header.position = position;
			header.dataSize = dataSize;
			header.duration = dataSize / ((sampleRateInHz * channels * audioFormat) / 8);
			
			return header;
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
 	}

	private static class Header {
		int channels;
		int sampleRateInHz;
		int audioFormat;
		int position;
		int dataSize;
		int duration;
		
		@Override
		public String toString() {
			return "Header [channels=" + channels + ", sampleRateInHz=" + sampleRateInHz + ", audioFormat=" + audioFormat + ", position="
					+ position + ", dataSize=" + dataSize + ", duration=" + duration + "]";
		}
	}
}
