package com.longfor.datav.admin.controller;

import cn.hutool.core.map.MapUtil;
import com.longfor.datav.admin.service.IDataService;
import com.longfor.datav.common.vo.Response;
import com.longfor.datav.common.vo.req.CalculateScoreReq;
import com.longfor.datav.common.vo.resp.CalculateScoreResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Optional;

/**
 * 数据管理
 * @author zhaoyl
 * @date 2024/1/26 10:30
 * @since 1.0
 */
@CrossOrigin
@RestController
@RequestMapping("/admin/datav")
public class DataController {

    @Autowired
    private IDataService dataService;


    /**
     * 得分导入
     * @param request
     * @return
     */
    @PostMapping("/v1/data/snapshot/synch")
    public Response<String> snapshotSynch(@RequestBody Map<String,String> params) {
        dataService.handelPersonSnapshotData(Optional.ofNullable(MapUtil.getStr(params,"account")).orElse("zhaoyalong"),null,null,MapUtil.getStr(params,"sprint"));
        return Response.ok("success");
    }

    /**
     * 得分导入
     * @return
     */
    @PostMapping("/v1/data/score/import")
    public Response<String> scoreImport(@RequestPart(value = "file") MultipartFile file) {
        return Response.ok(dataService.dataImport(file));
    }

    /**
     * 得分导入模版下载
     * @return
     */
    @GetMapping("/v1/data/score/template/download")
    public ResponseEntity<Resource> scoreTemplateDownload() {
        // 从 resources 目录中获取 Excel 模板文件
        Resource templateResource = new ClassPathResource("templates/数据导入模版.xlsx");

        // 构建响应头
        HttpHeaders headers = new HttpHeaders();
        String fileName = "得分数据导入模版.xlsx";
        try {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        // 构建 ResponseEntity 对象
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(templateResource);
    }

    /**
     * 指标得分推送
     *
     */
    @PostMapping("v1/calculate/score")
    public Response<CalculateScoreResp> calculateScore(@Valid @RequestBody CalculateScoreReq req) {
        return Response.ok(dataService.calculateScore(req));
    }

}
