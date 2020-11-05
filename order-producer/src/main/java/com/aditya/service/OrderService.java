package com.aditya.service;

import com.aditya.dto.OrderDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@EnableBinding({Source.class, Sink.class})
public class OrderService {

    private Map<String, OrderDto> database = new HashMap<>();

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private Source source;

    public void persist(OrderDto orderDto) {
        //send message to rabbitMQ
        source.output().send(MessageBuilder.withPayload(orderDto).build());
        database.put(orderDto.getOrderId(), orderDto);
        System.out.println("Data sent to exchange successfully");
    }

    public OrderDto getOrderDetails(String orderId) {
        return database.get(orderId);
    }

    @StreamListener(target = Sink.INPUT)
    public void getStatus(String orderPair) throws JsonProcessingException {
        TypeReference<HashMap<String, String>> typeRef
                = new TypeReference<>() {
        };
        System.out.println("@@ "+orderPair);
        Map<String, String> map = mapper.readValue(orderPair, typeRef);
        database = database.entrySet().stream()
                .filter(entry -> map.containsKey(entry.getKey()))
                .map(entry -> {
                    OrderDto order = entry.getValue();
                    order.setStatus(map.get(entry.getKey()));
                    return order;
                })
                .collect(Collectors.toMap(entry -> entry.getOrderId(), entry->entry));
    }
}
