package muddler

import static spark.Spark.get;

import java.util.Map;
import java.util.function.Consumer;

import com.grachro.muddler.GroovyLoader;
import com.grachro.muddler.RequestManager;
import com.grachro.muddler.Table;
import com.grachro.muddler.TableRecord;

class Page {

	static void redirect(url, toPath) {
		//このgetはspark.Spark.get
		get(url) { request, response ->
			response.redirect(toPath);
		}
	}

	static void add(url, controlFile) {
		//このgetはspark.Spark.get
		get(url) { request, response ->
			RequestManager rm = new RequestManager(request)
			GroovyLoader.load(controlFile, rm)

			def viewString = rm.getModel("viewString")
			return viewString
		}
	}

	
	static start(rm, Closure cl) {


		if (cl == null) {
			throw new IllegalArgumentException("Closure cl is null.");
		}

		println "Page#start::2"
		
		Page page = new Page(rm)
		cl.delegate = page
		cl.call(page)

		println "Page#start::3"

	}

	private RequestManager rm
	private def database
	
	private Page(RequestManager rm) {
		this.rm = rm
	}

	public void database(database) {
		this.database = database
	}

	public Table loadTable(String sql) {
		
		println "Page#loadTable::START database=" + database + " sql=" + sql

		Table table = new Table(this.rm)
		table.database(database)
		table.load(sql)

		println "Page#loadTable::END"

		return table
	}

	public TableRecord loadFirst(String sql) {

		println "Page#loadFirst::START database=" + database + " sql=" + database
		
		Table table = new Table(this.rm)
		table.database(database)
		table.load(sql)

		TableRecord line = table.loadFirst(sql);

		println "Page#loadFirst::END"
		
		return line;
	}
	
	public void viewParam(String name, Object o) {
		this.rm.setViewModel(name, o);
	}

	public void viewParamDefault(String name, Object o) {
		this.rm.setDefault(name, o);
	}

	public Map<String, Object> getViewParam() {
		return this.rm.getModels();
	}

	public void setView(viewPath) {

		def f = new File('script/' + viewPath)
		def engine = new groovy.text.SimpleTemplateEngine()
		def binding = this.rm.getModels()
		def template = engine.createTemplate(f).make(binding)
		def viewString = template.toString()
		this.rm.setViewModel("viewString",viewString)
	}
	
	public String toTsv(String filePath,Table table){
		def f = new File(filePath)
		f.withWriter {
			//table.fieldNames.eachWithIndex{fieldName,i->
			//	if (i > 0) {
			//		it << "\t"
			//	}
			//	it << "${fieldName}"
			//}
			//it << "\n"
	
			table.records.each{line->
				table.fieldNames.eachWithIndex{fieldName,i->
					if (i > 0) {
						it << "\t"
					}
					it << "${line.get(fieldName)}"
				}
				it << "\n"
			}
		}
		
		return f.getAbsolutePath()
	}

	public Table createEmptyTable() {
		new Table(this.rm)
	}
}