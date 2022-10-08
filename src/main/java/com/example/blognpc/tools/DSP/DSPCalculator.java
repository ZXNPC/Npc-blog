package com.example.blognpc.tools.DSP;

import lombok.Data;

import java.util.List;

@Data
public class DSPCalculator {
    @Data
    class DSPItem {
        String name;
        Integer num;
    }

    @Data
    class Facility extends DSPItem {
    }

    @Data
    class Component extends DSPItem {
    }

    @Data
    class formula {
        private List<DSPItem> fromList;
        private List<DSPItem> toList;
        private Double costTime;
        private Facility facility;
    }

    private String name;
    private Long yield;

    public DSPCalculator(String name, Long yield) {
        this.name = name;
        this.yield = yield;

    }


}
