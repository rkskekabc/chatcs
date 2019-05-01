package com.cafe24.network.chat.client;

import com.cafe24.network.chat.util.EncodingUtil;

public class test {
	public static void main(String[] args) {
		String message = "ㅇ";
		System.out.println(message);
		System.out.println(EncodingUtil.Encode(message));
		System.out.println(EncodingUtil.Decode(EncodingUtil.Encode(message)));
	}
}
