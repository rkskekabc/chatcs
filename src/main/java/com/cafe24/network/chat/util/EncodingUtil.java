package com.cafe24.network.chat.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public class EncodingUtil {
	public static String Encode(String content) {
		byte[] contentBytes = content.getBytes();
		Encoder encoder = Base64.getEncoder();
		byte[] encodingBytes = encoder.encode(contentBytes);
		
		try {
			return new String(encodingBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "error";
		}
	}
	
	public static String Decode(String content) {
		byte[] contentBytes = content.getBytes();
		Decoder decoder = Base64.getDecoder();
		byte[] decodingBytes = decoder.decode(contentBytes);
		
		try {
			return new String(decodingBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "error";
		}
	}
}
