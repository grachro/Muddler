package system

import static spark.Spark.get;

import com.grachro.muddler.GroovyLoader;
import com.grachro.muddler.RequestManager;
import com.grachro.muddler.View;

class Page {

    static void redirect(url, toPath) {
		//このgetはspark.Spark.get
        get(url) { request, response -> 
            response.redirect(toPath);
        }
    }
    
    static void add(url, controlFile, viewFile) {
        //このgetはspark.Spark.get
		get(url) { request, response ->
            RequestManager rm = new RequestManager(request)
            GroovyLoader.load(controlFile, rm)
            return View.edit(rm.getModels(), viewFile)
        }
    }
    
 
}