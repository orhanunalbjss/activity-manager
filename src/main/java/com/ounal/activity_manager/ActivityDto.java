package com.ounal.activity_manager;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityDto {

    @JsonProperty("activity")
    private String name;
    private String type;
    private Integer participants;
}
