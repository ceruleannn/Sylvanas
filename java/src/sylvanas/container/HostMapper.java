package sylvanas.container;

import sylvanas.component.lifecycle.LifecycleState;

import java.util.HashMap;
import java.util.Map;

/**
 * <Context path="/index/abc" docBase="Sylvanas" reloadable="true" />
 * path = 访问url , docBase = 项目磁盘路径, 当放在webApp文件夹中为项目名
 *
 * for host:
 * key = path (默认是项目名) ;  value = context
 * if url starts with host key (或逐字符匹配,同时匹配取较长的 /app, /app/a1 匹配后者)
 */
public class HostMapper{

    private final Map<String, Context> contextMap = new HashMap<>();

    public HostMapper(){

    }

    public void addContext(Context context, String path){

        contextMap.put(path,context);
    }

    public Context map(String uri){

        int longest = 0;
        Context re = null;
        for (Map.Entry<String, Context> stringContextEntry : contextMap.entrySet()) {
            String key = stringContextEntry.getKey();
            if (uri.startsWith(key)){

                if (re==null||key.length()>longest){
                    longest = key.length();
                    re = stringContextEntry.getValue();
                }

            }
        }
        if (re!=null){
            if (!LifecycleState.STARTED.equals(re.getState())){
                re = null;
            }
        }
        return re;
    }

}

