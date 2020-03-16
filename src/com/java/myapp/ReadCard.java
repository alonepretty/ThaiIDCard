package com.java.myapp;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.JButton;
import java.awt.Window;

public class ReadCard extends JFrame{
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				MyForm frame = new MyForm();
				frame.setVisible(true);
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public MyForm() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 458, 287);
		setTitle("ThaiCreate.Com GUI Tutorial");
		
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		JButton btn3 = new JButton("ตกลง");
		springLayout.putConstraint(SpringLayout.NORTH, btn3, 82, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, btn3, 132, SpringLayout.WEST, getContentPane());
		getContentPane().add(btn3);
	}

}
