/**
 * Created by qianzise on 2017/5/25.
 */
public class Func {

    public String name;
    public String type;
    public int lev=-1;
    public int fadr=-1;
    public int ladr=-1;


    public Func(String name, String type,int lev) {
        this.name = name;
        this.type = type;
        this.lev=lev;
    }
}
