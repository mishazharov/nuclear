package logic;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import graphics.*;
import main.Main;
import objects.ControlRodBundle;
import objects.GameObject;
import objects.Pipe;
import objects.Pipe.Orientation;
import objects.Plant;
import objects.PowerProduction;
import objects.Reactor;

public class Integrator {
	
	private static boolean clicked = false;
	private static int tutorialStep = 0;
	private long tutorialTime = 0;
	
	private static int level = 1;
	public static boolean running = true;//If false, the game quits
	public static boolean paused = false;
	private long last;//The long which stores the time in milliseconds at the last update
	public static int x;//The size of the screen
	public static int y;//The size of the screen
	private int x_offset = 0;//The horizontal offset of the gameworld (not UI though)
	private int y_offset = -300;//The horizontal offset of the gameworld (not UI though), -300 to start with the image in an ideal location
	public static float scale = 0.6f; // The scale of the game (Hopefully not UI though)
	public static Canvas canvas;//Canvas component
	private BufferStrategy buffer;//Buffer for drawing and creating graphics
	private Graphics2D g;//This object will be used to draw
	private static ArrayList<GameObject> objects = new ArrayList<GameObject>();//This is where all the game objects will be stored
	private static ArrayList<UIComponent> ui = new ArrayList<UIComponent>();//This is where all the game objects will be stored
	private boolean active(){
		for(UIComponent temp:ui){
			if(temp.getActive())
				return true;
		}
		return false;
	}
	public static void add(UIComponent ui1){
		canvas.addKeyListener(ui1.key);
		canvas.addMouseListener(ui1.mouse);
		canvas.addMouseMotionListener(ui1.mouse);
		ui.add(ui1);
	}
	public static void add(GameObject ob){
		ArrayList<UIComponent> all = ob.getUi();
		for(UIComponent temp:all){
			add(temp);
		}
		objects.add(ob);
		
		ArrayList<GameObject> allObj = ob.getObj();
		allObj.remove(0);
		if(allObj.size() != 0){
			for(GameObject temp:allObj){
				add(temp);
			}
		}
	}
	public void offset_x(int i){
		if(!active())
		x_offset+=i;
	}
	public void offset_y(int i){
		if(!active())
		y_offset+=i;
	}
	public void zoom(float i){
		if(scale - i < 0){
			scale = 0;
			return;
		}
		scale -= i;
	}
	int scroll = 40;//This controls the scroll speed
	float zoom_factor = 0.01f;//Zoom speed
	public static int int_last_x_offset, int_last_y_offset;
	public void start(){
		running = true;
		//Making level below
		Plant plant = new Plant("res/chernobyl.jpg");//Creates a plant
		Reactor reactor4 = new Reactor(705, 240, 150, 320, "4");
		Reactor reactor3 = new Reactor(1345, 240, 150, 320, "3");
		Reactor reactor2 = new Reactor(2850, 195, 170, 350, "2");
		Reactor reactor1 = new Reactor(3945, 195, 170, 350, "1");
		reactor4.addObj(new ControlRodBundle(736, 357, 90));
		reactor3.addObj(new ControlRodBundle(1378, 357, 90));
		reactor2.addObj(new ControlRodBundle(2890, 418, 90));
		reactor1.addObj(new ControlRodBundle(3985, 418, 90));
		plant.addObj(reactor1);
		plant.addObj(reactor2);
		plant.addObj(reactor3);
		plant.addObj(reactor4);
		if(level != 1){
			add(plant);//Adds the plant to the world array so it can be rendered
		}
		//Making the level above
		
		//PIPES BELOW
		//Pipes are just for aesthetics, do not include them in physics
		Pipe pipe = new Pipe(840, 560, Orientation.VERTICAL, 300, 10);
		pipe.setColor(Color.blue);
		
		
		Pipe pipe1 = new Pipe(715, 560, Orientation.VERTICAL, -300, 10);
		pipe1.setColor(Color.blue);
		
		Pipe pipe2 = new Pipe(777, 937, Orientation.VERTICAL, 172, 10);
		
		Pipe pipe3 = new Pipe(200, 937, Orientation.VERTICAL, 172, 10);
		
		Pipe pipe4 = new Pipe(200, 937, Orientation.VERTICAL, 172, 10);
		
		Pipe mainElectric = new Pipe(200, 1100, Orientation.HORIZONTAL, 4700, 10);
		
		if(level != 1){
			add(pipe);
			add(pipe1);
			add(pipe2);
			add(pipe3);
			add(pipe4);
			add(mainElectric);
		}
		//PIPES ABOVE
		
		
		//Making paused GUI below
		UIText pauseText = new UIText(x/2-x/8, y/5, x/4, UIComponent.defaultHeight);
		pauseText.setText("Paused");
		pauseText.setVisible(false);
		
		
		UIButton quitButton = new UIButton(x/2-x/8, y/5+UIComponent.defaultHeight+10, x/4, UIComponent.defaultHeight);
		quitButton.setText("Quit");
		quitButton.setVisible(false);
		quitButton.setUsableDuringPaused(true);
		
		
		UIButton mainButton = new UIButton(x/2-x/6, y/5+UIComponent.defaultHeight*2+20, x/3, UIComponent.defaultHeight);
		mainButton.setText("Main Menu");
		mainButton.setVisible(false);
		mainButton.setUsableDuringPaused(true);
		
		//Making paused GUI above
		
		//UI below
		PowerProduction powerDisplay = new PowerProduction(10, 10);
		if(level != 1){
			add(powerDisplay);
		}
		//Tutorial UI below
		UIText tutorial = new UIText(10, 10, x-30, 100);
		tutorial.setTextDisplacement(25, 74);
		tutorial.setMovable(false);
		tutorial.setFontSize(50);
		tutorial.setText("Welcome to Nuclear Reactor simulator (click to continue)");
		
		UIButton tutButton = new UIButton(x/2-150, y/2, 300, 100);
		tutButton.setText("Button");
		tutButton.setVisible(false);

		UISlider tutSlider = new UISlider(x/2-150, y/2, 300, 100);
		tutSlider.setText("Slider");
		tutSlider.setFontSize(55);
		tutSlider.setTextDisplacement(25, 75);
		tutSlider.setVisible(false);
		if(level == 1){
			add(tutButton);
			add(tutSlider);
			add(tutorial);
		}
		//Tutorial UI above
		//UI above
		
		quitButton.setMovable(false);
		mainButton.setMovable(false);
		add(pauseText);
		add(quitButton);
		add(mainButton);
		canvas.createBufferStrategy(2);//Enables double buffering
		buffer = canvas.getBufferStrategy();//Initializes the buffer
		g = (Graphics2D) buffer.getDrawGraphics();//Gets the graphics object
		
		RenderingHints rh = new RenderingHints(//Turns ANTIALIASING on
	             RenderingHints.KEY_ANTIALIASING,
	             RenderingHints.VALUE_ANTIALIAS_ON);
		canvas.requestFocusInWindow();//Makes sure that the canvas will receive clicks
		MouseAdapter mouse = new MouseAdapter(){
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {//Adds the scrolling action listener
				if(e.getWheelRotation()>0){
					zoom(zoom_factor);
				}else if(e.getWheelRotation()<0){
					zoom(-zoom_factor);
				}
			}
			public void mouseClicked(MouseEvent e){
				clicked = true;
				if(!paused){
					for(GameObject temp: objects){
						temp.mouseClicked(e);
					}
				}
			}
	    };
		canvas.addMouseWheelListener(mouse);
		canvas.addMouseListener(mouse);
	    canvas.addKeyListener(new KeyListener(){//Adds the arrow action listener
			@Override
			public void keyPressed(KeyEvent e) {
				if(!paused && e.getKeyCode() == KeyEvent.VK_RIGHT || !paused && e.getKeyCode() == KeyEvent.VK_D){
					if(x_offset > -3500){
						offset_x(-scroll);
					}
				}
				if(!paused && e.getKeyCode() == KeyEvent.VK_LEFT || !paused && e.getKeyCode() == KeyEvent.VK_A){
					if(x_offset < 100){
						offset_x(scroll);
					}
				}
				if(!paused && e.getKeyCode() == KeyEvent.VK_UP || !paused && e.getKeyCode() == KeyEvent.VK_W){
					if(y_offset < 300){
						offset_y(scroll);
					}
				}
				if(!paused && e.getKeyCode() == KeyEvent.VK_DOWN || !paused && e.getKeyCode() == KeyEvent.VK_S){
					if(y_offset > -400){
						offset_y(-scroll);
					}
				}
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
					paused = !paused;
				}
				if(!paused){
					for(GameObject temp: objects){
						temp.keyPressed(e);
					}
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {}//These are required, but not used
			@Override
			public void keyTyped(KeyEvent arg0) {}//These are required, but not used
	    });
	    int last_x_offset = x_offset;//This is for interpolation purposes, initially sets the values as the same
	    int last_y_offset = x_offset;//This is for interpolation purposes, initially sets the values as the same
	    last = System.currentTimeMillis();//Sets the last time to the start time
	    long now = System.currentTimeMillis();//Sets the now time so it is not null or 0
	    long deltaTime = System.currentTimeMillis();//Sets the delta time so it is not null or 0
	    long dt = 10;//This is the time in milliseconds at which the game will process physics, 100 milliseconds at a time
		Font defaultFont = new Font("TimesRoman", Font.PLAIN, 15);
		AffineTransform old;
		float c = 0;
	    while(running){

			g.setColor(Color.white);//Clears the screen
			g.fillRect(0,0,x,y);
			g = (Graphics2D) buffer.getDrawGraphics();
			g.setRenderingHints(rh);
			g.setFont(defaultFont);//Sets font
			now = System.currentTimeMillis();//Sets now
			deltaTime += now - last;//Calculates time since last update
			last = System.currentTimeMillis();//Updates the last time
			if(deltaTime>25)deltaTime=25;//Makes sure that the physics does not try to do too much
			while(!paused && (deltaTime) >= dt){//As long as we still need to do physics updates, do physics updates
				//Do physics here
				for(GameObject temp: objects){
					temp.update(deltaTime);
				}
				//DO NOT RENDER ANYWHERE IN THIS WHILE LOOP
				
				deltaTime-=dt;//Counts down the time that needs to be processed
			}
			//ONLY RENDER PAST THIS POINT
			old = g.getTransform();
			
			if(!paused){
				c = deltaTime/(float)dt;//Calculates a time which will be used for linear interpolation
			}
			int_last_x_offset = (int) (x_offset * c + (1-c) * last_x_offset);//Does the linear interpolation
			int_last_y_offset = (int) (y_offset * c + (1-c) * last_y_offset);//Does the linear interpolation
			last_x_offset = int_last_x_offset;//Updates last time for interpolation
			last_y_offset = int_last_y_offset;//Updates last time for interpolation
			g.translate(x/2.0, y/2.0);
			g.scale(scale, scale);
			g.translate(-x/2.0, -y/2.0);
			
			
			for(GameObject temp : objects){
				temp.drawObj(g);
				g.setColor(Color.black); //individual objects that set the color will permanently change it, so we have to reset it to black
			}
			g.setTransform(old);
			powerDisplay.updatePower(reactor1.powerGeneration(), reactor2.powerGeneration(), reactor3.powerGeneration(), reactor4.powerGeneration());
			if(paused){
				pauseText.setVisible(true);
				quitButton.setVisible(true);
				mainButton.setVisible(true);
			}else{
				quitButton.setVisible(false);
				pauseText.setVisible(false);
				mainButton.setVisible(false);
			}
			for(UIComponent temp : ui){
				if(temp.getVisible())
				temp.drawObj(g);//Draws the thing
			}
			buffer.show();//Shows the picture
			if(quitButton.clicked)System.exit(0);
			if(mainButton.clicked){
				clear();
				Main.resume();
			}
			if(level == 1){
				switch(tutorialStep){
				case 0:
					if(clicked){
						tutorialStep++;
						tutorial.setText("Here you will learn the basics of the game (click)");
						clicked = false;
					}
					break;
				case 1:
					if(clicked){
						tutorialStep++;
						clicked = false;
					}
					break;
				case 2:
					tutButton.setVisible(true);
					tutorial.setText("This is a button (click)");
					if(clicked){
						tutorialStep++;
						clicked = false;
						tutorial.setText("It can be dragged around like most components (wait)");
						if(tutorialTime == 0){
							tutorialTime = System.currentTimeMillis();
						}
						tutButton.clicked = false;
					}
					break;
				case 3:
					if(tutorialTime + 10000 < System.currentTimeMillis()){
						tutorial.setText("Try clicking it (click it)");
						tutorialTime=0;
						if(tutButton.clicked)tutorialStep++;
						clicked = false;//This prevents the current click from setting off the next one
					}
					tutButton.clicked=false;
					break;
				case 4:
					tutorial.setText("Good. This is a slider (click)");
					tutButton.setVisible(false);
					tutSlider.setVisible(true);
					if(clicked){
						tutorialStep++;
						clicked = false;
						tutorial.setText("Click it to bring it into focus");
					}
					tutSlider.active = false;
					break;
				case 5:
					if(tutSlider.active){
						tutorial.setText("Now press '↓' to bring down the slider level to 0");
						tutorialStep++;
					}
					break;
				case 6:
					if(tutSlider.getPercentage() == 0){
						tutorial.setText("Now press '↑' to bring down the slider level to max");
						tutorialStep++;
					}
					break;
				case 7:
					if(tutSlider.getPercentage() == 1){
						tutorial.setText("Good");
						tutorialStep++;
					}
					break;
				}
			}
		}
		g.dispose();//Cleans the graphics (although this is not required)
	}
	public Integrator(int x1, int y1){//A constructor which takes the screen size
		canvas = new Canvas();//Initializes the canvas
		x=x1;
		y=y1;
	}
	public static void clear(){
		running = false;
		paused = false;
		scale = 0.6f;
		objects.clear();
		ui.clear();
	}
	public static void setLevel(int lvl) {
		level = lvl;
	}
}
