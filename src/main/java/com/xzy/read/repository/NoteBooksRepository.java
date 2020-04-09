package com.xzy.read.repository;

import com.xzy.read.entity.NoteBooks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author XieZhongYi
 * 2020/03/25 15:05
 */
public interface NoteBooksRepository extends JpaRepository<NoteBooks, Long> {

    List<NoteBooks> findAllByUserIdAndIsDeleted(Long userId, Boolean isDeleted);

    NoteBooks findByNameAndUserId(String name,Long userId);

    Optional<NoteBooks> findByIdAndIsDeleted(Long id, Boolean isDeleted);

    Page<NoteBooks> findAllByNameLike(String content, Pageable pageable);

}
