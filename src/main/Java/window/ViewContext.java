package window;

/**
 * Created by Souverain73 on 25.05.2017.
 */
public class ViewContext {
    public static final double FIXED_SCALE = 85000;

    public double getCollideRadius() {
        if (fixedSize){
            return 1/FIXED_SCALE;
        }
        return 1/getScale();
    }

    public static enum DrawMode{
        FULL,
        SIMPLE
    }

    double x;
    double y;
    double pdfWidth;
    double pdfHeight;
    double scalex = 1;
    double scaley = 2;
    DrawMode drawMode;
    boolean showText;
    boolean fixedSize;

    public ViewContext setX(double x) {
        this.x = x;
        return this;
    }

    public ViewContext setY(double y) {
        this.y = y;
        return this;
    }

    public ViewContext setScalex(double scalex) {
        this.scalex = scalex;
        return this;
    }

    public ViewContext setScaley(double scaley) {
        this.scaley = scaley;
        return this;
    }

    public ViewContext setScale(double scale){
        this.scalex = scale;
        this.scaley = 2 * scale;
        return this;
    }

    public ViewContext setShowText(boolean showText) {
        this.showText = showText;
        return this;
    }

    public ViewContext setFixedSize(boolean fixedSize) {
        this.fixedSize = fixedSize;
        return this;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getScale(){
        return scalex;
    }

    public double getScalex() {
        return scalex;
    }

    public double getScaley() {
        return scaley;
    }

    public DrawMode getDrawMode() {
        return drawMode;
    }

    public boolean isShowText() {
        return showText;
    }

    public boolean isFixedSize() {
        return fixedSize;
    }

    public ViewContext setDrawMode(DrawMode drawMode) {
        this.drawMode = drawMode;
        return this;
    }

    public void reset(){
        x = 0;
        y = 0;
        scalex = 1;
    }

    public void move(double dx, double dy){
        x += dx;
        y += dy;
    }

    public double vtix(double x){
        return (x - this.x) / scalex;
    }

    public double vtiy(double y){
        return (y - this.y) / scaley;
    }

    public double absX(double x){
        return this.x + x*scalex;
    }

    public double absY(double y){
        return this.y + y*scaley;
    }

    public void setPdfSize(double w, double h){
        pdfHeight = h;
        pdfWidth = w;
    }

    public double pdfx(double x){
        return vtix(x);
    }

    public double pdfy(double y){
        return pdfHeight - vtiy(y);
    }

    @Override
    public String toString() {
        return "ViewContext{" +
                "x=" + x +
                ", y=" + y +
                ", scalex=" + scalex +
                '}';
    }
}
