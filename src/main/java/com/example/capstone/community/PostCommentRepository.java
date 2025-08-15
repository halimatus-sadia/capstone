package com.example.capstone.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    @Query("""
              SELECT DISTINCT c
              FROM PostComment c
              LEFT JOIN FETCH c.replies r
              WHERE c.post.id = :postId
                AND c.parent IS NULL
              ORDER BY c.createdAt ASC
            """)
    List<PostComment> findTopLevelWithReplies(Long postId);

    List<PostComment> findByPostIdOrderByCreatedAtAsc(Long postId);
}
