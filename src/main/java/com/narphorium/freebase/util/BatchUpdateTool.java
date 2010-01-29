package com.narphorium.freebase.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

import com.narphorium.freebase.query.ParameterTypeResolver;
import com.narphorium.freebase.query.Query;
import com.narphorium.freebase.query.io.QueryParser;
import com.narphorium.freebase.services.ReadService;
import com.narphorium.freebase.services.exceptions.FreebaseServiceException;

public class BatchUpdateTool extends AbstractFreebaseTool {

	public BatchUpdateTool() {
		super("freebase-update");
	}

	public void run() {
		int offset = config.getInt("offset");
		int limit = config.contains("limit") ? config.getInt("limit") : 0;
		
		Map<String, String> presets = new HashMap<String, String>();
		for (String param : config.getStringArray("params")) {
			String[] parts = param.split("=");
			presets.put(parts[0], parts[1]);
		}

		try {
			writeService.authenticate(config.getString("user"), config.getString("password"));
			
			QueryParser queryParser = new QueryParser();
			Query query = queryParser.parse(config.getFile("query"));
			
			ReadService readService = new ReadService();
			ParameterTypeResolver typeResolver = new ParameterTypeResolver(readService);
			typeResolver.process(query);

			CSVReader reader = new CSVReader(new FileReader(config.getFile("file")), getSeparatorChar());
			String dataDow[] = null;
			String columnNames[] = reader.readNext();
			int row = 0;
			
			while ((dataDow = reader.readNext()) != null && (limit > 0 && row < offset + limit)) {
				if (row >= offset) {
					for (String parameter : presets.keySet()) {
						query.parseParameterValue(parameter, presets.get(parameter));
					}
					for (int p = 0; p < columnNames.length; p++) {
						String parameter = columnNames[p];
						if (query.hasParameter(parameter)) {
							query.parseParameterValue(parameter, dataDow[p]);
						}
					}

					//System.out.println(data[1] + ":\t" + query);
					
					String result = writeService.write(query);
					System.out.println(result);
					System.out.println();
					//writer.write(result + "\n");
				}
				row++;
			}
			reader.close();
			//writer.close();
		} catch (FileNotFoundException e) {
			logger.severe(e.getMessage());
		} catch (IOException e) {
			logger.severe(e.getMessage());
		} catch (FreebaseServiceException e) {
			logger.severe(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		BatchUpdateTool tool = new BatchUpdateTool();
		tool.config(args);
		tool.run();
	}

}
