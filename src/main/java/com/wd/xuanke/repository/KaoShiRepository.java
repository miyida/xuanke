package com.wd.xuanke.repository;

import com.wd.xuanke.entiy.KaoShiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface KaoShiRepository extends JpaRepository<KaoShiEntity, Integer>, JpaSpecificationExecutor<KaoShiEntity> {

}
