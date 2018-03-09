package com.home.batch.listeners;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

public class DbToCsvChunkListener implements ChunkListener{

	public void afterChunk(ChunkContext arg0) {
		System.out.println("After Chunk [ ChunkListener ] ReadCount :" + arg0.getStepContext().getStepExecution().getReadCount());
		System.out.println("      Chunk [ ChunkListener ] WriteCount :" + arg0.getStepContext().getStepExecution().getWriteCount());
		System.out.println("      Chunk [ ChunkListener ] Commit :" + arg0.getStepContext().getStepExecution().getCommitCount());
	}

	public void afterChunkError(ChunkContext arg0) {
		// TODO Auto-generated method stub
		
	}

	public void beforeChunk(ChunkContext arg0) {
		System.out.println("Before Chunk [ ChunkListener ] ReadCount :" + arg0.getStepContext().getStepExecution().getReadCount());
		System.out.println("       Chunk [ ChunkListener ] WriteCount :" + arg0.getStepContext().getStepExecution().getWriteCount());
		System.out.println("       Chunk [ ChunkListener ] Commit :" + arg0.getStepContext().getStepExecution().getCommitCount());
		
	}

}
