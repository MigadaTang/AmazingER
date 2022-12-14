package io.github.MigadaTang.entity;

import io.github.MigadaTang.common.EntityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityDO {
    private Long ID;
    private String name;
    private Long schemaID;
    private EntityType entityType;
    private Long belongStrongEntityID;
    private Integer aimPort;
    private Integer isDelete;
    private Date gmtCreate;
    private Date gmtModified;

    public EntityDO(Long ID) {
        this.ID = ID;
    }

    public EntityDO(Long ID, Integer aimPort, Long belongStrongEntityID) {
        this.ID = ID;
        this.aimPort = aimPort;
        this.belongStrongEntityID = belongStrongEntityID;
    }

    public EntityDO(String name, Long schemaID, EntityType entityType) {
        this.name = name;
        this.schemaID = schemaID;
        this.entityType = entityType;
    }
}
