package io.hexlet.spring.dto;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

@Getter
@Setter
public class PostPatchDTO {
    private JsonNullable<String> title = JsonNullable.undefined();
    private JsonNullable<String> content = JsonNullable.undefined();
    private JsonNullable<Boolean> published = JsonNullable.undefined();
    private JsonNullable<List<Long>> tags = JsonNullable.undefined();
}
