package com.tangdeng.hssystem.pojo.vo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class EchartVO {
    private String title;

    @JsonProperty("xAxis")
    private List xAxis;

    @JsonProperty("yAxis")
    private List<List> yAxis;

    private List<String> seriesNames;
}
