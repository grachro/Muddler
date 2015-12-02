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
		if(StringUtils.isEmpty(workspace)) {
			workspace = new File(".").getAbsolutePath();
		}
		ROOT_PATH = workspace;
		System.out.println("ROOT_PATH=" + ROOT_PATH);
		
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
