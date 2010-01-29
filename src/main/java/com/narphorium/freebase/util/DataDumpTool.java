package com.narphorium.freebase.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVWriter;

import com.narphorium.freebase.query.Parameter;
import com.narphorium.freebase.query.Query;
import com.narphorium.freebase.query.io.QueryParser;
import com.narphorium.freebase.results.Result;
import com.narphorium.freebase.results.ResultSet;
import com.narphorium.freebase.services.TransService;
import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public class DataDumpTool extends AbstractFreebaseTool {

	private TransService transService = new TransService();
	
	public DataDumpTool() {
		super("freebase-dump");
	}
	
	public void run() {
		File outputFile = config.getFile("output");

		List<String> columnNames = new ArrayList<String>();
		Map<String, Integer> targetColumnIndexByName = new HashMap<String, Integer>();
		
		int r = 0;
		int offset = config.getInt("offset");
		int limit = config.getInt("limit");
		
		try {
		
			QueryParser queryParser = new QueryParser();
			Query query = null;
			if (config.contains("query")) {
				File queryFile = config.getFile("query");
				query = queryParser.parse(queryFile);
			} else if (config.contains("view")) {
				String viewId = config.getString("view");
				String queryString;
				try {
					queryString = transService.fetchArticle(viewId);
					query = queryParser.parse("view1", queryString);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
			int index = 0;
			for (Parameter parameter : query.getBlankFields()) {
				//String key = parameter.getName() != null ? parameter.getName() : parameter.getPath().toString();
				String key = parameter.getName();
				columnNames.add(key);
				targetColumnIndexByName.put(key, index);
				//System.out.println(key);
				index++;
			}
		
			ResultSet resultSet = readService.read(query);
			CSVWriter writer = new CSVWriter(new FileWriter(outputFile), getSeparatorChar());

			writer.writeNext(columnNames.toArray(new String[0]));
			
			while (resultSet.hasNext()) {
				Result result = resultSet.next();
				if (r >= offset && r < offset + limit) {
					writer.writeNext(convertResult2Row(columnNames, targetColumnIndexByName, result));
				}
				r++;
			}
			
			writer.close();
		} catch (FileNotFoundException e) {
			logger.severe(e.getMessage());
		} catch (IOException e) {
			logger.severe(e.getMessage());
		} catch (FreebaseServiceException e) {
			logger.severe(e.getMessage());
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataDumpTool tool = new DataDumpTool();
		tool.config(args);
		tool.run();
	}

}
