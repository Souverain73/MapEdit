package helpers;

import java.util.Map;

/**
 * Created by Souverain73 on 09.06.2017.
 */
public class Params {
    public static String paramsToXmlString(Map<String, String> params){
        StringBuilder sb = new StringBuilder();
        for (String key : params.keySet()){
            sb.append(XML.tag(key, params.get(key).toString()));
            sb.append("\n");
        }

        return sb.toString();
    }
}
