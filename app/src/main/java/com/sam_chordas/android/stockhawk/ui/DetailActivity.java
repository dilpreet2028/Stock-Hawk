package com.sam_chordas.android.stockhawk.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.StocksData;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLineSeries;

public class DetailActivity extends AppCompatActivity implements StocksData.StockCallBack{

    private StocksData stocksData;
	private ValueLineChart valueLineChart;
	private TextView symbolView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("Details");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		symbolView=(TextView)findViewById(R.id.detailsSymbolName);
		String symbol=getIntent().getStringExtra(getString(R.string.intent_symbol));
		symbolView.setText(symbol);
		symbolView.setContentDescription("Symbol name "+ symbol);
		stocksData=new StocksData(symbol,"6m",this);
		valueLineChart=(ValueLineChart)findViewById(R.id.cubiclinechart);
		valueLineChart.setContentDescription(symbol+" Graph");
    }


	@Override
	public void onRequestStart() {

	}

	@Override
	public void onRequestEnd(final ValueLineSeries valueLineSeries) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				valueLineChart.addSeries(valueLineSeries);
				valueLineChart.startAnimation();
			}
		});

	}
}
