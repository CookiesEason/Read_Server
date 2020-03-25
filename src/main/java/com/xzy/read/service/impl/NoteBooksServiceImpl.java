package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.NoteBooks;
import com.xzy.read.repository.NoteBooksRepository;
import com.xzy.read.service.NoteBooksService;
import com.xzy.read.util.ResultVoUtil;
import com.xzy.read.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author XieZhongYi
 * 2020/03/25 15:08
 */
@Service
public class NoteBooksServiceImpl implements NoteBooksService {

    private NoteBooksRepository noteBooksRepository;

    public NoteBooksServiceImpl(NoteBooksRepository noteBooksRepository) {
        this.noteBooksRepository = noteBooksRepository;
    }

    @Override
    public ResultVo getAll() {
        List<NoteBooks> noteBooks = noteBooksRepository.findAllByTelephone(SecurityUtil.getAuthentication().getName());
        return ResultVoUtil.success(noteBooks);
    }

    @Override
    public ResultVo create(NoteBooks noteBooks) {
        if (noteBooksRepository.findByNameAndTelephone(noteBooks.getName(),
                SecurityUtil.getAuthentication().getName())!=null) {
            return ResultVoUtil.error(0,"该文集名称已经存在");
        }
        noteBooks.setTelephone(SecurityUtil.getAuthentication().getName());
        noteBooksRepository.save(noteBooks);
        return ResultVoUtil.success();
    }

    @Override
    public ResultVo update(NoteBooks noteBooks) {
        Optional<NoteBooks> nb = noteBooksRepository.findById(noteBooks.getId());
        if (nb.isPresent()) {
            nb.get().setName(noteBooks.getName());
            noteBooksRepository.save(nb.get());
            return ResultVoUtil.success();
        }
        return ResultVoUtil.error(0,"该文集不存在");
    }


    @Override
    public ResultVo delete(Long id) {
        noteBooksRepository.deleteById(id);
        return ResultVoUtil.success();
    }
}
