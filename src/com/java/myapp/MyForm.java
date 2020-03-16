package com.java.myapp;

import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

public class MyForm {
	public static String toString(byte[] bytes) {
		StringBuilder sbTmp = new StringBuilder();
		for(byte b : bytes) {
			sbTmp.append(String.format("%X", b));
		}
		return sbTmp.toString();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			TerminalFactory factory = TerminalFactory.getDefault();
			List<CardTerminal> terminals = factory.terminals().list();
			System.out.println("Terminals count : " + terminals.size());
			System.out.println("Terminals: " + terminals);
			CardTerminal terminal = (CardTerminal) terminals.get(0);
			Card card = terminal.connect("*");
			System.out.println("card : " + card);
			@SuppressWarnings("MismatchedReadAndWriteOfArray")
			byte[] baATR = card.getATR().getBytes();
			System.out.println("ATR : " + baATR);
			CardChannel channel = card.getBasicChannel();
			byte[] baCommandAPDU = {
					(byte) 0x00, (byte) 0xA4, (byte) 0x04,
					(byte) 0x00, (byte) 0x80, (byte) 0xA0,
					(byte) 0x00, (byte) 0x00, (byte) 0x00, 
					(byte) 0x54, (byte) 0x48, (byte) 0x00, 
					(byte) 0x01
				};
			ResponseAPDU r = channel.transmit(new CommandAPDU(baCommandAPDU));
			//==============Th fullname ======================
			byte[] thaifullname = {
				(byte) 0x80, (byte)	0xB0, (byte) 0x00, (byte) 0x11, (byte) 0x02, (byte) 0x00, (byte) 0x64	
			};
			ResponseAPDU response_thaifullname = channel.transmit(new CommandAPDU(thaifullname));
			String[] parts;
			try {
				parts = new String(response_thaifullname.getBytes(), "TIS620").substring(0,35).split("#");
				String part1 = parts[0]; //คำนำหน้า
				String part2 = parts[1]; //ชื่อ
				String part3 = parts[3].replaceFirst(" ", ""); //สกุล
				System.out.println(part1);//คำนำหน้า
				System.out.println(part2);//ชื่อ
				System.out.println(part3);//นามสกุล
			} catch (UnsupportedEncodingException e) {}
		
		//for control error Smartcard
		}catch(CardException ex){}
	}

}

