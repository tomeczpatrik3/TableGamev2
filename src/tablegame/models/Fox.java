package tablegame.models;

import java.util.Random;
import tablegame.enums.Action;
import static tablegame.enums.Action.ATTACK;
import static tablegame.enums.Action.DEFEND;
import static tablegame.enums.Action.MOVE;
import tablegame.utils.Position;

public class Fox implements RobotInterface{
    public enum BattleMode { KÖZELEDÉS, VÉDEKEZÉS, TÁMADÁS, MENEKÜLÉS };

    //A robot nevének tárolására:
    private String name;
    //A robot utolsó végrehajtott akciója:
    private Action lastAction;
    //A robot jelenlegi pozíciója:
    private Position pos;
    //Az aréna, amiben a robot szerepel:
    private Arena arena;
    //A robot maximális páncélja:
    private int maxArmor;
    //A robot aktuális páncélja:
    private int actualArmor;
    //A robot sebzése:
    private final int damage = 1;
    //A robot státusza:
    private boolean isAlive;
    
    //Fox robot speciális attribútumai:
    
    //Maximum menekülő körök száma:
    private final int maxRun = 3;    
    //Az érték, hogy milyen messzire kell eltávolodnia a másik robottól:
    private final int distance = 2;
    //Maximum rnd generálások száma:
    private final int maxRnd = 10;
    //Körök száma amíg menekült:
    private int runCnt;
    //Védekezett körök száma:
    private int defCnt;
    //Az ellenfél pozíciója a meneküléshez:
    private Position enemyPos;
    //Éppen mit tesz: Közeledik, Védekezik, Támad vagy Menekül
    private BattleMode battleMode;
    
    public Fox(String name, Arena arena, Position pos, int armor) {
        this.name = name;
        this.arena = arena;
        this.maxArmor = armor;
        this.actualArmor = armor;
        this.pos = pos;
        this.isAlive = true;
        this.battleMode = BattleMode.KÖZELEDÉS;
    }
      
    public Fox(String name, Arena arena, int x, int y, int armor) {
        this.name = name;
        this.arena = arena;
        this.maxArmor = armor;
        this.actualArmor = armor;
        this.pos = new Position(x,y);
        this.isAlive = true;
        this.battleMode = BattleMode.KÖZELEDÉS;
    }
    
    /*
        Név lekérdezése
    */
    @Override
    public String getName() {
        return this.name;
    }
    
    /*
        Név beállítása
    */
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    /*
        Aktuális pozíció lekérdezése
    */
    @Override
    public Position getActualPosition() {
        return this.pos;
    }
    
    /*
        Aktuális pozíció beállítása
    */
    @Override
    public void setActualPosition(int x, int y) {
        this.pos.setX(x);
        this.pos.setY(y);
    }
    
    /*
        Utolsó akció lekérdezése
    */
    @Override
    public Action getLastAction() {
        return this.lastAction;
    }
    
    /*
        Utolsó akció beállítása
    */
    @Override
    public void setLastAction(Action act) {
        this.lastAction = act;
    }
    
    /*
        Maximum páncél lekérdezése
    */
    @Override
    public int getMaxArmor() {
        return this.maxArmor;
    }
     
    /*
        Maximum páncél beállítása
    */
    @Override
    public void setMaxArmor(int armor) {
        this.maxArmor = armor;
    }
    
    /*
        Jelenlegi páncél lekérdezése
    */
    @Override
    public int getActualArmor() {
        return this.actualArmor;
    }
    
    /*
        Jelenlegi páncél beállítása, feltéve ha
        nem haladja meg a maximális páncélt
    */
    @Override
    public void setActualArmor(int armor) {
        if (armor <= this.maxArmor )
            this.actualArmor = armor;
    }
    
    /*
        Sebzés lekérdezése
    */
    @Override
    public int getDmg(){
        return this.damage;
    }
    
    /*
        Aréna méretének lekérdezése
    */
    @Override
    public int[] getArenaSize() {
        return this.arena.getSize();
    }
    
    /*
        Levonás a páncélból, ha páncél<=0, akkor a robot meghalt
    */
    @Override
    public void sufferDmg(int dmg) {
        this.actualArmor = this.actualArmor-dmg;
        if (this.actualArmor <= 0)
            this.isAlive = false;
    }
    
    /*
        Státusz lekérdezése:
    */
    @Override
    public boolean isAlive() {
        return this.isAlive;
    } 
   
    /*
        Az adott osztályra jellemző speciális cselekvés:
        "Amig a másik robot távol van tőle, addig közeledik hozzá, 
        majd védekezik két körön keresztűl, végül támad és 
        eltávolodik, míg bizonyos távolságra nem ér"
    */
    @Override
    public void performAction(Position pos) { 
        switch (this.battleMode) {
            case KÖZELEDÉS:
                this.lastAction = MOVE;
                getCloserToPosition(pos);
                if (tablegame.utils.Position.distance(this.pos, pos) == 1)
                    this.battleMode = BattleMode.VÉDEKEZÉS;
                break;
            case VÉDEKEZÉS:
                this.lastAction = DEFEND;
                this.defCnt++;
                if (this.defCnt>=2) {
                    this.battleMode = BattleMode.TÁMADÁS;
                    this.defCnt = 0;
                }
                break;
            case TÁMADÁS:
                this.lastAction = ATTACK;
                this.enemyPos = new Position(pos.getX(), pos.getY());
                this.battleMode = BattleMode.MENEKÜLÉS;
                break;
            case MENEKÜLÉS:
                this.lastAction = MOVE;
                if ( tablegame.utils.Position.distance(this.pos, this.enemyPos) >= this.distance || this.runCnt>= this.maxRun ) {
                    this.battleMode = BattleMode.KÖZELEDÉS;
                }
                else {
                    getAwayFromAPositon();
                    this.runCnt++;
                }
                break;
        }
    }
    
    /*
        Közeledés egy adott pozícióhoz:
    */
    public void getCloserToPosition(Position pos) {
        Position save = this.pos;
        
        if ( this.pos.getX() != pos.getX() ) {
            if (this.pos.getX() < pos.getX())
                this.pos = new Position(this.pos.getX()+1, this.pos.getY());
            else
                this.pos = new Position(this.pos.getX()-1, this.pos.getY());
        }
        else if ( this.pos.getY() != pos.getY() ) {
            if (this.pos.getY() < pos.getY())
                this.pos = new Position(this.pos.getX(), this.pos.getY()+1);
            else
                this.pos = new Position(this.pos.getX(), this.pos.getY()-1);            
        }
        
        //Hogy ne lépjenek "egymásra"
        if (this.pos.equals(pos))
            this.pos = save;
    }
    
    /*
        Távolodás egy adott pozíciótól, egy adott távolságra:
    */
    public void getAwayFromAPositon() {
        
    }
    
    @Override
    public void whoAmI() {
        System.out.println("Fox");
    }
}