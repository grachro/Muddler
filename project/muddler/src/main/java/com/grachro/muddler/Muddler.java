package com.grachro.muddler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import spark.utils.StringUtils;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.port;

public class Muddler {

    static String ROOT_PATH;
    public static Map<String, EntityManagerFactory> entityManagerFactoryMap = new HashMap<String, EntityManagerFactory>();

    public static void main(String... args) {

        String workspace = System.getProperty("workspace");
        if (StringUtils.isEmpty(workspace)) {
            workspace = new File(".").getAbsolutePath();
        }
        ROOT_PATH = workspace;
        System.out.println("ROOT_PATH=" + ROOT_PATH);

        String persistenceUnitNames = System
                .getProperty("persistenceUnitNames");

        if (persistenceUnitNames == null) {
            System.out.println("-DpersistenceUnitNames is nothing..");
            System.out.println("set persistenceUnitName:default");
            entityManagerFactoryMap.put("default", Persistence.createEntityManagerFactory("default"));
        } else {
            System.out.println("-DpersistenceUnitNames is "
                    + persistenceUnitNames);
            for (String name : persistenceUnitNames.split(",")) {
                System.out.println("set persistenceUnitName:" + name);
                entityManagerFactoryMap.put(name,
                        Persistence.createEntityManagerFactory(name));
            }
        }

        port(48088);

        before((request, response) -> {
            RequestManager.before(request);
        });

        after((request, response) -> {
            RequestManager.after(request);
        });

        get("/hello", (req, res) -> "This is Muddler.");

        GroovyLoader.init("config.groovy");

        get("/reloadConfig", (request, response) -> {
            GroovyLoader.init("config.groovy");
            return "ok";
        });

    }
}
