package com.liferunner.service.impl;

import com.liferunner.custom.ProductCustomMapper;
import com.liferunner.dto.IndexProductDTO;
import com.liferunner.service.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ProductServiceImpl for : TODO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/15
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductServiceImpl implements IProductService {

    private final ProductCustomMapper productCustomMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<IndexProductDTO> getIndexProductDtoList(Integer rootCategoryId) {
        log.info("====== ProductServiceImpl#getIndexProductDtoList(rootCategoryId) : {}=======", rootCategoryId);
        Map<String, Integer> map = new HashMap<>();
        map.put("rootCategoryId", rootCategoryId);
        val indexProductDtoList = this.productCustomMapper.getIndexProductDtoList(map);
        if (CollectionUtils.isEmpty(indexProductDtoList)) {
            log.warn("ProductServiceImpl#getIndexProductDtoList未查询到任何商品信息");
        }
        log.info("查询结果：{}", indexProductDtoList);
        return indexProductDtoList;
    }
}
