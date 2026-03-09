package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface MemberRepository extends MongoRepository<Member,String> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("{ $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'email': { $regex: ?0, $options: 'i' } } ] }")
    Page<Member> searchMembers(String keyword, Pageable pageable);

}