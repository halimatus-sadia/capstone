package com.example.capstone.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    // Top-level comments only (parent IS NULL), ordered oldest→newest
    @Query("""
              SELECT c FROM PostComment c
              WHERE c.post.id = :postId AND c.parent IS NULL
              ORDER BY c.createdAt ASC
            """)
    List<PostComment> findTopLevel(Long postId);

    // Replies for a given parent (already ordered by @OrderBy on the entity, but this is handy if you need it)
    List<PostComment> findByParentIdOrderByCreatedAtAsc(Long parentId);
}
