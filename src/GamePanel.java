import javax.swing.*;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener {

	static final int SCREEN_WIDTH = 1280; //Szerokoœæ okna gry
	static final int SCREEN_HEIGHT = 720; //Wysokoœæ okna gry
	static final int UNIT_SIZE = 20; //Wielkoœæ jednostki u¿ywanej w grze
	static final int GAME_UNITS = ((1000*640)/UNIT_SIZE); //Jednostki u¿ywane w grze
	static final int DELAY = 110; //OpóŸnienie, wp³ywa na prêdkoœæ gry
	final int x[] = new int[GAME_UNITS]; //
	final int y[] = new int[GAME_UNITS];
	int BodyParts = 3; //Liczba segmentów wê¿a
	int FoodEaten = 0; //Iloœæ zdobytego jedzenia
	int FoodX; //Po³o¿enie jedzenia w osi X
	int FoodY; //Po³o¿enie jedzenia w osi Y
	int DifficultyLevel = 3; //Poziom trudnoœci
	int Score; //Wynik
	int SnakeColor = 2; //Zmienna odpowiadaj¹ca za kolor wê¿a
	Color SnakeHeadColor = new Color(0,255,0); //Zmienna przechowuj¹ca kolor g³owy wê¿¹
	Color SnakeBodyColor = new Color(0,128,0); //Zmienna przechowuj¹ca kolor cia³a wê¿¹
	char Direction = 'R'; //Strona w któr¹ zwrócony jest w¹¿, domyœlnie w prawo
	boolean Running = false; //Zmienna decyduj¹ca czy gra trwa
	Timer time; //Zegar
	Random rand; //Zmienna losowa
	//Konstruktor panelu z gr¹
	GamePanel() {
		rand = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new KeyListener());
		startGame(DifficultyLevel);
	}
	//Zmienna rozpoczynaj¹ca rozgrywkê
	public void startGame(int DiffLevel){
		newFood(); //Generowanie nowych koordynatów dla jedzenia
		Running = true; //Rozpoczynanie rozgrywki
		time = new Timer(DELAY-(DiffLevel*15), this); //Ustawienie zegara
		time.start(); //Uruchomienie zegara
	}
	
	public void paintComponent(Graphics G) {
		super.paintComponent(G);
		drawComponent(G);
	}
	//Funkcja rysuj¹ca interfejs gry
	public void drawComponent(Graphics G) {
		
		if(Running) {
			//Górna granica
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
			
			//Licznik punktów
			G.setColor(Color.yellow);
			G.setFont(new Font("SansSerif", Font.BOLD, 24));
			G.drawString("Wynik: "+Score, 20, 690);
			//Legenda dotycz¹ca sterowania
			G.drawString("Sterowanie: ", 1030, 40);
			G.setFont(new Font("SansSerif", Font.PLAIN, 16));
			G.drawString("Strza³ki = Ruch wê¿em", 1030, 80);
			G.drawString("Spacja = Pauza", 1030, 120);
			G.drawString("X = Zwiêkszenie trudnoœci", 1030, 160);
			G.drawString("Z = Zmniejszenie trudnoœci", 1030, 200);
			G.drawString("Enter = Restart gry", 1030, 240);
			G.drawString("Escape = Wy³¹czenie gry", 1030, 280);
			G.drawString("S = Zmiana koloru wê¿a", 1030, 320);
			//Aktualny poziom trudnoœci
			G.drawString("Poziom trudnoœci:"+DifficultyLevel,1140, 715);

			
			if(FoodEaten%9==0 && FoodEaten != 0) {
				//Wyœwietlanie specjalnego jedzenia
				G.setColor(Color.yellow);
				G.fillOval(FoodX, FoodY, UNIT_SIZE, UNIT_SIZE);
			}
			else {
				//Wyœwietlanie zwyk³ego jedzenia
				G.setColor(Color.red);
				G.fillOval(FoodX, FoodY, UNIT_SIZE, UNIT_SIZE);
			}
			
			for(int i=0; i<BodyParts; i++) {
				if(i!=0) {
					//Wyœwietlanie cia³a wê¿¹
					G.setColor(SnakeBodyColor);
					G.fillRect(x[i]+20, y[i]+20, UNIT_SIZE, UNIT_SIZE);
				}
				else {
					//Wyœwietlanie g³owy wê¿a
					G.setColor(SnakeHeadColor);
					G.fillRect(x[i]+20, y[i]+20, UNIT_SIZE, UNIT_SIZE);
				}
			}
		}
		else {
			//Wyœwietlanie interfejsu koñca rogrywki po przegraniu
			gameOver(G);
		}
		
	}
	//Funkcja generuj¹ca koordynaty dla jedzenia
	public void newFood() {
			FoodX = rand.nextInt((int)(1000/UNIT_SIZE))*UNIT_SIZE+20;
			FoodY = rand.nextInt((int)((640)/UNIT_SIZE))*UNIT_SIZE+20;
	}
	//Funkcja odpowiedzialna za ruch wê¿a
	public void move() {
		//Pêtla iteruj¹ca przez wszystkie segmenty wê¿a, aby pod¹¿a³y za g³ow¹
		for(int i=BodyParts;i>0;i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		//Segment odpowiedzialny za ruch wê¿a w danym kierunku
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
	//Funkcja sprawdzaj¹ca czy jedzenie zosta³o zjedzone
	public void checkFood() {
		if((x[0]+20 == FoodX) && (y[0]+20 == FoodY)) {
			BodyParts++;
			//Co dziesi¹te jedzenie jest jedzeniem specjalnym, daj¹cym bonusowe punkty
			if(FoodEaten % 9 == 0 && FoodEaten != 0)
				Score = Score + (int)(Score*(0.1*DifficultyLevel));
			else
				Score = Score + DifficultyLevel;
			FoodEaten++;
			newFood();
		}
	}
	
	public void checkCollisions() {
		//Sprawdza czy nast¹pi³a kolizja g³owy wê¿a z jego cia³em
		for(int i=BodyParts; i>0; i--) {
			if((x[0]==x[i]) && (y[0]==y[i])){
				Running = false;
			}
		}
		//Sprawdza czy nast¹pi³a kolizja g³owy wê¿a z lew¹ granic¹
		if(x[0]+20 < 20)
			Running = false;
		//Sprawdza czy nast¹pi³a kolizja g³owy wê¿a z praw¹ granic¹
		if(x[0]+20 > 1000)
			Running = false;
		//Sprawdza czy nast¹pi³a kolizja g³owy wê¿a z górn¹ granic¹
		if(y[0]+20 < 20)
			Running = false;
		//Sprawdza czy nast¹pi³a kolizja g³owy wê¿a z doln¹ granic¹
		if(y[0]+20 > 640)
			Running = false;
	
		if(!Running)
			time.stop();
	}
	//Funkcja wyœwietlaj¹ca interfejsu koñca gry
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
		G.drawString("Wciœnij enter aby zagraæ ponownie", (SCREEN_WIDTH - mtx3.stringWidth("Wciœnij enter aby zagraæ ponownie"))/2, (SCREEN_HEIGHT/2));
		G.setFont(new Font("SansSerif", Font.BOLD, 28));
		FontMetrics mtx4 = getFontMetrics(G.getFont());
		G.drawString("Wciœnij ESC aby Wyjœæ", (SCREEN_WIDTH - mtx4.stringWidth("Wciœnij ESC aby Wyjœæ"))/2, (SCREEN_HEIGHT/2+60));
		G.setFont(new Font("SansSerif", Font.BOLD, 28));
	}
	//Funkcja steruj¹ca gr¹
	public void actionPerformed(ActionEvent e) {
		if(Running) {
			move();
			checkFood();
			checkCollisions();
		}
		repaint();
	}
	//Funkcja odpowiadaj¹ca za kolor wê¿a
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
	//Funkcja restartuj¹ca grê w przypadku klikniêcia przycisku enter
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
