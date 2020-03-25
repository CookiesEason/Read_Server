package com.xzy.read.repository;

import com.xzy.read.entity.NoteBooks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author XieZhongYi
 * 2020/03/25 15:05
 */
public interface NoteBooksRepository extends JpaRepository<NoteBooks, Long> {

    List<NoteBooks> findAllByUserId(Long userId);

    NoteBooks findByNameAndUserId(String name,Long userId);

}
