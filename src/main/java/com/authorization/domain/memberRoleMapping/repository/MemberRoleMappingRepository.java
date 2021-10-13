package com.authorization.domain.memberRoleMapping.repository;

import com.authorization.domain.memberRoleMapping.model.entity.MemberRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRoleMappingRepository extends JpaRepository<MemberRoleMapping, Long> {
}
