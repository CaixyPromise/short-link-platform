package ${packageName}.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${packageName}.annotation.AuthCheck;
import ${packageName}.common.Result;
import ${packageName}.common.DeleteRequest;
import ${packageName}.common.ErrorCode;
import ${packageName}.common.ResultUtils;
import ${packageName}.constant.UserConstant;
import ${packageName}.exception.BusinessException;
import ${packageName}.exception.ThrowUtils;
import ${packageName}.model.dto.${modelPackageName}.${upperDataKey}AddRequest;
import ${packageName}.model.dto.${modelPackageName}.${upperDataKey}EditRequest;
import ${packageName}.model.dto.${modelPackageName}.${upperDataKey}QueryRequest;
import ${packageName}.model.dto.${modelPackageName}.${upperDataKey}UpdateRequest;
import ${packageName}.model.entity.${upperDataKey};
import ${packageName}.model.enums.UserRoleEnum;
import ${packageName}.model.vo.user.UserVO;
import ${packageName}.model.vo.${modelPackageName}.${upperDataKey}VO;
import ${packageName}.service.${upperDataKey}Service;
import ${packageName}.manager.Authorization.AuthManager;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;

/**
 * ${dataName}接口
 * @author: CAIXYPROMISE
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/${dataKey}")
public class ${upperDataKey}Controller {

    private final ${upperDataKey}Service ${dataKey}Service;

    private final AuthManager authManager;

    // region 增删改查

    /**
     * 创建${dataName}
     *
     * @param ${dataKey}AddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public Result<Long> add${upperDataKey}(@RequestBody ${upperDataKey}AddRequest ${dataKey}AddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(${dataKey}AddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        ${upperDataKey} ${dataKey} = new ${upperDataKey}();
        BeanUtils.copyProperties(${dataKey}AddRequest, ${dataKey});
        // 数据校验
        ${dataKey}Service.valid${upperDataKey}(${dataKey}, true);
        // todo 填充默认值
        UserVO loginUser = authManager.getLoginUser();
        // ${dataKey}.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = ${dataKey}Service.save(${dataKey});
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long new${upperDataKey}Id = ${dataKey}.getId();
        return ResultUtils.success(new${upperDataKey}Id);
    }

    /**
     * 删除${dataName}
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public Result<Boolean> delete${upperDataKey}(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = authManager.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        ${upperDataKey} old${upperDataKey} = ${dataKey}Service.getById(id);
        ThrowUtils.throwIf(old${upperDataKey} == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        // checkIsSelfOrAdmin(old${upperDataKey});
        // 操作数据库
        boolean result = ${dataKey}Service.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新${dataName}（仅管理员可用）
     *
     * @param ${dataKey}UpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Boolean> update${upperDataKey}(@RequestBody ${upperDataKey}UpdateRequest ${dataKey}UpdateRequest) {
        if (${dataKey}UpdateRequest == null || ${dataKey}UpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        ${upperDataKey} ${dataKey} = new ${upperDataKey}();
        BeanUtils.copyProperties(${dataKey}UpdateRequest, ${dataKey});
        // 数据校验
        ${dataKey}Service.valid${upperDataKey}(${dataKey}, false);
        // 判断是否存在
        long id = ${dataKey}UpdateRequest.getId();
        ${upperDataKey} old${upperDataKey} = ${dataKey}Service.getById(id);
        ThrowUtils.throwIf(old${upperDataKey} == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = ${dataKey}Service.updateById(${dataKey});
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取${dataName}（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public Result<${upperDataKey}VO> get${upperDataKey}VOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        ${upperDataKey} ${dataKey} = ${dataKey}Service.getById(id);
        ThrowUtils.throwIf(${dataKey} == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(${dataKey}Service.get${upperDataKey}VO(${dataKey}, request));
    }

    /**
     * 分页获取${dataName}列表（仅管理员可用）
     *
     * @param ${dataKey}QueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public Result<Page<${upperDataKey}>> list${upperDataKey}ByPage(@RequestBody ${upperDataKey}QueryRequest ${dataKey}QueryRequest) {
        long current = ${dataKey}QueryRequest.getCurrent();
        long size = ${dataKey}QueryRequest.getPageSize();
        // 查询数据库
        Page<${upperDataKey}> ${dataKey}Page = ${dataKey}Service.page(new Page<>(current, size),
                ${dataKey}Service.getQueryWrapper(${dataKey}QueryRequest));
        return ResultUtils.success(${dataKey}Page);
    }

    /**
     * 分页获取${dataName}列表（封装类）
     *
     * @param ${dataKey}QueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public Result<Page<${upperDataKey}VO>> list${upperDataKey}VOByPage(@RequestBody ${upperDataKey}QueryRequest ${dataKey}QueryRequest,
                                                               HttpServletRequest request) {
        long current = ${dataKey}QueryRequest.getCurrent();
        long size = ${dataKey}QueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<${upperDataKey}> ${dataKey}Page = ${dataKey}Service.page(new Page<>(current, size),
                ${dataKey}Service.getQueryWrapper(${dataKey}QueryRequest));
        // 获取封装类
        return ResultUtils.success(${dataKey}Service.get${upperDataKey}VOPage(${dataKey}Page, request));
    }

    /**
     * 分页获取当前登录用户创建的${dataName}列表
     *
     * @param ${dataKey}QueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public Result<Page<${upperDataKey}VO>> listMy${upperDataKey}VOByPage(@RequestBody ${upperDataKey}QueryRequest ${dataKey}QueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(${dataKey}QueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        UserVO loginUser = authManager.getLoginUser();
        ${dataKey}QueryRequest.setUserId(loginUser.getId());
        long current = ${dataKey}QueryRequest.getCurrent();
        long size = ${dataKey}QueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<${upperDataKey}> ${dataKey}Page = ${dataKey}Service.page(new Page<>(current, size),
                ${dataKey}Service.getQueryWrapper(${dataKey}QueryRequest));
        // 获取封装类
        return ResultUtils.success(${dataKey}Service.get${upperDataKey}VOPage(${dataKey}Page, request));
    }

    /**
     * 编辑${dataName}（给用户使用）
     *
     * @param ${dataKey}EditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public Result<Boolean> edit${upperDataKey}(@RequestBody ${upperDataKey}EditRequest ${dataKey}EditRequest, HttpServletRequest request) {
        if (${dataKey}EditRequest == null || ${dataKey}EditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        ${upperDataKey} ${dataKey} = new ${upperDataKey}();
        BeanUtils.copyProperties(${dataKey}EditRequest, ${dataKey});
        // 数据校验
        ${dataKey}Service.valid${upperDataKey}(${dataKey}, false);
        UserVO loginUser = authManager.getLoginUser();
        // 判断是否存在
        long id = ${dataKey}EditRequest.getId();
        ${upperDataKey} old${upperDataKey} = ${dataKey}Service.getById(id);
        ThrowUtils.throwIf(old${upperDataKey} == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        // checkIsSelfOrAdmin(old${upperDataKey});
        // 操作数据库
        boolean result = ${dataKey}Service.updateById(${dataKey});
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    // private void checkIsSelfOrAdmin(${upperDataKey} ${dataKey})
    // {
    //     if (!${dataKey}.getUserId().equals(loginUser.getId()) && !authManager.isAdmin())
    //     {
    //         throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    //     }
    // }
}
