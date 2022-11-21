package io.github.MigadaTang;

import io.github.MigadaTang.common.AttributeType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.exception.ERException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestSchema {

    @Before
    public void init() throws Exception {
        ER.initialize(TestCommon.usePostgre);
    }

    @Test
    public void createVanillaERTest() {
        Schema testSchema = ER.createSchema("testSchema", "wt22");

        Entity teacher = testSchema.addEntity("teacher");
        teacher.addAttribute("teacher_id", DataType.VARCHAR, true, AttributeType.Mandatory);
        teacher.addAttribute("name", DataType.VARCHAR, false, AttributeType.Mandatory);
        teacher.addAttribute("age", DataType.INT, false, AttributeType.Mandatory);

        Entity student = testSchema.addEntity("student");
        student.addAttribute("student_id", DataType.VARCHAR, true, AttributeType.Mandatory);
        student.addAttribute("name", DataType.VARCHAR, false, AttributeType.Mandatory);
        student.addAttribute("grade", DataType.INT, false, AttributeType.Mandatory);

        Relationship ts = testSchema.createRelationship("teaches", teacher, student, Cardinality.ZeroToMany, Cardinality.ZeroToMany);

        Schema dbSchema = Schema.queryByID(testSchema.getID());
        Assert.assertNotNull(dbSchema);
        Assert.assertEquals(dbSchema.getEntityList().size(), 2);
        Assert.assertEquals(dbSchema.getRelationshipList().size(), 1);
    }

    @Test
    public void updateSchemaTest() {
        Schema firstSchema = ER.createSchema("first schema", "tw");
        String newSchemaName = "new schema name";
        firstSchema.updateInfo(newSchemaName);

        Schema newSchema = Schema.queryByID(firstSchema.getID());
        Assert.assertEquals(newSchema.getName(), newSchemaName);
    }

    @Test
    public void deleteSchemaTest() {
        Schema testDeleteSchema = ER.createSchema("first schema", "tw");
        testDeleteSchema = Schema.queryByID(testDeleteSchema.getID());
        Assert.assertNotNull(testDeleteSchema);

        ER.deleteSchema(testDeleteSchema);

        Schema finalTestDeleteSchema = testDeleteSchema;
        assertThrows(ERException.class, () -> Schema.queryByID(finalTestDeleteSchema.getID()));
    }

    @Test
    public void deleteEntityTest() {
        Schema firstSchema = ER.createSchema("first schema", "tw");
        Entity firstEntity = firstSchema.addEntity("teacher");
        Assert.assertNotNull(firstEntity);

        firstSchema.deleteEntity(firstEntity);

        Schema newSchema = Schema.queryByID(firstSchema.getID());
        Assert.assertEquals(newSchema.getEntityList().size(), 0);
        assertThrows(ERException.class, () -> Entity.queryByID(firstEntity.getID()));
    }

    @Test
    public void querySchemaTest() {
        Schema firstSchema = ER.createSchema("first schema", "tw");
        List<Schema> schemas = Schema.queryAll();
        Assert.assertNotEquals(schemas.size(), 0);
    }
}
