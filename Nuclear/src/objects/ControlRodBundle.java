package objects;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import logic.Integrator;

public class ControlRodBundle extends GameObject{
	static BufferedImage sprite = null;
	static BufferedImage red = null;
	static BufferedImage orange = null;
	static BufferedImage green = null;
	
	public ControlRodBundle(int x, int y, int size){
		xSize = ySize = size;
		bounds = new Rectangle(x, y, ySize, xSize);
		try {
			if(sprite==null){
				sprite = ImageIO.read(new File("res/control.jpg"));
				green = sprite.getSubimage(0, 0, xSize, ySize);
				orange = sprite.getSubimage(0, ySize, xSize, ySize);
				red = sprite.getSubimage(0, ySize*2, xSize, ySize);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.x=x;
		this.y=y;
	}
	public ControlRodBundle(){
		xSize = ySize = 15;
		bounds = new Rectangle(x, y, xSize, ySize);
		try {
			if(sprite==null){
				sprite = ImageIO.read(new File("res/control.jpg"));
				green = sprite.getSubimage(0, 0, xSize, ySize);
				orange = sprite.getSubimage(0, ySize, xSize, ySize);
				red = sprite.getSubimage(0, ySize*2, xSize, ySize);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public BufferedImage getImage(){
		return green;
	}
	public int getImageX(){
		return xSize + this.x;
	}
	public int getImageY(){
		return ySize + this.y;
	}
	public void mouseClicked(MouseEvent e){
		bounds.setLocation(x+Integrator.int_last_x_offset, y + Integrator.int_last_y_offset);
		AffineTransform g = new AffineTransform();
		g.translate(Integrator.x/2.0, Integrator.y/2.0);
		g.scale(Integrator.scale, Integrator.scale);
		g.translate(-Integrator.x/2.0, -Integrator.y/2.0);
		Shape temp = g.createTransformedShape(bounds);
		if(temp.contains((e.getX()), (e.getY()))){
			//Hit
			System.out.println("Clicked");
		}
		bounds.setLocation(x, y);
	}
	public void drawObj(Graphics2D g){
		g.drawImage(green, getX() + (Integrator.int_last_x_offset), getY() + Integrator.int_last_y_offset, getImageX() + Integrator.int_last_x_offset,  getImageY() + Integrator.int_last_y_offset, 0, 0, xSize, ySize, null);
	}
}