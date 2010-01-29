package com.narphorium.freebase.util;


public class BatchCreateTool extends AbstractFreebaseTool {

	public BatchCreateTool() {
		super("freebase-create");
	}
	
	public void run() {
		// TODO
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BatchCreateTool tool = new BatchCreateTool();
		tool.config(args);
		tool.run();
	}

}
