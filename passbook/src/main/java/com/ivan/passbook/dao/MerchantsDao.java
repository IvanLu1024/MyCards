package com.ivan.passbook.dao;

import com.ivan.passbook.entity.Merchants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <h1>Merchant Dao 接口 </h1>
 */
public interface MerchantsDao extends JpaRepository<Merchants,Integer> {

    /**
     * <h2>通过 id 获取商户对象 </h2>
     * @param id
     * @return {@link Merchants}
     */
    Merchants findById(Integer id);

    /**
     * <h2> 通过商户名称获取商户对象 </h2>
     * @param name 商户名称
     * @return {@link Merchants}
     */
    Merchants findByName(String name);

    /**
     * <h2> 根据商户 ids 获取多个商户对象 </h2>
     * @param ids 商户 ids
     * @return {@link Merchants}
     */
    List<Merchants> findByIdIn(List<Integer> ids);

}
