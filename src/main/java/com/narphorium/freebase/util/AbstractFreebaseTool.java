package com.narphorium.freebase.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.narphorium.freebase.query.Parameter;
import com.narphorium.freebase.query.io.QueryParser;
import com.narphorium.freebase.results.Result;
import com.narphorium.freebase.services.ReadService;
import com.narphorium.freebase.services.WriteService;

public abstract class AbstractFreebaseTool extends AbstractDataTool implements Runnable {
	
	protected ReadService readService = new ReadService();
	protected WriteService writeService = new WriteService();
	protected QueryParser queryParser = new QueryParser();
	
	protected String command;
	
	List<String> sourceColumnNames = new ArrayList<String>();
	List<String> targetColumnNames = new ArrayList<String>();
	//private Map<String, Integer> sourceColumnIndexByName = new HashMap<String, Integer>();
	//private Map<String, Integer> targetColumnIndexByName = new HashMap<String, Integer>();
	
	public AbstractFreebaseTool(String command) {
		super(command);
	}
	
	public void config(String[] args) {
		super.config(args);
		
		try {
			URL baseUrl = new URL("http://www.freebase.com/api");
			if (config.getBoolean("sandbox")) {
				baseUrl = new URL("http://sandbox.freebase.com/api");
			}
			readService = new ReadService(baseUrl);
			writeService = new WriteService(baseUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	protected void addColumn(List<String> columnNames, Map<String, Integer> columnIndexByName, String columnName) {
		columnNames.add(columnName);
		columnIndexByName.put(columnName, columnNames.size() - 1);
		
	}
	
	protected String[] convertResult2Row(List<String> columnNames, Map<String, Integer> columnIndexByName, Result result) {
		Map<String, String> rowData = new HashMap<String, String>();
		
		for (Parameter parameter : result.getQuery().getParameters()) {
			String key = parameter.getName();
			rowData.put(key, result.getObject(key).toString());
		}
		
		for (Parameter blankField : result.getQuery().getBlankFields()) {
			String key = blankField.getPath().toString();
			Object obj = result.getObject(blankField.getPath());
			if (obj != null) {
				rowData.put(key, obj.toString());
			} else {
				rowData.put(key, null);
			}
			
		}
		
		return convertMap2Row(columnNames, columnIndexByName, rowData);
	}

}
