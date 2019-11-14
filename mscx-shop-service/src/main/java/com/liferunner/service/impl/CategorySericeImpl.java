package com.liferunner.service.impl;

import com.liferunner.custom.CategoryCustomMapper;
import com.liferunner.dto.CategoryResponseDTO;
import com.liferunner.dto.SecondSubCategoryResponseDTO;
import com.liferunner.enums.CategoryTypeEnum;
import com.liferunner.mapper.CategoryMapper;
import com.liferunner.pojo.Category;
import com.liferunner.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * CategorySericeImpl for : 分类service impl
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/13
 */
@Service
@Slf4j
public class CategorySericeImpl implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryCustomMapper categoryCustomMapper;

    @Override
    public List<CategoryResponseDTO> getAllRootCategorys() {
        Example example = new Example(Category.class);
        val conditions = example.createCriteria();
        conditions.andEqualTo("type", CategoryTypeEnum.ROOT.type);
        val categoryList = this.categoryMapper.selectByExample(example);
        //声明返回对象
        List<CategoryResponseDTO> categoryResponseDTOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(categoryList)) {
            //赋值
            CategoryResponseDTO dto;
            for (Category category : categoryList) {
                dto = new CategoryResponseDTO();
                BeanUtils.copyProperties(category, dto);
                categoryResponseDTOS.add(dto);
            }
        }
        return categoryResponseDTOS;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<SecondSubCategoryResponseDTO> getAllSubCategorys(Integer parentId) {
        return this.categoryCustomMapper.getSubCategorys(parentId);
    }
}
