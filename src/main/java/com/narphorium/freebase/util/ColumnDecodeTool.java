package com.narphorium.freebase.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class ColumnDecodeTool extends AbstractDataTool {
	
	private static Pattern unicodePattern = Pattern.compile("\\\\u([0-9,a-f]{4})");
	
	public ColumnDecodeTool() {
		super("freebase-decode");
	}
	
	private static String decode(String text){
		Matcher unicodeMatcher = unicodePattern.matcher(text);
		while (unicodeMatcher.find()) {
			int charCode = Integer.parseInt(unicodeMatcher.group(1), 16);
			text = unicodeMatcher.replaceAll("" + (char)charCode);
			unicodeMatcher.reset(text);
		}
		text = text.replaceAll("\\\\\\\\\\\\", "");
		text = text.replaceAll("\\\\/", "/");
		System.out.println(text);
		return text;
	}
	
	public void run() {
		CSVReader reader = null;
		CSVWriter writer = null;
		
		
		try {
			reader = new CSVReader(new InputStreamReader(new FileInputStream(config.getFile("input")), "UTF-8"), getSeparatorChar(), '|');
			writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(config.getFile("output")), "UTF-8"), getSeparatorChar(), '"');
			
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
				row[columnIndex] = decode(row[columnIndex]);
				writer.writeNext(row);
			}
			
			reader.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ColumnDecodeTool tool = new ColumnDecodeTool();
		tool.config(args);
		tool.run();
	}

}
