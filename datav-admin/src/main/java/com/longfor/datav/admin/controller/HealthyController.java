package com.longfor.datav.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查接口
 * @author zyh
 * @since jdk 1.8
 * @date 2024-01-25
 */

@RestController
@RequestMapping("/admin/datav")
public class HealthyController {

    @GetMapping("/healthy")
    public boolean healthy() {
        return Boolean.TRUE;
    }
}
