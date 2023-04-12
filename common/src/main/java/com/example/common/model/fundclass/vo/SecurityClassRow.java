package com.example.common.model.fundclass.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class SecurityClassRow extends ClassRow{
    private String indexCode;

    public SecurityClassRow(String c1Code, String c1Name, String c2Code, String c2Name, String c3Code, String c3Name) {
        super(c1Code, c1Name, c2Code, c2Name, c3Code, c3Name);
    }
}
