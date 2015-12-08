package com.grachro.muddler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import spark.Request;

public class RequestManager {

	public static void before(Request request) {
		Map<String, EntityManager> entityManagerMap = new HashMap<String, EntityManager>();
		for (Entry<String, EntityManagerFactory> entry : Muddler.entityManagerFactoryMap.entrySet()) {
			entityManagerMap.put(entry.getKey(), entry.getValue().createEntityManager());
		}
		request.attribute("entityManagerMap", entityManagerMap);

		Map<String, Object> viewModel = new HashMap<String, Object>();

		viewModel.putAll(request.queryMap().toMap());
		for (String queryParam : request.queryParams()) {
			String[] ss = request.queryParamsValues(queryParam);
			if (ss == null) {
				continue;
			}

			if (ss.length == 1) {
				viewModel.put(queryParam, ss[0]);
			} else {
				viewModel.put(queryParam, ss);
			}

		}

		request.attribute("viewModel", viewModel);

	}

	public static void after(Request request) {
		@SuppressWarnings("unchecked")
		Map<String, EntityManager> entityManagerMap = (Map<String, EntityManager>) request.attribute("entityManagerMap");
		for (EntityManager em : entityManagerMap.values()) {
			em.close();
		}

	}

	private final Request request;

	public RequestManager(Request request) {
		this.request = request;
	}

	public EntityManager getEntityManager(String db) {
		@SuppressWarnings("unchecked")
		Map<String, EntityManager> entityManagerMap = (Map<String, EntityManager>) request.attribute("entityManagerMap");

		return entityManagerMap.get(db);
	}

	public void setViewModel(String name, Object o) {
		Map<String, Object> map = this.request.attribute("viewModel");
		map.put(name, o);
	}
	

	public void setDefault(String name,String value) {
		Map<String, Object> map = this.request.attribute("viewModel");
		if (map.get(name) == null) {
			map.put(name, value);
		}
	}

	public Table getTsvModel(String name) {
		Map<String, Object> map = this.request.attribute("viewModel");
		return (Table) map.get(name);
	}

	public Map<?, ?> getMapModel(String name) {
		Map<String, Object> map = this.request.attribute("viewModel");
		return (Map<?, ?>) map.get(name);
	}

	public Object getModel(String name) {
		Map<String, Object> map = this.request.attribute("viewModel");
		return map.get(name);
	}

	public Map<String, Object> getModels() {
		Map<String, Object> map = this.request.attribute("viewModel");
		return map;
	}
}
