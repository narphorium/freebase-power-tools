package com.narphorium.freebase.util;

import java.io.FileReader;
import java.io.FileWriter;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class ColumnEscapeTool extends AbstractDataTool {
	
	public ColumnEscapeTool() {
		super("freebase-escape");
	}
	
	private static String escape(String text){
		String result = "";
		for (char ch : text.toCharArray()) {
			if ((ch >= 'a' && ch <= 'z') ||
				(ch >= 'A' && ch <= 'Z') ||
				(ch >= '0' && ch <= '9') ||
				ch == '_')
			{
				result += ch;
			} else {
				result += "$";
				String code = Integer.toHexString(ch).toUpperCase();
				for (int i = code.length(); i < 4; i++) {
					result += "0";
				}
				result += code;
			}
		}
		return result;
	}
	
	public void run() {
		try {
			
			CSVReader reader = new CSVReader(new FileReader(config.getFile("input")), getSeparatorChar());
			CSVWriter writer = new CSVWriter(new FileWriter(config.getFile("output")), getSeparatorChar());
			
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
				row[columnIndex] = escape(row[columnIndex]);
				writer.writeNext(row);
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
		ColumnEscapeTool tool = new ColumnEscapeTool();
		tool.config(args);
		tool.run();
	}

}
