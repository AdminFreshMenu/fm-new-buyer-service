package com.buyer.entity.OrderEnum;

public enum RecordStatus {
    INACTIVE((byte)0), ACTIVE((byte)1) , DELETED(null), COMPLETED((byte)2);

    private Byte id;
    private RecordStatus(Byte id){
        this.id = id;
    }

    public Byte getId(){
        return this.id;
    }
}
