package com.csvToBD.config;

import com.csvToBD.model.Athlete;
import org.springframework.batch.item.ItemProcessor;

public class AthleteItemProcessor implements ItemProcessor<Athlete, Athlete> {
    @Override
    public Athlete process(Athlete athlete) throws Exception {
        return athlete;
    }
}
