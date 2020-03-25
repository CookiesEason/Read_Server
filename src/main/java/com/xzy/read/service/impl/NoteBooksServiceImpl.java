package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.NoteBooks;
import com.xzy.read.repository.NoteBooksRepository;
import com.xzy.read.service.NoteBooksService;
import com.xzy.read.service.UserService;
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

    private UserService userService;

    public NoteBooksServiceImpl(NoteBooksRepository noteBooksRepository, UserService userService) {
        this.noteBooksRepository = noteBooksRepository;
        this.userService = userService;
    }

    @Override
    public ResultVo getAll() {
        Long id = userService.getUserId();
        List<NoteBooks> noteBooks = noteBooksRepository.findAllByUserId(id);
        return ResultVoUtil.success(noteBooks);
    }

    @Override
    public ResultVo create(NoteBooks noteBooks) {
        Long id = userService.getUserId();
        if (noteBooksRepository.findByNameAndUserId(noteBooks.getName(), id)!=null) {
            return ResultVoUtil.error(0,"该文集名称已经存在");
        }
        noteBooks.setUserId(id);
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
