package vn.java.EcommerceWeb.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractEntity<T extends Serializable> implements Serializable {
//    @CreatedBy
//    @Column(name = "created_by")
//    private T createdBy;
//
//    @LastModifiedBy
//    @Column(name = "updated_by")
//    private T updatedBy;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private T id;

    @Column(name = "created_at")
    @CreationTimestamp
//    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
//    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}
