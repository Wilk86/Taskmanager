package pl.taskmanager.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Role {

    @Id                                                     // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // AI
    private Long role_id;
    private String roleName;

}
