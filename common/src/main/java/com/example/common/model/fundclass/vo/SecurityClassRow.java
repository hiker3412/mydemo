package com.example.common.model.fundclass.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SecurityClassRow {
    private String indexCode;
    private String c1Code;
    private String c1Name;
    private String c2Code;
    private String c2Name;
    private String c3Code;
    private String c3Name;

    public SecurityClassRow(String c1Code, String c1Name, String c2Code, String c2Name, String c3Code, String c3Name) {
        this.c1Code = c1Code;
        this.c1Name = c1Name;
        this.c2Code = c2Code;
        this.c2Name = c2Name;
        this.c3Code = c3Code;
        this.c3Name = c3Name;
    }

    public SecurityClassRow copyC1() {
        return new SecurityClassRow().setC1Code(this.c1Code).setC1Name(this.c1Name);
    }
    public SecurityClassRow copyC2() {
        return new SecurityClassRow().setC1Code(this.c1Code).setC1Name(this.c1Name)
                .setC2Code(this.c2Code).setC2Name(this.c2Name);
    }
    public SecurityClassRow copyC3() {
        return new SecurityClassRow().setC1Code(this.c1Code).setC1Name(this.c1Name)
                .setC2Code(this.c2Code).setC2Name(this.c2Name)
                .setC3Code(this.c3Code).setC3Name(this.c3Name);
    }
}
