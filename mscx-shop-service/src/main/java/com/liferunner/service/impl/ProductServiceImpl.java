package com.liferunner.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.liferunner.custom.ProductCustomMapper;
import com.liferunner.dto.IndexProductDTO;
import com.liferunner.dto.ProductCommentDTO;
import com.liferunner.dto.ShopcartResponseDTO;
import com.liferunner.mapper.ProductsCommentsMapper;
import com.liferunner.mapper.ProductsImgMapper;
import com.liferunner.mapper.ProductsMapper;
import com.liferunner.mapper.ProductsParamMapper;
import com.liferunner.mapper.ProductsSpecMapper;
import com.liferunner.pojo.Products;
import com.liferunner.pojo.ProductsComments;
import com.liferunner.pojo.ProductsImg;
import com.liferunner.pojo.ProductsParam;
import com.liferunner.pojo.ProductsSpec;
import com.liferunner.service.IProductService;
import com.liferunner.utils.CommonPagedResult;
import com.liferunner.utils.SecurityTools;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

/**
 * ProductServiceImpl for : 商品操作相关service
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
    private final ProductsCommentsMapper productsCommentsMapper;

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

    @Override
    public Integer countProductCommentLevel(String pid, Integer level) {
        ProductsComments condition = new ProductsComments();
        condition.setCommentLevel(level);
        condition.setProductId(pid);
        val count = this.productsCommentsMapper.selectCount(condition);
        return count;
    }

    @Override
    public CommonPagedResult getProductComments(String pid, Integer level, Integer pageNumber, Integer pageSize) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("productId", pid);
        paramMap.put("commentLevel", level);
        // mybatis-pagehelper
        PageHelper.startPage(pageNumber, pageSize);
        val productCommentList = this.productCustomMapper.getProductCommentList(paramMap);
        for (ProductCommentDTO item : productCommentList) {
            item.setNickname(SecurityTools.hiddenPartString4SecurityDisplay(item.getNickname()));
        }
        // 获取mybatis插件中获取到信息
        PageInfo<?> pageInfo = new PageInfo<>(productCommentList);
        // 封装为返回到前端分页组件可识别的视图
        val commonPagedResult = CommonPagedResult.builder()
                .pageNumber(pageNumber)
                .rows(productCommentList)
                .totalPage(pageInfo.getPages())
                .records(pageInfo.getTotal())
                .build();
        return commonPagedResult;
    }

    @Override
    public CommonPagedResult searchProductList(String keyword, String sortby, Integer pageNumber, Integer pageSize) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("keyword", keyword);
        paramMap.put("sortby", sortby);
        // mybatis-pagehelper
        PageHelper.startPage(pageNumber, pageSize);
        val searchProductDTOS = this.productCustomMapper.searchProductList(paramMap);
        // 获取mybatis插件中获取到信息
        PageInfo<?> pageInfo = new PageInfo<>(searchProductDTOS);
        // 封装为返回到前端分页组件可识别的视图
        val commonPagedResult = CommonPagedResult.builder()
                .pageNumber(pageNumber)
                .rows(searchProductDTOS)
                .totalPage(pageInfo.getPages())
                .records(pageInfo.getTotal())
                .build();
        return commonPagedResult;
    }

    // 方法重载
    @Override
    public CommonPagedResult searchProductList(Integer categoryId, String sortby, Integer pageNumber, Integer pageSize) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("categoryId", categoryId);
        paramMap.put("sortby", sortby);
        // mybatis-pagehelper
        PageHelper.startPage(pageNumber, pageSize);
        val searchProductDTOS = this.productCustomMapper.searchProductListByCategoryId(paramMap);
        // 获取mybatis插件中获取到信息
        PageInfo<?> pageInfo = new PageInfo<>(searchProductDTOS);
        // 封装为返回到前端分页组件可识别的视图
        val commonPagedResult = CommonPagedResult.builder()
                .pageNumber(pageNumber)
                .rows(searchProductDTOS)
                .totalPage(pageInfo.getPages())
                .records(pageInfo.getTotal())
                .build();
        return commonPagedResult;
    }

    @Override
    public List<ShopcartResponseDTO> refreshShopcart(String specIds) {
        String[] temp = specIds.split(",");
        List<String> specIdList = CollectionUtils.arrayToList(temp);
        val shopcartResponseDTOS = this.productCustomMapper.refreshShopcart(specIdList);
        return shopcartResponseDTOS;
    }

    @Override
    public List<ProductsSpec> getProductSpecByIds(String specIds) {
        return this.productCustomMapper.getAllProductSpec(
                CollectionUtils.arrayToList(specIds.split(",")));
    }

    @Transactional
    @Override
    public void decreaseProductSpecStock(String specId, Integer buyNumber) {
        val result = this.productCustomMapper.decreaseProductSpecStock(specId, buyNumber);
        if (result < 1) {
            log.error("购买{}件商品,扣减库存失败:{}", buyNumber, specId);
            throw new RuntimeException("扣减库存失败");
        }
    }
}
