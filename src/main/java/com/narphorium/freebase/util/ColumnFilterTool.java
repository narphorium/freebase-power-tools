package com.narphorium.freebase.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class ColumnFilterTool extends AbstractDataTool {
	
	public ColumnFilterTool() {
		super("freebase-filter");
	}
	
	public void run() {
		try {
			
			CSVReader reader = new CSVReader(new FileReader(config.getFile("input")), getSeparatorChar());
			CSVWriter writer = new CSVWriter(new FileWriter(config.getFile("output")), getSeparatorChar());
			
			Pattern inclusionPattern = null; 
			Pattern exclusionPattern= null; 
			
			if (config.contains("include")) {
				inclusionPattern = Pattern.compile(config.getString("include"));
			}
			if (config.contains("exclude")) {
				exclusionPattern = Pattern.compile(config.getString("exclude"));
			}
			
			String row[] = reader.readNext();
			writer.writeNext(row);
			
			String columnName = config.getString("column");
			int columnIndex = -1;
			for (int i = 0; i < row.length; i++) {
				if (columnName.equals(row[i])) {
					columnIndex = i;
				}
			}
			if (columnIndex < 0) {
				throw new Exception("Can't find column \"" + columnName + "\"");
			}
			
			while ((row = reader.readNext()) != null) {
				String value = row[columnIndex];
				if ((inclusionPattern == null ||  inclusionPattern.matcher(value).matches()) &&
					(exclusionPattern == null || !exclusionPattern.matcher(value).matches()))
				{
					writer.writeNext(row);
				}
			}
			
			reader.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ColumnFilterTool tool = new ColumnFilterTool();
		tool.config(args);
		tool.run();
	}

}
