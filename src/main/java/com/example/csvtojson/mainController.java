package com.example.csvtojson;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

@RestController
public class mainController {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @GetMapping("/convertcsv")
    public Object jsontocsv() throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        String sql = "select id,subject,announcementcontent,createddate,filename as docname,filesize as docsize,filemimetype as doctype from gep_announcement order by createddate desc limit 10";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

        List<Map<String,Object>> data = namedParameterJdbcTemplate.queryForList(sql, mapSqlParameterSource);

        String json = mapper.writeValueAsString(data);

        JsonNode jsonNode = mapper.readTree(json);

        CsvSchema.Builder builder = CsvSchema.builder();
		jsonNode.elements().next().fieldNames().forEachRemaining(f -> builder.addColumn(f));

        CsvSchema csvSchema = builder.build().withHeader();

        CsvMapper csvMapper = new CsvMapper();
		// csvMapper.configure(Feature.IGNORE_UNKNOWN, true);
		csvMapper.writerFor(JsonNode.class)
				.with(csvSchema)
				.writeValue(new File("src/main/resources/data.csv"), jsonNode);

        System.out.println("Converted Successfully....");

        return null;

    }

}
