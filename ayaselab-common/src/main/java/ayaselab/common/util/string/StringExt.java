package ayaselab.common.util.string;

/**
 * String工具类
 */
public class StringExt {

    /**
     * 校验String是否为null或""
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str){
        return str == null || str.isEmpty();
    }

    /**
     * 校验String是否为null或""或仅包含空格
     * @param str
     * @return
     */
    public static boolean isNullOrWhiteSpace(String str){
        if(isNullOrEmpty(str)){
            return true;
        }

        for(int i = 0; i < str.length(); i++){
            if(!Character.isWhitespace(str.charAt(i))){
                return false;
            }
        }
        return true;
    }
}
