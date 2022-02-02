import javax.swing.*;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener {

	static final int SCREEN_WIDTH = 1280; //Szeroko�� okna gry
	static final int SCREEN_HEIGHT = 720; //Wysoko�� okna gry
	static final int UNIT_SIZE = 20; //Wielko�� jednostki u�ywanej w grze
	static final int GAME_UNITS = ((1000*640)/UNIT_SIZE); //Jednostki u�ywane w grze
	static final int DELAY = 110; //Op�nienie, wp�ywa na pr�dko�� gry
	final int x[] = new int[GAME_UNITS]; //
	final int y[] = new int[GAME_UNITS];
	int BodyParts = 3; //Liczba segment�w w�a
	int FoodEaten = 0; //Ilo�� zdobytego jedzenia
	int FoodX; //Po�o�enie jedzenia w osi X
	int FoodY; //Po�o�enie jedzenia w osi Y
	int DifficultyLevel = 3; //Poziom trudno�ci
	int Score; //Wynik
	int SnakeColor = 2; //Zmienna odpowiadaj�ca za kolor w�a
	Color SnakeHeadColor = new Color(0,255,0); //Zmienna przechowuj�ca kolor g�owy w꿹
	Color SnakeBodyColor = new Color(0,128,0); //Zmienna przechowuj�ca kolor cia�a w꿹
	char Direction = 'R'; //Strona w kt�r� zwr�cony jest w��, domy�lnie w prawo
	boolean Running = false; //Zmienna decyduj�ca czy gra trwa
	Timer time; //Zegar
	Random rand; //Zmienna losowa
	//Konstruktor panelu z gr�
	GamePanel() {
		rand = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new KeyListener());
		startGame(DifficultyLevel);
	}
	//Zmienna rozpoczynaj�ca rozgrywk�
	public void startGame(int DiffLevel){
		newFood(); //Generowanie nowych koordynat�w dla jedzenia
		Running = true; //Rozpoczynanie rozgrywki
		time = new Timer(DELAY-(DiffLevel*15), this); //Ustawienie zegara
		time.start(); //Uruchomienie zegara
	}
	
	public void paintComponent(Graphics G) {
		super.paintComponent(G);
		drawComponent(G);
	}
	//Funkcja rysuj�ca interfejs gry
	public void drawComponent(Graphics G) {
		
		if(Running) {
			//G�rna granica
			G.setColor(new Color(78, 90, 101));
			G.drawLine(20, 20, 1020, 20);
			//Dolna granica
			G.setColor(new Color(78, 90, 101));
			G.drawLine(20, 660, 1020, 660);
			//Lewa granica
			G.setColor(new Color(78, 90, 101));
			G.drawLine(20, 20, 20, 660);
			//Prawa granica
			G.setColor(new Color(78, 90, 101));
			G.drawLine(1020, 20, 1020, 660);
			
			//Licznik punkt�w
			G.setColor(Color.yellow);
			G.setFont(new Font("SansSerif", Font.BOLD, 24));
			G.drawString("Wynik: "+Score, 20, 690);
			//Legenda dotycz�ca sterowania
			G.drawString("Sterowanie: ", 1030, 40);
			G.setFont(new Font("SansSerif", Font.PLAIN, 16));
			G.drawString("Strza�ki = Ruch w�em", 1030, 80);
			G.drawString("Spacja = Pauza", 1030, 120);
			G.drawString("X = Zwi�kszenie trudno�ci", 1030, 160);
			G.drawString("Z = Zmniejszenie trudno�ci", 1030, 200);
			G.drawString("Enter = Restart gry", 1030, 240);
			G.drawString("Escape = Wy��czenie gry", 1030, 280);
			G.drawString("S = Zmiana koloru w�a", 1030, 320);
			//Aktualny poziom trudno�ci
			G.drawString("Poziom trudno�ci:"+DifficultyLevel,1140, 715);

			
			if(FoodEaten%9==0 && FoodEaten != 0) {
				//Wy�wietlanie specjalnego jedzenia
				G.setColor(Color.yellow);
				G.fillOval(FoodX, FoodY, UNIT_SIZE, UNIT_SIZE);
			}
			else {
				//Wy�wietlanie zwyk�ego jedzenia
				G.setColor(Color.red);
				G.fillOval(FoodX, FoodY, UNIT_SIZE, UNIT_SIZE);
			}
			
			for(int i=0; i<BodyParts; i++) {
				if(i!=0) {
					//Wy�wietlanie cia�a w꿹
					G.setColor(SnakeBodyColor);
					G.fillRect(x[i]+20, y[i]+20, UNIT_SIZE, UNIT_SIZE);
				}
				else {
					//Wy�wietlanie g�owy w�a
					G.setColor(SnakeHeadColor);
					G.fillRect(x[i]+20, y[i]+20, UNIT_SIZE, UNIT_SIZE);
				}
			}
		}
		else {
			//Wy�wietlanie interfejsu ko�ca rogrywki po przegraniu
			gameOver(G);
		}
		
	}
	//Funkcja generuj�ca koordynaty dla jedzenia
	public void newFood() {
			FoodX = rand.nextInt((int)(1000/UNIT_SIZE))*UNIT_SIZE+20;
			FoodY = rand.nextInt((int)((640)/UNIT_SIZE))*UNIT_SIZE+20;
	}
	//Funkcja odpowiedzialna za ruch w�a
	public void move() {
		//P�tla iteruj�ca przez wszystkie segmenty w�a, aby pod��a�y za g�ow�
		for(int i=BodyParts;i>0;i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		//Segment odpowiedzialny za ruch w�a w danym kierunku
		switch(Direction) {
		case 'U': 
			y[0] = y[0] - UNIT_SIZE; break;
		case 'D': 
			y[0] = y[0] + UNIT_SIZE; break;
		case 'L': 
			x[0] = x[0] - UNIT_SIZE; break;
		case 'R': 
			x[0] = x[0] + UNIT_SIZE; break;
		}
	}
	//Funkcja sprawdzaj�ca czy jedzenie zosta�o zjedzone
	public void checkFood() {
		if((x[0]+20 == FoodX) && (y[0]+20 == FoodY)) {
			BodyParts++;
			//Co dziesi�te jedzenie jest jedzeniem specjalnym, daj�cym bonusowe punkty
			if(FoodEaten % 9 == 0 && FoodEaten != 0)
				Score = Score + (int)(Score*(0.1*DifficultyLevel));
			else
				Score = Score + DifficultyLevel;
			FoodEaten++;
			newFood();
		}
	}
	
	public void checkCollisions() {
		//Sprawdza czy nast�pi�a kolizja g�owy w�a z jego cia�em
		for(int i=BodyParts; i>0; i--) {
			if((x[0]==x[i]) && (y[0]==y[i])){
				Running = false;
			}
		}
		//Sprawdza czy nast�pi�a kolizja g�owy w�a z lew� granic�
		if(x[0]+20 < 20)
			Running = false;
		//Sprawdza czy nast�pi�a kolizja g�owy w�a z praw� granic�
		if(x[0]+20 > 1000)
			Running = false;
		//Sprawdza czy nast�pi�a kolizja g�owy w�a z g�rn� granic�
		if(y[0]+20 < 20)
			Running = false;
		//Sprawdza czy nast�pi�a kolizja g�owy w�a z doln� granic�
		if(y[0]+20 > 640)
			Running = false;
	
		if(!Running)
			time.stop();
	}
	//Funkcja wy�wietlaj�ca interfejsu ko�ca gry
	public void gameOver(Graphics G) {
		G.setColor(Color.yellow);
		G.setFont(new Font("SansSerif", Font.BOLD, 75));
		FontMetrics mtx1 = getFontMetrics(G.getFont());
		G.drawString("Koniec Gry", (SCREEN_WIDTH - mtx1.stringWidth("Koniec Gry"))/2, SCREEN_HEIGHT/2-120);
		G.setColor(Color.red);
		G.setFont(new Font("SansSerif", Font.BOLD, 40));
		FontMetrics mtx2 = getFontMetrics(G.getFont());
		G.drawString("Wynik: "+Score, (SCREEN_WIDTH - mtx2.stringWidth("Wynik: "+Score))/2, (SCREEN_HEIGHT/2-60));
		G.setColor(Color.yellow);
		G.setFont(new Font("SansSerif", Font.BOLD, 40));
		FontMetrics mtx3 = getFontMetrics(G.getFont());
		G.drawString("Wci�nij enter aby zagra� ponownie", (SCREEN_WIDTH - mtx3.stringWidth("Wci�nij enter aby zagra� ponownie"))/2, (SCREEN_HEIGHT/2));
		G.setFont(new Font("SansSerif", Font.BOLD, 28));
		FontMetrics mtx4 = getFontMetrics(G.getFont());
		G.drawString("Wci�nij ESC aby Wyj��", (SCREEN_WIDTH - mtx4.stringWidth("Wci�nij ESC aby Wyj��"))/2, (SCREEN_HEIGHT/2+60));
		G.setFont(new Font("SansSerif", Font.BOLD, 28));
	}
	//Funkcja steruj�ca gr�
	public void actionPerformed(ActionEvent e) {
		if(Running) {
			move();
			checkFood();
			checkCollisions();
		}
		repaint();
	}
	//Funkcja odpowiadaj�ca za kolor w�a
	public void SnakeColor(int SC) {
		
		switch(SC) {
			case 1:
				SnakeHeadColor = new Color(255,0,0);
				SnakeBodyColor = new Color(128,0,0);
			break;
			case 2:
				SnakeHeadColor = new Color(0,255,0);
				SnakeBodyColor = new Color(0,128,0);
			break;
			case 3:
				SnakeHeadColor = new Color(0,0,255);
				SnakeBodyColor = new Color(0,0,128);
			break;
			case 4:
				SnakeHeadColor = new Color(255,255,0);
				SnakeBodyColor = new Color(128,128,0);
			break;
			case 5:
				SnakeHeadColor = new Color(255,0,255);
				SnakeBodyColor = new Color(128,0,128);
			break;
			case 6:
				SnakeHeadColor = new Color(0,255,255);
				SnakeBodyColor = new Color(0,128,128);
			break;
			case 7:
				SnakeHeadColor = new Color(255,255,255);
				SnakeBodyColor = new Color(128,128,128);
			break;
		}
	}
	//Funkcja restartuj�ca gr� w przypadku klikni�cia przycisku enter
	public void GameRestart() {
		BodyParts = 3;
		FoodEaten = 0;
		DifficultyLevel = 3;
		Score = 0;
		SnakeColor = 2;
		Direction = 'R';
		x[0] = 0;
		y[0] = 0;
		newFood();
		Running = true;
		time.stop();
		time.setDelay(DELAY-(DifficultyLevel*15));
		time.start();
	}
	//Klasa odpowiedzialna wprowadzenie danych z klawiatury
	//i przypisanie im odpowiednich akcji
	public class KeyListener extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent E) {
			switch(E.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if(Direction != 'R')
					Direction = 'L';
			break;
			
			case KeyEvent.VK_RIGHT:
				if(Direction != 'L')
					Direction = 'R';
			break;
			
			case KeyEvent.VK_UP:
				if(Direction != 'D')
					Direction = 'U';
			break;
			
			case KeyEvent.VK_DOWN:
				if(Direction != 'U')
					Direction = 'D';
			break;
			
			case KeyEvent.VK_SPACE:
				if(time.isRunning())
					time.stop();
				else
					time.start();
			break;
			
			case KeyEvent.VK_Z:
				if(DifficultyLevel>1) {
					DifficultyLevel-=1;
					time.setDelay(110-(DifficultyLevel*15));
					time.restart();
				}
			break;
			
			case KeyEvent.VK_X:
				if(DifficultyLevel<5) {
					DifficultyLevel+=1;
					time.setDelay(110-(DifficultyLevel*15));
					time.restart();
				}
			break;
			
			case KeyEvent.VK_S:
				SnakeColor+=1;
				if(SnakeColor==8)
					SnakeColor=1;
				SnakeColor(SnakeColor);
			break;
			
			case KeyEvent.VK_ENTER:
				GameRestart();
			break;
			
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
			break;
			
			}
		}
	}
	
}
