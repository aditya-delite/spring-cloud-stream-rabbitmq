package com.aditya;

import com.aditya.dto.OrderDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableBinding({Sink.class, Source.class})
@Component
public class OrderFulfilmentConsumerApplication {

    @Autowired
    private Source source;

    @Autowired
    private ObjectMapper mapper;

    public static void main(String[] args) {
        SpringApplication.run(OrderFulfilmentConsumerApplication.class, args);
    }

    @Bean
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }

    @StreamListener(target = Sink.INPUT)
    public void orderFulfilmentCheck(OrderDto orderDto) throws JsonProcessingException {
        System.out.println(orderDto);
        Map<String, String> orderStatus = new HashMap<>();
        if (orderDto.getQuantity() % 2 == 0) {
            //order is placed
            orderStatus.put(orderDto.getOrderId(), "Placed");
            String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(orderStatus);
            source.output().send(MessageBuilder.withPayload(s).build());
        } else {
            //order failed to be placed
            orderStatus.put(orderDto.getOrderId(), "Failed");
            String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(orderStatus);
            source.output().send(MessageBuilder.withPayload(s).build());
        }
    }
}
