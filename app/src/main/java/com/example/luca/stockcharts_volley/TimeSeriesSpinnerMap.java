package com.example.luca.stockcharts_volley;

import java.util.LinkedHashMap;

/**
 * Created by Luca on 16/03/2018.
 */

class TimeSeriesSpinnerMap {

    // timeSeries will contain the query functions relative to the different time resolutions
    LinkedHashMap<String, String> timeSeries = new LinkedHashMap<>();

    public TimeSeriesSpinnerMap() {
        super();

        initTimeSeries();
    }


    private void initTimeSeries() {
        timeSeries.put("Monthly", "function=TIME_SERIES_MONTHLY");
        timeSeries.put("Weekly","function=TIME_SERIES_WEEKLY");
        timeSeries.put("Daily", "function=TIME_SERIES_DAILY");
        timeSeries.put("60min" ,"function=TIME_SERIES_INTRADAY&interval=60min");
        timeSeries.put("30min", "function=TIME_SERIES_INTRADAY&interval=30min");
        timeSeries.put("15min", "function=TIME_SERIES_INTRADAY&interval=15min");
        timeSeries.put("5min", "function=TIME_SERIES_INTRADAY&interval=5min");
        timeSeries.put("1min", "function=TIME_SERIES_INTRADAY&interval=1min");
    }

    public LinkedHashMap<String, String> getTimeSeries() {
        return timeSeries;
    }
}
