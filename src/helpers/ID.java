package helpers;

/**
 * Created by Souverain73 on 31.05.2017.
 */
public class ID {
    private static int last;

    public static int getNext(){
        return last++;
    }
    public static void use(int id){
        last = Math.max(last, id+1);
    }
}
