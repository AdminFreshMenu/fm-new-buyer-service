package com.buyer.entity.PaymentEnum;

public enum PaymentMethod {
    CARDS(false), SAVED_CARDS(false), NETBANKING(false), MOBIKWIK(false), MOBIKWIK_WALLET(false), PAYTM(
            false), PAYTM_WALLET(false), CITRUS(false), CITRUS_WALLET(false), FREECHARGE(false), FREECHARGE_WALLET(
            false), OLA(false), OLAMONEY(false), OLA_WALLET(false), PAYUW(false), PAYUW_WALLET(
            false), PAYUMONEY(false),MAGIC_PIN(true), BITSILA_ONDC(true), FALCONS(true), THRIVE(true), ZOMATO(
            true), FOODPANDA(true), FRESHMENU_DISCOUNT(false), COD(false), ONLINE(false), MPESA(false), SBIBUDDY(
            false), HUNGER_BOX(true), UBER_EATS(
            true), LAZYPAY(false), SIMPL(false), AMAZON_PAY(false), PHONEPE(false), PAYTM_POS(false), MAGIC_PIN_OFFER(true), BITSILA_ONDC_OFFER(true), FALCONS_OFFER(true), THRIVE_OFFER(true), ZOMATO_OFFER(
            true), SODEXO(false), GOOGLE_TEZ(false), AXIS_UPI(false), HDFC_UPI(false), ICICI_UPI(false), UPI(false),
    BHIM_UPI(false), PAYTM_UPI(false), CHILLAR_UPI(false), POCKET_UPI(false), SBI_UPI(false), WHATSAPP_UPI(
            false), PHONEPE_APP(true), HELP_CHAT(true), SWIGGY(true), GOOGLE_TEZ_MOBILE(false), PAYPAL(
            false), ORDER_UPDATE(false),
    PHONEPE_AFFILIATE(false),
    PHONEPE_WANDERCRUST_AFFILIATE(false),
    AMAZON_PAY_AFFILIATE(false),
    OLAPOSTPAID(false),
    PAYZAPP(false),
    DAILY_NINJA(true), QWIK_SILVER(false), PAYTM_POSTPAID(false),
    PAYTM_AFFILIATE(false), AMAZON_UPI(false),
    TWID_AFFILIATE(false), MIPAY(false),
    GOKHANA(true),
    PAYZAPP_AFFILIATE(true),
    GOOGLE_PAY_SPOT(false),
    TWID_PAY(false),
    URBAN_PIPER(true),
    WHATSAPP(true),
    BULK_ORDER(true),

    DOTPE(true),

    POS(true),
    CORPORATE_ORDER(true),
    RAPIDO_FOOD(true);


    private Boolean isThirdParty;

    private PaymentMethod(Boolean isThirdParty) {
        this.isThirdParty = isThirdParty;
    }

    public Boolean getIsThirdParty() {
        return isThirdParty;
    }

    public void setIsThirdParty(Boolean isThirdParty) {
        this.isThirdParty = isThirdParty;
    }

}
