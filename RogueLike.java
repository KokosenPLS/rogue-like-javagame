import static java.lang.Integer.*;

public class RogueLike extends EasyGraphics{
	public static void main(String[] args) {
		launch(args);
	}

	public void run(){
		final int VINDU = 500;
		makeWindow("Roguelike", VINDU, VINDU);
		// Lager svart bakgrunn og setter fargen tilbake til en hvit farge
		setColor(0,0,0);
		fillRectangle(0,0, VINDU, VINDU);
		setColor(230,230,230);
		setFont("arial", 20);
		
		// Loop for å holde spillet i gang, selv etter spilleren er død, kan man starte et nytt spill
		boolean gameIsOn = true;

		// SE NEDERST FOR Å SE STATS TIL PLAYER OG MONSTERET

		// Definerer hvor høyt stats til Monsteret kan bli
		int enemyMaxHP = 5;
		int enemyDmgMax = 2;
		int enemyDefMax = 3;
		int runde = 0;

		while(gameIsOn){
			// PLAYER BLIR LAGET HER, KAN FORANDRE PÅ HP, ATTACK OG DEFENSE STATS
			Character player = new Character(15, 4, 2);
			while(player.hp > 0){

				setColor(255,0,0);
				setFont("arial", 20);
				int dispHP = drawString("HP: " + player.hp, 5, 25);
				setColor(230,230,230);
				setFont("arial", 13);
				int dispAtt = drawString("Attack: " + player.attack, VINDU/6, VINDU - 10);
				int dispDef = drawString("Defense: " + player.defense, 2*VINDU/3, VINDU - 10);

				boolean monsterDrept = false; // I tilfelle man velger løp så kan man sjekke om det skal skrives at monsteret drepes

				// Fjerner alt av tekst etter hvert møte med et monster, og viser det fram igjen når det kommer et nytt monster
				setFont("arial", 28);
				int encounter = drawString("Du møter et monster!", 120, 60);
				setFont("arial", 20);
				int attack = drawString("1. Angrip", 100, 100);
				int run = drawString("2. Løp", 350, 100);
				setVisible(attack, false);
				setVisible(run, false);

				// Må forhåndsdeklarere teksten til hvor mye damage man gjør hver turn for å bruke de i neste while loop
				int playerAttackTxt = drawString("",0,0);
				int enemyAttackTxt = drawString("", 0,0);

				// Vanskelighetsgrad (runde % x) x = 1 veldig vanskelig, jo høye jo lettere
				// Hver andre runde er det en 50/50 sjanse for at monsteret sin max damage og defense økes med 1
				if (runde % 2 == 0 && runde != 0) {
					int oppgrader = trekkTall(1,2);
					if (oppgrader == 2) {
						enemyDmgMax++;
					}
					oppgrader = trekkTall(1,2);
					if (oppgrader == 2) {
						enemyDefMax++;
					}
				}
				// Hver tredje runde økes monsterets potensielle max HP med 1
				if (runde % 3 == 0 && runde != 0)
					enemyMaxHP++;

				// SPILL

				// LAGER FIENDER PÅ STARTEN AV HVERT "ROM"
				Monster enemy = new Monster(trekkTall(3, enemyMaxHP), trekkTall(2, enemyDmgMax), trekkTall(1, enemyDefMax));

				// Monsterets stats, skrevet ut for testing, men jeg syntes det var greit å ha det her
				// Kan legge inn Monsteret sine stats hvis jeg skulle ønske det, men jeg syntes det blir gøyere uten å se det
				String enemyStat;
				if (enemy.hp <= 4) {
					enemyStat = "Monsteret ser ut til å være i dårlig form";
				}
				else if (enemy.hp > 4 && enemy.hp < 6){
					enemyStat = "Monsterer ser normalt ut";
				}
				else{
					enemyStat = "Monsteret ser ut til å være i god form";
				}
				setFont("arial", 20);
				int enemyHP = drawString(enemyStat, 80, 2*VINDU/3);

				System.out.println(enemy.hp);
				System.out.println(enemy.attack);
				System.out.println(enemy.defense);

				while(enemy.hp > 0){
					// Fjerner forrige rundes stats (player)
					setVisible(dispHP, false);
					setVisible(dispAtt, false);
					setVisible(dispDef, false);
					// Setter inn nye stats (player)
					setColor(255,0,0);
					setFont("arial", 20);
					dispHP = drawString("HP: " + player.hp, 5, 25);
					setColor(230,230,230);
					setFont("arial", 13);
					dispAtt = drawString("Attack: " + player.attack, VINDU/6, VINDU - 10);
					dispDef = drawString("Defense: " + player.defense, 2*VINDU/3, VINDU - 10);
					
					// Viser ANGRIP og LØP options
					setVisible(attack, true);
					setVisible(run, true);

					// Spør om input som skal være 1 eller 2. Try og Catch er der hvis det ikke blir skrevet noe, eller om det kommer bokstaver
					int input;
					try{
						input = parseInt(getText("Hva gjør du?"));
					}	catch (NumberFormatException nfe){
						input = 0;
					}
					while (input < 1 || input > 2 ){
						try{
							input = parseInt(getText("Hva gjør du?"));
						} catch(NumberFormatException nfe){
							input = 0;
						}
					}

					if (input == 1) 
						setVisible(run, false);

					// Fjerner forrige rundes angrep tekster
					setFont("arial", 30);
					setVisible(playerAttackTxt, false);
					setVisible(enemyAttackTxt, false);
					// Load er en metode definert under, som bare skriver X antall ".", her "..." med 400ms intervaller
					load(3, 400);

					// Hvis spilleren velger å angripe
					if (input == 1){ // Angrip
						int damage = player.attack - Math.round(3*enemy.defense/4); // Prøvde meg fram til noe som jeg tror scaler bra, men idk
						if (damage <= 0) {
							damage = 1 + (player.attack/4);
						}
						enemy.hp -= damage;
							
						// Skriver spilleren sitt angrep
						setFont("arial", 18);
						playerAttackTxt = drawString("Du angriper og skader for " + damage + " HP"
														, 10
														, VINDU/2 - 25
														 );
						
						setFont("arial", 30);
						load(3, 400);

						// Hvis monsteret forstatt lever skal den angripe spilleren
						if (enemy.hp > 0) {
							// Monsteret angriper tilbake
							damage = enemy.attack - player.defense;
							if (damage <= 0) {
								damage = 1;
							}
							player.hp -= damage;

							setFont("arial", 18);
							enemyAttackTxt = drawString("Monsteret skader deg for " + damage +" HP", 10, VINDU/2);
						}
						// monsterDrept = true for å skrive at monsteret blir drept
						if (enemy.hp <= 0) {
							monsterDrept = true;
						}
						// Fjerner ANGRIP og LØP valgene, for å slippe å gjøre det i neste loop (?) idk
						setVisible(attack, false);
						setVisible(run, false);
					}

					// Hvis spilleren velger å løpe. Hadde vært mer brukt hvis man kunne se ATTACK og DEFENSE på monsteret, men er risk å teste med å angripe
					else if (input == 2){
						System.out.println("Du løper");
						enemy.hp = 0; 				// Bare får å komme ut av loopen, tror jeg kunne brukt break; som jeg gjør i neste if-setning
						setVisible(attack, false);
						setVisible(run, false);
					}

					// Hvis spiller dør hopper den ut av loopen, kunne ha skrevet en || i while loopen, men kom på det litt sent, er ez fix tho
					if (player.hp <= 0) {
						break;
					}
					
				}

				// Har nå drept monsteret eller løpt vekk, fjerner monstertekst og angreptekst
				setVisible(enemyHP, false);
				setVisible(encounter, false);
				setVisible(playerAttackTxt, false);
				setVisible(enemyAttackTxt, false);
				int drept = drawString("", 0,0);

				// Forhåndsdeklarerer belønningstekstene
				int rewardTxt1 = drawString("", 0, 0);
				int rewardTxt2 = drawString("", 0, 0);
				String utTxt="";

				// Hvis monsteret blir drept skrives det at monsteret blir drept
				if (monsterDrept) {
					setFont("arial", 25);
					drept = drawString("Monsteret er drept!", 130, VINDU/2 - 75);
					load(2, 500);

					int reward = trekkTall(1, 10);
					// Og man har en 80% sjanse for å finne en kiste som kan inneholde oppgraderinger, eller ta damage
					if (reward < 8) {
						setFont("arial", 20);
						utTxt = "Det ligger en kiste i rommet"; 					// Kiste tekst
						rewardTxt1 = drawString(utTxt, 10, VINDU/2 - 25);
						load(2, 500);
						utTxt = "Skriv 1 for å åpne eller 2 for å la være";
						rewardTxt2 = drawString(utTxt, 10, VINDU/2);
						int input;
						try{														// Try og catch, samme som før
							input = parseInt(getText("1 eller 2"));
						} catch(NumberFormatException nfe){
							input = 0;
						}

						while(input < 1 || input > 2){
							try{
								input = parseInt(getText("1 eller 2"));
							} catch(NumberFormatException nfe){
								input = 0;
							}
						}
						setVisible(rewardTxt1, false);
						setVisible(rewardTxt2, false);
							// Fjerner belønningstekstene, de skal brukes på nytt hvis spilleren velger å åpne kista (input == 1)
						if (input == 1) {
							reward = trekkTall(1,14); // trekker et av 14 tall, der det er 9/14 sjanse for å få en oppgradering
							System.out.println(reward);
							if (reward >= 1 && reward <= 3) { 						// Første 3 er økt DAMAGE
								utTxt = "Du finner et nytt sverd i kisten!";
								rewardTxt1 = drawString(utTxt, 10, VINDU/2 - 25);
								load(3, 400);
								utTxt = "Du får +1 Attack!";
								rewardTxt2 = drawString(utTxt, 10, VINDU/2);
								player.attack++;

							}
							else if(reward >= 4 && reward <= 6){					// De neste 3 er økt DEFENSE
								utTxt = "Du finner bedre rustning i kisten!";
								rewardTxt1 = drawString(utTxt, 10, VINDU/2 - 25);
								load(3, 400);
								utTxt = "Du får +1 Defense!";
								rewardTxt2 = drawString(utTxt, 10, VINDU/2);
								player.defense++;
							}
							else if(reward >= 7 && reward <=9){						// De neste 3 øker HP fra 1-3
								utTxt = "Du finner en kakebit i kisten!";
								rewardTxt1 = drawString(utTxt, 10, VINDU/2 - 25);
								load(3, 400);
								int heal = trekkTall(1,3);
								utTxt = "Du får +"+ heal +" HP!";
								rewardTxt2 = drawString(utTxt, 10, VINDU/2);
								player.hp += heal;
							}
							else{													// Resten dealer 1-3 DAMAGE til player
								utTxt = "Det kommer en illeluktende gass opp av kisten!";
								rewardTxt1 = drawString(utTxt, 10, VINDU/2 - 25);
								load(3, 400);
								int damage = trekkTall(1,3);
								utTxt = "Du mottar -"+damage+"HP!";
								rewardTxt2 = drawString(utTxt, 10, VINDU/2);
								player.hp-=damage;
							}
						}
					}	
				}
				// Forhåndsdeklarerer tekst hvis du valgte å løpe
				int escape = drawString("", 0,0);
				if(!monsterDrept && player.hp > 0){ // Skriver nevnt tekst
					setFont("arial", 25);
					escape = drawString("Du løper tilbake!", 160, VINDU/2 - 75);
				}
				int nesteTxt = drawString("", 0, 0); 				// Går videre til neste rom
				if (player.hp > 0) {
					setFont("arial", 25);
					nesteTxt = drawString("Skriv noe for å gå videre", 110, VINDU - 100);
					int neste; 
					try{
						neste = parseInt(getText("Skriv noe for å gå videre"));
					} catch(NumberFormatException nfe){
						neste = 1;
					}
					while(neste != 1){
						neste=1;
					}	
				}
				// Fjerner all ny tekst som har kommet, alt som har blir forhåndsdeklarert
				setVisible(nesteTxt, false);
				setVisible(escape, false);
				setVisible(rewardTxt1, false);
				setVisible(rewardTxt2, false);
				setVisible(drept, false);

				if(player.hp > 0){	// Hvis ikke den if-setninga hadde vært her hadde det blitt skrevet at man går inn i neste rom, selvom man er dø
					setFont("arial", 25);
					int videre = drawString("Du går videre til neste rom", 90, VINDU/2 - 75);
					load(3, 500);
					setVisible(videre, false);
				}

				// Fjerner denne rundens stats, og skriver de nye stats i starten av samme loop
				setVisible(dispHP, false);
				setVisible(dispAtt, false);
				setVisible(dispDef, false);

				// Runde brukes til å vise hvor langt man kom, en HIGH-SCORE på en måte
				runde++;
				// Kan lagre dette med printwriter og lage en fil, hvor man også kan ta og skrive inn brukernavn på starten og legge det til

			}

			// Når spilleren had død, HP er mindre enn 0
			if (player.hp <= 0) {
				setFont("arial", 25);
				int gameOver = drawString("Game over", 170, VINDU/2-50);
				int antRunder = drawString("Du overlevde "+runde+" runder!", 100, VINDU/2);
				setFont("arial", 20);
				int restartTxt1 = drawString("For å spille på nytt, skriv 1", 100, VINDU/2+50);
				int restartTxt2 = drawString("For å avslutte spill, skriv 2", 100, VINDU/2+100);
				int input;
				// Sjekker om spilleren vil spille på nytt, 1 ja, 2 nei
				try{
					input = parseInt(getText("1 eller 2"));
				} catch(NumberFormatException nfe){
					input = 0;
				}
				while(input < 1 || input > 2){
					try{
						input = parseInt(getText("1 eller 2"));
					} catch(NumberFormatException nfe){
						input = 0;
					}
				}
				if (input == 2) {
					gameIsOn = false;
					setVisible(restartTxt1, false);
					setVisible(restartTxt2, false);
				}
				else{
					setVisible(gameOver, false);
					setVisible(antRunder, false);
					setVisible(restartTxt1, false);
					setVisible(restartTxt2, false);
				}
			}
		}

	}

	// Metoden som trekker tilfeldige tall
	private int trekkTall(int min, int max){
		return min + (int)(Math.random()*(max-min+1));
	}

	// Metoden som tar pauser og setter inn "..."
	private void load(int prikker, int intervall){
		String txt = "";
		for(int i = 0; i < prikker; i++){
			txt += ".";
			int load = drawString(txt, 245, 150);
			pause(intervall);
			setVisible(load, false);
		}
	}
}

// Notat fra 2 år senere hehe
// Dette var før vi lærte om objektorientert programmering
// som er hvorfor jeg ikke bruker polymorfi her hehe

// Definerer objektet character, hvor jeg definerer hvilke egenskaper spilleren skal ha (veldig basic)
class Character {
	int hp;
	int attack;
	int defense;

	// Metoden for å lage en ny character(player), bruker tre int inputs
	public Character(int hp, int attack, int defense){
		this.hp = hp;
		this.attack = attack;
		this.defense = defense;
	}
}

// Definerer objektet Monster(enemy), med samme stats som character(player)
class Monster{
	int hp;
	int attack;
	int defense;

	// Metoden for å lage et nytt Monster
	public Monster(int hp, int attack, int defense){
		this.hp = hp;
		this.attack = attack;
		this.defense = defense;
	}
}