package com.qbanalytix.cognostest.common;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import com.ganesha.desktop.component.XJPanel;

public class ImagePanel extends XJPanel {
	private static final long serialVersionUID = 5;
	private BufferedImage bufferedImage;

	public void showImage(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
		setSize(getWidth(), getHeight());
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(bufferedImage, 0, 0, null);
	}

	public void setSize(int newW, int newH) {
		if (bufferedImage == null) {
			super.setSize(newW, newH);
		} else {
			Image tmp = bufferedImage.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
			BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

			Graphics2D g2d = dimg.createGraphics();
			g2d.drawImage(tmp, 0, 0, null);
			g2d.dispose();

			bufferedImage = dimg;
		}
	}
}
