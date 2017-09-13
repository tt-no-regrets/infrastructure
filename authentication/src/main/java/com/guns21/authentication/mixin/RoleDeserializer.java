package com.guns21.authentication.mixin;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.guns21.authentication.api.entity.Role;

import java.io.*;

class RoleDeserializer extends JsonDeserializer<Role> {
    RoleDeserializer() {
    }

    public Role deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode jsonNode = (JsonNode) mapper.readTree(jp);
        Role role = new Role();
        role.setNickname(readJsonNode(jsonNode, "nickname").asText());
        role.setId(readJsonNode(jsonNode, "id").asText());
        role.setName(readJsonNode(jsonNode, "name").asText());
        return role;
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return (JsonNode) (jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance());
    }
}
