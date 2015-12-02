package com.grachro.muddler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.persistence.EntityManager;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.ResultType;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.sessions.DatabaseRecord;

import com.grachro.muddler.TsvLine.TsvLineNull;

public class Tsv {

	public static Tsv start(RequestManager rm, Consumer<Tsv> closure) {
		Tsv tsv = new Tsv(rm);
		closure.accept(tsv);
		return tsv;
	}

	private RequestManager rm;
	private EntityManager em;
	private List<String> fieldNames = new ArrayList<String>();
	private List<TsvLine> records = new ArrayList<TsvLine>();

	private Map<String, TsvLine> indexMap;

	public Tsv(RequestManager rm) {
		this.rm = rm;
	}

	public Tsv database(String db) {
		this.em = this.rm.getEntityManager(db);
		return this;
	}

	public TsvLine loadFirst(String sql) {
		this.load(() -> {
			return sql;
		});
		return this.getFirst();
	}

	public TsvLine loadFirst(String sql, Consumer<TsvLine> first) {
		TsvLine line = this.loadFirst(sql);
		first.accept(line);
		return line;
	}

	public TsvLine loadFirst(Supplier<String> sql, Consumer<TsvLine> first) {
		TsvLine line = this.load(sql).getFirst();
		first.accept(line);
		return line;
	}

	public Tsv load(String sql) {
		return this.load(() -> {
			return sql;
		});
	}

	public Tsv load(Supplier<String> sql) {

		this.fieldNames = new ArrayList<String>();
		this.records = new ArrayList<TsvLine>();

		String sSql = sql.get();

		System.out.println("#load#################");
		System.out.println(sSql);
		System.out.println("######################");

		@SuppressWarnings("unchecked")
		List<DatabaseRecord> list = this.em.createNativeQuery(sSql).setHint(QueryHints.RESULT_TYPE, ResultType.Map).getResultList();

		boolean first = true;
		for (DatabaseRecord record : list) {
			if (first) {
				for (DatabaseField f : record.getFields()) {
					this.fieldNames.add(f.getName());
				}
				first = false;
			}

			TsvLine m = new TsvLine();
			for (String fieldName : this.fieldNames) {
				m.put(fieldName, record.get(fieldName));
			}

			this.records.add(m);
		}

		return this;
	}

	public List<String> getFieldNames() {
		return Collections.unmodifiableList(this.fieldNames);
	}

	public List<TsvLine> getRecords() {
		return this.records;
	}

	public TsvLine getFirst() {
		return this.records.get(0);
	}

	public Tsv forEach(Consumer<TsvLine> executer) {
		for (TsvLine line : this.records) {
			executer.accept(line);
		}
		return this;
	}

	public Tsv addIndex(Function<TsvLine, String> key) {

		if (this.indexMap == null) {
			this.indexMap = new HashMap<String, TsvLine>();
		}
		for (TsvLine line : this.records) {
			String k = key.apply(line);
			this.indexMap.put(k, line);
		}
		return this;
	}

	public TsvLine seek(String key) {
		TsvLine line = this.indexMap.get(key);
		if (line == null) {
			return new TsvLineNull();
		}
		return line;
	}

	public void viewParam(String name) {
		this.rm.setViewModel(name, this);
	}

	public void viewParam(String name, Object o) {
		this.rm.setViewModel(name, o);
	}

	public Map<String, Object> getViewParam() {
		return this.rm.getModels();
	}
}
