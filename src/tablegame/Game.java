package tablegame;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static tablegame.enums.Action.*;
import static tablegame.utils.RobotUtils.areCloseEnough;

import tablegame.models.Arena;
import tablegame.utils.Position;
import tablegame.models.RobotInterface;
import static tablegame.utils.RobotUtils.getActualArmor;
import static tablegame.utils.RobotUtils.getActualPosition;
import static tablegame.utils.RobotUtils.getDmg;
import static tablegame.utils.RobotUtils.getLastAction;
import static tablegame.utils.RobotUtils.isAlive;
import static tablegame.utils.RobotUtils.sufferDmg;

public class Game {
    private Arena pvpArena;
    private Class aRobotClass;
    private Class bRobotClass;
    private Constructor aRobotCtor;
    private Constructor bRobotCtor;
    private Object aRobot;
    private Object bRobot;
    private int maxRound;
    private int roundCnt;
    
    public Game(int n, int m, String aClassName, String bClassName, int maxRound) {
        try {
            this.maxRound = maxRound;
            
            /*
                Aréna inicializálása:
            */
            this.pvpArena = new Arena(n,m);
            
            /*
                Adott osztály kiválasztása az input alapján
            */
            this.aRobotClass = Class.forName("tablegame.models."+aClassName);
            this.bRobotClass = Class.forName("tablegame.models."+bClassName);
            /*
                A paramétereknek megfelelő konstruktor kiválasztása
            */
            this.aRobotCtor = aRobotClass.getConstructor(new Class[]{String.class, Arena.class, Position.class, int.class});
            this.bRobotCtor = bRobotClass.getConstructor(new Class[]{String.class, Arena.class, Position.class, int.class}); 
            
            this.initRobots("A", "B");
            
        } catch (ClassNotFoundException ex) {
            System.err.println("Nem létező osztály");
            ex.printStackTrace();
            System.exit(1);
        } catch (NoSuchMethodException ex) {
            System.err.println("Nem létező metódus");
            ex.printStackTrace();
            System.exit(2);
        } catch (SecurityException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(3);
        }
    }
    
    /*
        Robotok inicializálása
    */
    private void initRobots(String aName, String bName) {
        /*
            Random pozíciók generálása a robotoknak:
        */
        Position aPos = pvpArena.getRndSpawn();
        Position bPos = pvpArena.getRndSpawn();
        while (aPos.getX()==bPos.getX() || aPos.getY()==bPos.getY()) {
            bPos = pvpArena.getRndSpawn();
        }
        
        /*
            Objektum inicalizálása:
        */
        try {
            this.aRobot = aRobotCtor.newInstance(aName, this.pvpArena, aPos, 5);
            this.bRobot = bRobotCtor.newInstance(bName, this.pvpArena, bPos, 3);    
        } catch (Exception ex) {
            System.err.println("Hiba az objektumok (robotok) inicializálása közben");
            ex.printStackTrace();
            System.exit(4);
        }
    }
    
    public void run() {
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        defaultTerminalFactory.setSwingTerminalFrameTitle("Table Game 0.1");
        
        Terminal terminal = null;
        
        try {
            terminal = defaultTerminalFactory.createTerminal();

            terminal.enterPrivateMode();
            terminal.setCursorVisible(false);

            TextGraphics textGraphics = terminal.newTextGraphics();
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

            //Kör számláló és logikai változó inicializálása:
            roundCnt = 0;
            boolean atLeastOneIsDead = false;
            
            //
            Scanner sc = new Scanner(System.in);
            /*
                A játék addig tart amíg el nem jutunk egy adott körig (maxRound)
                vagy valaki nem győz
            */
            while (!atLeastOneIsDead && roundCnt <= this.maxRound) {                    
                //Robotok léptetése:
                
                if (roundCnt != 0) {
                    ((RobotInterface)aRobot).performAction( getActualPosition(bRobot) );
                    ((RobotInterface)bRobot).performAction( getActualPosition(aRobot) );
                }
                    
                //Ellenorzés, elég közel állnak-e a robotok egymáshoz?
                if ( areCloseEnough( (RobotInterface)aRobot, (RobotInterface)(bRobot) )) {
                    simulateFight();
                }
                
                textGraphics.putString(1, 1, roundCnt + ". kör:", SGR.BOLD);
                buildArena(textGraphics);
                showStatistics(textGraphics);

                terminal.flush();   

                if (!areTheRobotsAlive()) {
                    atLeastOneIsDead = true;
                }
                else {
                    roundCnt++;
                }
                
                //Sleep:
                //TimeUnit.SECONDS.sleep(3);
                if (roundCnt!=0)
                    log();
                System.out.println();
                sc.nextLine();
            }
            
            announceTheResult(textGraphics);
            
            textGraphics.putString( 1 , this.pvpArena.getSize()[0]+15, "A terminál 10 másodpercen belül bezáródik...", SGR.BOLD);
            TimeUnit.SECONDS.sleep(10);
            terminal.exitPrivateMode();
            
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Hibás paraméterezés!");
            System.exit(1);
        }
    }
    
    private void log() {
        System.out.println("----- " + roundCnt + ". kör: -----");
        System.out.print(((RobotInterface)aRobot).getName()+" robot ("+ aRobotClass.getSimpleName()+ ") cselekvése: "+ getLastAction(aRobot));
        System.out.println(" - "+getActualPosition(aRobot).getX() + "," +getActualPosition(aRobot).getY());
        System.out.print(((RobotInterface)bRobot).getName()+" robot ("+ bRobotClass.getSimpleName()+ ") cselekvése: "+ getLastAction(bRobot));
        System.out.println(" - "+getActualPosition(bRobot).getX() + "," +getActualPosition(bRobot).getY());
    }
    
    /*
        Csata szimulálása a két robot cselekvései alapján:
    */
    private void simulateFight() {
        if ( getLastAction(aRobot) == ATTACK && getLastAction(bRobot) == ATTACK  ) {
            sufferDmg(aRobot, getDmg(bRobot));
            sufferDmg(bRobot, getDmg(aRobot));
        }
        if ( getLastAction(aRobot) == ATTACK && getLastAction(bRobot) == MOVE  ) {
            sufferDmg(bRobot, getDmg(aRobot));
        }
        if ( getLastAction(bRobot) == ATTACK && getLastAction(aRobot) == MOVE  ) {
            sufferDmg(aRobot, getDmg(bRobot));
        }
        //Egyéb esetekben nem történik páncélvesztés
    }
    
    private boolean areTheRobotsAlive() {
        return ( isAlive(aRobot) == true ) && ( isAlive(bRobot) == true );
    }
    
    //Aréna "felépítése" a terminálon belül:
    private void buildArena(TextGraphics textGraphics) {
        for (int i=0; i<this.pvpArena.getSize()[0]+2; i++) {
            for (int j=0; j<this.pvpArena.getSize()[1]+2; j++) {
                if ( getActualPosition(aRobot).getX()+1 == i && getActualPosition(aRobot).getY()+1 == j)
                    textGraphics.putString(i+1, j+3, "A", SGR.BOLD);
                else if (getActualPosition(bRobot).getX()+1 == i && getActualPosition(bRobot).getY()+1 == j)
                    textGraphics.putString(i+1, j+3, "B", SGR.BOLD);
                else if ( i==0 || j==0 || i==this.pvpArena.getSize()[0]+1 || j==this.pvpArena.getSize()[1]+1)
                    textGraphics.putString(i+1, j+3, "#", SGR.BOLD); 
                else
                    textGraphics.putString(i+1, j+3, ".", SGR.BOLD); 
            }
        }
    }
    
    //Robotok statisztikáinak megjelenítése:
    private void showStatistics(TextGraphics textGraphics) {
        textGraphics.putString( 1 , this.pvpArena.getSize()[0]+6, "\""+ ((RobotInterface)aRobot).getName() +"\" robot: " + this.aRobotClass.getSimpleName() + ".class", SGR.BOLD);
        textGraphics.putString( 1 , this.pvpArena.getSize()[0]+7, "Páncél: " + getActualArmor(aRobot) + "/" + ((RobotInterface)aRobot).getMaxArmor() + "   ", SGR.BOLD);
        textGraphics.putString( 1 , this.pvpArena.getSize()[0]+9, "\""+ ((RobotInterface)bRobot).getName() + "\" robot: " + this.bRobotClass.getSimpleName() + ".class", SGR.BOLD);
        textGraphics.putString( 1 , this.pvpArena.getSize()[0]+10, "Páncél: " + getActualArmor(bRobot) + "/" + ((RobotInterface)bRobot).getMaxArmor() + "   ", SGR.BOLD);    
}
    
    /*
        Eredmény kihirdetése:
        -Ha az egyik robot már nem él, akkor a másik a győztes
        -Ha mindkettő él, akkor a páncéljaikat hasonlítjuk össze
    */
    private void announceTheResult(TextGraphics tg) {
        tg.putString( 1 , this.pvpArena.getSize()[0]+12, "--- A játék véget ért --- ", SGR.BOLD);
        if ( isAlive(aRobot) == true && isAlive(bRobot) == false ) {
            tg.putString( 1 , this.pvpArena.getSize()[0]+13, "A győztes: " + ((RobotInterface)aRobot).getName() + " robot (a másik elpusztult)", SGR.BOLD);
        }
        else if ( isAlive(bRobot) == true && isAlive(aRobot) == false ) {
            tg.putString( 1 , this.pvpArena.getSize()[0]+13, "A győztes: " + ((RobotInterface)bRobot).getName() + " robot (a másik elpusztult)", SGR.BOLD);
        }
        else if ( isAlive(bRobot) == false && isAlive(aRobot) == false ) {
            tg.putString( 1 , this.pvpArena.getSize()[0]+13, "Mindkét robot elpusztult: a játék döntetlen", SGR.BOLD);
        }
        else { //Ha mindkét robot túlélte a játékot:
            if ( getActualArmor(aRobot) > getActualArmor(bRobot) )
                tg.putString( 1 , this.pvpArena.getSize()[0]+13, "A győztes: " + ((RobotInterface)aRobot).getName() + " robot (páncél alapján)", SGR.BOLD);
            if ( getActualArmor(bRobot) > getActualArmor(aRobot) )
                tg.putString( 1 , this.pvpArena.getSize()[0]+13, "A győztes: " + ((RobotInterface)bRobot).getName() + " robot (páncél alapján)", SGR.BOLD);
            else
                tg.putString( 1 , this.pvpArena.getSize()[0]+13, "Mindkét robot túlélte, de a páncéljuk egyenlő: a játék döntetlen", SGR.BOLD);
        }
    }     
}