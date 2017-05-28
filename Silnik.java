import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.System;
import java.util.Arrays;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
 
import javax.swing.JButton;
import javax.swing.JPanel;
 
class dana {
    String nazwa;
    int ilosc;
    int[][] wartosci;          // wartosc[x][0] = ilu_argumentowy, wartosc[x][1] = jaka_wartosc_logiczna (2 - szukana, 3 - porazka)
}
 
class wzor {
        int ile_w;
        int[] wynik;                // wynik[0]  = czy przed stoi ! czy nie;
        int ile_r;
        int[] regula;
}
 
class poziom {
        int ile_r;
        int ile_w;
        int wartosc;
        int poziom;
        int[] regula;
        int[] wynik;
}
 
class ButtonPanel extends JPanel implements ActionListener{
 
	public static final int HEIGHT = 325;
	public static final int WIDTH = 750;
	private JButton Zapisz;
	private JButton WPrzod;
	private JButton Wtyl;
        private JLabel naglowek1;
        private JLabel naglowek2;
        private JLabel naglowek3;
        private JTextArea szukaneArea;
        private JTextArea daneArea;
        private JTextArea wzoryArea;
        String szukane, dane, wzory;
        //String szukane= "C(X,Y)";
        //String dane = "D(Y)=T,D(X,Y)=T";
        //String wzory = "A($Y) && B($X,$Y)=>C($X,$Y)\n" +
//"D($Y)=>A($Y)\n" +
//"D($X,$Y)=>B($X,$Y)\n";
        
        Glowna wnioskujemy;
 
	public ButtonPanel() {
		Zapisz = new JButton("Zapisz");
		WPrzod = new JButton("W przód");
		Wtyl = new JButton("W ty³");
                
                naglowek1 = new JLabel("Szukane:");
                naglowek2 = new JLabel("Dane:");
                naglowek3 = new JLabel("Wzory:");
 
		Zapisz.addActionListener(this);
		WPrzod.addActionListener(this);
		Wtyl.addActionListener(this);
                
                wnioskujemy = new Glowna();
 
		setLayout(new GridLayout(3,3,50,50));
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		add(naglowek1);
		add(naglowek2);
		add(naglowek3);
                szukane = wnioskujemy.wczytaj_plik("C:\\Users\\Fiszcz\\Desktop\\Fiszcz\\szukane.txt");
                dane = wnioskujemy.wczytaj_plik("C:\\Users\\Fiszcz\\Desktop\\Fiszcz\\dane.txt");
                wzory = wnioskujemy.wczytaj_plik("C:\\Users\\Fiszcz\\Desktop\\Fiszcz\\wzor.txt");
                wnioskujemy.wczytaj_dane(szukane, dane);
                wnioskujemy.wczytaj_wzory(wzory);
                                
                szukaneArea = new JTextArea(szukane);
                daneArea = new JTextArea(dane);
                wzoryArea = new JTextArea(wzory);
                
                add(szukaneArea);
                add(daneArea);
                add(wzoryArea);
                add(Zapisz);
                add(WPrzod);
                add(Wtyl);
	}
 
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
 
		if(source == Zapisz){
			szukane = szukaneArea.getText(); 
                        dane = daneArea.getText();
                        wzory = wzoryArea.getText();
                }
 
		else if(source == WPrzod)
			wnioskujemy.zaczynamy(szukane, dane, wzory, 0);
 
		else if(source == Wtyl)
			wnioskujemy.zaczynamy(szukane, dane, wzory, 1);
	}
}
 
class ActionFrame extends JFrame {
 
    public ActionFrame() {
        super("Wnioskowanie");
 
        JPanel buttonPanel = new ButtonPanel();
        add(buttonPanel);
 
        setLocation(0,0);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }
}
 
class Glowna{
 
public boolean b(int a)
{
	return a != 0;
}
 
public static int memcmp(byte b1[], byte b2[], int sz){
    for(int i = 0; i < sz; i++){
        if(b1[i] != b2[i]){
            if((b1[i] >= 0 && b2[i] >= 0)||(b1[i] < 0 && b2[i] < 0))
                return b1[i] - b2[i];
            if(b1[i] < 0 && b2[i] >= 0)
                return 1;
            if(b2[i] < 0 && b1[i] >=0)
                return -1;
        }
    }
    return 0;
}
 
String[] argumenty = new String[200];
dana[] rekordy = new dana[100];
wzor[] reguly = new wzor[100];
int ile_danych;
int ile_wzorow;
 
int liczba_poziomow=0;
int ostatni_poziom=0;
poziom[] poziomy = new poziom[200];
 
String wczytaj_plik ( String nazwa_pliku ) 
{    
      File file = new File(nazwa_pliku);
      Scanner in = null;
    try {
        in = new Scanner(file);
    } catch (FileNotFoundException ex) {
        Logger.getLogger(Glowna.class.getName()).log(Level.SEVERE, null, ex);
    }
 
    String zdanie;
    String calosc = "";
 
    while(in.hasNextLine()){
        calosc = calosc + in.nextLine() + "\n";
    }
 
    return calosc;
}
 
public void wczytaj_dane ( String szukana, String dane)
{
        int ktory_argument = 0;
        ile_danych = 0;
 
        //zmienne dotyczace petli
        int i=-1;
        int poczatek_nazwy=0;
        int ile_argumentow;
        int w_nawiasie = 0;   // odnosi siê do tego czy dana nazwa jest argumentem czy po prostu nazwa glowna
        int zakonczone = 1;   // odnosi siê to tego czy jestesmy w trakcie przechodzenia przez wyraz czy poza jakimkolwiek wyrazem
        int dlugosc_nazwy;
        String tymczasowa = "";
        int n;
        int istnieje = 0;
        int do_ktorej = 0;    // do ktorej danych tworzymy wartosci
        int ilosc = 0;
        boolean warunek;
        int jest;
 
        do
        {
                i++;
                if((i == szukana.length())) warunek = true;
                else
                warunek = ( szukana.charAt(i) == ',' || szukana.charAt(i) == '(' || szukana.charAt(i) == ' ' || szukana.charAt(i) == ')' || szukana.charAt(i) == '\n' || (i == szukana.length()));
                if( (zakonczone == 0) && warunek)
                {
                        dlugosc_nazwy = i-poczatek_nazwy;
                        tymczasowa = szukana.substring(poczatek_nazwy, poczatek_nazwy+dlugosc_nazwy);
                        jest = 0;
                        if( w_nawiasie == 1 )
                        {
                                for(n=0; n< ktory_argument; n++)
                                {
                                        if(( tymczasowa == argumenty[n]))
                                        {
                                                jest=1;
                                                break;
                                        }
                                }
                                if(jest == 0)
                                {
					argumenty = (String[])Arrays.copyOf(argumenty, ktory_argument+1);
					argumenty[ktory_argument] = tymczasowa;
                                        ilosc = rekordy[do_ktorej].ilosc;
                                        rekordy[do_ktorej].wartosci[ilosc-1][0] ++ ;
					//rekordy[do_ktorej].wartosci[ilosc-1] = (int[])Arrays.copyOf(rekordy[do_ktorej].wartosci[ilosc-1],(rekordy[do_ktorej].wartosci[ilosc-1][0] + 2));
                                        rekordy[do_ktorej].wartosci[ilosc-1][ rekordy[do_ktorej].wartosci[ilosc-1][0]+1 ] = ktory_argument;
                                        ktory_argument++;
                                }
                                if(jest == 1)
                                {
                                        ilosc = rekordy[do_ktorej].ilosc;
                                        rekordy[do_ktorej].wartosci[ilosc-1][0] ++ ;
                                        //rekordy[do_ktorej].wartosci[ilosc-1] = (int[])Arrays.copyOf(rekordy[do_ktorej].wartosci[ilosc-1],(rekordy[do_ktorej].wartosci[ilosc-1][0]+2) );
                                        rekordy[do_ktorej].wartosci[ilosc-1][ rekordy[do_ktorej].wartosci[ilosc-1][0]+1 ] = n;
                                }
                        }
                        else
                        {
                                for(n=0; n< ile_danych; n++)
                                {
                                        if(tymczasowa == rekordy[n].nazwa)
                                        {
                                                jest=1;
                                                break;
                                        }
                                }
                                if(jest == 0)
                                {
                                        do_ktorej = ile_danych;
                                        ile_danych += 1;
                                        //rekordy = Arrays.copyOf( rekordy, ile_danych );
                                        rekordy[do_ktorej] = new dana();
                                        rekordy[do_ktorej].nazwa = new String();
                                        rekordy[do_ktorej].nazwa = tymczasowa;
                                        rekordy[do_ktorej].ilosc = 1;
                                        rekordy[do_ktorej].wartosci = new int[10][10];
                                }
                                if(jest == 1)
                                {
                                        do_ktorej = n;
                                        rekordy[do_ktorej].ilosc ++;
                                }
                                ilosc = rekordy[do_ktorej].ilosc;
                                //rekordy[ do_ktorej].wartosci = (int[][])Arrays.copyOf(rekordy[ do_ktorej].wartosci,ilosc);
                                //rekordy[ do_ktorej].wartosci[ilosc-1] = (int[])Arrays.copyOf(rekordy[ do_ktorej].wartosci[ilosc-1] ,2);
                                rekordy[ do_ktorej].wartosci[ilosc-1][0] = 0;
                                rekordy[ do_ktorej].wartosci[ilosc-1][1] = 2;
                        }
                        zakonczone = 1;
                }
                else if( zakonczone == 1 && !warunek)
                {
                        zakonczone = 0;
                        poczatek_nazwy = i;
                }
                if(i != szukana.length()){
                if( szukana.charAt(i) == '(' )
                        w_nawiasie++;
                if( szukana.charAt(i) == ')')
                        w_nawiasie--;}
        }
        while ((i) != szukana.length());
 
        i = -1;
 
        do
        {
                i++;
                if((i == dane.length())) warunek = true;
                else 
                warunek = ( dane.charAt(i) == ',' || dane.charAt(i) == '(' || dane.charAt(i) == ' ' || dane.charAt(i) == ')' || dane.charAt(i) == '\n' || dane.charAt(i) == '=' || dane.charAt(i) == '>');
                if( zakonczone == 0 && warunek)
                {
                        dlugosc_nazwy = i-poczatek_nazwy;
                        tymczasowa = dane.substring(poczatek_nazwy, poczatek_nazwy+dlugosc_nazwy);
                        jest = 0;
                        if( w_nawiasie == 1 )
                        {
                                for(n=0; n< ktory_argument; n++)
                                {
                                        if(tymczasowa.equals(argumenty[n]))
                                        {
                                                jest=1;
                                                break;
                                        }
                                }
                                if(jest == 0)
                                {
                                        argumenty = (String[])Arrays.copyOf(argumenty ,(ktory_argument+1));
                                        argumenty[ktory_argument] = new String();
                                        argumenty[ktory_argument] = tymczasowa;
                                        ilosc = rekordy[do_ktorej].ilosc;
                                        rekordy[do_ktorej].wartosci[ilosc-1][0] ++ ;
                                        rekordy[do_ktorej].wartosci[ilosc-1][ rekordy[do_ktorej].wartosci[ilosc-1][0]+1 ] = ktory_argument;
                                        ktory_argument++;
                                }
                                if(jest == 1)
                                {
                                        ilosc = rekordy[do_ktorej].ilosc;
                                        rekordy[do_ktorej].wartosci[ilosc-1][0] ++ ;
                                        //rekordy[do_ktorej].wartosci[ilosc-1] = (int[])Arrays.copyOf( rekordy[do_ktorej].wartosci[ilosc-1] ,(rekordy[do_ktorej].wartosci[ilosc-1][0]+2) );
                                        rekordy[do_ktorej].wartosci[ilosc-1][ rekordy[do_ktorej].wartosci[ilosc-1][0]+1 ] = n;
                                }
                        }
                        else
                        {
                                for(n=0; n< ile_danych; n++)
                                {
                                        if(tymczasowa.equals(rekordy[n].nazwa))
                                        {
                                                jest=1;
                                                break;
                                        }
                                }
                                if(jest == 0)
                                {
                                        do_ktorej = ile_danych;
                                        ile_danych += 1;
                                        //rekordy = (dana[])Arrays.copyOf( rekordy, ile_danych );
                                        rekordy[do_ktorej] = new dana();
                                        rekordy[do_ktorej].nazwa = new String();
                                        rekordy[do_ktorej].nazwa = tymczasowa;
                                        rekordy[do_ktorej].ilosc = 1;
                                        rekordy[do_ktorej].wartosci = new int[10][10];
                                        for(int p=0;p<10;p++)
                                            rekordy[do_ktorej].wartosci[p] = new int[10];
                                }
                                if(jest == 1)
                                {
                                        do_ktorej = n;
                                        rekordy[do_ktorej].ilosc ++;
                                }
                                ilosc = rekordy[do_ktorej].ilosc;
                                //rekordy[ do_ktorej].wartosci = (int[][])Arrays.copyOf( rekordy[ do_ktorej].wartosci, ilosc);
                                //rekordy[ do_ktorej].wartosci[ilosc-1] = (int[])Arrays.copyOf(rekordy[ do_ktorej].wartosci[ilosc-1] , 2);
                                rekordy[ do_ktorej].wartosci[ilosc-1][0] = 0;
                        }
                        zakonczone = 1;
                }
                else if( zakonczone == 1 && !warunek)
                {
                        zakonczone = 0;
                        poczatek_nazwy = i;
                }
                if((i != dane.length())){
                if( dane.charAt(i) == '=')
                {
                        while( dane.charAt(i) != 'T' && dane.charAt(i) != 'F')
                                i++;
                        if(dane.charAt(i) == 'T')
                                rekordy[ do_ktorej].wartosci[ilosc-1][1] = 1;
                        else
                                rekordy[ do_ktorej].wartosci[ilosc-1][1] = 0;
                }
                if( dane.charAt(i) == '(' )
                        w_nawiasie++;
                if( dane.charAt(i) == ')')
                        w_nawiasie--;
                }
        }
        while((i) != dane.length());
}
 
void wczytaj_wzory ( String wzory)
{
        ile_wzorow = 0;
 
        // sterowanie petla
        int i=-1;
        int znak_wzoru = -1;
        boolean w_nawiasie = false;
        int zakonczone = 1;            // 1 - nie rozpoczeto wczytywania nazwy, 0 - przechodzimy przez c-string jakiejs nazwy
        int co_robimy = 0;           // 0 - regule, 1 - wynik
        int poczatek_nazwy = 0;
        boolean warunek;
        int dlugosc_nazwy;
 
        // sterowanie zmiennymi we wzorze    $
        String[] zmienne = new String[15];
        int ile_zmiennych=0;
        int n;
 
	reguly[0] = new wzor();
        reguly[0].wynik = new int[30];
        reguly[0].regula = new int[40];
        reguly[0].wynik[0] = 0;
 
        do
        {
                i++;
                if((i == wzory.length())) warunek = true;
                else
                warunek = (wzory.charAt(i) == '(' || wzory.charAt(i) == ',' || wzory.charAt(i) == ')' || wzory.charAt(i) == '|' || wzory.charAt(i) == '&' || wzory.charAt(i) == '!' || wzory.charAt(i) == '=' || wzory.charAt(i) == ' ' || wzory.charAt(i) == '\n');
                if( (!warunek) && b(zakonczone) )
                {
                        poczatek_nazwy = i;
			zakonczone = 0;
                        continue;
                }
                if( warunek && !b(zakonczone))
                {
                        dlugosc_nazwy = i-poczatek_nazwy;
                        if( w_nawiasie == true )
                        {
                                for(n=0; n<ile_zmiennych; n++)
                                {
                                        if( dlugosc_nazwy == (zmienne[n].length()) && (zmienne[n].equals(wzory.substring(poczatek_nazwy,poczatek_nazwy+dlugosc_nazwy))) )
                                        {
                                                if(co_robimy == 0)
                                                        reguly[ile_wzorow].regula[++znak_wzoru] = n;
                                                else
                                                        reguly[ile_wzorow].wynik[++znak_wzoru] = n;
						zakonczone = 1; 
                                                break;
                                        }
                                }
                                if( n == ile_zmiennych)
                                {
                                        zmienne = (String[])Arrays.copyOf( zmienne, (ile_zmiennych+1));
                                        zmienne[ile_zmiennych] = new String();
                                        zmienne[ile_zmiennych] = wzory.substring(poczatek_nazwy,poczatek_nazwy+dlugosc_nazwy);
                                        if( co_robimy == 0 )
                                                reguly[ile_wzorow].regula[++znak_wzoru] = ile_zmiennych;
                                        else
                                                reguly[ile_wzorow].wynik[++znak_wzoru] = ile_zmiennych;
                                        ile_zmiennych++;
					zakonczone = 1;
                                }
                        }
                        else
                        {
                                for(n=0; n<ile_danych; n++)
                                {
                                        if( dlugosc_nazwy == (rekordy[n].nazwa.length()) && (rekordy[n].nazwa.equals(wzory.substring(poczatek_nazwy,poczatek_nazwy+dlugosc_nazwy))) )
                                        {
                                                if( co_robimy == 0 )
                                                        reguly[ile_wzorow].regula[++znak_wzoru] = n;
                                                else
                                                        reguly[ile_wzorow].wynik[++znak_wzoru] = n;
						zakonczone = 1; 
                                                break;
                                        }
                                }
                                if( n == ile_danych)
                                {
                                        //rekordy = (dana[])Arrays.copyOf( rekordy, (ile_danych+1) );
                                        rekordy[ile_danych] = new dana();
                                        rekordy[ile_danych].nazwa = new String();
                                        rekordy[ile_danych].nazwa = wzory.substring(poczatek_nazwy,poczatek_nazwy+dlugosc_nazwy);
                                        rekordy[ile_danych].ilosc = 0;
                                        if( co_robimy == 0 )
                                                reguly[ile_wzorow].regula[++znak_wzoru] = ile_danych;
                                        else
                                                reguly[ile_wzorow].wynik[++znak_wzoru] = ile_danych;
                                        ile_danych++;
					zakonczone = 1;
                                }
                        }
                }
                if((i != wzory.length())){
                if( wzory.charAt(i) == '=' )
                {
			i++;
                        co_robimy = 1;
                        reguly[ile_wzorow].ile_r = znak_wzoru+1;
                        reguly[ile_wzorow].wynik[0] = 0;
                        znak_wzoru = 0;
                        continue;
                }
               	if( wzory.charAt(i) == '(' || wzory.charAt(i) == ')')
               	{
                       	if((wzory.charAt(i) == '(') && (reguly[ile_wzorow].regula[znak_wzoru] < 0 || znak_wzoru == -1) && co_robimy==0)
                                reguly[ile_wzorow].regula[++znak_wzoru] = -4;
                       	else if((wzory.charAt(i) == ')') && w_nawiasie == false)
				reguly[ile_wzorow].regula[++znak_wzoru] = -5;
			else
                               	w_nawiasie = !w_nawiasie;
                       	continue;
                }
                if(wzory.charAt(i) == '!')
                {
                        if(co_robimy == 0)
                                reguly[ile_wzorow].regula[++znak_wzoru] = -1;
                        else
                                reguly[ile_wzorow].wynik[0] = -1;
                        continue;
                }
                if(wzory.charAt(i) == '|')
                {
                        i++;
                        reguly[ile_wzorow].regula[++znak_wzoru] = -2;
                        continue;
                }
                if(wzory.charAt(i) == '&')
                {
                        i++;
                        reguly[ile_wzorow].regula[++znak_wzoru] = -3;
                        continue;
                }}
                if((i != wzory.length())){
                if(!(w_nawiasie) && (wzory.charAt(i) == ',' || wzory.charAt(i) == '\n') && b(co_robimy))
                {
                        reguly[ile_wzorow].ile_w = znak_wzoru+1;
                        ile_wzorow++;
                        co_robimy = 0;
			//reguly = (wzor[])Arrays.copyOf( reguly, (ile_wzorow+1) );
			reguly[ile_wzorow] = new wzor();
                        reguly[ile_wzorow].wynik = new int[30];
                        reguly[ile_wzorow].regula = new int[40];
                        reguly[ile_wzorow].wynik[0] = 0;
                        znak_wzoru = -1;
                }}
        } 
	while ((i) != wzory.length());
}
 
public int lub(int a, int b)
{
        if ((b(a)||b(b))==true) return 1;
	else return 0;
}
 
public int I(int a, int b)
{
        if ((b(a)&&b(b))==true) return 1;
	else return 0;
}
 
public int obliczanie_wzoru (int[] wzor, int dlugosc)
{
        int znak = 0;             // na ktorym znaku wzoru jestesmy
        int tablica[] = {2,2,2};             // wartosci na ktorych beda za chwile odbywac sie obliczenia, 2 - oznacza puste miejsce
        int buf;             // wartosc gotowa do przypisania do jednego z miejsc tablicy
        int ile_nawiasow=0;    // ilosc nawiasow zamykajacych do pominiecia
        int stan = 0;        //odpowiada za kolejnosc wykonywania dzialan,
                                                 // 0 - poczatek(nic),
                                                 // 1 - czekanie na wczytanie drugiego argumentu do &&,
                                                 // 2 - pojawienie sie || i czekanie do wczytania kolejnej operacji,
                                                 // 3 - pojawienie sie drugi raz z rzêdu ||,
                                                 // 4 - pojawienie sie && po ||,
        int negacja = 0;
	int dalsza_tablica[];
 
        while(true)
        {
                if(wzor[znak] == -4)
                {
			dalsza_tablica = new int[dlugosc];
			System.arraycopy(wzor, znak+1, dalsza_tablica, 0, dlugosc);
                        buf = obliczanie_wzoru (dalsza_tablica, dlugosc);
                        if(buf==2)
                                return 2;
                        if(negacja==1)
                        {
                                negacja = 0;
				if(buf==0) buf = 1;
					else buf = 0;
                        }
                        if(tablica[0]==2) { tablica[0] = buf; }
                        else if(tablica[1]==2) {tablica[1] = buf; }
                        else {tablica[2] = buf;}
                        ile_nawiasow = 1;
                        while(ile_nawiasow!=0)
                        {
                                znak++;
                                if(wzor[znak]==-4)
                                {
                                        ile_nawiasow++;
                                }
                                if(wzor[znak]==-5)
                                {
                                        ile_nawiasow--;
                                }
                        }
                        znak++;
                }
                if(wzor[znak] >= 0)
                {
                        buf = wzor[znak];
                        if(buf==2)
                                return 2;
                        if(negacja==1)
                        {
                                negacja=0;
                                if(buf==0) buf = 1;
                                        else buf = 0;
                        }
                        if(tablica[0]==2) { tablica[0] = buf; }
                        else if(tablica[1]==2) {tablica[1] = buf; }
                        else {tablica[2] = buf;}
                        znak++;
                }
                if(wzor[znak] == -3)
                {
                        znak++;
                        if(stan == 2)
                        {
                                stan = 4;
                        }
                        else stan = 1;
                }
                if(wzor[znak] == -1)
                {
			if(negacja==0) negacja = 1;
                              else negacja = 0;
                        znak++;
                }
                if(wzor[znak] == -2)
                {
                        znak++;
                        if(stan == 2)
                        {
                                stan = 3;
                        }
                        else stan = 2;
                }
                if(wzor[znak] == -5 || dlugosc<=(znak+1))        //koniec dzialania funkcji przy napodkaniu ) i =
                {
                        if(stan==1) { return I(tablica[0], tablica[1]); }
                        if(stan==2) { return lub(tablica[0], tablica[1]); }
                        return tablica[0];
                }
                //przetwarzanie stanow
                if(stan == 1)
                {
                        if( tablica[0] != 2 && tablica[1] != 2)
                        {
                                tablica[0] = I(tablica[0], tablica[1]);
                                tablica[1] = 2;
                                stan = 0;
                        }
                }
                if(stan == 3)
                {
                        tablica[0] = lub(tablica[0], tablica[1]);
                        tablica[1] = 2;
                        stan = 2;
                }
                if(stan == 4)
                {
                        tablica[1] = I(tablica[1], tablica[2]);
                        tablica[2] = 2;
                        stan = 2;
                }
        }
}
 
public String wyswietl( int[] tekst, int dlugosc)
{
        String wynik = "";
        int i;
        int stan = 0;     // 0 - przed wczytaniem nazwy,  1 - od razu po wczytaniu nazwy, 2 - po wczytaniu argumentu
        for(i=0; i<dlugosc; i++)
        {
                if(tekst[i] >=0)
                {
                        if(stan == 0)
                        {	
                                 wynik = wynik + rekordy[tekst[i]].nazwa;
                                 stan = 1;
                                 continue;
                        }
                        if(stan == 1)
                        {
				wynik = wynik + ("(");
                                wynik = wynik + (argumenty[tekst[i]]);
				stan = 2;
                                continue;
                        }
			if(stan == 2)
			{
				wynik = wynik + (",");
				wynik = wynik + (argumenty[tekst[i]]);
				continue;
			}
                }
		if(stan == 2)
			wynik = wynik + (")");
                stan = 0;
                switch(tekst[i])
                {
                        case -1: wynik = wynik + ("!");
                                break;
                        case -2: wynik = wynik + ("||");
                                break;
                        case -3: wynik = wynik + ("&&");
                                break;
                        case -4: wynik = wynik + ("(");
                                break;
                        case -5: wynik = wynik + (")");
                                break;
                }
        }
	if(stan == 2)
		wynik = wynik + (")");
        return wynik; 
}
 
int k, m;
 
int znajdz_szukana()
{
        int n;
        int j;
        for(n=0; n<ile_danych; n++)
        {
                for(j=0; j<rekordy[n].ilosc; j++)
                {
                        if( rekordy[n].wartosci[j][1] == 2)
                        {
                                k = n;
                                m = j;
                                return 1;
                        }
                }
        }
        k = -1;
        return -1;
}
 
int szukanie_wartosci (int[] wskaznik, int ile_arg, int level)
{
        int ilosc = rekordy[wskaznik[0]].ilosc;
        int n;
        int zwrotna;
        for( n = 0; n < ilosc; n++)
        {
                if( rekordy[wskaznik[0]].wartosci[n][0] == ile_arg )
                {
                        if( Arrays.equals( Arrays.copyOfRange(wskaznik, 1, 1+ile_arg), Arrays.copyOfRange(rekordy[wskaznik[0]].wartosci[n],2,2+ile_arg)))
                        {
                                if( rekordy[wskaznik[0]].wartosci[n][1] < 2 )
                                {
                                        return rekordy[wskaznik[0]].wartosci[n][1];
                                }
                                else
                                {
                                        zwrotna = zacznij_przypadek( ile_arg, wskaznik[0], n, level);
                                        rekordy[wskaznik[0]].wartosci[n][1] = zwrotna;
                                        return zwrotna;
                                }
                        }
                }
        }
 
        // tworzenie nowego przypadku w rekordzie
        rekordy[wskaznik[0]].ilosc++;
        if(rekordy[wskaznik[0]].wartosci == null) rekordy[wskaznik[0]].wartosci = new int[10][10]; 
        //rekordy[wskaznik[0]].wartosci = (int[][])Arrays.copyOf(  rekordy[wskaznik[0]].wartosci, rekordy[wskaznik[0]].ilosc);
        rekordy[wskaznik[0]].wartosci[rekordy[wskaznik[0]].ilosc - 1] = new int[10];
        rekordy[wskaznik[0]].wartosci[rekordy[wskaznik[0]].ilosc - 1][0] = ile_arg;
        rekordy[wskaznik[0]].wartosci[rekordy[wskaznik[0]].ilosc - 1][1] = 3;
	for(int i=0; i<ile_arg; i++)
        	rekordy[wskaznik[0]].wartosci[rekordy[wskaznik[0]].ilosc - 1][2+i] = wskaznik[1+i];
 
        zwrotna = zacznij_przypadek( ile_arg, wskaznik[0], n, level);
        rekordy[wskaznik[0]].wartosci[n][1] = zwrotna;
        return zwrotna;
}
 
int rozwin_przypadek (int i, int k, int m, int ile_arg, int level)
{
        int[] tablica_arg = new int[ile_arg*2];
        int n;
        for( n = 0; n < 2*ile_arg; n+=2)
        {
                tablica_arg[n] = reguly[i].wynik[n/2+2];
                tablica_arg[n+1] = rekordy[k].wartosci[m][n/2+2];
        }
        int[] uzupelniona_regula =  new int[reguly[i].ile_r+1];
 
        int stan = 0;        // 1- wczytana nazwa danej, oczekiwanie na argumenty;
        int l;
        for( n = 0; n < reguly[i].ile_r; n++)
        {
                if( reguly[i].regula[n] < 0 )
                {
                        stan = 0;
                        uzupelniona_regula[n] = reguly[i].regula[n];
                }
                else
                {
                        if(stan == 0)
                        {
				uzupelniona_regula[n] = reguly[i].regula[n];
                                stan = 1;
				continue;
                        }
                        if(stan == 1)
                        {
                                for(l =0; l < (2*ile_arg); l+=2)
                                        if( reguly[i].regula[n] == tablica_arg[l] )
                                        {
                                                uzupelniona_regula[n] = tablica_arg[l+1];
                                                break;
                                        }
                        }
                }
        }
 
        // czesc odpowiedzialna za zapisane poziomy
        //poziomy = (poziom[])Arrays.copyOf ( poziomy, (ostatni_poziom+1));
        poziomy[ostatni_poziom] = new poziom();
        poziomy[ostatni_poziom].ile_r=reguly[i].ile_r;
        poziomy[ostatni_poziom].ile_w=reguly[i].ile_w;
        poziomy[ostatni_poziom].poziom = level;
	poziomy[ostatni_poziom].regula = new int[40];
	poziomy[ostatni_poziom].wynik = new int[30];
        poziomy[ostatni_poziom].regula = new int[poziomy[ostatni_poziom].ile_r];
        poziomy[ostatni_poziom].wynik = new int[poziomy[ostatni_poziom].ile_w];
        poziomy[ostatni_poziom].regula = Arrays.copyOf(uzupelniona_regula,poziomy[ostatni_poziom].ile_r);
	if(reguly[i].wynik[0] == -1)
		poziomy[ostatni_poziom].wynik[0] = -1;
	else
		poziomy[ostatni_poziom].wynik[0] = -10;  //trzeba zrobic tak zeby funkcja wyswietlaj() nie brala tego miejsca pod uwage
        poziomy[ostatni_poziom].wynik[1] = reguly[i].wynik[1];
        for(n=0; n < ile_arg; n++)
        {
                poziomy[ostatni_poziom].wynik[n+2] = tablica_arg[2*n+1];
        }
        ostatni_poziom++;
        ///////////////////////////////////////////////
 
        int[] poczatek_nazwy = new int[10];
        int ile_arg2 =0 ;
        stan = 0;             // 1 - wczytywanie argumentow; 0 - poza nazwa i argumentami
        int wartosc;
        int znak = 0;
        int dlugosc = 0;
        for( n = 0; n <= reguly[i].ile_r; n++)
        {
                if( uzupelniona_regula[n] >= 0 && stan == 0 && (n!=reguly[i].ile_r))
                {
                        poczatek_nazwy = Arrays.copyOfRange(uzupelniona_regula, n, reguly[i].ile_r);
                        stan = 1;
                }
                else if ( uzupelniona_regula[n] >= 0 && stan == 1 && (n!=reguly[i].ile_r))
                {
                        ile_arg2 ++;
                }
                else if ( stan == 1 || (stan == 1 && n == reguly[i].ile_r))
                {
                        stan = 0;
                        wartosc = szukanie_wartosci( poczatek_nazwy, ile_arg2, level+1 );
                        uzupelniona_regula[znak] = wartosc;
                        znak++;
                        dlugosc++;
			if( ile_arg2 == 0 && n != reguly[i].ile_r)
			{
				n--;
				continue;
			}
                        ile_arg2 = 0;
                        n--;
                }
                else
                {
                        dlugosc++;
                        uzupelniona_regula[znak] = uzupelniona_regula[n];
                        znak++;
                }
        }
        wartosc =  obliczanie_wzoru (uzupelniona_regula, dlugosc);
        poziomy[ostatni_poziom-1].wartosc = wartosc;
        return wartosc;
}
 
public int zacznij_przypadek( int ile_arg, int k, int  m , int level)
{
        int i;
        int zwracana;
        for(i=0; i<ile_wzorow; i++)
        {
                if(reguly[i].wynik[1] == k && (reguly[i].ile_w-2) == ile_arg )
                {
                        zwracana = rozwin_przypadek ( i, k, m, ile_arg, level );
                        if( zwracana == 0 )
                        {
				poziomy[level].wartosc = 3;
                        	return 3;	
                        }
                        else if( zwracana == 1)
                        {
                                if( reguly[i].wynik[0] == -1 )
				{
					poziomy[level].wartosc = 0;
                                        return 0;
				}
                                else
				{
					poziomy[level].wartosc = 1;
                                        return 1;
				}
                        }
                }
        }
	return 0;
}
 
void w_tyl()
{
        JFrame frame = new JFrame("Diagram wnioskowania");
        JPanel contentPane = (JPanel) frame.getContentPane();
        contentPane.setLayout(null);
        frame.setSize(new Dimension(1000, 600));
 
        JLabel[] tabela = new JLabel[20];
        int po_odstepach = 1;
        int po_tabelce = 0;
        int i,m,n;
        String wynik = "";
        int liczba_na_poziomie = 0;
        int odstepy;
        int z = 0;
        for(i=(ostatni_poziom-1); i>=0 ; i--)
        {
                for(m=(ostatni_poziom-1); m>=0; m--)
                {
                        if(poziomy[m].poziom == i)
                        {
                            liczba_na_poziomie ++;
                        }
                }
                odstepy = 1000 / (liczba_na_poziomie+1);
                for(m=(ostatni_poziom-1);m>=0;m--)
                {
                        if(poziomy[m].poziom == i)
                        {
                                wynik = wyswietl( poziomy[m].regula, poziomy[m].ile_r);
                                wynik = wynik + ( " => ");
                                wynik = wynik + wyswietl( poziomy[m].wynik, poziomy[m].ile_w);
                                wynik = wynik + ( "\n          " );
                                if( poziomy[m].wartosc == 1 )
                                        wynik = wynik + ("T\n");
                                else
                                        wynik = wynik + ("F\n");
                               // tabela[po_tabelce]=null;
                                tabela[po_tabelce]=new JLabel(wynik);
                                contentPane.add(tabela[po_tabelce]);
                                tabela[po_tabelce].setBounds(odstepy*po_odstepach,z*100+45, 220, 50);
                                po_odstepach++;
                                po_tabelce++;
                        }
                        wynik = "";
                }
                po_odstepach=1;
                z++;
                liczba_na_poziomie = 0;
        }
        frame.setLocation(0,0);
        frame.setVisible(true);
}
 
void w_przod()
{
        JFrame frame = new JFrame("Diagram wnioskowania");
        JPanel contentPane = (JPanel) frame.getContentPane();
        contentPane.setLayout(null);
        frame.setSize(new Dimension(1000, 600));
 
        JLabel[] tabela = new JLabel[20];
        int po_odstepach = 1;
        int po_tabelce = 0;
        int i,m,n;
        String wynik = "";
        int liczba_na_poziomie = 0;
        int odstepy;
        int z = 0;
        int k = 0;
        for(i=(ostatni_poziom-1); i>=0 ; i--)
        {
                /*for(m=(ostatni_poziom-1); m>=0; m--)
                {
                        if(poziomy[m].poziom == i)
                        {
                            liczba_na_poziomie ++;
                        }
                }*/
                odstepy = 1000 / (liczba_na_poziomie+1);
                for(m=(ostatni_poziom-1);m>=0;m--)
                {       
                        if(poziomy[m].poziom == i)
                        {
                                wynik = wyswietl( poziomy[m].regula, poziomy[m].ile_r);
                                wynik = wynik + ( " => ");
                                wynik = wynik + wyswietl( poziomy[m].wynik, poziomy[m].ile_w);
                                wynik = wynik + ( "\n          " );
                                if( poziomy[m].wartosc == 1 )
                                        wynik = wynik + ("T\n");
                                else
                                        wynik = wynik + ("F\n");
                                tabela[po_tabelce]=new JLabel(wynik);
                                contentPane.add(tabela[po_tabelce]);
                                tabela[po_tabelce].setBounds(0,k*100, 220, 50);
                                po_odstepach++;
                                po_tabelce++;
                                k++;
                        }
                        wynik = "";
                }
                po_odstepach=1;
                z++;
                liczba_na_poziomie = 0;
        }
        frame.setLocation(0,0);
        frame.setVisible(true);
}
 
void wnioskowanie(int tryb)
{
        int zwrotna;
        int ilu_arg_szukana;
        while(znajdz_szukana() != -1)
        {
		ostatni_poziom = 0;
                ilu_arg_szukana = rekordy[k].wartosci[m][0];
                zwrotna = zacznij_przypadek(ilu_arg_szukana, k, m, 0);
		if( b(tryb) )
		{
	                w_tyl();
		}
       		else
                	w_przod();
                rekordy[k].wartosci[m][1] = zwrotna;
        }
}
 
void sprawdzanie_spojnosci()
{
	int n, i, j, ile_arg;
	for(n=0; n<ile_danych; n++)
	{
		if(rekordy[n].ilosc > 1)
		{
			for(i=rekordy[n].ilosc-1; i>0; i--)
			{
				ile_arg = rekordy[n].wartosci[i][0]; 
				for(j=i-1; j>=0; j--)
				{
					if(ile_arg == 0)
					{	
						if(rekordy[n].wartosci[j][0] == 0)
						{
							System.out.print("Podane dane nie s¹ spójne!");
							System.exit(0);
						}
					}
					else if(ile_arg == rekordy[n].wartosci[j][0])
					{
                                            for(int p = 0; p<ile_arg; p++)
                                            {
                                                if(rekordy[n].wartosci[i][p+2] == rekordy[n].wartosci[j][p+2])
                                                {
                                                    System.out.print("Podane dane nie s¹ spójne!");
                                                    System.exit(0);
                                                }
                                            }
					}
				}
			}
		}
	}
	
	/*for(n=ile_wzorow-1; n>0; n--)
	{
		for(i=n-1; i>=0; i--)
		{
			if((reguly[n].ile_w == reguly[i].ile_w) && (reguly[n].wynik[1] == reguly[i].wynik[1]))
			{
				System.out.print("Podane wzory s¹ sprzeczne!");
				System.exit(0);
			}
		}
	}*/
}
 
public void zaczynamy(String szukane, String dane, String wzory, int tryb){
    Glowna program = new Glowna();
    program.wczytaj_dane (szukane, dane);
        program.wczytaj_wzory ( wzory );
 
	program.sprawdzanie_spojnosci();
 
        if((tryb == 0 ) == true )
                program.wnioskowanie(0);
        else if((tryb == 1 ) == true )
                program.wnioskowanie(1);
}
 
public static void main(String[] args) throws java.io.IOException{
        EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
            new ActionFrame();
        }
    });
}
}