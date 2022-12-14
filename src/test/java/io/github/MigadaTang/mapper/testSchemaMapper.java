package io.github.MigadaTang.mapper;

import io.github.MigadaTang.TestCommon;
import io.github.MigadaTang.entity.SchemaDO;
import io.github.MigadaTang.transform.RandomUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

public class testSchemaMapper {
    @BeforeClass
    public static void init() throws Exception {
        TestCommon.setUp();
    }


    @Test
    public void testQueryAllSchemas() {
        SchemaDO schemaDO = new SchemaDO(RandomUtils.generateID(), "schema3", 0, new Date(), new Date());
//        ER.schemaMapper.insert(schemaDO);
//        List<SchemaDO> schemaDOList = ER.schemaMapper.selectAll();
//        assertTrue(schemaDOList.size() > 0);
    }

    @Test
    public void testQueryByID() {
        SchemaDO schemaDO = new SchemaDO(RandomUtils.generateID(), "schema3", 0, new Date(), new Date());
//        ER.schemaMapper.insert(schemaDO);
//        SchemaDO search = ER.schemaMapper.selectByID(schemaDO.getID());
//        System.out.println(search);
    }

    @Test
    public void testCreateSchema() {
        SchemaDO schemaDO = new SchemaDO(RandomUtils.generateID(), "schema3", 0, new Date(), new Date());
//        assertEquals(ER.schemaMapper.insert(schemaDO), 1);
    }

    @Test
    public void testDeleteSchema() {
        SchemaDO schemaDO = new SchemaDO(RandomUtils.generateID(), "schema3", 0, new Date(), new Date());
//        ER.schemaMapper.insert(schemaDO);
//        assertEquals(ER.schemaMapper.deleteByID(schemaDO.getID()), 1);
    }

    @Test
    public void testQuerySchema() {
        SchemaDO schemaDO = new SchemaDO(RandomUtils.generateID(), "schema1", 0, null, null);
//        ER.schemaMapper.insert(schemaDO);
//        List<SchemaDO> res = ER.schemaMapper.selectBySchema(schemaDO);
//        System.out.println(res);
    }

    @Test
    public void testUpdateSchema() {
        SchemaDO schemaDO = new SchemaDO(RandomUtils.generateID(), "schema3update", 0, new Date(), new Date());
//        ER.schemaMapper.insert(schemaDO);
//        assertEquals(ER.schemaMapper.updateByID(schemaDO), 1);
    }
}
