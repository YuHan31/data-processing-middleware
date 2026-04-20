package com.middleware.org.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.middleware.org.common.Result;
import com.middleware.org.entity.CleanRule;
import com.middleware.org.repository.CleanRuleRepository;
import com.middleware.org.service.ICleanRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 清洗规则 Controller
 */
@Tag(name = "清洗规则", description = "规则库查询接口")
@RestController
@RequestMapping("/api/clean-rule")
public class CleanRuleController {

    @Autowired
    private ICleanRuleService cleanRuleService;

    /**
     * 获取全部可用清洗规则（供前端展示/勾选）
     * GET /api/clean-rule/all
     */
    @Operation(summary = "获取规则列表", description = "返回所有已启用的清洗规则，供前端展示和勾选")
    @GetMapping("/all")
    public Result<Map<String, Object>> getAllRules(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.fail(401, "用户未登录");
        }

        List<CleanRule> rules = cleanRuleService.getAllEnabledRules();

        // 按 ruleType 分组返回
        Map<String, List<Map<String, String>>> grouped = rules.stream()
                .collect(Collectors.groupingBy(
                        CleanRule::getRuleType,
                        Collectors.mapping(r -> {
                            Map<String, String> map = new HashMap<>();
                            map.put("ruleCode", r.getRuleCode());
                            map.put("ruleName", r.getRuleName());
                            map.put("description", r.getDescription() != null ? r.getDescription() : "");
                            map.put("level", r.getLevel() != null ? r.getLevel() : "basic");
                            return map;
                        }, Collectors.toList())
                ));

        Map<String, Object> data = new HashMap<>();
        data.put("rules", grouped);
        data.put("total", rules.size());

        return Result.success(data);
    }

    /**
     * 获取全部规则（含禁用状态，后台管理用）
     * GET /api/clean-rule/list
     */
    @Operation(summary = "获取规则列表（管理）", description = "返回所有规则（含启用状态）")
    @GetMapping("/list")
    public Result<List<CleanRule>> listRules(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.fail(401, "用户未登录");
        }
        List<CleanRule> rules = cleanRuleService.getAllEnabledRules();
        return Result.success(rules);
    }

    /**
     * 切换规则启用状态（后台管理）
     * POST /api/clean-rule/toggle/{id}
     */
    @Operation(summary = "切换规则启用状态", description = "启用/禁用指定规则")
    @PostMapping("/toggle/{id}")
    public Result<Void> toggleRule(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.fail(401, "用户未登录");
        }
        cleanRuleService.toggleEnabled(id);
        return Result.success("规则状态已更新");
    }
}