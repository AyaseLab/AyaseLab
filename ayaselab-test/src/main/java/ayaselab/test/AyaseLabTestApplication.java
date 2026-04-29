package ayaselab.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ayaselab"})
public class AyaseLabTestApplication {
    public static void main(String[] args){
        try{
            SpringApplication.run(AyaseLabTestApplication.class, args);
            System.out.println("Ayase Lab Test App start successfully!");
        }catch (Exception ex){
            System.err.println(ex.getMessage());
        }
    }
}
