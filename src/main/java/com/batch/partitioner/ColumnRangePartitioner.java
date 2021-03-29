package com.batch.partitioner;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Component
public class ColumnRangePartitioner implements Partitioner {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {

		Map<String, ExecutionContext> result = new HashMap<>();
		Integer min = jdbcTemplate.queryForObject("SELECT MIN(id) FROM customer " , Integer.class);
		Integer max = jdbcTemplate.queryForObject("SELECT MAX(id) FROM customer " , Integer.class);

		Integer targetSize = (max) / 10;
		Integer number = 1;
		Integer start = min;
		Integer end = start + targetSize;

		for (int i = 1; i <= 10; i++) {
			ExecutionContext value = new ExecutionContext();
			result.put("partition" + number, value);
			if (end >= max) {
				end = max;
			}
			value.putInt("minValue", start);
			value.putInt("maxValue", end);
			start += targetSize;
			end += targetSize;
			number++;
		}

		int i =1;
		for (ExecutionContext value : result.values()) {
			System.out.println(value.get("partition"+i));
			System.out.println(value.get("minValue"));
			System.out.println(value.get("maxValue"));
			i++;
		}

		return result;
	}
}


