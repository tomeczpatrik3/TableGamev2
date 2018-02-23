package tablegame.models;

import static tablegame.enums.Action.ATTACK;
import static tablegame.enums.Action.MOVE;
import tablegame.utils.Position;

public final class Wolf extends Robot{
    public Wolf(String name, Arena arena, Position pos, int armor) {
        super(name, arena, pos, armor);
    }
    
    public Wolf(String name, Arena arena, int x, int y, int armor) {
        super(name, arena, x, y, armor);
    }
    
    /*
        Az adott osztályra jellemző speciális cselekvés:
        "Közeledik az ellenfélhez, majd folyamatosan támad, míg ő vagy a másik ki nem nyúlik"
    */
    @Override
    public void performAction(Position pos) {
        this.steps++;
        
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
