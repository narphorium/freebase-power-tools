package com.narphorium.freebase.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class ColumnSplitTool extends AbstractDataTool {

	public ColumnSplitTool() {
		super("freebase-split");
	}

	public void run() {
		Set<String> values = new TreeSet<String>();
		
		try {
			CSVReader reader = new CSVReader(new FileReader(config.getFile("input")), getSeparatorChar());
			String dataRow[] = null;
			String row[] = reader.readNext();
			
			String columnName = config.getString("column");
			int columnIndex = -1;
			for (int i = 0; i < row.length; i++) {
				if (columnName.equals(row[i])) {
					columnIndex = i;
				}
			}
			
			while ((dataRow = reader.readNext()) != null) {
				values.add(dataRow[columnIndex]);
			}
			
			reader.close();
			
			CSVWriter writer = new CSVWriter(new FileWriter(config.getFile("output")), getSeparatorChar());
			row = new String[1];
			row[0] = columnName;
			writer.writeNext(row);
			
			for (String value : values) {
				row[0] = value;
				writer.writeNext(row);
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
		ColumnSplitTool tool = new ColumnSplitTool();
		tool.config(args);
		tool.run();
	}
}
