package tablegame.models;

import tablegame.enums.Action;
import static tablegame.enums.Action.ATTACK;
import static tablegame.enums.Action.MOVE;
import tablegame.utils.Position;

public class Wolf implements RobotInterface{
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
    private final int damage = 2;
    //A robot státusza:
    private boolean isAlive;
    
    public Wolf(String name, Arena arena, Position pos, int armor) {
        this.name = name;
        this.arena = arena;
        this.maxArmor = armor;
        this.actualArmor = armor;
        this.pos = pos;
        this.isAlive = true;
    }
    
    
    public Wolf(String name, Arena arena, int x, int y, int armor) {
        this.name = name;
        this.arena = arena;
        this.maxArmor = armor;
        this.actualArmor = armor;
        this.pos = new Position(x,y);
        this.isAlive = true;
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
        "Közeledik az ellenfélhez, majd folyamatosan támad, míg ő vagy a másik ki nem nyúlik"
    */
    @Override
    public void performAction(Position pos) {
        if ( tablegame.utils.Position.distance(this.pos, pos) == 1 ) {
            //Támadás
            this.lastAction = ATTACK;
        }
        else { //Közeledés
            this.lastAction = MOVE;
            getCloserToPosition(pos);
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
        else {
            if (this.pos.getY() < pos.getY())
                this.pos = new Position(this.pos.getX(), this.pos.getY()+1);
            else
                this.pos = new Position(this.pos.getX(), this.pos.getY()-1);            
        }
        
        //Hogy ne lépjenek "egymásra"
        if (this.pos.equals(pos))
            this.pos = save;
    }
    
    @Override
    public void whoAmI() {
        System.out.println("Wolf");
    }
}
