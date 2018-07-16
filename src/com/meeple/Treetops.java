package com.meeple;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.meeple.main.generate.FractalLandscape;
import com.meeple.main.generate.FractalLandscape2D;

public class Treetops {
	//Main classes
	BufferedImage image;
	JLabel label;
	static long start = System.currentTimeMillis();

	public static void println(Object s) {
		System.out.println(s + " (" + new DecimalFormat("#.##").format((double) (System.currentTimeMillis() - start) / 1000) + " seconds from start)");
	}

	public Treetops() {

		int w = 300, h = 300;
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		JFrame frame = new JFrame("Test");
		Random random = new Random(1);
		


		println("Starting fractal");
		FractalLandscape fl = new FractalLandscape(w, h,random, 7);
		println("Starting fractal generate");
		fl.generate();
		println("finish fractal generate");
		fl.print();

		
		FractalLandscape2D fl2 = new FractalLandscape2D(w, 10, random);
		fl2.generate();
		image = (BufferedImage) fl2.print();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(0, 0, 400, 400);
		frame.setVisible(true);
		frame.add(label = new JLabel(new ImageIcon(image)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.clearRect(0, 0, frame.getWidth(), frame.getHeight());
				g.drawImage(image, 0, 0, null);
			}

		});
		frame.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				fl2.generate();
				image = (BufferedImage) fl2.print();
				frame.repaint();

			}
		});
		//js= new JuliaSet(zoom);

	}

	public static void main(String[] args) {
		new Treetops();
	}
}
