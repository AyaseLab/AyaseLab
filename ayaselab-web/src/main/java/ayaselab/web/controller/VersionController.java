package ayaselab.web.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController
@RequestMapping("/Version")
public class VersionController {

    private ApplicationContext applicationContext;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = {"", "/index"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public String index(){
        return "Ayase Lab API: Java 1.0.0";
    }
}
