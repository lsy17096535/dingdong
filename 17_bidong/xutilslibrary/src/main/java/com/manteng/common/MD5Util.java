package com.manteng.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	private static ThreadLocal<MessageDigest> messageDigestHolder = new ThreadLocal<MessageDigest>();

	static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	static {
		try {
			MessageDigest message = MessageDigest.getInstance("MD5");
			messageDigestHolder.set(message);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

//	public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
//        MessageDigest md = MessageDigest.getInstance("SHA-1");
//        md.update(text.getBytes("iso-8859-1"), 0, text.length());
//        byte[] sha1hash = md.digest();
//        return convertToHex(sha1hash);
//    }
	
//	private static String convertToHex(byte[] data) {
//        StringBuilder buf = new StringBuilder();
//        for (byte b : data) {
//            int halfbyte = (b >>> 4) & 0x0F;
//            int two_halfs = 0;
//            do {
//                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
//                halfbyte = b & 0x0F;
//            } while (two_halfs++ < 1);
//        }
//        return buf.toString();
//    }
	
	public static String getMD5Format(String data) {
		try {
			MessageDigest message = (MessageDigest) messageDigestHolder.get();
			if (message == null) {
				message = MessageDigest.getInstance("MD5");
				messageDigestHolder.set(message);
			}

			message.update(data.getBytes());
			byte[] b = message.digest();

			String digestHexStr = "";
			for (int i = 0; i < 16; i++) {
				digestHexStr = digestHexStr + byteHEX(b[i]);
			}

			return digestHexStr;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

//	public static String getMD5Format(byte[] data) {
//		try {
//			MessageDigest message = (MessageDigest) messageDigestHolder.get();
//			if (message == null) {
//				message = MessageDigest.getInstance("MD5");
//				messageDigestHolder.set(message);
//			}
//
//			message.update(data);
//			byte[] b = message.digest();
//
//			String digestHexStr = "";
//			for (int i = 0; i < 16; i++) {
//				digestHexStr = digestHexStr + byteHEX(b[i]);
//			}
//
//			return digestHexStr;
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		
//	}

	private static String byteHEX(byte ib) {
		char[] ob = new char[2];
		ob[0] = hexDigits[(ib >>> 4 & 0xF)];
		ob[1] = hexDigits[(ib & 0xF)];

		return new String(ob);
	}
}