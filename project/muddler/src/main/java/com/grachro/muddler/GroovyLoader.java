package com.grachro.muddler;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

import java.util.Map.Entry;

public class GroovyLoader {

	private static String getPath() {
		return Muddler.ROOT_PATH + "/script";
	}
	
	public static void init(String script) {
		try {
			String path = getPath();
			GroovyScriptEngine gse = new GroovyScriptEngine(path);
			gse.run(script, "nothing param");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static RequestManager load(String script, RequestManager rm) {

		try {
			String path = getPath();
			GroovyScriptEngine gse = new GroovyScriptEngine(path);

			Binding bind = new Binding();
			bind.setVariable("rm", rm);

			for (Entry<String, Object> e : rm.getModels().entrySet()) {
				bind.setVariable(e.getKey(), e.getValue());
			}

			gse.run(script, bind);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return rm;
	}
}
