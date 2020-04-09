package com.xzy.read.controller;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XieZhongYi
 * 2020/04/09 11:06
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {


    private SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResultVo search(@RequestParam String content, @RequestParam(defaultValue = "1") int page,
                           @RequestParam String type) {
        return searchService.search(content, page, type);
    }

}
