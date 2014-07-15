package gse.pathfinder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import android.annotation.SuppressLint;
import android.util.Base64;

public class Crypto {
	private static final String INTERNAL_PASSWORD = "MYPASSWORD";

	@SuppressLint("TrulyRandom")
	public static String encrypt(String value) {
		try {
			DESKeySpec keySpec = new DESKeySpec(INTERNAL_PASSWORD.getBytes("UTF8"));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(keySpec);

			byte[] clearText = value.getBytes("UTF8");
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, key);

			String encrypedValue = Base64.encodeToString(cipher.doFinal(clearText), Base64.DEFAULT);
			return encrypedValue;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public static String decrypt(String value) {
		try {
			DESKeySpec keySpec = new DESKeySpec(INTERNAL_PASSWORD.getBytes("UTF8"));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(keySpec);

			byte[] encrypedPwdBytes = Base64.decode(value, Base64.DEFAULT);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decrypedValueBytes = (cipher.doFinal(encrypedPwdBytes));

			String decrypedValue = new String(decrypedValueBytes);
			return decrypedValue;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
}
