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

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.SpringLayout;
import java.awt.EventQueue;
import javax.swing.JLabel;

public class Main {
    
    public static String toString(byte[] bytes) {
        StringBuilder sbTmp = new StringBuilder();
        for(byte b : bytes){
                sbTmp.append(String.format("%X", b));
        }
        return sbTmp.toString();
    }
    
    public static void main(String[] args) {
    	JFrame frame = new JFrame("โปรแกรมอ่านบัตร");
    	frame.setSize(500, 500);
    	frame.setLocation(200, 200);
    	frame.setVisible(true);
    	
        try {
                // Show the list of available terminals
                // On Windows see HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Cryptography\Calais\Readers
                TerminalFactory factory = TerminalFactory.getDefault();
                List<CardTerminal> terminals = factory.terminals().list();//แก้จาก List terminals = factory.terminals().list();
                System.out.println("Terminals count: " + terminals.size());
                System.out.println("Terminals: " + terminals);

                // Get the first terminal in the list
                CardTerminal terminal = (CardTerminal) terminals.get(0);

                // Establish a connection with the card using
                // "T=0", "T=1", "T=CL" or "*"
                Card card = terminal.connect("*");
                System.out.println("Card: " + card);

                // Get ATR
                byte[] baATR = card.getATR().getBytes();
                System.out.println("ATR: " + Main.toString(baATR) );

                // Select Card Manager
                // - Establish channel to exchange APDU
                // - Send SELECT Command APDU
                // - Show Response APDU
                CardChannel channel = card.getBasicChannel();

                //SELECT Command
                // See GlobalPlatform Card Specification (e.g. 2.2, section 11.9)
                // CLA: 00
                // INS: A4
                // P1: 04 i.e. b3 is set to 1, means select by name
                // P2: 00 i.e. first or only occurence
                // Lc: 08 i.e. length of AID see below
                // Data: A0 00 00 00 03 00 00 00
                // AID of the card manager,
                // in the future should change to A0 00 00 01 51 00 00
                // อ่านค่า Select
                byte[] baCommandAPDU = {
                    (byte) 0x00, (byte) 0xA4, (byte) 0x04,
                    (byte) 0x00, (byte) 0x08, (byte) 0xA0,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x54, (byte) 0x48, (byte) 0x00,
                    (byte) 0x01
                };
                System.out.println("APDU <<<: " + Main.toString(baCommandAPDU));

                ResponseAPDU r = channel.transmit(new CommandAPDU(baCommandAPDU));
                System.out.println("APDU >>>: " + Main.toString(r.getBytes()));
                // อ่านเลขบัตรประชาชน
                byte[] command_idcard = {
                    (byte) 0x80, (byte) 0xb0, (byte) 0x00,
                    (byte) 0x04, (byte) 0x02, (byte) 0x00,
                    (byte) 0x0d
                };
                System.out.println("APDU <<<:: " + Main.toString(command_idcard));

                ResponseAPDU response_command_idcard = channel.transmit(new CommandAPDU(command_idcard));
                System.out.println("APDU >>>: " + Main.toString(response_command_idcard.getBytes()));
                
                //=========Th Fullname==============================
                byte[] command_thfullname = {
                    (byte) 0x80, (byte) 0xb0, (byte) 0x00,
                    (byte) 0x11, (byte) 0x02, (byte) 0x00,
                    (byte) 0x64
                };
        
                ResponseAPDU response_command_thfullname = channel.transmit(new CommandAPDU(command_thfullname));
                String[] parts;
                try {
                    parts = new String(response_command_thfullname.getBytes(), "TIS620").substring(0, 35).split("#");
                    String part1 = parts[0]; // คำนำหน้า
                    String part2 = parts[1]; // ชื่อ
                    //String part3 = parts[2]; // ว่าง
                    String part3 = parts[3].replaceFirst(" ", ""); // สกุล
                               //parts[3].substring(0,20).replaceFirst(" ", "")   
                    System.out.println(part1); // คำนำหน้า
                    System.out.println(part2); // ชื่อ                    
                    System.out.println(part3); // สกุล
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                //=========Address======================
                byte[] command_address = {
                    (byte) 0x80, (byte) 0xb0, (byte) 0x15,
                    (byte) 0x79, (byte) 0x02, (byte) 0x00,
                    (byte) 0x64
                };

                ResponseAPDU answer6 = channel.transmit(new CommandAPDU(command_address));
                //System.out.println(new String(answer6.getBytes(), "TIS620").substring(0,60));
        
                String[] enpart11;
                try {
                    enpart11 = new String(answer6.getBytes(), "TIS620").substring(0, 60).split("#");
                    String enpart12 = enpart11[0]; // บ้านเลขที่
                    String enpart13 = enpart11[1]; // หมู่ที่
                    //String enpart14 = enpart11[2];
                    //String enpart15 = enpart11[3];
                    //String enpart16 = enpart11[4];
                    String enpart17 = enpart11[5]; // ตำบล
                    String enpart18 = enpart11[6]; // อำเภอ
                    String enpart19 = enpart11[7].substring(7, enpart11[7].length()); ; // จังหวัด
            
                    System.out.println(enpart12);
                    System.out.println(enpart13);
                    System.out.println(enpart17);
                    System.out.println(enpart18);
                    System.out.println(enpart19);
                    //===========================
                    String str2 = String.format("%3s", enpart12).replace(' ', '0'); // บ้านเลขที่
                    System.out.println(str2);
                    String moo = enpart11[1].substring(7,9).replaceFirst(" ", "");// ลบค่าว่างทิ้ง
                    enpart11[1].substring(7,9); // หมู่ที่
                    String moo2 = String.format("%2s", moo).replace(' ', '0');//เติม 0 หน้า
                    System.out.println(moo2);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                //=========Date of birth=======================
                byte[] command_dateofbirth = {
                    (byte) 0x80, (byte) 0xb0, (byte) 0x00,
                    (byte) 0xd9, (byte) 0x02, (byte) 0x00,
                    (byte) 0x08
                };            
        
                ResponseAPDU answer1 = channel.transmit(new CommandAPDU(command_dateofbirth));
                //System.out.println(new String(answer1.getBytes(), "TIS620").substring(0,8));
        
                String year;
                String month;
                String day;
                int foo,foo2;
                               
                try {
                    year = new String(answer1.getBytes(), "TIS620").substring(0,4);
                    month = new String(answer1.getBytes(), "TIS620").substring(4,6);
                    day = new String(answer1.getBytes(), "TIS620").substring(6,8);
                    foo = Integer.parseInt(year);
                    foo2 = foo-543;
                    //txtBirthday.setText(day+"/"+month+"/"+year);
                    System.out.println(day+"/"+month+"/"+year);
                    System.out.println(foo2+"-"+month+"-"+day);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                
                byte response[] = response_command_idcard.getData();
                for (int i=0; i < response.length; i++);
                System.out.print(response);// แก้ จาก System.out.print((Char)response);
                System.out.println();

                // Disconnect
                // true: reset the card after disconnecting card.
                card.disconnect(true);

        }
        catch(CardException ex)  {
        }
    }    
}