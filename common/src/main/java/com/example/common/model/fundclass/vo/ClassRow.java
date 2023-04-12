package com.example.common.model.fundclass.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClassRow {
    private String c1Code;
    private String c1Name;
    private String c2Code;
    private String c2Name;
    private String c3Code;
    private String c3Name;

    public ClassRow copyC1() {
        return new ClassRow().setC1Code(this.c1Code).setC1Name(this.c1Name);
    }
    public ClassRow copyC2() {
        return new ClassRow().setC1Code(this.c1Code).setC1Name(this.c1Name)
                .setC2Code(this.c2Code).setC2Name(this.c2Name);
    }
    public ClassRow copyC3() {
        return new ClassRow().setC1Code(this.c1Code).setC1Name(this.c1Name)
                .setC2Code(this.c2Code).setC2Name(this.c2Name)
                .setC3Code(this.c3Code).setC3Name(this.c3Name);
    }
}
