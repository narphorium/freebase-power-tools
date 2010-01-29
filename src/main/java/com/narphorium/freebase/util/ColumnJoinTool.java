package com.narphorium.freebase.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class ColumnJoinTool extends AbstractDataTool {

	public ColumnJoinTool() {
		super("freebase-join");
	}

	public void run() {
		try {
			CSVReader reader = new CSVReader(new FileReader(config.getFile("source")), getSeparatorChar());
			String dataRow[] = null;
			String row[] = reader.readNext();
			
			Map<String, Map<String, String>> rowValues = new HashMap<String, Map<String, String>>();
			Map<String, String> targetColumnsBySource = new HashMap<String, String>();
			Map<String, String> sourceColumnsByTarget = new HashMap<String, String>();
			Map<String, Integer> sourceColumnIndices = new HashMap<String, Integer>();
			Map<String, Integer> targetColumnIndices = new HashMap<String, Integer>();
			
			String sourceKeyColumnName = config.getString("key_column");
			Set<String> valueColumns = new HashSet<String>();

			if (sourceKeyColumnName.contains("-")) {
				String parts[] = sourceKeyColumnName.split("-");
				sourceKeyColumnName = parts[0].trim();
				targetColumnsBySource.put(sourceKeyColumnName, parts[1].trim());
			} else {
				targetColumnsBySource.put(sourceKeyColumnName, sourceKeyColumnName);
			}
			
			String valueMappings[] = config.getStringArray("value_column");
			for (String mapping : valueMappings) {
				String source = mapping.trim();
				if (source.contains("-")) {
					String parts[] = source.split("-");
					source = parts[0].trim();
					targetColumnsBySource.put(source, parts[1].trim());
				} else {
					targetColumnsBySource.put(source, source);
				}
				valueColumns.add(source);
			}
			
			for (Map.Entry<String, String> entry : targetColumnsBySource.entrySet()) {
				sourceColumnsByTarget.put(entry.getValue(), entry.getKey());
			}
			
			/*System.out.println("Value Columns");
			for (String valueColumn : valueColumns) {
				System.out.println(valueColumn);
			}*/
			
			System.out.println("Source Column Indices");
			for (int i = 0; i < row.length; i++) {
				if (targetColumnsBySource.containsKey(row[i])) {
					sourceColumnIndices.put(row[i], i);
					System.out.println(row[i] + " => " + i);
				}
			}
			
			while ((dataRow = reader.readNext()) != null) {
				String key = dataRow[sourceColumnIndices.get(sourceKeyColumnName)];
				Map<String, String> values = rowValues.get(key);
				if (values == null) {
					values = new HashMap<String, String>();
					rowValues.put(key, values);
				}
				for (String valueColumnName : valueColumns) {
					int index = sourceColumnIndices.get(valueColumnName);
					if (index < dataRow.length) {
						values.put(valueColumnName, dataRow[index]);
					}
				}
			}
			
			reader.close();
			
			CSVWriter writer = new CSVWriter(new FileWriter(config.getFile("output")), getSeparatorChar());
			reader = new CSVReader(new FileReader(config.getFile("target")), getSeparatorChar());
			dataRow = null;
			row = reader.readNext();
			writer.writeNext(row);
			
			for (int i = 0; i < row.length; i++) {
				if (sourceColumnsByTarget.containsKey(row[i])) {
					targetColumnIndices.put(row[i], i);
				}
			}
			
			System.out.println("Target Column Indices");
			for (Map.Entry<String, Integer> entry : targetColumnIndices.entrySet()) {
				System.out.println(entry.getKey() + " => " + entry.getValue());
			}
			
			while ((dataRow = reader.readNext()) != null) {
				String key = dataRow[targetColumnIndices.get(targetColumnsBySource.get(sourceKeyColumnName))];
				Map<String, String> values = rowValues.get(key);
				if (values != null) {
					System.out.println("MATCHED");
					for (String valueColumnName : valueColumns) {
						Integer valueColumnIndex = targetColumnIndices.get(targetColumnsBySource.get(valueColumnName));
						if (dataRow[valueColumnIndex] == null || dataRow[valueColumnIndex].length() == 0) {
							dataRow[valueColumnIndex] = values.get(valueColumnName);
						}
					}
				}
				writer.writeNext(dataRow);
			}
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ColumnJoinTool tool = new ColumnJoinTool();
		tool.config(args);
		tool.run();
	}
}
