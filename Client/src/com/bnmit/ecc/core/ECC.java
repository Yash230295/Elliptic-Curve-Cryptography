package com.bnmit.ecc.core;

import java.math.BigInteger;
import java.util.Random;
public class ECC {
	public static final long AUXILIARY_CONSTANT_LONG = 1000;
	public static final BigInteger AUXILIARY_CONSTANT = BigInteger.valueOf(AUXILIARY_CONSTANT_LONG);

	// The execution time of the last action, in millisecond.
	private static long executionTime = -1;
	private static long startExecutionTime;

	/**
	 */
	public static byte[] encrypt(byte[] plainText, PublicKey key) throws Exception {
		initializeExecutionTime();

		EllipticCurve c = key.getCurve();
		ECPoint g = c.getBasePoint();
		ECPoint publicKey = key.getKey();
		BigInteger p = c.getP();
		int numBits = p.bitLength();
		int blockSize = getBlockSize(c);
		int cipherTextBlockSize = getCipherTextBlockSize(c);

		// Pad the plainText
		byte[] padded = pad(plainText, blockSize);

		// Chunk the plainText into blocks.
		byte[][] block = new byte[padded.length / blockSize][blockSize];
		for (int i = 0; i < block.length; ++i) {
			for (int j = 0; j < blockSize; ++j) {
				block[i][j] = padded[i * blockSize + j];
			}
		}

		// Encode each block into unique point.
		ECPoint[] encoded = new ECPoint[block.length];
		for (int i = 0; i < encoded.length; ++i) {
			encoded[i] = encode(block[i], c);
		}

		// Encrypt each encoded point into a pair of points:
		// [C_1, C_2] = [kG, P_m + kP_G], where:
		// k is a randomly generated integer such that 1 <= k < p-1,
		// G is the base point (provided in the key),
		// P_m is the encoded point from the plain text,
		// P_G is the point provided in the public key.
		ECPoint[][] encrypted = new ECPoint[block.length][2];
		Random rnd = new Random(System.currentTimeMillis());
		for (int i = 0; i < encrypted.length; ++i) {
			BigInteger k;
			do {
				k = new BigInteger(numBits, rnd);
			} while (k.mod(p).compareTo(BigInteger.ZERO) == 0);
			encrypted[i][0] = c.multiply(g, k);
			encrypted[i][1] = c.add(encoded[i], c.multiply(publicKey, k));
		}

		// Represent the ciphertext as an array of bytes
		byte[] cipherText = new byte[encrypted.length * cipherTextBlockSize * 4];
		for (int i = 0; i < encrypted.length; ++i) {
			// encrypted[0].x
			byte[] cipher = encrypted[i][0].x.toByteArray();
			int offset = i * cipherTextBlockSize * 4 + cipherTextBlockSize * 0 + (cipherTextBlockSize - cipher.length);
			for (int j = 0; j < cipher.length; ++j) {
				cipherText[j + offset] = cipher[j];
			}
			// encrypted[0].y
			cipher = encrypted[i][0].y.toByteArray();
			offset = i * cipherTextBlockSize * 4 + cipherTextBlockSize * 1 + (cipherTextBlockSize - cipher.length);
			for (int j = 0; j < cipher.length; ++j) {
				cipherText[j + offset] = cipher[j];
			}
			// encrypted[1].x
			cipher = encrypted[i][1].x.toByteArray();
			offset = i * cipherTextBlockSize * 4 + cipherTextBlockSize * 2 + (cipherTextBlockSize - cipher.length);
			for (int j = 0; j < cipher.length; ++j) {
				cipherText[j + offset] = cipher[j];
			}
			// encrypted[1].y
			cipher = encrypted[i][1].y.toByteArray();
			offset = i * cipherTextBlockSize * 4 + cipherTextBlockSize * 3 + (cipherTextBlockSize - cipher.length);
			for (int j = 0; j < cipher.length; ++j) {
				cipherText[j + offset] = cipher[j];
			}
		}

		finalizeExecutionTime();

		return cipherText;
	}

	public static byte[] decrypt(byte[] cipherText, PrivateKey key) throws Exception {
		initializeExecutionTime();

		EllipticCurve c = key.getCurve();
		ECPoint g = c.getBasePoint();
		BigInteger privateKey = key.getKey();
		BigInteger p = c.getP();
		int numBits = p.bitLength();
		int blockSize = getBlockSize(c);
		int cipherTextBlockSize = getCipherTextBlockSize(c);

		// Chunk the cipherText into blocks.
		if (cipherText.length % cipherTextBlockSize != 0 || (cipherText.length / cipherTextBlockSize) % 4 != 0) {
			throw new Exception("The length of the cipher text is not valid");
		}
		byte block[][] = new byte[cipherText.length / cipherTextBlockSize][cipherTextBlockSize];
		for (int i = 0; i < block.length; ++i) {
			for (int j = 0; j < cipherTextBlockSize; ++j) {
				block[i][j] = cipherText[i * cipherTextBlockSize + j];
			}
		}

		// Calculate the encoded point
		// P_m = C_2 - kC_1, where:
		// [C_1, C_2] is the ciphertext,
		// k is the private key.
		ECPoint encoded[] = new ECPoint[block.length / 4];
		for (int i = 0; i < block.length; i += 4) {
			ECPoint c1 = new ECPoint(new BigInteger(block[i]), new BigInteger(block[i + 1]));
			ECPoint c2 = new ECPoint(new BigInteger(block[i + 2]), new BigInteger(block[i + 3]));
			encoded[i / 4] = c.subtract(c2, c.multiply(c1, privateKey));
		}

		// Decode the encoded point
		byte plainText[] = new byte[encoded.length * blockSize];
		for (int i = 0; i < encoded.length; ++i) {
			byte decoded[] = decode(encoded[i], c);
			for (int j = Math.max(blockSize - decoded.length, 0); j < blockSize; ++j) {
				plainText[i * blockSize + j] = decoded[j + decoded.length - blockSize];
			}
		}
		plainText = unpad(plainText, blockSize);

		finalizeExecutionTime();
		return plainText;
	}

	/**
	 */
	public static KeyPair generateKeyPair(EllipticCurve c, Random rnd) throws Exception {
		initializeExecutionTime();

		// Randomly select the private key, such that it is relatively
		// prime to p
		BigInteger p = c.getP();
		BigInteger privateKey;
		do {
			privateKey = new BigInteger(p.bitLength(), rnd);
		} while (privateKey.mod(p).compareTo(BigInteger.ZERO) == 0);

		// Calculate the public key, k * g.
		// First, randomly generate g if it is not present in the curve.
		ECPoint g = c.getBasePoint();
		if (g == null) {
			// Randomly generate g using Koblits method.
			// The starting value of x should be random.
			BigInteger x = new BigInteger(p.bitLength(), rnd);
			g = koblitzProbabilistic(c, x);
			c.setBasePoint(g);
		}
		ECPoint publicKey = c.multiply(g, privateKey);

		KeyPair result = new KeyPair(new PublicKey(c, publicKey), new PrivateKey(c, privateKey));

		finalizeExecutionTime();
		return result;
	}

	
	public static KeyPair generateKeyPair2(EllipticCurve c, BigInteger b1, BigInteger b2) throws Exception {
		initializeExecutionTime();

		// Randomly select the private key, such that it is relatively
		// prime to p
		BigInteger p = c.getP();
		BigInteger privateKey;
		do {
			privateKey = b1;
		} while (privateKey.mod(p).compareTo(BigInteger.ZERO) == 0);

		// Calculate the public key, k * g.
		// First, randomly generate g if it is not present in the curve.
		ECPoint g = c.getBasePoint();
		if (g == null) {
			// Randomly generate g using Koblits method.
			// The starting value of x should be random.
			BigInteger x = b2;
			g = koblitzProbabilistic(c, x);
			c.setBasePoint(g);
		}
		ECPoint publicKey = c.multiply(g, privateKey);

		KeyPair result = new KeyPair(new PublicKey(c, publicKey), new PrivateKey(c, privateKey));

		finalizeExecutionTime();
		return result;
	}

	/**
	 */
	public static long getLastExecutionTime() {
		return executionTime;
	}

	/**
	 * Return the encoded point from a block of byte.
	 * 
	 * @param block
	 * @param c
	 * @return
	 */
	private static ECPoint encode(byte[] block, EllipticCurve c) throws Exception {
		// pad two zero byte
		byte[] paddedBlock = new byte[block.length + 2];
		for (int i = 0; i < block.length; ++i) {
			paddedBlock[i + 2] = block[i];
		}
		return koblitzProbabilistic(c, new BigInteger(paddedBlock));
	}

	/**
	 */
	private static byte[] decode(ECPoint point, EllipticCurve c) {
		return point.x.divide(AUXILIARY_CONSTANT).toByteArray();
	}

	private static int getBlockSize(EllipticCurve c) {
		return Math.max(c.getP().bitLength() / 8 - 5, 1);
	}

	/**
	 * 
	 * @param c
	 * @return
	 */
	private static int getCipherTextBlockSize(EllipticCurve c) {
		return c.getP().bitLength() / 8 + 5;
	}

	/**
	 * Pad the array of byte b so its length will be multiple of blockSize.
	 * 
	 * There will be at least one byte padded. The last byte will contain the
	 * number of padded bytes.
	 * 
	 * @param b
	 * @return
	 */
	private static byte[] pad(byte[] b, int blockSize) {
		int paddedLength = blockSize - (b.length % blockSize);
		byte[] padded = new byte[b.length + paddedLength];
		for (int i = 0; i < b.length; ++i) {
			padded[i] = b[i];
		}
		for (int i = 0; i < paddedLength - 1; ++i) {
			padded[b.length + i] = 0;
		}
		padded[padded.length - 1] = (byte) paddedLength;

		return padded;
	}

	/**
	 * Recover the original array of byte given the padded array of byte b.
	 * 
	 * @param b
	 * @param blockSize
	 * @return
	 */
	private static byte[] unpad(byte[] b, int blockSize) {
		int paddedLength = b[b.length - 1];
		byte[] unpadded = new byte[b.length - paddedLength];
		for (int i = 0; i < unpadded.length; ++i) {
			unpadded[i] = b[i];
		}
		return unpadded;
	}

	/**
	 * Find a point inside the curve with the x-coordinate equals x *
	 * AUXILIARY_CONSTANT + k, where k is as small as possible.
	 * 
	 * There is a very small probability that k will be as large as the
	 * AUXILIARY_CONSTANT, and this method relies on the fact. If k exceeds the
	 * constant, an exception will be thrown.
	 * 
	 * This method works only for p = 3 (mod 4), as finding the solution to the
	 * quadratic congruence is non-deterministic for p = 1 (mod 4). If p equals
	 * 1 (mod 4), an exception will also be thrown.
	 * 
	 * Source:
	 * http://www.ams.org/journals/mcom/1987-48-177/S0025-5718-1987-0866109-5/
	 * S0025-5718-1987-0866109-5.pdf
	 * 
	 * @param c
	 * @param x
	 * @return
	 */
	private static ECPoint koblitzProbabilistic(EllipticCurve c, BigInteger x) throws Exception {
		BigInteger p = c.getP();

		// throw an exception if p != 3 (mod 4)
		if (!p.testBit(0) || !p.testBit(1)) {
			throw new Exception("P should be 3 (mod 4)");
		}
		BigInteger pMinusOnePerTwo = p.subtract(BigInteger.ONE).shiftRight(1);

		BigInteger tempX = x.multiply(AUXILIARY_CONSTANT).mod(p);
		for (long k = 0; k < AUXILIARY_CONSTANT_LONG; ++k) {
			BigInteger newX = tempX.add(BigInteger.valueOf(k));

			// Calculates the rhs of the elliptic curve equation, call it a
			BigInteger a = c.calculateRhs(newX);

			// Determine whether this value is a quadratic residue modulo p
			// It is if and only if a ^ ((p - 1) / 2) = 1 (mod p)
			if (a.modPow(pMinusOnePerTwo, p).compareTo(BigInteger.ONE) == 0) {
				// We found it! Now, the solution is y = a ^ ((p + 1) / 4)
				BigInteger y = a.modPow(p.add(BigInteger.ONE).shiftRight(2), p);
				return new ECPoint(newX.mod(p), y);
			}
		}

		// If we reach this point, then no point are found within the limit.
		throw new Exception("No point found within the auxiliary constant");
	}

	private static void initializeExecutionTime() {
		startExecutionTime = System.currentTimeMillis();
	}

	private static void finalizeExecutionTime() {
		executionTime = System.currentTimeMillis() - startExecutionTime;
	}

	public static void main(String arg[]) throws Exception {
		EllipticCurve ecc = EllipticCurve.NIST_P_192;
		KeyPair keys = generateKeyPair(ecc, new Random(System.currentTimeMillis()));
		String pt = "Ashok Kumar K";
		System.out.println("Plain text .. " + pt);
		keys.getPublicKey().saveToFile("C:\\Ashok\\aa");
		PublicKey pk = new PublicKey("C:\\Ashok\\aa");
		byte[] ct = encrypt(pt.getBytes(), pk);
		String s1 = new String(ct);
		System.out.println("Cipher text .. " + s1);
		byte[] decrypted = decrypt(ct, keys.getPrivateKey());
		String s2 = new String(decrypted);
		System.out.println("Decrypted text .. " + s2);
	}
}
