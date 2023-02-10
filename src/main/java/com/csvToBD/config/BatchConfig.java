package com.csvToBD.config;

import com.csvToBD.model.Athlete;
import com.csvToBD.repository.AthleteRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.repository.CrudRepository;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor

public class BatchConfig {

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private AthleteRepository athleteRepo;

    @Bean
    public FlatFileItemReader<Athlete> reader() {
        FlatFileItemReader<Athlete> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/athletes.csv"));
        itemReader.setName("csvReader");
//        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<Athlete> lineMapper() {
        //extract the value from csv file
        DefaultLineMapper<Athlete> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter(",");
        delimitedLineTokenizer.setStrict(false);
        delimitedLineTokenizer.setNames("name", "sport", "nationality", "age", "weight", "height");

        // map the information to the objects
        // LineTokenizer will map the value to the target class 'Athlete'
        BeanWrapperFieldSetMapper<Athlete> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Athlete.class);

        lineMapper.setLineTokenizer(delimitedLineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        System.out.println("===================> "+lineMapper);
        return lineMapper;
    }


    @Bean
    public AthleteItemProcessor processor() {
        return new AthleteItemProcessor();
    }

    @Bean
    public RepositoryItemWriter<Athlete> writer() {
        RepositoryItemWriter<Athlete> writer = new RepositoryItemWriter<>();
        writer.setRepository((CrudRepository<Athlete, ?>) athleteRepo);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory.get("csv-step").<Athlete, Athlete>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer()).build();
    }

    @Bean
    public Job runJob() {
        return jobBuilderFactory.get("importAthletes")
                .flow(step1())
                .end()
                .build();
    }

//    public BatchConfig(JobBuilderFactory jobBuilderFactory) {
//        this.jobBuilderFactory = jobBuilderFactory;
//    }




}


