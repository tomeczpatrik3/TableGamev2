package tablegame.models;

import java.util.Random;
import static tablegame.enums.Action.ATTACK;
import static tablegame.enums.Action.DEFEND;
import static tablegame.enums.Action.MOVE;
import tablegame.utils.Position;

public final class Fox extends Robot{
    public enum BattleMode { KÖZELEDÉS, VÉDEKEZÉS, TÁMADÁS, MENEKÜLÉS };
    //Maximum menekülő körök száma:
    private final int maxRun = 5;    
    //Az érték, hogy milyen messzire kell eltávolodnia a másik robottól:
    private final int distance = 3;
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
        super(name, arena, pos, armor);
        this.battleMode = BattleMode.KÖZELEDÉS;
    }
      
    public Fox(String name, Arena arena, int x, int y, int armor) {
        super(name, arena, x, y, armor);
        this.battleMode = BattleMode.KÖZELEDÉS;
    }
    
    /*
        Az adott osztályra jellemző speciális cselekvés:
        "Amig a másik robot távol van tőle, addig közeledik hozzá, 
        majd védekezik két körön keresztűl, végül támad és 
        eltávolodik, míg bizonyos távolságra nem ér"
    */
    @Override
    public void performAction(Position pos) { 
        this.steps++;
        
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
        (Lépések generálása, addig amíg megfelelő lépést nem kapunk
        (azaz távolodunk), vagy el nem érjük a maximum próbák számát)
    */
    public void getAwayFromAPositon() {
        Position posClone = new Position(this.pos.getX(), this.pos.getY());
        double minDistance = tablegame.utils.Position.distance(this.pos, this.enemyPos);
        
        int cnt = 0;
        
        while ((cnt <= this.maxRnd && minDistance >= tablegame.utils.Position.distance(this.pos, this.enemyPos)) || !this.arena.isValidPosition(this.pos)) {
            genRndMove(posClone);
            cnt++;
        }

    }
    
    /*
        Véletlenszerű lépés generálása:
    */
    public void genRndMove(Position posClone) {  
        Random rnd = new Random();
        int rndNum = rnd.nextInt(4);

        switch(rndNum) {
            case 0: this.pos = new Position(posClone.getX()+1, posClone.getY()); break;
            case 1: this.pos = new Position(posClone.getX()-1, posClone.getY()); break;
            case 2: this.pos = new Position(posClone.getX(), posClone.getY()+1); break;
            case 3: this.pos = new Position(posClone.getX(), posClone.getY()-1); break;
        }  
    }
    
    @Override
    public void whoAmI() {
        System.out.println("Fox");
    }
}