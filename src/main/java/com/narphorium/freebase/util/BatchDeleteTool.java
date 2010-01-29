package com.narphorium.freebase.util;


public class BatchDeleteTool extends AbstractFreebaseTool {

	public BatchDeleteTool() {
		super("freebase-delete");
	}
	
	public void run() {
		// TODO
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BatchDeleteTool tool = new BatchDeleteTool();
		tool.config(args);
		tool.run();
	}

}
