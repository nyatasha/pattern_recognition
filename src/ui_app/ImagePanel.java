package ui_app;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.JLabel;

public class ImagePanel extends JLabel {

	private static final long serialVersionUID = 1L;
	int[] arr = new int[4];
	public ImagePanel(int type) {
		arr[0] = 0;
		arr[1] = 0;
		arr[2] = 0;
		arr[3] = 0;
		if(type == 1) {
			this.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					press = e.getPoint();
					remove = false;
				}
				public void mouseClicked(MouseEvent e) {
					removeRect();
				}
				public void mouseReleased(MouseEvent e) {

				}
			});
			this.addMouseMotionListener(new MouseAdapter() {
				public void mouseDragged(MouseEvent e) {
					double x, y, w, h;

					if (e.getX() > (int) press.getX()) {
						x = press.getX();
						w = (double) e.getX() - press.getX();
					} else {
						x = (double) e.getX();
						w = press.getX() - (double) e.getX();
					}

					if (e.getY() > (int) press.getY()) {
						y = press.getY();
						h = (double) e.getY() - press.getY();
					} else {
						y = (double) e.getY();
						h = press.getY() - (double) e.getY();
					}
					arr[0] = (int)x;
					arr[1] = (int)y;
					arr[2] = (int)w;
					arr[3] = (int)h;
					setRect(new Rectangle2D.Double(x, y, w, h));
					remove = false;
				}
			});
		}
	}
	public int getCoords(int i){
		return arr[i];
	}
	public void setRect(Rectangle2D rect) {
		this.rect = rect;
		repaint();
	}
	public void removeRect(){
		remove = true;
        repaint();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(!remove){
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.white);
			g2.draw(rect);			
			g2.dispose();
		}
	}
	private Rectangle2D rect = new Rectangle2D.Double();
	private Point2D press = new Point2D.Double(0, 0);
	private boolean remove = false;
}