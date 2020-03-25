package com.xzy.read.service;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.NoteBooks;

/**
 * @author XieZhongYi
 * 2020/03/25 15:06
 */
public interface NoteBooksService {

    ResultVo getAll();

    ResultVo create(NoteBooks noteBooks);

    ResultVo update(NoteBooks noteBooks);

    ResultVo delete(Long id);

}
