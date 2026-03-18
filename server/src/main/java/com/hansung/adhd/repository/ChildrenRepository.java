package com.hansung.adhd.repository;

import com.hansung.adhd.domain.Children;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChildrenRepository extends JpaRepository<Children, Long> {

    // JPA의 마법! 메서드 이름만 이렇게 지어주면, 알아서 select 쿼리를 짜준다
    Optional<Children> findByLastConnectedDeviceId(String lastConnectedDeviceId);
}