package com.buyer.entity.MongoDB;

import com.buyer.dto.TaxDTO;
import com.buyer.entity.MongoDB.dto.ExperimentData;
import com.buyer.entity.MongoDB.dto.OMSPaymentDetail;
import com.buyer.entity.MongoDB.dto.OrderStatusDto;
import com.buyer.entity.MongoDB.dto.ShopifyAddress;
import com.buyer.entity.MongoDB.dto.ShopifyDiscountCode;
import com.buyer.entity.MongoDB.dto.ShopifyFulfillment;
import com.buyer.entity.MongoDB.dto.ShopifyOrderLineItem;
import com.buyer.entity.OrderEnum.Channel;
import com.buyer.entity.OrderEnum.OrderAdditionalData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"line_items", "fulfillments", "paymentDetails", "experimentDataList" , "status"})
@EqualsAndHashCode(exclude = {"line_items", "fulfillments", "paymentDetails", "experimentDataList" , "status"})
@Document(collection = "orders")
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class MongoOrder  {

    @Id
    private Long id;   // Mongo _id
    private Long order_number;
    private String financial_status;
    private List<ShopifyFulfillment> fulfillments;
    private DateTime cancelled_at;
    private String created_at;
    private String email;
    private String fulfillment_status;
    private List<ShopifyOrderLineItem> line_items;
    private String name;
    private String note;
    private ShopifyDiscountCode[] discount_codes;
    private Number total_discounts;
    private Number cart_level_discounts;
    private ShopifyAddress shipping_address;
    private Number sub_total;
    private Number shipping_charge;
    private Number packaging_fee;
    private Number total_price;
    private Integer amountToBeCollected;
    private TaxDTO taxDTO;
    private Long fc_id;
    private String fc_name;
    private Long subZoneId;
    private String subZoneName;
    private String subZoneShortCode;
    private Long userAddressId;
    private Channel channel;
    private Map<OrderAdditionalData, String> orderAdditionalDetails;
    private List<OMSPaymentDetail> paymentDetails;
    private OrderStatusDto status;
    private DateTime deliveryTime;
    private String loggedInUserName;
    private Boolean isNewUser = false;
    private Number promoBalance;
    private Number bankOffer;
    private Boolean reportingNewUser = false;
    private List<ExperimentData> experimentDataList;
    private Long userId;
    private Boolean addressChange;
    private Integer brandId;
    private String searchKey;
    private BigDecimal productDiscount;
}
