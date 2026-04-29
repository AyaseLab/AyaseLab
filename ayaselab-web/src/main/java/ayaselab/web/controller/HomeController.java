package ayaselab.web.controller;

import ayaselab.common.apollo.ApolloConfigReader;
import ayaselab.common.apollo.ApolloNamespace;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/Home")
public class HomeController {

    @Resource
    private ApolloConfigReader apolloConfigReader;

    @RequestMapping("/testApollo")
    public String testApollo(String apolloKey){
        return apolloConfigReader.getValue(apolloKey, ApolloNamespace.Root);
    }
}
