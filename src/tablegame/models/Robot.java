package tablegame.models;

import tablegame.enums.Action;
import tablegame.utils.Position;

public abstract class Robot implements RobotInterface {
    //A robot nevének tárolására:
    protected String name;
    //A robot utolsó végrehajtott akciója:
    protected Action lastAction;
    //A robot jelenlegi pozíciója:
    protected Position pos;
    //Az aréna, amiben a robot szerepel:
    protected Arena arena;
    //A robot maximális páncélja:
    protected int maxArmor;
    //A robot aktuális páncélja:
    protected int actualArmor;
    //A robot sebzése:
    protected final int damage = 1;
    //A robot státusza:
    protected boolean isAlive;
    //Megtett lépések száma:
    protected int steps; 
    
    protected Robot(String name, Arena arena, Position pos, int armor) {
        this.name = name;
        this.arena = arena;
        this.maxArmor = armor;
        this.actualArmor = armor;
        this.pos = pos;
        this.isAlive = true;
        this.steps = 1;
    }
    
    protected Robot(String name, Arena arena, int x, int y, int armor) {
        this.name = name;
        this.arena = arena;
        this.maxArmor = armor;
        this.actualArmor = armor;
        this.pos = new Position(x,y);
        this.isAlive = true;
        this.steps = 1;
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
        Megtett lépések lekérdezése:
    */
    @Override
    public int getSteps() {
        return this.steps;
    } 
       
}
