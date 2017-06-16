package helpers;

import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.BaseFont;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maksim on 12.06.2017.
 */
public class ITEXTPDF {
    static Map<String, BaseFont> cache = new HashMap<>();
    public static BaseFont getFontByName(String fontName){
        if (cache.containsKey(fontName)){
            return  cache.get(fontName);
        }

        BaseFont font;
        try{
            FontFactory.registerDirectories();
            font = FontFactory.getFont(fontName, BaseFont.IDENTITY_H, true ).getBaseFont();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        cache.put(fontName, font);
        return font;
    }
}
