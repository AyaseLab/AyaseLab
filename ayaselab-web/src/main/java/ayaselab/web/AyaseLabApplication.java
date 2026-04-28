package ayaselab.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ayaselab"})
public class AyaseLabApplication {
    public static void main(String[] args){
        try{
            SpringApplication.run(AyaseLabApplication.class, args);
            System.out.println("Ayase Lab start successfully!");
        }catch (Exception ex){
            System.err.println(ex.getMessage());
        }
    }
}
