package com.sbroussi.dto.test.testA;

import com.sbroussi.dto.annotations.DtoComment;
import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.annotations.DtoFieldReference;
import com.sbroussi.dto.annotations.DtoResponse;
import lombok.Getter;
import lombok.Setter;

@DtoComment(notes = {
        "", // do not remove: Unit-Test will check that the first empty lines are ignored
        "", // do not remove: Unit-Test will check that the first empty lines are ignored
        "TestResponseA: comment line 1",
        "", // do not remove: Unit-Test will check that inner empty lines are respected
        "TestResponseA: comment line 2"})
@DtoResponse(name = "TESTREPA")
@Getter
@Setter
public class TestResponseA {

    @DtoComment(notes = "the field 1")
    @DtoField(length = 15)
    public String myResponseField1;

    @DtoComment(notes = {"the field 2",
            "",
            "possible values:",
            "- ABC for ABC type",
            "- XYZ for XYZ type"})
    @DtoFieldReference(TestResponseA.MY_DATATYPE_REF.class)
    public String myResponseField2;


    @DtoComment(notes = {"This is the documentation of a datatype",
            "that can be referenced by many fields to share/centralize",
            "definitions of DataTypes",
            "here we have: a plain String of 3 characters maximum"})
    @DtoField(length = 3)
    public static class MY_DATATYPE_REF {
    }

}
