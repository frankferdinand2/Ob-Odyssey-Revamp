import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseMotionAdapter;

/*
 * This class represents the 'graphical user interface' or 'presentation' layer or 'frame'. Its job is to continuously 
 * read input from the user (i.e. keyboard, mouse) and to render a universe or 'logical' layer. Also, it
 * continuously prompts the logical layer to update itself based on the number of milliseconds that have elapsed.
 * 
 * The presentation layer generally does not try to affect the logical layer; most information
 * passes "upwards" from the logical layer to the presentation layer.
 */

public class AnimationFrame extends JFrame {

	final public static int FRAMES_PER_SECOND = 60;
	protected long REFRESH_TIME = 1000 / FRAMES_PER_SECOND;	//MILLISECONDS

	protected static int SCREEN_HEIGHT = 720;
	protected static int SCREEN_WIDTH = 1280;

	//These variables control where the screen is centered in relation to the logical center of universe.
	//Generally it makes sense to have these start at half screen width and height, so that the logical
	//center is rendered in the center of the screen. Changing them will 'pan' the screen.
	protected int screenOffsetX = SCREEN_WIDTH / 2;
	protected int screenOffsetY = SCREEN_HEIGHT / 2;

	protected boolean SHOW_GRID = false;
	protected boolean DISPLAY_TIMING = false;
	
	//scale at which to render the universe. When 1, each logical unit represents 1 pixel in both x and y dimension
	protected double scale = 1;
	//point in universe on which the screen will center
	protected double logicalCenterX = 0;		
	protected double logicalCenterY = 0;

	//basic controls on interface... these are protected so that subclasses can access
	protected JPanel panel = null;
	protected JButton btnPauseRun;
	protected JLabel lblTop;
	protected JLabel lblBottom;

	protected static boolean stop = false;

	protected long total_elapsed_time = 0;
	protected long lastRefreshTime = 0;
	protected long deltaTime = 0;
	protected boolean isPaused = false;

	protected KeyboardInput keyboard = KeyboardInput.getKeyboard();
	protected Universe universe = null;

	//local (and direct references to various objects in universe ... should reduce lag by avoiding dynamic lookup
	protected Animation animation = null;
	protected ArrayList<DisplayableSprite> sprites = null;
	protected ArrayList<Background> backgrounds = null;
	protected Background background = null;
	
	/*
	 * Much of the following constructor uses a library called Swing to create various graphical controls. You do not need
	 * to modify this code to create an animation, but certainly many custom controls could be added.
	 */
	public AnimationFrame(Animation animation)
	{
		super("");
		getContentPane().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				thisContentPane_mousePressed(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				thisContentPane_mouseReleased(e);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				contentPane_mouseExited(e);
			}
		});
		
		this.animation = animation;
		this.setVisible(true);		
		this.setFocusable(true);
		this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				this_windowClosing(e);
			}
		});

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				keyboard.keyPressed(arg0);
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
				keyboard.keyReleased(arg0);
			}
		});
		getContentPane().addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				contentPane_mouseMoved(e);
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				contentPane_mouseMoved(e);
			}
		});

		Container cp = getContentPane();
		cp.setBackground(Color.BLACK);
		cp.setLayout(null);

		panel = new AnimationPanel();
		panel.setLayout(null);
		panel.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		getContentPane().add(panel, BorderLayout.CENTER);

		btnPauseRun = new JButton("||");
		btnPauseRun.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				btnPauseRun_mouseClicked(arg0);
			}
		});

		btnPauseRun.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnPauseRun.setBounds(SCREEN_WIDTH - 64, 20, 48, 32);
		btnPauseRun.setFocusable(false);
		getContentPane().add(btnPauseRun);
		getContentPane().setComponentZOrder(btnPauseRun, 0);

		lblTop = new JLabel(""); 
		lblTop.setForeground(Color.BLACK);
		lblTop.setFont(new Font("Consolas", Font.BOLD, 20));
		lblTop.setBounds(16, 22, SCREEN_WIDTH - 16, 30);
		getContentPane().add(lblTop);
		getContentPane().setComponentZOrder(lblTop, 0);

		lblBottom = new JLabel("");
		lblBottom.setForeground(Color.BLACK);
		lblBottom.setFont(new Font("Consolas", Font.BOLD, 30));
		lblBottom.setBounds(16, SCREEN_HEIGHT - 30 - 16, SCREEN_WIDTH - 16, 36);
		lblBottom.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblBottom);
		getContentPane().setComponentZOrder(lblBottom, 0);

	}

	public void start()
	{
		Thread thread = new Thread()
		{
			public void run()
			{
				animationLoop();
				System.out.println("run() complete");
			}
		};

		thread.start();
		animationInitialize();
				
		System.out.println("main() complete");

	}

	protected void animationInitialize() {
		
	}
	
	protected void animationStart() {		
	}
	
	protected void universeSwitched() {
	}
	
	protected void animationEnd() {
		
	}

	private void setLocalObjectVariables() {
		universe = animation.getCurrentUniverse();
		sprites = universe.getSprites();
		backgrounds = universe.getBackgrounds();
		this.scale = universe.getScale();
	}
	
	protected void paintAnimationPanel(Graphics g) {
		
	}
	
	private void animationLoop() {

		lastRefreshTime = System.currentTimeMillis();		
		universe = animation.getCurrentUniverse();

		animationStart();
		
		while (stop == false && animation.isComplete() == false) {
			
			universeSwitched();
			animation.acknowledgeUniverseSwitched();			
			setLocalObjectVariables();
			keyboard.reset();
			keyboard.poll();
			
			while (stop == false && animation.isComplete() == false && universe.isComplete() == false && animation.getUniverseSwitched() == false) {
				
				long target_wake_time = System.currentTimeMillis() + REFRESH_TIME;
				while (System.currentTimeMillis() < target_wake_time)
				{
					Thread.yield();
					try {
						Thread.sleep(1);
					}
					catch(Exception e) {    					
					} 
				}

				deltaTime = (isPaused ? 0 : System.currentTimeMillis() - lastRefreshTime);
				lastRefreshTime = System.currentTimeMillis();
				total_elapsed_time += deltaTime;
				
				keyboard.poll();
				handleKeyboardInput();

				animation.update(this, deltaTime);
				universe.update(animation, deltaTime);

				updateControls();
				this.logicalCenterX = universe.getXCenter();
				this.logicalCenterY = universe.getYCenter();
				MouseInput.logicalX = translateToLogicalX(MouseInput.screenX);
				MouseInput.logicalY = translateToLogicalY(MouseInput.screenY);

				this.repaint();

			}
			
		}

		System.out.println("animation complete");
		AudioPlayer.setStopAll(true);
		dispose();	

	}

	protected void updateControls() {
		if (universe != null) {
			this.lblBottom.setText("");
		}
	}

	protected void btnPauseRun_mouseClicked(MouseEvent arg0) {
		if (isPaused) {
			isPaused = false;
			this.btnPauseRun.setText("||");
		}
		else {
			isPaused = true;
			this.btnPauseRun.setText(">");
		}
	}

	private void handleKeyboardInput() {
		if (keyboard.keyDownOnce(KeyboardInput.KEY_T)) {
			this.DISPLAY_TIMING = !this.DISPLAY_TIMING;
		}
	}

	class AnimationPanel extends JPanel {

		public void paintComponent(Graphics g)
		{	
			if (universe == null) {
				return;
			}

			if (backgrounds != null) {
				for (Background background: backgrounds) {
					paintBackground(g, background);
				}
			}

			if (sprites != null) {
				for (DisplayableSprite activeSprite : sprites) {
					DisplayableSprite sprite = activeSprite;
					if (sprite.getVisible()) {
						if (sprite.getImage() != null) {
							g.drawImage(sprite.getImage(),
								translateToScreenX(sprite.getMinX()),
								translateToScreenY(sprite.getMinY()),
								scaleLogicalX(sprite.getWidth()),
								scaleLogicalY(sprite.getHeight()),
								null);
						}
						else {
							g.setColor(Color.BLUE);
							g.fillRect(
								translateToScreenX(sprite.getMinX()),
								translateToScreenY(sprite.getMinY()),
								scaleLogicalX(sprite.getWidth()),
								scaleLogicalY(sprite.getHeight()));
						}
					}
				}				
			}
			
			paintAnimationPanel(g);
		}
		
		private void paintBackground(Graphics g, Background background) {
			if ((g == null) || (background == null)) {
				return;
			}
			
			double logicalLeft = (logicalCenterX  - (screenOffsetX / scale) - background.getShiftX());
			double logicalTop =  (logicalCenterY - (screenOffsetY / scale) - background.getShiftY()) ;
						
			int row = background.getRow((int)(logicalTop - background.getShiftY() ));
			int col = background.getCol((int)(logicalLeft - background.getShiftX()  ));
			Tile tile = background.getTile(col, row);
			
			boolean rowDrawn = false;
			boolean screenDrawn = false;
			while (screenDrawn == false) {
				while (rowDrawn == false) {
					tile = background.getTile(col, row);
					if (tile.getWidth() <= 0 || tile.getHeight() <= 0) {
						g.setColor(Color.GRAY);
						g.fillRect(0,0, SCREEN_WIDTH, SCREEN_HEIGHT);					
						rowDrawn = true;
						screenDrawn = true;						
					}
					else {
						Tile nextTile = background.getTile(col+1, row+1);
						int width = translateToScreenX(nextTile.getMinX()) - translateToScreenX(tile.getMinX());
						int height = translateToScreenY(nextTile.getMinY()) - translateToScreenY(tile.getMinY());
						g.drawImage(tile.getImage(),
							translateToScreenX(tile.getMinX() + background.getShiftX()),
							translateToScreenY(tile.getMinY() + background.getShiftY()),
							width, height, null);
					}					
					if (translateToScreenX(tile.getMinX() + background.getShiftX() + tile.getWidth()) > SCREEN_WIDTH || tile.isOutOfBounds()) {
						rowDrawn = true;
					}
					else {
						col++;
					}
				}
				if (translateToScreenY(tile.getMinY() + background.getShiftY() + tile.getHeight()) > SCREEN_HEIGHT || tile.isOutOfBounds()) {
					screenDrawn = true;
				}
				else {
					col = background.getCol(logicalLeft);
					row++;
					rowDrawn = false;
				}
			}
		}				
	}

	protected int translateToScreenX(double logicalX) {
		return screenOffsetX + scaleLogicalX(logicalX - logicalCenterX);
	}		
	protected int scaleLogicalX(double logicalX) {
		return (int) Math.round(scale * logicalX);
	}
	protected int translateToScreenY(double logicalY) {
		return screenOffsetY + scaleLogicalY(logicalY - logicalCenterY);
	}		
	protected int scaleLogicalY(double logicalY) {
		return (int) Math.round(scale * logicalY);
	}

	protected double translateToLogicalX(int screenX) {
		double offset = screenX - screenOffsetX;
		return (offset / scale) + (universe != null ? universe.getXCenter() : 0);
	}
	protected double translateToLogicalY(int screenY) {
		double offset = screenY - screenOffsetY ;
		return (offset / scale) + (universe != null ? universe.getYCenter() : 0);		
	}
	
	protected void contentPane_mouseMoved(MouseEvent e) {
		Point point = this.getContentPane().getMousePosition();
		if (point != null) {
			MouseInput.screenX = point.x;		
			MouseInput.screenY = point.y;
			MouseInput.logicalX = translateToLogicalX(MouseInput.screenX);
			MouseInput.logicalY = translateToLogicalY(MouseInput.screenY);
		}
		else {
			MouseInput.screenX = -1;		
			MouseInput.screenY = -1;
			MouseInput.logicalX = Double.NaN;
			MouseInput.logicalY = Double.NaN;
		}
	}
	
	protected void thisContentPane_mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			MouseInput.leftButtonDown = true;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			MouseInput.rightButtonDown = true;
		}
	}
	protected void thisContentPane_mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			MouseInput.leftButtonDown = false;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			MouseInput.rightButtonDown = false;
		}
	}

	protected void this_windowClosing(WindowEvent e) {
		System.out.println("windowClosing()");
		stop = true;
		dispose();	
	}
	protected void contentPane_mouseExited(MouseEvent e) {
		contentPane_mouseMoved(e);
	}
}
