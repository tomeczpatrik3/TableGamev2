package tablegame.models;

import tablegame.enums.Action;
import tablegame.utils.Position;

/*
    A megvalósításra váró függvények.
    A függvények leírása megtalálható a BaseEntity osztályban
*/
public interface RobotInterface {
    String getName();
    
    void setName(String name);
    
    Position getActualPosition();
    
    void setActualPosition(int x, int y);
    
    Action getLastAction();
    
    void setLastAction(Action act);
    
    int getMaxArmor();
    
    void setMaxArmor(int armor);
    
    int getActualArmor();
    
    void setActualArmor(int armor);
    
    int getDmg();
    
    void sufferDmg(int dmg);
    
    int[] getArenaSize();
    
    boolean isAlive();
    
    void performAction(Position pos);
    
    void whoAmI();
}
