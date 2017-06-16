package helpers;

/**
 * Created by Souverain73 on 31.05.2017.
 */
public class ID {
    private static int last;
    private static int lastTemplate = -2;

    public static int getNext(){
        return last++;
    }
    public static void use(int id){
        last = Math.max(last, id+1);
    }

    public static int getNextTemplate() {return lastTemplate--;}
    public static void useTemplate(int id) {lastTemplate = Math.min(id, lastTemplate);}
}
