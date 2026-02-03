package com.buyer.repository.MongoDB;

import com.buyer.entity.MongoDB.MongoOrder;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdersRepository extends MongoRepository<MongoOrder, String> {

    Optional<MongoOrder> findById(String id);

}
