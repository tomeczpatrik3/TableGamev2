package tablegame.utils;

import tablegame.enums.Action;
import tablegame.models.RobotInterface;

public class RobotUtils {   
    /*
        Robotok távolságának ellenőrzése:
    */
    public static <T extends RobotInterface> boolean areCloseEnough(T a, T b) {
        return ( Position.distance(a.getActualPosition(), b.getActualPosition()) == 1 );
    }
    
    //Néhány gyakran használt castolás a rövidebb kód érdekében:
    public static Action getLastAction(Object robot) {
        return ((RobotInterface)robot).getLastAction();
    }
    
    public static int getDmg(Object robot) {
        return ((RobotInterface)robot).getDmg();
    }
    
    public static void sufferDmg(Object robot, int dmg) {
        ((RobotInterface)robot).sufferDmg(dmg);
    }
    
    public static boolean isAlive(Object robot) {
        return ((RobotInterface)robot).isAlive();
    }
    
    public static int getActualArmor(Object robot) {
        return ((RobotInterface)robot).getActualArmor();
    }
    
    public static Position getActualPosition(Object robot) {
        return ((RobotInterface)robot).getActualPosition();
    }
    //-----
}
