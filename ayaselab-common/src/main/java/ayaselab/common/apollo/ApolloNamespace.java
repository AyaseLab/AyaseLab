package ayaselab.common.apollo;

public enum ApolloNamespace {

    Default("application"),

    Root("Root"),

    HelpText("HelpText"),

    ServiceCollection("ServiceCollection");

    private final String mappingKey;

    public String mappingKey(){
        return mappingKey;
    }

    ApolloNamespace(String mappingKey){
        this.mappingKey = mappingKey;
    }
}
