package io.github.MigadaTang;

import io.github.MigadaTang.common.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestTransform {
    Schema view;

    @Test
    public void testERModelToRSSucc() throws IOException, SQLException {
        ER.initialize(TestCommon.usePostgre);
        view = ER.createSchema("testTransform1", "wd");
        Entity student = view.addEntity("student");
        Attribute studentId = student.addAttribute("id", DataType.INT, true, AttributeType.Mandatory);
        Attribute studentName = student.addAttribute("name", DataType.VARCHAR, false, AttributeType.Mandatory);
        Attribute studentAge = student.addAttribute("age", DataType.INT, false, AttributeType.Mandatory);
        Entity teacher = view.addEntity("teacher");
        Attribute teacherId = teacher.addAttribute("id", DataType.INT, true, AttributeType.Mandatory);
        Attribute teacherName = teacher.addAttribute("name", DataType.VARCHAR, false, AttributeType.Mandatory);
        Attribute teacherAge = teacher.addAttribute("age", DataType.INT, false, AttributeType.Mandatory);
        Entity school = view.addEntity("school");
        Attribute schoolId = school.addAttribute("id", DataType.INT, true, AttributeType.Mandatory);
        Attribute schoolName = school.addAttribute("name", DataType.INT, false, AttributeType.Mandatory);

        Relationship twisr = view.createRelationship("work_in", teacher, school, Cardinality.OneToOne, Cardinality.OneToMany);
        Relationship ssisr = view.createRelationship("study_in", student, school, Cardinality.ZeroToOne, Cardinality.OneToMany);
        Relationship stbtr = view.createRelationship("teach by", student, teacher, Cardinality.ZeroToMany, Cardinality.OneToMany);


        Tranform tranform = new Tranform();
        ResultState resultState = tranform.ERModelToSql(view.getID());
        System.out.println(resultState.getMsg());
        assert resultState.getStatus().equals(ResultStateCode.Success);
        String sql = (String) resultState.getData();
        System.out.println(sql);
    }


    @Test
    public void testERModelToRSFail1() throws IOException, SQLException {
        ER.initialize(TestCommon.usePostgre);
        view = ER.createSchema("testTransform1", "wd");
        Entity student = view.addEntity("student");
        Attribute studentId = student.addAttribute("id", DataType.INT, true, AttributeType.Mandatory);
        Attribute studentName = student.addAttribute("name", DataType.VARCHAR, false, AttributeType.Mandatory);
        Attribute studentAge = student.addAttribute("age", DataType.INT, false, AttributeType.Mandatory);
        Entity teacher = view.addEntity("teacher");
        Attribute teacherId = teacher.addAttribute("id", DataType.INT, true, AttributeType.Mandatory);
        Attribute teacherName = teacher.addAttribute("name", DataType.VARCHAR, false, AttributeType.Mandatory);
        Attribute teacherAge = teacher.addAttribute("age", DataType.INT, false, AttributeType.Mandatory);
        Entity school = view.addEntity("school");
        Attribute schoolId = school.addAttribute("id", DataType.INT, false, AttributeType.Mandatory);
        Attribute schoolName = school.addAttribute("name", DataType.INT, false, AttributeType.Mandatory);

        Relationship twisr = view.createRelationship("work_in", teacher, school, Cardinality.OneToOne, Cardinality.OneToMany);
        Relationship ssisr = view.createRelationship("study_in", student, school, Cardinality.ZeroToOne, Cardinality.OneToMany);
        Relationship stbtr = view.createRelationship("teach by", student, teacher, Cardinality.ZeroToMany, Cardinality.OneToMany);


        Tranform tranform = new Tranform();
        ResultState resultState = tranform.ERModelToSql(view.getID());
        assert resultState.getStatus().equals(ResultStateCode.Failure);
        System.out.println(resultState.getMsg());
    }


    @Test
    public void testERModelToRSSucc2() throws IOException, SQLException {
        ER.initialize(TestCommon.usePostgre);
        view = ER.createSchema("testTransform1", "wd");
        Entity branch = view.addEntity("branch");
        Attribute sortcode = branch.addAttribute("sortcode", DataType.INT, true, AttributeType.Mandatory);
        Attribute bname = branch.addAttribute("bname", DataType.VARCHAR, false, AttributeType.Mandatory);
        Attribute cash = branch.addAttribute("cash", DataType.FLOAT, false, AttributeType.Mandatory);
        Entity account = view.addEntity("account");
        Attribute no = account.addAttribute("no", DataType.INT, true, AttributeType.Mandatory);
        Attribute type = account.addAttribute("type", DataType.VARCHAR, false, AttributeType.Mandatory);
        Attribute cname = account.addAttribute("cname", DataType.VARCHAR, false, AttributeType.Mandatory);
        Attribute rate = account.addAttribute("rate", DataType.FLOAT, false, AttributeType.Optional);
        Entity movement = view.addEntity("movement");
        Attribute mid = movement.addAttribute("mid", DataType.INT, true, AttributeType.Mandatory);
        Attribute tdate = movement.addAttribute("tdate", DataType.DATETIME, false, AttributeType.Mandatory);
        Attribute amount = movement.addAttribute("amount", DataType.FLOAT, false, AttributeType.Mandatory);

        Relationship bhar = view.createRelationship("holds", branch, account, Cardinality.ZeroToMany, Cardinality.OneToOne);
        Relationship ahmr = view.createRelationship("has", account, movement, Cardinality.ZeroToMany, Cardinality.OneToOne);

        Tranform tranform = new Tranform();
        ResultState resultState = tranform.ERModelToSql(view.getID());
        assert resultState.getStatus().equals(ResultStateCode.Success);
        String sql = (String) resultState.getData();
        System.out.print(sql);
    }


    @Test
    public void testERModelToRSSuccWithNaryAndSubset() throws IOException, SQLException {
        ER.initialize(TestCommon.usePostgre);
        view = ER.createSchema("testTransform1", "wd");
        Entity department = view.addEntity("department");
        department.addAttribute("dname", DataType.INT, true, AttributeType.Mandatory);

        Entity person = view.addEntity("person");
        person.addAttribute("salary_number", DataType.INT, true, AttributeType.Mandatory);

        Entity manager = view.addSubset("manager", person);

        List<EntityWithCardinality> entityWithCardinalityList = new ArrayList<>();
        EntityWithCardinality entity1 = new EntityWithCardinality(department, Cardinality.ZeroToMany);
        EntityWithCardinality entity2 = new EntityWithCardinality(person, Cardinality.ZeroToMany);
        EntityWithCardinality entity3 = new EntityWithCardinality(manager, Cardinality.ZeroToMany);
        entityWithCardinalityList.add(entity1);
        entityWithCardinalityList.add(entity2);
        entityWithCardinalityList.add(entity3);
        view.createNaryRelationship("works in", entityWithCardinalityList);

        Tranform tranform = new Tranform();
        ResultState resultState = tranform.ERModelToSql(view.getID());
        assert resultState.getStatus().equals(ResultStateCode.Success);
        String sql = (String) resultState.getData();
        System.out.print(sql);
    }


    @Test
    public void testERModelToRSSuccWithNaryAndWeakEntity() throws IOException, SQLException {
        ER.initialize(TestCommon.usePostgre);
        view = ER.createSchema("testTransformWeakEntity", "wd");
        Entity department = view.addEntity("department");
        department.addAttribute("dname", DataType.INT, true, AttributeType.Mandatory);

        Entity person = view.addEntity("person");
        person.addAttribute("salary_number", DataType.INT, true, AttributeType.Mandatory);

        ImmutablePair<Entity, Relationship> managerRelPair = view.addWeakEntity("manager", person
                , "is a ", Cardinality.OneToOne, Cardinality.OneToMany);
        Entity manager = managerRelPair.left;
        manager.addAttribute("name", DataType.VARCHAR, true, AttributeType.Mandatory);

        List<EntityWithCardinality> entityWithCardinalityList = new ArrayList<>();
        EntityWithCardinality entity1 = new EntityWithCardinality(department, Cardinality.ZeroToMany);
        EntityWithCardinality entity2 = new EntityWithCardinality(person, Cardinality.ZeroToMany);
        EntityWithCardinality entity3 = new EntityWithCardinality(manager, Cardinality.ZeroToMany);
        entityWithCardinalityList.add(entity1);
        entityWithCardinalityList.add(entity2);
        entityWithCardinalityList.add(entity3);
        view.createNaryRelationship("works in", entityWithCardinalityList);

        Tranform tranform = new Tranform();
        ResultState resultState = tranform.ERModelToSql(view.getID());
        assert resultState.getStatus().equals(ResultStateCode.Success);
        String sql = (String) resultState.getData();
        System.out.print(sql);
    }


    @Test
    public void testRSToERModel() throws IOException, SQLException {
        ER.initialize(true);
        Tranform tranform = new Tranform();
//        ResultState resultState = tranform.relationSchemasToERModel(RDBMSType.POSTGRESQL, "jdbc:postgresql://db.doc.ic.ac.uk:5432/wh722",
//                "wh722", "4jC@A3528>0N6");
//        ResultState resultState = tranform.relationSchemasToERModel(RDBMSType.POSTGRESQL, "db.doc.ic.ac.uk", "5432", "wh722",
//                "wh722", "4jC@A3528>0N6");
        ResultState resultState = tranform.relationSchemasToERModel(RDBMSType.POSTGRESQL, "db.doc.ic.ac.uk", "5432", "wt22",
                "wt22", "22V**66+C5JPu");
        resultState.getData();
        System.out.println(resultState.getData().toString());
    }
}
