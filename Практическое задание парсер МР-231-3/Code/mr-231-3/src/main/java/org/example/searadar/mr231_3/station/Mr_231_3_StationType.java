package org.example.searadar.mr231_3.station;

import org.example.searadar.mr231_3.convert.Mr231_3Converter;

public class Mr_231_3_StationType {

    private static final String STATION_TYPE = "МР-231-3";
    private static final String CODEC_NAME = "mr231-3";

    public Mr231_3Converter createConverter() {
        return new Mr231_3Converter();
    }
}
