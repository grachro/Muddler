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
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

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

	public Table setFieldNames(Collection<String> fieldNames) {
		this.fieldNames = new ArrayList<String>(fieldNames);
		return this;
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

	public void addIndex(TableRecord record, String key) {
		if (this.indexMap == null) {
			this.indexMap = new HashMap<String, TableRecord>();
		}
		this.indexMap.put(key, record);
	}

	public TableRecord seek(String key) {
		if (this.indexMap == null) {
			return new TableEnptyRecord();
		}

		TableRecord line = this.indexMap.get(key);
		if (line == null) {
			return new TableEnptyRecord();
		}
		return line;
	}

	public TableRecord seekOrNull(String key) {
		if (this.indexMap == null) {
			return null;
		}
		return this.indexMap.get(key);
	}

	public TableRecord createNewRecord() {
		TableRecord line = new TableRecord();

		for (String fieldName : this.fieldNames) {
			line.put(fieldName, null);
		}

		this.records.add(line);

		return line;
	}

	public int executeSqlWithTransaction(String sql) {

		EntityTransaction tx = this.em.getTransaction();
		tx.begin();
		int result = executeSql(sql);
		tx.commit();

		return result;
	}

	public int executeSql(String sql) {
		// System.out.println("Table.executeSql:" + sql);

		Query query = this.em.createNativeQuery(sql);
		int result = query.executeUpdate();

		return result;
	}

	public String createTableSqlForSqliet3(String tableName) {
		StringBuilder sb = new StringBuilder();

		sb.append("create table ").append(tableName).append(" (\n");
		boolean first = true;
		for (String fieldName : fieldNames) {
			if (first) {
				first = false;
			} else {
				sb.append(",\n");
			}
			sb.append(fieldName);
		}
		sb.append("\n)");

		return sb.toString();
	}

	public void insertSqlsForSqliet3(String tableName, Consumer<String> executer) {
		for (TableRecord line : this.records) {
			String sql = line.insertSqlForSqliet3(tableName, this.fieldNames);
			executer.accept(sql);
		}

	}

	public void toSqlite3(String tableName) {
		String dropSql = "drop table if exists " + tableName;
		this.executeSqlWithTransaction(dropSql);

		String crateSql = this.createTableSqlForSqliet3(tableName);
		this.executeSqlWithTransaction(crateSql);

		EntityTransaction tx = this.em.getTransaction();
		tx.begin();
		this.insertSqlsForSqliet3(tableName, (insertSql) -> {
			this.executeSql(insertSql);
		});
		tx.commit();
	}
}
