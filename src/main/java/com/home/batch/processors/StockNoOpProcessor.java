package com.home.batch.processors;

import org.springframework.batch.item.ItemProcessor;

import com.home.batch.model.Stock;
import com.home.batch.model.StockVolume;


public class StockNoOpProcessor implements ItemProcessor<Stock,StockVolume>{
	
	public StockVolume process(Stock stock) throws Exception {

		StockVolume sv = new StockVolume();
		sv.setStock(stock.getStock());
		sv.setVolume(stock.getShares());
		System.out.println("= Stock Volume Creation --> "+ stock.getStock());
		return sv;
	}

}
