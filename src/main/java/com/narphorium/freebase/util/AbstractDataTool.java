package com.narphorium.freebase.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.lang.StringUtils;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public abstract class AbstractDataTool implements Runnable {

	protected JSAP jsap;
	protected JSAPResult config;
	protected Logger logger;
	
	protected String command;
	
	private char separatorChar = ',';

	public AbstractDataTool(String command) {
		this.command = command;
		String jsapConfiguation = command + ".jsap.xml";
		
		
		try {
			jsap = new JSAP(jsapConfiguation);
			
			logger = Logger.getLogger(command);
			logger.setUseParentHandlers(false);
			logger.setLevel(Level.INFO);
			File logDir = new File(".." + File.separator + "logs");
			if (!logDir.exists()) {
				logDir.mkdir();
			}
			FileHandler fileTxt = new FileHandler(logDir + File.separator + command + "_%g.log", 1000000, 3, true);
			//fileTxt.setFormatter(new SimpleFormatter());
			fileTxt.setFormatter(new CustomLogFormatter());
			logger.addHandler(fileTxt);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSAPException e) {
			e.printStackTrace();
		}
	}
	
	public void config(String[] args) {
		config = jsap.parse(args);
		
		if (args.length == 1 && args[0].equals("--help")) {
    		System.out.println();
            System.out.println("Usage: " + command + " " + jsap.getUsage());
            System.out.println();
    		System.out.println(jsap.getHelp());
    		System.exit(1);
		}
		
		logger.info(command + " " + StringUtils.join(args, " "));
		
		if (!config.success()) {
            System.err.println();
            System.err.println("Usage: " + command + " " + jsap.getUsage());
            System.err.println();

            for (Iterator<String> i = config.getErrorMessageIterator(); i.hasNext();) {
				String errorMessage = i.next();
            	System.err.println(errorMessage);
            	logger.log(Level.SEVERE, errorMessage);
			}
            
            System.exit(1);
        } else {
        	if (config.getString("format").equalsIgnoreCase("tsv")) {
				separatorChar = '\t';
			} else if (config.getString("format").equalsIgnoreCase("csv")) {
				separatorChar = ',';
			} else {
				logger.log(Level.SEVERE, "Unrecognized format");
				System.exit(1);
			}
        }
	}
	
	public char getSeparatorChar() {
		return separatorChar;
	}
	
	public JSAPResult getConfig() {
		return config;
	}

	/*private static void copyFile(File srFile, File dtFile){
	    try{
	      InputStream in = new FileInputStream(srFile);
	      OutputStream out = new FileOutputStream(dtFile);

	      byte[] buf = new byte[1024];
	      int len;
	      while ((len = in.read(buf)) > 0){
	        out.write(buf, 0, len);
	      }
	      in.close();
	      out.close();
	    }
	    catch(FileNotFoundException ex){
	      System.out.println(ex.getMessage() + " in the specified directory.");
	      System.exit(0);
	    }
	    catch(IOException e){
	      System.out.println(e.getMessage());      
	    }
	}*/
	
	protected void indexColumnNames(String[] row, List<String> columnNames, Map<String, Integer> columnIndexByName) {
		int i = 0;
		for (String columnName : row) {
			columnNames.add(columnName);
			columnIndexByName.put(columnName, i);
			i++;
		}
	}
	
	protected String[] convertMap2Row(List<String> columnNames, Map<String, Integer> columnIndexByName, Map<String, String> data) {
		String[] rowData = new String[columnNames.size()];
		for (String columnName : columnNames) {
			Integer columnIndex = columnIndexByName.get(columnName);
			if (columnIndex != null) {
				rowData[columnIndex] = data.get(columnName);
			}
		}
		return rowData;
	}

	protected Map<String, String> convertRow2Map(String[] row, Map<String, Integer> columnIndexByName) {
		Map<String, String> data = new HashMap<String, String>();
		for (String columnName : columnIndexByName.keySet()) {
			Integer columnIndex = columnIndexByName.get(columnName);
			if (columnIndex != null && columnIndex < row.length) {
				Object value = row[columnIndex];
				data.put(columnName, value.toString());
			}
		}
		return data;
	}

}
