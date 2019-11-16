package com.liferunner.service.impl;

import com.liferunner.custom.ProductCustomMapper;
import com.liferunner.dto.IndexProductDTO;
import com.liferunner.mapper.ProductsImgMapper;
import com.liferunner.mapper.ProductsMapper;
import com.liferunner.mapper.ProductsParamMapper;
import com.liferunner.mapper.ProductsSpecMapper;
import com.liferunner.pojo.Products;
import com.liferunner.pojo.ProductsImg;
import com.liferunner.pojo.ProductsParam;
import com.liferunner.pojo.ProductsSpec;
import com.liferunner.service.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

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

    // RequiredArgsConstructor 构造器注入
    private final ProductCustomMapper productCustomMapper;
    private final ProductsMapper productsMapper;
    private final ProductsParamMapper productsParamMapper;
    private final ProductsImgMapper productsImgMapper;
    private final ProductsSpecMapper productsSpecMapper;

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

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Products findProductByPid(String pid) {
        return this.productsMapper.selectByPrimaryKey(pid);
    }

    @Override
    public List<ProductsImg> getProductImgsByPid(String pid) {
        Example example = new Example(ProductsImg.class);
        val condition = example.createCriteria();
        condition.andEqualTo("productId", pid);
        return this.productsImgMapper.selectByExample(example);
    }

    @Override
    public List<ProductsSpec> getProductSpecsByPid(String pid) {
        Example example = new Example(ProductsSpec.class);
        val condition = example.createCriteria();
        condition.andEqualTo("productId", pid);
        return this.productsSpecMapper.selectByExample(example);
    }

    @Override
    public ProductsParam findProductParamByPid(String pid) {
        Example example = new Example(ProductsParam.class);
        val condition = example.createCriteria();
        condition.andEqualTo("productId", pid);
        return this.productsParamMapper.selectOneByExample(example);
    }
}
