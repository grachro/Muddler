package com.grachro.muddler;

import java.util.ArrayList;
import java.util.Collection;
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

import com.grachro.muddler.TableRecord.TableEnptyRecord;

public class Table {

	private RequestManager rm;
	private EntityManager em;
	private List<String> fieldNames = new ArrayList<String>();
	private List<TableRecord> records = new ArrayList<TableRecord>();

	private Map<String, TableRecord> indexMap;

	public Table(RequestManager rm) {
		this.rm = rm;
	}

	public Table database(String db) {
		this.em = this.rm.getEntityManager(db);
		return this;
	}

	public TableRecord loadFirst(String sql) {
		this.load(() -> {
			return sql;
		});
		return this.getFirst();
	}

	public TableRecord loadFirst(String sql, Consumer<TableRecord> first) {
		TableRecord line = this.loadFirst(sql);
		first.accept(line);
		return line;
	}

	public TableRecord loadFirst(Supplier<String> sql, Consumer<TableRecord> first) {
		TableRecord line = this.load(sql).getFirst();
		first.accept(line);
		return line;
	}

	public Table load(String sql) {
		return this.load(() -> {
			return sql;
		});
	}

	public Table load(Supplier<String> sql) {

		this.fieldNames = new ArrayList<String>();
		this.records = new ArrayList<TableRecord>();

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

			TableRecord m = new TableRecord();
			for (String fieldName : this.fieldNames) {
				m.put(fieldName, record.get(fieldName));
			}

			this.records.add(m);
		}

		return this;
	}

	public void setFieldNames(Collection<String> fieldNames) {
		this.fieldNames = new ArrayList<String>(fieldNames);
	}

	public List<String> getFieldNames() {
		return Collections.unmodifiableList(this.fieldNames);
	}

	public List<TableRecord> getRecords() {
		return this.records;
	}

	public TableRecord getFirst() {
		return this.records.get(0);
	}

	public Table forEach(Consumer<TableRecord> executer) {
		for (TableRecord line : this.records) {
			executer.accept(line);
		}
		return this;
	}

	public Table addIndex(Function<TableRecord, String> key) {

		if (this.indexMap == null) {
			this.indexMap = new HashMap<String, TableRecord>();
		}
		for (TableRecord line : this.records) {
			String k = key.apply(line);
			this.indexMap.put(k, line);
		}
		return this;
	}

	public TableRecord seek(String key) {
		TableRecord line = this.indexMap.get(key);
		if (line == null) {
			return new TableEnptyRecord();
		}
		return line;
	}

}
