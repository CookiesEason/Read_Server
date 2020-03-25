package com.xzy.read.controller;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.NoteBooks;
import com.xzy.read.service.NoteBooksService;
import org.springframework.web.bind.annotation.*;

/**
 * @author XieZhongYi
 * 2020/03/25 15:12
 */
@RestController
@RequestMapping("/api")
public class NoteBooksController {

    private NoteBooksService noteBooksService;

    public NoteBooksController(NoteBooksService noteBooksService) {
        this.noteBooksService = noteBooksService;
    }

    @GetMapping("/notebooks")
    public ResultVo get() {
        return noteBooksService.getAll();
    }

    @PostMapping("/notebooks")
    public ResultVo create(@RequestBody NoteBooks noteBooks) {
        return noteBooksService.create(noteBooks);
    }

    @PutMapping("/notebooks")
    public ResultVo update(@RequestBody NoteBooks noteBooks) {
        return noteBooksService.update(noteBooks);
    }

    @DeleteMapping("/notebooks")
    public ResultVo delete(@RequestBody NoteBooks noteBooks) {
        return noteBooksService.delete(noteBooks.getId());
    }

}
