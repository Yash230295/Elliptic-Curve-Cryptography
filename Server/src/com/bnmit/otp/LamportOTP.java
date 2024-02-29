
package com.bnmit.otp;

public class LamportOTP {

	double factor;

	double number = 9;

	LamportOTP() {
		factor = 9;
	}

	public LamportOTP(int factor) {
		this.factor = factor;
	}

	public double function() {
		double ret;
		ret = ((3 * number) + 3 + factor) / 2;
		number = ret;
		return ret;
	}

	public double inverserfunction(double number) {
		double ret;
		ret = ((2 * number) - 3 - factor) / 3;
		return ret;
	}

	public static void main(String arg[]) {
		LamportOTP otp = new LamportOTP(455);

		double first = otp.function();
		double sec = otp.function();
		double third = otp.function();
		double fourth = otp.function();
		System.out.println("1. " + first);
		System.out.println("2. " + sec);
		System.out.println("3. " + third);
		System.out.println("4. " + fourth);

		System.out.println("Inverse started ..");
		System.out.println("4. " + fourth);
		System.out.println("3. " + otp.inverserfunction(fourth));
		System.out.println("2. " + otp.inverserfunction(third));
		System.out.println("1. " + otp.inverserfunction(sec));
		System.out.println("Seed : " + otp.inverserfunction(first));

	}

}
