package ayaselab.common.apollo;

import ayaselab.common.util.string.StringExt;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Component
public class ApolloConfigReader {

    private final Map<String, ApolloNamespace> nsMap = new ConcurrentHashMap<>();
    private final Map<ApolloNamespace, Config> configMap = new ConcurrentHashMap<>();
    private final Map<ApolloNamespace, List<Consumer<ConfigChangeEvent>>> onchangeMap = new ConcurrentHashMap<>();

    /**
     * Apollo配置初始化
     */
    @PostConstruct
    public void init(){
        for(ApolloNamespace ns : ApolloNamespace.values()){
            String mappingKey = ns.mappingKey();
            if(!StringExt.isNullOrWhiteSpace(mappingKey)){
                nsMap.put(mappingKey, ns);
                Config config = ConfigService.getConfig(mappingKey);
                configMap.put(ns, config);
                onchangeMap.put(ns, new CopyOnWriteArrayList<>());
                config.addChangeListener(event -> notifyChange(ns, event));
            }
        }
    }

    public String getValue(String key, ApolloNamespace ns){
        return getValue(key, ns, "");
    }

    public String getValue(String key, ApolloNamespace ns, String defaultValue){
        if(ns == null || !configMap.containsKey(ns) || StringExt.isNullOrEmpty(key)){
            return defaultValue;
        }

        return configMap.get(ns).getProperty(key, defaultValue);
    }

    public void addChangeListener(ApolloNamespace ns, Consumer<ConfigChangeEvent> listener){
        if(ns == null || listener == null || !onchangeMap.containsKey(ns)){
            return;
        }

        onchangeMap.get(ns).add(listener);
    }

    private void notifyChange(ApolloNamespace ns, ConfigChangeEvent event){
        List<Consumer<ConfigChangeEvent>> listeners = onchangeMap.get(ns);
        if(listeners == null || listeners.isEmpty()){
            return;
        }

        for(Consumer<ConfigChangeEvent> listener : listeners){
            listener.accept(event);
        }
    }

}
