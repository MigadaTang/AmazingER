package com.ic.er;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ic.er.common.Cardinality;
import com.ic.er.common.SchemaDeserializer;
import com.ic.er.entity.EntityDO;
import com.ic.er.entity.RelationshipDO;
import com.ic.er.entity.SchemaDO;
import com.ic.er.exception.ERException;
import lombok.Getter;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@JsonDeserialize(using = SchemaDeserializer.class)
@JsonIgnoreProperties({"id", "creator", "gmtCreate", "gmtModified"})
public class Schema {
    private Long ID;
    private String name;
    private List<Entity> entityList;
    private List<Relationship> relationshipList;
    private String creator;
    private Date gmtCreate;
    private Date gmtModified;

    protected Schema(Long ID, String name, List<Entity> entityList, List<Relationship> relationshipList, String creator, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.name = name;
        this.entityList = entityList;
        this.relationshipList = relationshipList;
        this.creator = creator;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        if (this.ID == 0) {
            insertDB();
        }
    }

    public Entity addEntity(String entityName) {
        return addEntity(entityName, 0.0, 0.0);
    }

    public Entity addEntity(String entityName, Double layoutX, Double layoutY) {
        if (entityName.equals("")) {
            throw new ERException("entityName cannot be empty");
        }
        List<Entity> entities = Entity.queryByEntity(new EntityDO(null, entityName, this.ID, null, null, null));
        if (entities.size() != 0) {
            throw new ERException(String.format("entity with name: %s already exists", entityName));
        }
        Entity entity = new Entity(0L, entityName, this.ID, new ArrayList<>(), null, layoutX, layoutY, new Date(), new Date());
        this.entityList.add(entity);
        return entity;
    }

    public void deleteEntity(Entity entity) {
        this.entityList.remove(entity);
        List<Relationship> relationships = Relationship.queryByRelationship(new RelationshipDO(null, null, this.ID, entity.getID(), null, null, null, null, null, null));
        for (Relationship relationship : relationships) {
            deleteRelationship(relationship);
        }
        relationships = Relationship.queryByRelationship(new RelationshipDO(null, null, this.ID, null, entity.getID(), null, null, null, null, null));
        for (Relationship relationship : relationships) {
            deleteRelationship(relationship);
        }
        entity.deleteDB();
    }

    public Relationship createRelationship(String relationshipName, Entity firstEntity, Entity secondEntity,
                                           Cardinality firstCardinality, Cardinality secondCardinality) {
        if (relationshipName.equals("")) {
            throw new ERException("relationshipName cannot be empty");
        }
        if (firstEntity.getID().equals(secondEntity.getID())) {
            throw new ERException("relationship cannot be created on the same entity");
        }
        if (Entity.queryByID(firstEntity.getID()) == null) {
            throw new ERException(String.format("entity with ID: %d not found", firstEntity.getID()));
        }
        if (Entity.queryByID(secondEntity.getID()) == null) {
            throw new ERException(String.format("entity with ID: %d not found", secondEntity.getID()));
        }
        if (Relationship.queryByRelationship(new RelationshipDO(firstEntity.getID(), secondEntity.getID())).size() != 0) {
            throw new ERException(String.format("relation between entity %s and %s already exists", firstEntity.getName(), secondEntity.getName()));
        }
        if (Relationship.queryByRelationship(new RelationshipDO(secondEntity.getID(), firstEntity.getID())).size() != 0) {
            throw new ERException(String.format("relation between entity %s and %s already exists", firstEntity.getName(), secondEntity.getName()));
        }
        if (!firstEntity.getSchemaID().equals(this.ID)) {
            throw new ERException(String.format("entity: %s does not belong to this schema", firstEntity.getName()));
        }
        if (!secondEntity.getSchemaID().equals(this.ID)) {
            throw new ERException(String.format("entity: %s does not belong to this schema", secondEntity.getName()));
        }
        Relationship relationship = new Relationship(0L, relationshipName, this.ID, firstEntity, secondEntity, firstCardinality, secondCardinality, null, new Date(), new Date());
        this.relationshipList.add(relationship);
        return relationship;
    }

    public void deleteRelationship(Relationship relationship) {
        this.relationshipList.remove(relationship);
        relationship.deleteDB();
    }

    private void insertDB() {
        try {
            SchemaDO schemaDO = new SchemaDO(0L, this.name, this.creator, 0L, 0, this.gmtCreate, this.gmtModified);
            int ret = ER.schemaMapper.insert(schemaDO);
            if (ret == 0) {
                throw new ERException("insertDB fail");
            }
            this.ID = schemaDO.getID();
        } catch (PersistenceException e) {
            throw new ERException("insertDB fail", e);
        }
    }

    public String toJSON() {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json;
        try {
            json = ow.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    public static List<Schema> queryAll() {
        return Trans.TransSchemaListFromDB(ER.schemaMapper.selectAll());
    }

    public static List<Schema> queryBySchema(SchemaDO SchemaDO) {
        List<SchemaDO> schemaDOList = ER.schemaMapper.selectBySchema(SchemaDO);
        return Trans.TransSchemaListFromDB(schemaDOList);
    }

    public static Schema queryByID(Long ID) {
        List<Schema> schemaDOList = queryBySchema(new SchemaDO(ID));
        if (schemaDOList.size() == 0) {
            throw new ERException(String.format("Schema with ID: %d not found", ID));
        } else {
            return schemaDOList.get(0);
        }
    }

    protected void deleteDB() {
        // cascade delete the entities and relationships in this schema
        for (Entity entity : entityList) {
            entity.deleteDB();
        }
        for (Relationship relationship : relationshipList) {
            relationship.deleteDB();
        }
        ER.schemaMapper.deleteByID(this.ID);
    }

    public void updateInfo(String name) {
        if (name != null) {
            this.name = name;
        }
        ER.schemaMapper.updateByID(new SchemaDO(this.ID, this.name, this.creator, 0L, 0, this.gmtCreate, new Date()));
    }
}