package ayaselab.common.apollo;

import ayaselab.common.util.string.StringExt;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class ApolloConfigReader {

    private final Map<String, ApolloNamespace> nsMap = new ConcurrentHashMap<>();
    private final Map<ApolloNamespace, Config> configMap = new ConcurrentHashMap<>();
    private final Map<ApolloNamespace, LinkedList<Consumer<ConfigChangeEvent>>> onchangeMap = new ConcurrentHashMap<>();

    /**
     * Apollo配置初始化
     */
    @PostConstruct
    public void init(){
        for(ApolloNamespace ns : ApolloNamespace.values()){
            String mappingKey = ns.mappingKey();
            if(!StringExt.isNullOrWhiteSpace(mappingKey)){
                nsMap.put(mappingKey, ns);
                configMap.put(ns, ConfigService.getConfig(mappingKey));
                onchangeMap.put(ns, new LinkedList<>());
            }
        }
    }

    public String getValue(String key, ApolloNamespace ns){
        if(!configMap.containsKey(ns) || StringExt.isNullOrEmpty(key)){
            return "";
        }

        return configMap.get(ns).getProperty(key, "");
    }

}
