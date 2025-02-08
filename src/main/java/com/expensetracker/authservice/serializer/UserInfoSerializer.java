package com.expensetracker.authservice.serializer;

import com.expensetracker.authservice.eventProducer.UserInfoEvent;
import com.expensetracker.authservice.model.UserInfoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class UserInfoSerializer implements Serializer<UserInfoEvent> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String s, UserInfoEvent userInfoDTO) {
        byte[] serializedBytes = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            serializedBytes = objectMapper.writeValueAsString(userInfoDTO).getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return serializedBytes;
    }

    @Override
    public byte[] serialize(String topic, Headers headers, UserInfoEvent data) {
        return Serializer.super.serialize(topic, headers, data);
    }

    @Override
    public void close() {
        Serializer.super.close();
    }
}
