package com.friendsfinder.app.model.entity;

import com.friendsfinder.app.model.Node;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Graph")
@Table(name = "graph")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Graph extends BaseEntity {
    @Id
    @Getter
    @Setter
    @Column(name = "user_id")
    private Integer userId;
    
    @Getter
    @Setter
    @Type(type = "jsonb")
    @Column(columnDefinition = "json")
    private ArrayList<ArrayList<ArrayList<Node>>> nodes;

    @Getter
    @Setter
    @Type(type = "jsonb")
    @Column(name = "unique_ids", columnDefinition = "json")
    private List<Integer> uniqueIds;

    @Getter
    @Setter
    private Integer width;

    @Getter
    @Setter
    private Integer depth;

    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "user_id",
            referencedColumnName = "token_user_id",
            insertable = false, updatable = false,
            foreignKey = @javax.persistence
                    .ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Token token;
}
